package cl.lowcost.ufs.Repos;

import cl.lowcost.ufs.Models.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PurchaseRepository extends JpaRepository<Purchase, Integer> {

    List<Purchase> findByUserIdOrderByDateDesc(UUID userId);

    Optional<Purchase> findByIdAndUserId(Integer id, UUID userId);

    List<Purchase> findAllByOrderByDateDesc();
}
