package ma.projet.resource;
import ma.projet.entity.Category;
import ma.projet.repository.CategoryRepository;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
@Path("/categories") // Le chemin principal de cette ressource
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CategoryResource {

    private final CategoryRepository repo;

    // Injection du Repository Spring Data
    public CategoryResource(CategoryRepository repo) {
        this.repo = repo;
    }

    // GET /categories
    @GET
    public Response list(@QueryParam("page") @DefaultValue("0") int page,
                         @QueryParam("size") @DefaultValue("20") int size) {

        Page<Category> p = repo.findAll(PageRequest.of(page, size));

        // Jersey retourne la Page Spring Data sous forme de JSON
        return Response.ok(p).build();
    }

    // GET /categories/{id}
    @GET
    @Path("{id}")
    public Response get(@PathParam("id") Long id) {
        return repo.findById(id)
                .map(Response::ok)
                .orElse(Response.status(Response.Status.NOT_FOUND))
                .build();
    }

    // POST /categories
    @POST
    public Response create(Category c, @Context UriInfo uriInfo) {
        Category saved = repo.save(c);

        // Construit l'URI de la nouvelle ressource (REST HATEOAS)
        URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(saved.getId())).build();

        return Response.created(uri).entity(saved).build();
    }

    // PUT /categories/{id}
    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") Long id, Category c) {
        return repo.findById(id).map(existing -> {
            existing.setName(c.getName());
            existing.setCode(c.getCode());
            repo.save(existing);
            return Response.ok(existing);
        }).orElse(Response.status(Response.Status.NOT_FOUND)).build();
    }

    // DELETE /categories/{id}
    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") Long id) {
        if(!repo.existsById(id)) return Response.status(Response.Status.NOT_FOUND).build();
        repo.deleteById(id);
        return Response.noContent().build();
    }
}