package cl.lowcost.ufs.Services;

import cl.lowcost.ufs.DTOs.NicknameRequest;
import cl.lowcost.ufs.DTOs.UserResponse;
import cl.lowcost.ufs.Models.Roles;
import cl.lowcost.ufs.Models.User;
import cl.lowcost.ufs.Repos.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository users;

    // Devuelve al usuario del token; si es su primera vez, crea su fila en users.
    @Transactional
    public User currentUser(Jwt jwt) {
        UUID id = UUID.fromString(jwt.getSubject());
        return users.findById(id).orElseGet(() -> {
            String email = jwt.getClaimAsString("email");
            String nickname = (email != null && email.contains("@"))
                    ? email.substring(0, email.indexOf('@'))
                    : "usuario";
            User user = new User();
            user.setId(id);
            user.setNickname(nickname);
            user.setRole(Roles.USER);
            return users.save(user);
        });
    }

    @Transactional
    public UserResponse updateNickname(Jwt jwt, NicknameRequest req) {
        User user = currentUser(jwt);
        user.setNickname(req.nickname().trim());
        return UserResponse.from(users.save(user));
    }
}
