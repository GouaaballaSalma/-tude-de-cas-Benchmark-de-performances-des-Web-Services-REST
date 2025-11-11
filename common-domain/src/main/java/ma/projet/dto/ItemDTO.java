package ma.projet.dto;


import java.math.BigDecimal;

public record ItemDTO(
        Long id,
        String sku,
        String name,
        BigDecimal price,
        Integer stock,
        String categoryCode
) {}
