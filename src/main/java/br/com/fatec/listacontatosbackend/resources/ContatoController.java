package br.com.fatec.listacontatosbackend.resources;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.fatec.listacontatosbackend.entities.Contato;

@RestController
public class ContatoController {
    
    @GetMapping("contato")
    public Contato getContato(){
        Contato c = new Contato();
        c.setId(0);
        c.setName(null);
        c.setEmail(null);
        c.setTelefone(null);
        c.setEndereco(null);
        c.setCidade(null);
        c.setCep(0);
        c.setEstado(null);
        return c;
    }

    @GetMapping("contatos")
    public List<Contato> getContatos(){

        ArrayList<Contato> contatos = new ArrayList<Contato>();

        Contato c1 = new Contato();
        c1.setId(0);
        c1.setName(null);
        c1.setEmail(null);
        c1.setTelefone(null);
        c1.setEndereco(null);
        c1.setCidade(null);
        c1.setCep(0);
        c1.setEstado(null);

        Contato c2 = new Contato();
        c2.setId(0);
        c2.setName(null);
        c2.setEmail(null);
        c2.setTelefone(null);
        c2.setEndereco(null);
        c2.setCidade(null);
        c2.setCep(0);
        c2.setEstado(null);

        Contato c3 = new Contato();
        c3.setId(0);
        c3.setName(null);
        c3.setEmail(null);
        c3.setTelefone(null);
        c3.setEndereco(null);
        c3.setCidade(null);
        c3.setCep(0);
        c3.setEstado(null);

        contatos.add(c1);
        contatos.add(c2);
        contatos.add(c3);
        

        return contatos;
    }
}
