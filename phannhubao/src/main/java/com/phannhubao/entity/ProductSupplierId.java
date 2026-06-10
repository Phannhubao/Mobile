package com.phannhubao.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductSupplierId implements Serializable {
    private java.util.UUID productId;
    private java.util.UUID supplierId;
}
