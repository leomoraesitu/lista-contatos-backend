package br.com.fatec.listacontatosbackend.services;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;

import br.com.fatec.listacontatosbackend.entities.Contato;
import br.com.fatec.listacontatosbackend.repositories.ContatoRepository;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class ContatoServiceTests {

    @Mock
    private ContatoRepository repository;

    private ContatoService service;

    @BeforeEach
    void setUp() {
        service = new ContatoService(repository);
    }

    @Test
    void propagatesOriginalExceptionWhenUpdateSaveFails() {
        Contato stored = new Contato();
        Contato input = new Contato();
        var failure = new DataAccessResourceFailureException(
                "Falha interna simulada");

        when(repository.getReferenceById(1)).thenReturn(stored);
        when(repository.save(stored)).thenThrow(failure);

        assertThatThrownBy(() -> service.update(1, input))
                .isSameAs(failure);

        verify(repository).save(stored);
    }

    @Test
    void throwsEntityNotFoundWhenUpdateContactDoesNotExist() {
        when(repository.getReferenceById(1))
                .thenThrow(new EntityNotFoundException());

        assertThatThrownBy(() -> service.update(1, new Contato()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Contato não cadastrado");

        verify(repository, never()).save(any(Contato.class));
    }
}