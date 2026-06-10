package com.phannhubao.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDataResponse {
    private String orderNo;
    private String date;
    private String trackingNumber;
    private int quantity;
    private int totalAmount;
    private String status;
    private String shippingAddress;
    private String paymentMethodCardNumber;
    private String paymentMethodType;
    private String deliveryMethod;
    private String discount;
    private List<OrderProductItemResponse> items;
}
