package ma.projet.repository;

import ma.projet.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepositoryRest extends JpaRepository<Category, Long> {
}
