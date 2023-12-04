package br.com.fatec.listacontatosbackend.resources;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.fatec.listacontatosbackend.entities.Contato;
import br.com.fatec.listacontatosbackend.services.ContatoService;

@RestController
@RequestMapping("contatos")
public class ContatoController {
    
    @Autowired
    private ContatoService service;

    
    @GetMapping()
    public ResponseEntity<List<Contato>> getContatos(){
        return ResponseEntity.ok(service.getContatos());
    }

    @GetMapping("{id}")
    public ResponseEntity<Contato> getContatosbyId(@PathVariable int id){     
        return ResponseEntity.ok(service.getContatosbyId(id));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteContato(@PathVariable int id){
        this.service.deleteContatoById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<Contato> save(@RequestBody Contato contato){

        Contato newContato = this.service.save(contato);

        return ResponseEntity.created(null).body(newContato);
    }
    

}
