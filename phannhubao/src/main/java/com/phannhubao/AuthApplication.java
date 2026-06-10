package com.phannhubao;

import com.phannhubao.entity.Role;
import com.phannhubao.entity.User;
import com.phannhubao.repository.RoleRepository;
import com.phannhubao.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Collections;

@SpringBootApplication
public class AuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }

    @Bean
    @org.springframework.core.annotation.Order(2)
    @ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
    public CommandLineRunner initDatabase(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            com.phannhubao.repository.OrderRepository orderRepository,
            com.phannhubao.repository.CustomerAddressRepository addressRepository,
            com.phannhubao.repository.CardRepository cardRepository,
            com.phannhubao.repository.CouponRepository couponRepository,
            com.phannhubao.repository.ReviewRepository reviewRepository,
            com.phannhubao.repository.ProductRepository productRepository,
            com.phannhubao.repository.OrderStatusRepository orderStatusRepository,
            com.phannhubao.repository.CustomerRepository customerRepository
    ) {
        return args -> {
            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_USER").build()));

            Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_ADMIN").build()));

            if (!userRepository.existsByEmail("baophan@gmail.com")) {
                User user = User.builder()
                        .name("baophan")
                        .email("baophan@gmail.com")
                        .password(passwordEncoder.encode("baophan112003"))
                        .provider("local")
                        .roles(new java.util.HashSet<>(java.util.List.of(userRole, adminRole)))
                        .build();
                userRepository.save(user);
                System.out.println(">>> SEEDED USER WITH ADMIN ROLE: baophan@gmail.com / baophan112003 <<<");
            } else {
                userRepository.findByEmail("baophan@gmail.com").ifPresent(user -> {
                    if (user.getRoles().stream().noneMatch(r -> r.getName().equals("ROLE_ADMIN"))) {
                        user.getRoles().add(adminRole);
                        userRepository.save(user);
                        System.out.println(">>> UPGRADED baophan@gmail.com TO ROLE_ADMIN <<<");
                    }
                });
            }

            // Seed user profile statistics mock data
            final User dbUser = userRepository.findByEmail("baophan@gmail.com").orElse(null);
            if (dbUser != null) {
                final Long userId = dbUser.getId();

                // Get or create Customer for this user
                final String[] names = dbUser.getName().split(" ", 2);
                final String firstName = names[0];
                final String lastName = names.length > 1 ? names[1] : firstName;
                
                final com.phannhubao.entity.Customer customer = customerRepository.findByUserId(userId)
                        .orElseGet(() -> customerRepository.save(com.phannhubao.entity.Customer.builder()
                                .user(dbUser)
                                .firstName(firstName)
                                .lastName(lastName)
                                .email(dbUser.getEmail())
                                .passwordHash(dbUser.getPassword() != null ? dbUser.getPassword() : "")
                                .active(true)
                                .registeredAt(java.time.OffsetDateTime.now())
                                .updatedAt(java.time.OffsetDateTime.now())
                                .build()));

                // Seed Orders (12 orders) if count is 0
                if (orderRepository.countByCustomerUserId(userId) == 0) {
                    final com.phannhubao.entity.OrderStatus defaultStatus = orderStatusRepository.findAll().stream().findFirst()
                            .orElseGet(() -> orderStatusRepository.save(com.phannhubao.entity.OrderStatus.builder()
                                    .statusName("Delivered")
                                    .color("#4CAF50")
                                    .createdAt(java.time.OffsetDateTime.now())
                                    .updatedAt(java.time.OffsetDateTime.now())
                                    .build()));

                    for (int i = 1; i <= 12; i++) {
                        final int daysAgo = i;
                        orderRepository.save(com.phannhubao.entity.Order.builder()
                                .customer(customer)
                                .createdAt(java.time.OffsetDateTime.now().minusDays(daysAgo))
                                .orderStatus(defaultStatus)
                                .build());
                    }
                    System.out.println(">>> Seeded 12 sample orders for user: " + userId + " <<<");
                }

                // Seed Addresses (3 addresses) if count is 0
                if (addressRepository.countByCustomerUserId(userId) == 0) {
                    addressRepository.save(com.phannhubao.entity.CustomerAddress.builder()
                            .customer(customer)
                            .addressLine1("3 Newbridge Court")
                            .city("Chino Hills")
                            .country("United States")
                            .postalCode("91709")
                            .phoneNumber("123456789")
                            .dialCode("+1")
                            .build());
                    addressRepository.save(com.phannhubao.entity.CustomerAddress.builder()
                            .customer(customer)
                            .addressLine1("562 Duong 3 Thang 2")
                            .city("Ho Chi Minh City")
                            .country("Vietnam")
                            .postalCode("70000")
                            .phoneNumber("987654321")
                            .dialCode("+84")
                            .build());
                    addressRepository.save(com.phannhubao.entity.CustomerAddress.builder()
                            .customer(customer)
                            .addressLine1("123 Phao Dai Lang")
                            .city("Hanoi")
                            .country("Vietnam")
                            .postalCode("10000")
                            .phoneNumber("555123456")
                            .dialCode("+84")
                            .build());
                    System.out.println(">>> Seeded 3 sample addresses for user: " + userId + " <<<");
                }

                // Seed Card (1 card: Visa **34) if count is 0
                if (cardRepository.countByCustomerUserId(userId) == 0) {
                    cardRepository.save(com.phannhubao.entity.Card.builder()
                            .customer(customer)
                            .cardType("Visa")
                            .lastFour("34")
                            .build());
                    System.out.println(">>> Seeded Visa card for user: " + userId + " <<<");
                }

                // Seed Coupons (5 promocodes) if count is 0
                if (couponRepository.count() == 0) {
                    for (int i = 1; i <= 5; i++) {
                        couponRepository.save(com.phannhubao.entity.Coupon.builder()
                                .code("PROMO" + i)
                                .discountType("PERCENTAGE")
                                .discountValue(10.0 + i)
                                .timesUsed(0.0)
                                .maxUsage(1.0)
                                .build());
                    }
                    System.out.println(">>> Seeded 5 promocodes <<<");
                }

                // Seed Reviews (4 reviews) if count is 0
                if (reviewRepository.countByUserId(userId) == 0) {
                    final java.util.List<com.phannhubao.entity.Product> products = productRepository.findAll();
                    if (!products.isEmpty()) {
                        for (int i = 1; i <= 4; i++) {
                            final com.phannhubao.entity.Product product = (products.size() >= i) ? products.get(i - 1) : products.get(0);
                            reviewRepository.save(com.phannhubao.entity.Review.builder()
                                    .user(dbUser)
                                    .product(product)
                                    .content("Sản phẩm rất tốt, giao hàng nhanh " + i)
                                    .rating(5)
                                    .createdAt(java.time.OffsetDateTime.now())
                                    .build());
                        }
                        System.out.println(">>> Seeded 4 product reviews for user: " + userId + " <<<");
                    }
                }
            }
        };
    }
}
