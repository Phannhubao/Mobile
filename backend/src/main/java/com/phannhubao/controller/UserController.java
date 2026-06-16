package com.phannhubao.controller;

import com.phannhubao.dto.request.*;
import com.phannhubao.dto.response.OrderCreatedResponse;
import com.phannhubao.dto.response.UserResponse;
import com.phannhubao.dto.response.UserProfileStatsResponse;
import com.phannhubao.entity.*;
import com.phannhubao.repository.*;
import com.phannhubao.security.CustomUserDetails;
import com.phannhubao.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final OrderRepository orderRepository;
    private final CustomerAddressRepository addressRepository;
    private final CardRepository cardRepository;
    private final CouponRepository couponRepository;
    private final ReviewRepository reviewRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartService cartService;

    private Customer getOrCreateCustomer(User user) {
        return customerRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    String[] names = user.getName().split(" ", 2);
                    String firstName = names[0];
                    String lastName = names.length > 1 ? names[1] : "";
                    if (lastName.isEmpty()) {
                        lastName = firstName;
                    }
                    Customer customer = Customer.builder()
                            .user(user)
                            .firstName(firstName)
                            .lastName(lastName)
                            .email(user.getEmail())
                            .passwordHash(user.getPassword() != null ? user.getPassword() : "")
                            .active(true)
                            .registeredAt(java.time.OffsetDateTime.now())
                            .updatedAt(java.time.OffsetDateTime.now())
                            .build();
                    return customerRepository.save(customer);
                });
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        Customer customer = customerRepository.findByUserId(user.getId()).orElse(null);
        
        UserResponse.UserResponseBuilder builder = UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .provider(user.getProvider())
                .avatarUrl(user.getAvatarUrl())
                .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()));

        if (customer != null) {
            builder.dateOfBirth(customer.getDateOfBirth() != null ? customer.getDateOfBirth().toString() : null)
                   .salesNotification(customer.getSalesNotification())
                   .newArrivalsNotification(customer.getNewArrivalsNotification())
                   .deliveryStatusNotification(customer.getDeliveryStatusNotification());
        } else {
            builder.salesNotification(true)
                   .newArrivalsNotification(true)
                   .deliveryStatusNotification(true);
        }

        return ResponseEntity.ok(builder.build());
    }

    @PutMapping("/profile")
    public ResponseEntity<Void> updateProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody ProfileUpdateRequest request
    ) {
        User user = userDetails.getUser();
        Customer customer = getOrCreateCustomer(user);

        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            user.setName(request.getName().trim());
            userRepository.save(user);
            
            String[] names = user.getName().split(" ", 2);
            customer.setFirstName(names[0]);
            customer.setLastName(names.length > 1 ? names[1] : names[0]);
        }

        if (request.getDateOfBirth() != null && !request.getDateOfBirth().trim().isEmpty()) {
            try {
                customer.setDateOfBirth(LocalDate.parse(request.getDateOfBirth().trim()));
            } catch (Exception e) {
                // Ignore parsing errors
            }
        }

        if (request.getSalesNotification() != null) {
            customer.setSalesNotification(request.getSalesNotification());
        }
        if (request.getNewArrivalsNotification() != null) {
            customer.setNewArrivalsNotification(request.getNewArrivalsNotification());
        }
        if (request.getDeliveryStatusNotification() != null) {
            customer.setDeliveryStatusNotification(request.getDeliveryStatusNotification());
        }

        customer.setUpdatedAt(java.time.OffsetDateTime.now());
        customerRepository.save(customer);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/profile-stats")
    public ResponseEntity<UserProfileStatsResponse> getProfileStats(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getId();
        long orders = orderRepository.countByCustomerUserId(userId);
        long addresses = addressRepository.countByCustomerUserId(userId);
        long coupons = couponRepository.count();
        long reviews = reviewRepository.countByUserId(userId);

        java.util.List<Card> cards = cardRepository.findByCustomerUserId(userId);
        String cardSummary = "No payment methods";
        if (!cards.isEmpty()) {
            Card primary = cards.get(0);
            String type = primary.getCardType() != null ? primary.getCardType() : "Visa";
            String lastFour = primary.getLastFour() != null ? primary.getLastFour() : "34";
            cardSummary = type + " **" + lastFour;
        }

        UserProfileStatsResponse stats = UserProfileStatsResponse.builder()
                .orderCount(orders)
                .addressCount(addresses)
                .paymentMethodSummary(cardSummary)
                .couponCount(coupons)
                .reviewCount(reviews)
                .build();
        return ResponseEntity.ok(stats);
    }

    @PostMapping("/orders")
    public ResponseEntity<OrderCreatedResponse> createOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody OrderCreateRequest request
    ) {
        User user = userDetails.getUser();
        Customer customer = getOrCreateCustomer(user);
        
        OrderStatus defaultStatus = orderStatusRepository.findAll().stream()
                .filter(s -> s.getStatusName().equalsIgnoreCase("Processing"))
                .findFirst()
                .orElseGet(() -> orderStatusRepository.save(OrderStatus.builder()
                        .statusName("Processing")
                        .color("#FFC107")
                        .createdAt(java.time.OffsetDateTime.now())
                        .updatedAt(java.time.OffsetDateTime.now())
                        .build()));

        Order order = Order.builder()
                .customer(customer)
                .createdAt(java.time.OffsetDateTime.now())
                .orderStatus(defaultStatus)
                .totalAmount(request.getTotalAmount())
                .shippingAddress(request.getShippingAddress())
                .deliveryMethod(request.getDeliveryMethod())
                .paymentMethod(request.getPaymentMethod())
                .discount(request.getDiscount())
                .build();

        List<OrderItem> items = new java.util.ArrayList<>();
        if (request.getItems() != null) {
            for (OrderItemRequest itemReq : request.getItems()) {
                Product product = productRepository.findById(itemReq.getProductId())
                        .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm"));
                
                items.add(OrderItem.builder()
                        .order(order)
                        .product(product)
                        .quantity(itemReq.getQuantity())
                        .price(itemReq.getPrice())
                        .selectedSize(itemReq.getSelectedSize())
                        .selectedColor(itemReq.getSelectedColor())
                        .build());
            }
        }
        order.setItems(items);

        Order saved = orderRepository.save(order);
        cartService.clearCart(user.getId());
        return ResponseEntity.ok(OrderCreatedResponse.builder()
                .orderId(saved.getId())
                .status(defaultStatus.getStatusName())
                .build());
    }

    @GetMapping("/cards")
    public ResponseEntity<List<Card>> getMyCards(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        Customer customer = getOrCreateCustomer(user);
        List<Card> cards = cardRepository.findByCustomerUserId(user.getId());
        return ResponseEntity.ok(cards);
    }

    @PostMapping("/cards")
    public ResponseEntity<Card> addCard(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody CardRequest request
    ) {
        User user = userDetails.getUser();
        Customer customer = getOrCreateCustomer(user);

        Card card = Card.builder()
                .customer(customer)
                .cardType(request.getCardType())
                .lastFour(request.getLastFour())
                .build();

        Card saved = cardRepository.save(card);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/addresses")
    public ResponseEntity<List<CustomerAddress>> getMyAddresses(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        getOrCreateCustomer(user);
        List<CustomerAddress> addresses = addressRepository.findByCustomerUserId(user.getId());
        return ResponseEntity.ok(addresses);
    }

    @PostMapping("/addresses")
    public ResponseEntity<CustomerAddress> addAddress(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody AddressRequest request
    ) {
        User user = userDetails.getUser();
        Customer customer = getOrCreateCustomer(user);

        CustomerAddress address = CustomerAddress.builder()
                .customer(customer)
                .addressLine1(valueOrBlank(request.getAddressLine1()))
                .addressLine2(valueOrBlank(request.getAddressLine2()))
                .city(valueOrBlank(request.getCity()))
                .country(valueOrBlank(request.getCountry()))
                .postalCode(valueOrBlank(request.getPostalCode()))
                .phoneNumber(valueOrBlank(request.getPhoneNumber()))
                .dialCode(valueOrBlank(request.getDialCode()))
                .build();

        CustomerAddress saved = addressRepository.save(address);
        return ResponseEntity.ok(saved);
    }

    private String valueOrBlank(String value) {
        return value == null ? "" : value.trim();
    }

    @PostMapping("/simulate/order")
    public ResponseEntity<Void> simulateOrder(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        Customer customer = getOrCreateCustomer(user);
        OrderStatus defaultStatus = orderStatusRepository.findAll().stream().findFirst()
                .orElseGet(() -> orderStatusRepository.save(OrderStatus.builder()
                        .statusName("Delivered")
                        .color("#4CAF50")
                        .createdAt(java.time.OffsetDateTime.now())
                        .updatedAt(java.time.OffsetDateTime.now())
                        .build()));

        orderRepository.save(Order.builder()
                .customer(customer)
                .createdAt(java.time.OffsetDateTime.now())
                .orderStatus(defaultStatus)
                .build());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/simulate/address")
    public ResponseEntity<Void> simulateAddress(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        Customer customer = getOrCreateCustomer(user);
        addressRepository.save(CustomerAddress.builder()
                .customer(customer)
                .addressLine1("Simulation Address " + (addressRepository.countByCustomerUserId(user.getId()) + 1))
                .city("Hanoi")
                .country("Vietnam")
                .postalCode("10000")
                .phoneNumber("111222333")
                .dialCode("+84")
                .build());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/simulate/card")
    public ResponseEntity<Void> simulateCard(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        Customer customer = getOrCreateCustomer(user);
        cardRepository.save(Card.builder()
                .customer(customer)
                .cardType("Mastercard")
                .lastFour("99")
                .build());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/simulate/review")
    public ResponseEntity<Void> simulateReview(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        java.util.List<Product> products = productRepository.findAll();
        Product product = products.isEmpty() ? null : products.get(0);
        if (product != null) {
            reviewRepository.save(Review.builder()
                    .user(user)
                    .product(product)
                    .content("Giao hang cuc ky nhanh, rat hai long!")
                    .rating(5)
                    .createdAt(java.time.OffsetDateTime.now())
                    .build());
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/simulate/coupon")
    public ResponseEntity<Void> simulateCoupon() {
        couponRepository.save(Coupon.builder()
                .code("SIMULATE" + (couponRepository.count() + 1))
                .discountType("PERCENTAGE")
                .discountValue(15.0)
                .timesUsed(0.0)
                .maxUsage(1.0)
                .build());
        return ResponseEntity.ok().build();
    }
}
