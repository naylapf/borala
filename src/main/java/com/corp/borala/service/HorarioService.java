package com.corp.borala.service;

import com.corp.borala.exception.CampoInvalidoException;
import com.corp.borala.form.HorarioForm;
import com.corp.borala.model.Horario;
import com.corp.borala.model.Restaurante;
import com.corp.borala.repository.HorarioRepository;
import com.corp.borala.repository.RestauranteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;

/** CRUD dos horários de funcionamento de um restaurante. */
@Service
public class HorarioService {

    private final HorarioRepository horarioRepository;
    private final RestauranteRepository restauranteRepository;

    public HorarioService(HorarioRepository horarioRepository, RestauranteRepository restauranteRepository) {
        this.horarioRepository = horarioRepository;
        this.restauranteRepository = restauranteRepository;
    }

    /** Ordena de segunda a domingo e, no mesmo dia, pela hora de abertura. */
    @Transactional(readOnly = true)
    public List<Horario> listar(Long restauranteId) {
        return horarioRepository.findByRestauranteId(restauranteId).stream()
                .sorted(Comparator.comparing(Horario::getDiaSemana).thenComparing(Horario::getAbertura))
                .toList();
    }

    @Transactional(readOnly = true)
    public HorarioForm montarFormulario(Long restauranteId, Long id) {
        Horario horario = buscar(restauranteId, id);
        HorarioForm form = new HorarioForm();
        form.setId(horario.getId());
        form.setDiaSemana(horario.getDiaSemana());
        form.setAbertura(horario.getAbertura());
        form.setFechamento(horario.getFechamento());
        return form;
    }

    @Transactional
    public void salvar(Long restauranteId, HorarioForm form) {
        if (form.getAbertura().equals(form.getFechamento())) {
            throw new CampoInvalidoException("fechamento", "O fechamento deve ser diferente da abertura");
        }
        validarHorarioUnico(restauranteId, form);

        if (form.getId() == null) {
            Restaurante restaurante = restauranteRepository.findById(restauranteId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurante não encontrado"));
            horarioRepository.save(new Horario(restaurante, form.getDiaSemana(), form.getAbertura(), form.getFechamento()));
        } else {
            buscar(restauranteId, form.getId()).atualizar(form.getDiaSemana(), form.getAbertura(), form.getFechamento());
        }
    }

    @Transactional
    public void excluir(Long restauranteId, Long id) {
        horarioRepository.delete(buscar(restauranteId, id));
    }

    private Horario buscar(Long restauranteId, Long id) {
        return horarioRepository.findByIdAndRestauranteId(id, restauranteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Horário não encontrado"));
    }

    /** Não pode haver dois horários do mesmo dia com a mesma hora de abertura. */
    private void validarHorarioUnico(Long restauranteId, HorarioForm form) {
        horarioRepository.findByRestauranteIdAndDiaSemanaAndAbertura(restauranteId, form.getDiaSemana(), form.getAbertura())
                .filter(existente -> !existente.getId().equals(form.getId()))
                .ifPresent(existente -> {
                    throw new CampoInvalidoException("abertura", "Já existe um horário neste dia com esta abertura");
                });
    }
}
