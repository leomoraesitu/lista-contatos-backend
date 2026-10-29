package br.com.fatec.listacontatosbackend.resources;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.com.fatec.listacontatosbackend.entities.Contato;
import br.com.fatec.listacontatosbackend.services.ContatoService;
import br.com.fatec.listacontatosbackend.dto.ContatoRequest;

@RestController
@RequestMapping("contatos")
@CrossOrigin
public class ContatoController {

    private final ContatoService service;

    public ContatoController(ContatoService service) {
        this.service = service;
    }

    @GetMapping()
    public ResponseEntity<List<Contato>> getContatos() {
        return ResponseEntity.ok(service.getContatos());
    }

    @GetMapping("{id}")
    public ResponseEntity<Contato> getContatosbyId(@PathVariable int id) {
        return ResponseEntity.ok(service.getContatosbyId(id));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteContato(@PathVariable int id) {
        this.service.deleteContatoById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<Contato> save(
            @Validated @RequestBody ContatoRequest request) {

        Contato contato = toEntity(request);
        Contato newContato = this.service.save(contato);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newContato.getId())
                .toUri();

        return ResponseEntity.created(location).body(newContato);
    }

    @PutMapping("{id}")
    public ResponseEntity<Void> update(
            @PathVariable int id,
            @Validated @RequestBody ContatoRequest request) {

        Contato contato = toEntity(request);
        this.service.update(id, contato);

        return ResponseEntity.ok().build();
    }

    private Contato toEntity(ContatoRequest request) {
        Contato contato = new Contato();

        contato.setName(request.name());
        contato.setEmail(request.email());
        contato.setTelefone(request.telefone());
        contato.setEndereco(request.endereco());
        contato.setCidade(request.cidade());
        contato.setCep(request.cep());
        contato.setEstado(request.estado());

        return contato;
    }

}
