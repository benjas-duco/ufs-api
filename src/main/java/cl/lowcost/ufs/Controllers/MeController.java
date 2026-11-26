package cl.lowcost.ufs.Controllers;

import cl.lowcost.ufs.DTOs.NicknameRequest;
import cl.lowcost.ufs.DTOs.UserResponse;
import cl.lowcost.ufs.Services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class MeController {

    private final UserService userService;

    @GetMapping
    public UserResponse me(@AuthenticationPrincipal Jwt jwt) {
        return UserResponse.from(userService.currentUser(jwt));
    }

    @PutMapping
    public UserResponse updateNickname(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody NicknameRequest req) {
        return userService.updateNickname(jwt, req);
    }
}
