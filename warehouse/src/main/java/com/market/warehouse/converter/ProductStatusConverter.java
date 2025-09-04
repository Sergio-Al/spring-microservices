package com.market.warehouse.converter;

import com.market.warehouse.entity.Product.ProductStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class ProductStatusConverter implements AttributeConverter<ProductStatus, String> {

    @Override
    public String convertToDatabaseColumn(ProductStatus attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getValue();
    }

    @Override
    public ProductStatus convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return null;
        }
        return ProductStatus.fromValue(dbData);
    }
}
