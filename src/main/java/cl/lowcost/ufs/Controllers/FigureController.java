package cl.lowcost.ufs.Controllers;

import cl.lowcost.ufs.DTOs.FigureRequest;
import cl.lowcost.ufs.DTOs.FigureResponse;
import cl.lowcost.ufs.Models.Brands;
import cl.lowcost.ufs.Models.States;
import cl.lowcost.ufs.Services.FigureService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/figures")
@RequiredArgsConstructor
public class FigureController {

    private final FigureService figureService;

    // Público. Filtros opcionales: ?brand=FIGMA&state=PRE_SALE&q=miku
    @GetMapping
    public List<FigureResponse> list(@RequestParam(required = false) Brands brand,
                                     @RequestParam(required = false) States state,
                                     @RequestParam(required = false) String q) {
        return figureService.list(brand, state, q);
    }

    // Público
    @GetMapping("/{id}")
    public FigureResponse get(@PathVariable Integer id) {
        return figureService.get(id);
    }

    // Solo admin
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FigureResponse create(@Valid @RequestBody FigureRequest req) {
        return figureService.create(req);
    }

    // Solo admin
    @PutMapping("/{id}")
    public FigureResponse update(@PathVariable Integer id, @Valid @RequestBody FigureRequest req) {
        return figureService.update(id, req);
    }
}
