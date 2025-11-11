package ma.projet.dto;

import ma.projet.entity.Item;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "inlineCategory", types = { Item.class })
public interface ItemProjection {
    Long getId();
    String getSku();
    String getName();
    java.math.BigDecimal getPrice();
    Integer getStock();
    Object getCategory(); // will include link/embedded depending on REST config
}

