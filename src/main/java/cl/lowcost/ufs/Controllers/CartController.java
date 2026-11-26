package cl.lowcost.ufs.Controllers;

import cl.lowcost.ufs.DTOs.AddCartItemRequest;
import cl.lowcost.ufs.DTOs.CartResponse;
import cl.lowcost.ufs.DTOs.UpdateCartItemRequest;
import cl.lowcost.ufs.Services.CartService;
import cl.lowcost.ufs.Services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserService userService;

    @GetMapping
    public CartResponse get(@AuthenticationPrincipal Jwt jwt) {
        return cartService.get(userService.currentUser(jwt));
    }

    @PostMapping("/items")
    public CartResponse add(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody AddCartItemRequest req) {
        return cartService.addItem(userService.currentUser(jwt), req.figureId(), req.units());
    }

    @PutMapping("/items/{figureId}")
    public CartResponse setUnits(@AuthenticationPrincipal Jwt jwt, @PathVariable Integer figureId,
                                 @Valid @RequestBody UpdateCartItemRequest req) {
        return cartService.setUnits(userService.currentUser(jwt), figureId, req.units());
    }

    @DeleteMapping("/items/{figureId}")
    public CartResponse remove(@AuthenticationPrincipal Jwt jwt, @PathVariable Integer figureId) {
        return cartService.removeItem(userService.currentUser(jwt), figureId);
    }

    @DeleteMapping
    public CartResponse clear(@AuthenticationPrincipal Jwt jwt) {
        return cartService.clear(userService.currentUser(jwt));
    }
}
