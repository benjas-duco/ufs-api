package cl.lowcost.ufs.Services;

import cl.lowcost.ufs.DTOs.FigureRequest;
import cl.lowcost.ufs.DTOs.FigureResponse;
import cl.lowcost.ufs.Models.Brands;
import cl.lowcost.ufs.Models.Figure;
import cl.lowcost.ufs.Models.States;
import cl.lowcost.ufs.Models.Tag;
import cl.lowcost.ufs.Repos.CartItemRepository;
import cl.lowcost.ufs.Repos.FigureRepository;
import cl.lowcost.ufs.Repos.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class FigureService {

    private final FigureRepository figures;
    private final TagRepository tagRepo;
    private final CartItemRepository cart;

    // Las figuras DELETED solo las ve un admin (con ?state=DELETED); en el listado normal no aparecen
    @Transactional(readOnly = true)
    public List<FigureResponse> list(Brands brand, States state, String q, boolean admin) {
        if (state == States.DELETED && !admin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo un admin puede ver figuras eliminadas");
        }
        Specification<Figure> spec = (root, query, cb) -> cb.conjunction();
        if (brand != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("brand"), brand));
        }
        if (state != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("state"), state));
        } else {
            spec = spec.and((root, query, cb) -> cb.notEqual(root.get("state"), States.DELETED));
        }
        if (q != null && !q.isBlank()) {
            String pattern = "%" + q.trim().toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("name")), pattern));
        }
        return figures.findAll(spec, Sort.by(Sort.Direction.DESC, "id"))
                .stream().map(FigureResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public FigureResponse get(Integer id, boolean admin) {
        Figure figure = find(id);
        if (figure.getState() == States.DELETED && !admin) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Figura no encontrada");
        }
        return FigureResponse.from(figure);
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
        if (figure.getState() == States.DELETED) {
            cart.deleteByFigureId(id);
        }
        return FigureResponse.from(figure);
    }

    // "Quitar" una figura = pasarla a DELETED (las compras antiguas siguen apuntando a ella)
    @Transactional
    public void delete(Integer id) {
        Figure figure = find(id);
        figure.setState(States.DELETED);
        cart.deleteByFigureId(id);
    }

    @Transactional(readOnly = true)
    public List<String> listTags() {
        return tagRepo.findAllByOrderByName().stream().map(Tag::getName).toList();
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

        // Cada tag que no exista todavía se crea automáticamente
        f.getTags().clear();
        if (req.tags() != null) {
            req.tags().stream()
                    .map(t -> t == null ? "" : t.trim().toLowerCase(Locale.ROOT))
                    .filter(t -> !t.isEmpty())
                    .distinct()
                    .forEach(name -> f.getTags().add(
                            tagRepo.findByName(name).orElseGet(() -> tagRepo.save(new Tag(name)))));
        }
    }
}
