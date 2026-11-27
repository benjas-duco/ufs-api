package cl.lowcost.ufs.Repos;

import cl.lowcost.ufs.Models.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Integer> {

    Optional<Tag> findByName(String name);

    List<Tag> findAllByOrderByName();
}
