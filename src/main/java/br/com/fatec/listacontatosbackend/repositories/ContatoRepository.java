package br.com.fatec.listacontatosbackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.fatec.listacontatosbackend.entities.Contato;

public interface ContatoRepository extends JpaRepository<Contato,Integer>{    

}
