package ma.projet.jersey;


import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;
import ma.projet.resource.CategoryResource;
import ma.projet.resource.ItemResource;

import jakarta.ws.rs.ApplicationPath;

@Component

@ApplicationPath("/")
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {
        register(CategoryResource.class);
        register(ItemResource.class);
    }
}