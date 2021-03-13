package com.examples.ourpetsdc.Firebase;

public class Info_Vacina_Desp {

    String nome, data, tipo, status, key, nome_pet, hora;

    public Info_Vacina_Desp(){}

    public Info_Vacina_Desp(String nome, String data, String tipo, String status, String key, String nome_pet, String hora){
        this.nome = nome;
        this.data = data;
        this.status = status;
        this.key = key;
        this.tipo = tipo;
        this.nome_pet = nome_pet;
        this.hora = hora;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getNome_pet() {
        return nome_pet;
    }

    public void setNome_pet(String nome_pet) {
        this.nome_pet = nome_pet;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
