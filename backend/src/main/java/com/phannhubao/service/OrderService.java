package com.phannhubao.service;

import com.phannhubao.dto.response.OrderItemDataResponse;
import com.phannhubao.dto.response.OrderProductItemResponse;
import com.phannhubao.entity.*;
import com.phannhubao.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerAddressRepository addressRepository;
    private final CardRepository cardRepository;

    public List<OrderItemDataResponse> getUserOrders(Long userId) {
        List<Order> orders = orderRepository.findByCustomerUserId(userId);

        // Fetch user default address
        List<CustomerAddress> addresses = addressRepository.findByCustomerUserId(userId);
        String shippingAddress = addresses.isEmpty() 
            ? "No shipping address found" 
            : addresses.get(0).getAddressLine1() + ", " + addresses.get(0).getCity() + ", " + addresses.get(0).getCountry();

        // Fetch user default card
        List<Card> cards = cardRepository.findByCustomerUserId(userId);
        String paymentMethodType = cards.isEmpty() ? "Cash" : cards.get(0).getCardType();
        String paymentMethodCardNumber = cards.isEmpty() ? "N/A" : "**** **** **** " + cards.get(0).getLastFour();

        return orders.stream().map(order -> {
            List<OrderItem> items = order.getItems();
            
            int quantity = items.stream().mapToInt(OrderItem::getQuantity).sum();
            int totalAmount = items.stream().mapToInt(item -> (int)(item.getPrice() * item.getQuantity())).sum();

            String statusName = order.getOrderStatus() != null 
                ? order.getOrderStatus().getStatusName() 
                : "Processing";

            String discount = "None";
            if (order.getCoupon() != null) {
                Coupon c = order.getCoupon();
                discount = c.getDiscountValue() + ("PERCENTAGE".equals(c.getDiscountType()) ? "%" : "$") + " off";
            }

            List<OrderProductItemResponse> itemResponses = items.stream().map(item -> {
                Product product = item.getProduct();
                
                return OrderProductItemResponse.builder()
                        .title(product != null ? product.getProductName() : "Unknown Product")
                        .brand(product != null ? product.getBrandName() : "Unknown Brand")
                        .color(item.getSelectedColor() != null ? item.getSelectedColor() : "Default")
                        .size(item.getSelectedSize() != null ? item.getSelectedSize() : "Default")
                        .units(item.getQuantity())
                        .price(item.getPrice().intValue())
                        .imageUrl(product != null ? product.getImageUrl() : "")
                        .productId(product != null ? product.getId().toString() : "")
                        .build();
            }).collect(Collectors.toList());

            String dateFormatted = order.getCreatedAt() != null 
                ? order.getCreatedAt().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) 
                : "Unknown Date";

            return OrderItemDataResponse.builder()
                    .orderNo("Order №" + order.getId().substring(0, 8).toUpperCase())
                    .date(dateFormatted)
                    .trackingNumber("TRK" + order.getId().substring(0, 8).toUpperCase())
                    .quantity(quantity)
                    .totalAmount(totalAmount)
                    .status(statusName)
                    .shippingAddress(shippingAddress)
                    .paymentMethodType(paymentMethodType)
                    .paymentMethodCardNumber(paymentMethodCardNumber)
                    .deliveryMethod("Standard Delivery")
                    .discount(discount)
                    .items(itemResponses)
                    .build();
        }).collect(Collectors.toList());
    }
}
