package br.com.fatec.listacontatosbackend.resources;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ListaController {

    @GetMapping("lista")
    public String lista(){
        return "Lista Contatos";
    }
}
