package br.com.fatec.listacontatosbackend.dto;

import jakarta.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value = { "id" })
public record ContatoRequest(
        @NotBlank(message = "O nome do Contato não pode estar em branco") String name,

        @NotBlank(message = "O email não pode estar em branco") String email,

        @NotBlank(message = "O Telefone não pode estar em branco") String telefone,

        @NotBlank(message = "O endereço não pode estar em branco") String endereco,

        @NotBlank(message = "A Cidade não pode estar em branco") String cidade,

        @NotBlank(message = "O CEP não pode estar em branco") String cep,

        @NotBlank(message = "O Estado não pode estar em branco") String estado) {
}