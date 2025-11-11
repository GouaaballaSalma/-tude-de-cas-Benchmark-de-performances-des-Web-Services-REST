package ma.projet.resource;

import ma.projet.entity.Item;
import ma.projet.repository.ItemRepository;
import ma.projet.repository.CategoryRepository;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;


import ma.projet.dto.ItemDTO;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import java.util.List;
import java.util.stream.Collectors;


import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;


@Component
@Path("/items")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ItemResource {

    private final ItemRepository itemRepo;
    private final CategoryRepository categoryRepo;

    public ItemResource(ItemRepository itemRepo, CategoryRepository categoryRepo){
        this.itemRepo = itemRepo; this.categoryRepo = categoryRepo;
    }

    // Méthode utilitaire de mapping
    private ItemDTO mapToDto(Item item) {
        return new ItemDTO(
                item.getId(),
                item.getSku(),
                item.getName(),
                item.getPrice(),
                item.getStock(),
                item.getCategory().getCode()
        );
    }

    @GET
    public Response list(@QueryParam("categoryId") Long categoryId,
                         @QueryParam("page") @DefaultValue("0") int page,
                         @QueryParam("size") @DefaultValue("20") int size) {

        List<Item> items;
        PageRequest pageable = PageRequest.of(page, size);

        if (categoryId != null) {
            // Mode Anti-N+1 pour le filtre ciblé
            items = itemRepo.findByCategoryIdJoinFetch(categoryId);
        } else {
            // Mode Anti-N+1 pour le listing global
            // Note : Puisque nous ne retournons pas d'objet Page<T>, nous appelons la méthode qui utilise JOIN FETCH
            items = itemRepo.findAllWithCategory(pageable).getContent(); // Appel à findAllWithCategory du Repository
        }

        // CORRECTION FINALE: Mappage direct vers List<ItemDTO>
        List<ItemDTO> dtos = items.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        // Retourne la LISTE des DTOs pour une stabilité maximale
        return Response.ok(dtos).build();
    }


    @GET
    @Path("{id}")
    public Response get(@PathParam("id") Long id) {
        return itemRepo.findById(id)
                .map(this::mapToDto)
                .map(Response::ok)
                .orElse(Response.status(Response.Status.NOT_FOUND))
                .build();
    }
}

