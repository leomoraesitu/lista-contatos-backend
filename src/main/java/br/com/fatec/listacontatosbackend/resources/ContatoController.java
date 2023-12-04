package br.com.fatec.listacontatosbackend.resources;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import br.com.fatec.listacontatosbackend.entities.Contato;
import br.com.fatec.listacontatosbackend.services.ContatoService;

@RestController
public class ContatoController {
    
    @Autowired
    private ContatoService service;

    
    @GetMapping("contatos")
    public ResponseEntity<List<Contato>> getContatos(){
        return ResponseEntity.ok(service.getContatos());
    }

    @GetMapping("contatos/{id}")
    public ResponseEntity<Contato> getContatosbyId(@PathVariable int id){     
        return ResponseEntity.ok(service.getContatosbyId(id));
    }

    @DeleteMapping("contatos/{id}")
    public ResponseEntity<Void> deleteContato(@PathVariable int id){
        this.service.deleteContatoById(id);
        return ResponseEntity.noContent().build();
    }
}
