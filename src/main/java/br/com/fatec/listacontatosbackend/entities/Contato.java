package br.com.fatec.listacontatosbackend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "TBL_CONTATO")
public class Contato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


    @Column(nullable = false)
    @NotBlank(message = "O nome do Contato não pode estar em branco")
    private String name;

    @NotBlank(message = "O email não pode estar em branco")
    private String email;

    @NotBlank(message = "O Telefone não pode estar em branco")    
    private String telefone;

    @NotBlank(message = "O endereço não pode estar em branco")
    private String endereco;

    @NotBlank(message = "A Cidade não pode estar em branco")
    private String cidade;

    @NotBlank(message = "O CEP não pode estar em branco")
    private String cep;

    @NotBlank(message = "O Estado não pode estar em branco")  
    private String estado;

    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getTelefone() {
        return telefone;
    }
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
    public String getEndereco() {
        return endereco;
    }
    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }
    public String getCidade() {
        return cidade;
    }
    public void setCidade(String cidade) {
        this.cidade = cidade;
    }
    public String getCep() {
        return cep;
    }
    public void setCep(String cep) {
        this.cep = cep;
    }
    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    } 

    
}
