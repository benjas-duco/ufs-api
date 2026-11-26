package cl.lowcost.ufs.Repos;

import cl.lowcost.ufs.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
}
