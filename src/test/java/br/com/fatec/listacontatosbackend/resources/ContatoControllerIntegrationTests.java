package br.com.fatec.listacontatosbackend.resources;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import br.com.fatec.listacontatosbackend.entities.Contato;
import br.com.fatec.listacontatosbackend.repositories.ContatoRepository;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:contact-validation-tests",
    "spring.sql.init.mode=never",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
class ContatoControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ContatoRepository repository;

    @BeforeEach
    void clearContacts() {
        repository.deleteAll();
    }

    static Stream<Arguments> invalidFields() {
        return Stream.of(
            new String[]{"name", "O nome do Contato não pode estar em branco"},
            new String[]{"email", "O email não pode estar em branco"},
            new String[]{"telefone", "O Telefone não pode estar em branco"},
            new String[]{"endereco", "O endereço não pode estar em branco"},
            new String[]{"cidade", "A Cidade não pode estar em branco"},
            new String[]{"cep", "O CEP não pode estar em branco"},
            new String[]{"estado", "O Estado não pode estar em branco"}
        ).flatMap(field -> Stream.of(null, "", "   ")
            .map(value -> Arguments.of(field[0], value, field[1])));
    }

    @ParameterizedTest(name = "POST rejects {0} with value [{1}]")
    @MethodSource("invalidFields")
    void rejectsInvalidCreation(String field, String value, String message) throws Exception {
        ObjectNode payload = validPayload();
        payload.put(field, value);

        mockMvc.perform(post("/contatos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.status").value(422))
            .andExpect(jsonPath("$.path").value("/contatos"))
            .andExpect(jsonPath("$.errors", contains(message)));

        assertThat(repository.count()).isZero();
    }

    @ParameterizedTest(name = "PUT rejects {0} with value [{1}]")
    @MethodSource("invalidFields")
    void rejectsInvalidUpdateWithoutChangingStoredContact(String field, String value, String message)
            throws Exception {
        Contato original = repository.saveAndFlush(objectMapper.treeToValue(validPayload(), Contato.class));
        ObjectNode payload = validPayload();
        payload.put("name", "Contato atualizado");
        payload.put("cidade", "Cidade atualizada");
        payload.put(field, value);
        String path = "/contatos/" + original.getId();

        mockMvc.perform(put(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.status").value(422))
            .andExpect(jsonPath("$.path").value(path))
            .andExpect(jsonPath("$.errors", contains(message)));

        assertThat(repository.count()).isEqualTo(1);
        assertThat(repository.findById(original.getId()).orElseThrow())
            .usingRecursiveComparison().isEqualTo(original);
    }

    @Test
    void createsValidContactWithLocationAndBody() throws Exception {
        ObjectNode payload = validPayload();

        var result = mockMvc.perform(post("/contatos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Contato de teste"))
            .andReturn();
        byte[] response = result.getResponse().getContentAsByteArray();

        Contato created = objectMapper.readValue(response, Contato.class);
        assertThat(created.getId()).isPositive();
        assertThat(repository.findById(created.getId()).orElseThrow())
            .usingRecursiveComparison().isEqualTo(created);
        assertThat(created).usingRecursiveComparison().ignoringFields("id")
            .isEqualTo(objectMapper.treeToValue(payload, Contato.class));
        assertThat(result.getResponse().getHeader("Location"))
            .isEqualTo("http://localhost/contatos/" + created.getId());
    }

    @Test
    void updatesValidContactWithEmptyResponse() throws Exception {
        Contato original = repository.saveAndFlush(objectMapper.treeToValue(validPayload(), Contato.class));
        ObjectNode payload = validPayload();
        payload.put("name", "Contato atualizado");
        payload.put("email", "atualizado@example.com");
        payload.put("telefone", "11999990000");
        payload.put("endereco", "Rua de teste, 20");
        payload.put("cidade", "Campinas");
        payload.put("cep", "13000000");
        payload.put("estado", "SP");

        mockMvc.perform(put("/contatos/{id}", original.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isOk())
            .andExpect(content().string(""));

        Contato expected = objectMapper.treeToValue(payload, Contato.class);
        expected.setId(original.getId());
        assertThat(repository.count()).isEqualTo(1);
        assertThat(repository.findById(original.getId()).orElseThrow())
            .usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void returnsNotFoundForValidUpdateOfMissingContact() throws Exception {
        mockMvc.perform(put("/contatos/{id}", Integer.MAX_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validPayload())))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.error").value("Resource not found"));

        assertThat(repository.count()).isZero();
    }

    private ObjectNode validPayload() {
        return objectMapper.createObjectNode()
            .put("name", "Contato de teste")
            .put("email", "contato@example.com")
            .put("telefone", "11988887777")
            .put("endereco", "Rua fictícia, 10")
            .put("cidade", "São Paulo")
            .put("cep", "01000000")
            .put("estado", "SP");
    }
}
