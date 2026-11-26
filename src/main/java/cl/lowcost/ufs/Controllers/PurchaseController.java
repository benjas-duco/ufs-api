package cl.lowcost.ufs.Controllers;

import cl.lowcost.ufs.DTOs.PurchaseResponse;
import cl.lowcost.ufs.DTOs.ShippingRequest;
import cl.lowcost.ufs.Services.PurchaseService;
import cl.lowcost.ufs.Services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;
    private final UserService userService;

    // Compra todo el carrito. El body trae los datos de envío.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PurchaseResponse checkout(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody ShippingRequest req) {
        return purchaseService.checkout(userService.currentUser(jwt), req);
    }

    @GetMapping
    public List<PurchaseResponse> mine(@AuthenticationPrincipal Jwt jwt) {
        return purchaseService.listMine(userService.currentUser(jwt));
    }

    @GetMapping("/{id}")
    public PurchaseResponse getMine(@AuthenticationPrincipal Jwt jwt, @PathVariable Integer id) {
        return purchaseService.getMine(userService.currentUser(jwt), id);
    }
}
