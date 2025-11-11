package ma.projet.controller;
import ma.projet.entity.Item;
import ma.projet.entity.Category;
import ma.projet.repository.ItemRepository;
import ma.projet.repository.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import ma.projet.dto.ItemDTO;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemRepository itemRepo;
    private final CategoryRepository categoryRepo;

    public ItemController(ItemRepository itemRepo, CategoryRepository categoryRepo) {
        this.itemRepo = itemRepo;
        this.categoryRepo = categoryRepo;
    }

    // Méthode utilitaire de mapping (pour éviter la répétition)
    private ItemDTO mapToDto(Item item) {
        // Le DTO utilise uniquement les champs simples et le code de la catégorie
        return new ItemDTO(
                item.getId(),
                item.getSku(),
                item.getName(),
                item.getPrice(),
                item.getStock(),
                item.getCategory().getCode() // Accès sécurisé au Code
        );
    }

    // GET /items?categoryId=... ou GET /items (Listing et Filtrage)
    @GetMapping
    public Page<ItemDTO> list(@RequestParam(required = false) Long categoryId, Pageable pageable) {
        Page<Item> itemPage;

        if (categoryId != null) {
            // Mode Anti-N+1 : Utilise JOIN FETCH pour le filtrage
            List<Item> items = itemRepo.findByCategoryIdJoinFetch(categoryId);
            itemPage = new PageImpl<>(items, pageable, items.size());

        } else {
            // Mode Anti-N+1 : Utilise findAllWithCategory pour le listing global (50% de la charge)
            itemPage = itemRepo.findAllWithCategory(pageable);
        }

        // CORRECTION FINALE: Mappage de Page<Item> vers Page<ItemDTO> pour éviter l'erreur de proxy
        return itemPage.map(this::mapToDto);
    }

    // GET /items/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ItemDTO> get(@PathVariable Long id) {
        // Mappage vers le DTO même pour l'ID unique
        return itemRepo.findById(id)
                .map(this::mapToDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST
    @PostMapping
    public ResponseEntity<ItemDTO> create(@RequestBody @Valid Item item) {
        // need category association: client can send category.id only
        if (item.getCategory() == null || item.getCategory().getId() == null)
            return ResponseEntity.badRequest().build();
        Category cat = categoryRepo.findById(item.getCategory().getId()).orElse(null);
        if (cat == null) return ResponseEntity.badRequest().build();
        item.setCategory(cat);
        Item saved = itemRepo.save(item);

        // Retourne le DTO de l'Item créé
        return ResponseEntity.ok(mapToDto(saved));
    }

    // PUT
    @PutMapping("/{id}")
    public ResponseEntity<ItemDTO> update(@PathVariable Long id, @RequestBody @Valid Item item) {
        return itemRepo.findById(id).map(existing -> {
            existing.setName(item.getName());
            existing.setPrice(item.getPrice());
            existing.setStock(item.getStock());
            if (item.getCategory() != null && item.getCategory().getId() != null) {
                categoryRepo.findById(item.getCategory().getId()).ifPresent(existing::setCategory);
            }
            itemRepo.save(existing);
            // Retourne le DTO de l'Item mis à jour
            return ResponseEntity.ok(mapToDto(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!itemRepo.existsById(id)) return ResponseEntity.notFound().build();
        itemRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Endpoint relationnel (pour le mix du TP, si utilisé)
    @GetMapping(params = "categoryId", path="/by-category")
    public Page<ItemDTO> itemsByCategory(@RequestParam Long categoryId, Pageable pageable) {
        // Mode Anti-N+1
        List<Item> items = itemRepo.findByCategoryIdJoinFetch(categoryId);
        Page<Item> itemPage = new PageImpl<>(items, pageable, items.size());

        // Mappage vers le DTO
        return itemPage.map(this::mapToDto);
    }
}

