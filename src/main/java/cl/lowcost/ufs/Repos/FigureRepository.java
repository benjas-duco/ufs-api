package cl.lowcost.ufs.Repos;

import cl.lowcost.ufs.Models.Figure;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface FigureRepository extends JpaRepository<Figure, Integer>, JpaSpecificationExecutor<Figure> {

    // Bloquea las filas mientras dura la compra para no vender más de lo que hay en stock
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select f from Figure f where f.id in :ids order by f.id")
    List<Figure> findAllByIdForUpdate(@Param("ids") Collection<Integer> ids);
}
