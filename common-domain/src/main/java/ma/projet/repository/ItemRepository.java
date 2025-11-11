package ma.projet.repository;
import ma.projet.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List; // Assurez-vous d'avoir cet import pour List<Item>

public interface ItemRepository extends JpaRepository<Item, Long> {

    Page<Item> findByCategoryId(Long categoryId, Pageable pageable);


    @Query("select i from Item i join fetch i.category c where c.id = :cid")
    List<Item> findByCategoryIdJoinFetch(@Param("cid") Long cid);

    @Query(value = "SELECT i FROM Item i JOIN FETCH i.category",
            countQuery = "SELECT count(i) FROM Item i")
    Page<Item> findAllWithCategory(Pageable pageable);
}
