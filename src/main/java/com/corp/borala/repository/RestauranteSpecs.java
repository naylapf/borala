package com.corp.borala.repository;

import com.corp.borala.form.RestauranteFiltro;
import com.corp.borala.model.Restaurante;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/** Monta a consulta da busca de restaurantes só com os filtros que foram preenchidos. */
public final class RestauranteSpecs {

    private RestauranteSpecs() {
    }

    public static Specification<Restaurante> comFiltro(RestauranteFiltro filtro) {
        return (root, query, cb) -> {
            List<Predicate> condicoes = new ArrayList<>();

            if (temTexto(filtro.getNome())) {
                condicoes.add(contem(cb, root.get("nome"), filtro.getNome()));
            }
            if (temTexto(filtro.getCidade())) {
                condicoes.add(contem(cb, root.get("endereco").get("cidade"), filtro.getCidade()));
            }
            if (temTexto(filtro.getBairro())) {
                condicoes.add(contem(cb, root.get("endereco").get("bairro"), filtro.getBairro()));
            }
            if (filtro.getTipoCulinariaId() != null) {
                condicoes.add(cb.equal(root.join("tiposCulinaria").get("id"), filtro.getTipoCulinariaId()));
                if (query != null) {
                    query.distinct(true);
                }
            }
            // a faixa do restaurante cruza o intervalo pedido: máximo >= "de" e mínimo <= "até"
            // (restaurante sem faixa, isto é, sem pratos, não aparece quando há filtro de preço)
            if (filtro.getPrecoDe() != null) {
                condicoes.add(cb.greaterThanOrEqualTo(
                        root.get("faixaPreco").<java.math.BigDecimal>get("maximo"), filtro.getPrecoDe()));
            }
            if (filtro.getPrecoAte() != null) {
                condicoes.add(cb.lessThanOrEqualTo(
                        root.get("faixaPreco").<java.math.BigDecimal>get("minimo"), filtro.getPrecoAte()));
            }
            return cb.and(condicoes.toArray(new Predicate[0]));
        };
    }

    private static boolean temTexto(String texto) {
        return texto != null && !texto.isBlank();
    }

    private static Predicate contem(jakarta.persistence.criteria.CriteriaBuilder cb,
                                    jakarta.persistence.criteria.Expression<String> campo, String texto) {
        String termo = texto.trim().toLowerCase()
                .replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
        return cb.like(cb.lower(campo), "%" + termo + "%", '\\');
    }
}
