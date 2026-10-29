package br.com.fatec.listacontatosbackend.resources;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import br.com.fatec.listacontatosbackend.entities.Contato;
import br.com.fatec.listacontatosbackend.repositories.ContatoRepository;
import br.com.fatec.listacontatosbackend.resources.exceptions.RessourceExceptionHandler;
import br.com.fatec.listacontatosbackend.services.ContatoService;

@ExtendWith(MockitoExtension.class)
class ContatoPersistenceErrorTests {

    private static final String INTERNAL_MESSAGE = "SQL_SIMULADO: falha ao gravar contatos";

    private static final String VALID_PAYLOAD = """
            {
                "name": "Contato de teste",
                "email": "teste@example.com",
                "telefone": "11999990000",
                "endereco": "Rua de teste, 10",
                "cidade": "Campinas",
                "cep": "13000000",
                "estado": "SP"
            }
            """;

    @Mock
    private ContatoRepository repository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        ContatoService service = new ContatoService(repository);

        mockMvc = MockMvcBuilders
                .standaloneSetup(new ContatoController(service))
                .setControllerAdvice(new RessourceExceptionHandler())
                .build();
    }

    @Test
    void returnsSafeInternalServerErrorWhenUpdateSaveFails()
            throws Exception {
        when(repository.getReferenceById(1)).thenReturn(new Contato());
        when(repository.save(any(Contato.class)))
                .thenThrow(new DataAccessResourceFailureException(
                        INTERNAL_MESSAGE));

        ResultActions result = mockMvc.perform(put("/contatos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(VALID_PAYLOAD));

        assertSafeInternalServerError(result, "/contatos/1");
        verify(repository).save(any(Contato.class));
    }

    @Test
    void returnsSafeInternalServerErrorWhenCreationSaveFails()
            throws Exception {
        when(repository.save(any(Contato.class)))
                .thenThrow(new DataAccessResourceFailureException(
                        INTERNAL_MESSAGE));

        ResultActions result = mockMvc.perform(post("/contatos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(VALID_PAYLOAD));

        assertSafeInternalServerError(result, "/contatos");
        verify(repository).save(any(Contato.class));
    }

    private void assertSafeInternalServerError(
            ResultActions result, String path) throws Exception {

        result.andExpect(status().isInternalServerError())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error")
                        .value("Internal Server Error"))
                .andExpect(jsonPath("$.message")
                        .value("Não foi possível concluir a operação."))
                .andExpect(jsonPath("$.timeStamp").isNotEmpty())
                .andExpect(jsonPath("$.path").value(path))
                .andExpect(jsonPath("$.trace").doesNotExist())
                .andExpect(jsonPath("$.exception").doesNotExist());

        String body = result.andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(body)
                .doesNotContain(INTERNAL_MESSAGE)
                .doesNotContain("SQL_SIMULADO")
                .doesNotContain("DataAccessResourceFailureException");
    }
}