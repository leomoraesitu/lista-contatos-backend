package br.com.fatec.listacontatosbackend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.fatec.listacontatosbackend.entities.Contato;
import br.com.fatec.listacontatosbackend.repositories.ContatoRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class ContatoService {
    
     @Autowired
    private ContatoRepository repository;

    public List<Contato> getContatos(){     
        return repository.findAll();
    }

	public Contato getContatosbyId(int id){     
	    return repository.findById(id).orElseThrow(
            () -> new EntityNotFoundException("Contato não existe")
        );
	}

    public void deleteContatoById(int id){
        if(this.repository.existsById(id)){
            this.repository.deleteById(id);
        }
        else{
            throw new EntityNotFoundException("Contato não existe");
        }
    }

    public Contato save(Contato contato) {
        return this.repository.save(contato);
    }

    public void update(int id, Contato contato) {
        try{
            Contato aux = repository.getReferenceById(id);
            aux.setName(contato.getName());
            aux.setEmail(contato.getEmail());
            aux.setTelefone(contato.getTelefone());
            aux.setEndereco(contato.getEndereco());
            aux.setCidade(contato.getCidade());
            aux.setCep(contato.getCep());
            aux.setEstado(contato.getEstado());
            this.repository.save(aux);
        }
        catch(EntityNotFoundException e)
        {
            throw new EntityNotFoundException("Contato não cadastrado");
        }
        catch(Exception e)
        {
            throw new EntityNotFoundException("Erro não identificado");
        }   
    }
}
