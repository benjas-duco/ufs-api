package cl.lowcost.ufs.Repos;

import cl.lowcost.ufs.Models.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {

    List<CartItem> findByUserIdOrderById(UUID userId);

    Optional<CartItem> findByUserIdAndFigureId(UUID userId, Integer figureId);

    @Modifying(flushAutomatically = true)
    @Query("delete from CartItem c where c.user.id = :userId")
    void deleteByUserId(@Param("userId") UUID userId);

    @Modifying(flushAutomatically = true)
    @Query("delete from CartItem c where c.figure.id = :figureId")
    void deleteByFigureId(@Param("figureId") Integer figureId);
}
