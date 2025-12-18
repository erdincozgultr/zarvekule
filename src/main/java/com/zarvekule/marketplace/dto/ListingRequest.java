package com.zarvekule.marketplace.dto;

import com.zarvekule.marketplace.enums.ProductCategory;
import com.zarvekule.marketplace.enums.ProductCondition;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ListingRequest {
    @NotBlank(message = "Başlık zorunludur.")
    private String title;

    @NotBlank(message = "Açıklama zorunludur.")
    private String description;

    @NotNull(message = "Fiyat zorunludur.")
    @DecimalMin(value = "0.0", message = "Fiyat negatif olamaz.")
    private BigDecimal price;

    @NotNull(message = "Kategori seçilmelidir.")
    private ProductCategory category;

    @NotNull(message = "Ürün durumu seçilmelidir.")
    private ProductCondition condition;

    @NotBlank(message = "İletişim bilgisi (Telefon, Email, Discord vb.) zorunludur.")
    private String contactInfo;

    private String imageUrl;
}