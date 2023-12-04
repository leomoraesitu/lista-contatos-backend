package br.com.fatec.listacontatosbackend.resources;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import br.com.fatec.listacontatosbackend.entities.Contato;
import br.com.fatec.listacontatosbackend.repositories.ContatoRepository;

@RestController
public class ContatoController {
    
    @Autowired
    private ContatoRepository repository;

    
    @GetMapping("contatos")
    public List<Contato> getContatos(){
        return repository.findAll();
    }

    @GetMapping("contatos/{id}")
    public Contato getClientsbyId(@PathVariable int id){     
        return repository.findById(id).get();
    }
}
