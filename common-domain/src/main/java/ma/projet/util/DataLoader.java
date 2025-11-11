package ma.projet.util;

import ma.projet.entity.Category;
import ma.projet.entity.Item;
import ma.projet.repository.CategoryRepository;
import ma.projet.repository.ItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Random;

@Component
public class DataLoader implements CommandLineRunner {

    private final CategoryRepository categoryRepo;
    private final ItemRepository itemRepo;

    public DataLoader(CategoryRepository categoryRepo, ItemRepository itemRepo) {
        this.categoryRepo = categoryRepo;
        this.itemRepo = itemRepo;
    }

    @Override
    public void run(String... args) {
        if (categoryRepo.count() > 0 || itemRepo.count() > 0) {
            System.out.println(" Données déjà existantes, aucun chargement.");
            return;
        }

        System.out.println(" Insertion de 2000 catégories et 100000 items...");

        for (int i = 1; i <= 2000; i++) {
            Category cat = new Category();
            cat.setCode("CAT" + i);
            cat.setName("Catégorie " + i);
            categoryRepo.save(cat);
        }

        Random rand = new Random();
        for (int i = 1; i <= 100_000; i++) {
            Item item = new Item();
            item.setSku("SKU" + i);
            item.setName("Produit " + i);
            item.setPrice(BigDecimal.valueOf(rand.nextDouble(10, 500)));
            item.setStock(rand.nextInt(1000));
            long randomCategoryId = rand.nextInt(2000) + 1;
            Category cat = categoryRepo.findById(randomCategoryId).orElse(null);
            if (cat != null) item.setCategory(cat);
            itemRepo.save(item);

            if (i % 5000 == 0)
                System.out.println("→ " + i + " items insérés...");
        }

        System.out.println("Chargement terminé !");
    }
}
