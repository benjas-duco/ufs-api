package cl.lowcost.ufs.Services;

import cl.lowcost.ufs.DTOs.FigureRequest;
import cl.lowcost.ufs.DTOs.FigureResponse;
import cl.lowcost.ufs.Models.Brands;
import cl.lowcost.ufs.Models.Figure;
import cl.lowcost.ufs.Models.States;
import cl.lowcost.ufs.Repos.FigureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FigureService {

    private final FigureRepository figures;

    @Transactional(readOnly = true)
    public List<FigureResponse> list(Brands brand, States state, String q) {
        Specification<Figure> spec = (root, query, cb) -> cb.conjunction();
        if (brand != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("brand"), brand));
        }
        if (state != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("state"), state));
        }
        if (q != null && !q.isBlank()) {
            String pattern = "%" + q.trim().toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("name")), pattern));
        }
        return figures.findAll(spec, Sort.by(Sort.Direction.DESC, "id"))
                .stream().map(FigureResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public FigureResponse get(Integer id) {
        return FigureResponse.from(find(id));
    }

    @Transactional
    public FigureResponse create(FigureRequest req) {
        Figure figure = new Figure();
        apply(figure, req);
        return FigureResponse.from(figures.save(figure));
    }

    @Transactional
    public FigureResponse update(Integer id, FigureRequest req) {
        Figure figure = find(id);
        apply(figure, req);
        return FigureResponse.from(figure);
    }

    private Figure find(Integer id) {
        return figures.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Figura no encontrada"));
    }

    private void apply(Figure f, FigureRequest req) {
        f.setName(req.name().trim());
        f.setBrand(req.brand());
        f.setPrice(req.price());
        f.setState(req.state());
        f.setUnits(req.units());
        f.setImageUrl(req.imageUrl());
        f.setDescription(req.description());
        f.getTags().clear();
        if (req.tags() != null) {
            f.getTags().addAll(req.tags().stream()
                    .map(String::trim)
                    .filter(t -> !t.isEmpty())
                    .toList());
        }
    }
}
