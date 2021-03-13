package com.examples.ourpetsdc.Firebase;

public class Info_User {

    String nome, imagem, thumb_imagem, email, lingua, animais, status, clinica;

    public Info_User(){}

    public String getAnimais() {
        return animais;
    }

    public void setAnimais(String animais) {
        this.animais = animais;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Info_User(String nome, String imagem, String thumb_imagem, String email, String lingua, String animais, String status, String clinica){
        this.nome = nome;
        this.imagem = imagem;
        this.thumb_imagem = thumb_imagem;
        this.email = email;
        this.lingua = lingua;
        this.animais = animais;
        this.status = status;
        this.clinica = clinica;
    }

    public String getClinica() {
        return clinica;
    }

    public void setClinica(String clinica) {
        this.clinica = clinica;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public String getThumb_imagem() {
        return thumb_imagem;
    }

    public void setThumb_imagem(String thumb_imagem) {
        this.thumb_imagem = thumb_imagem;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLingua() {
        return lingua;
    }

    public void setLingua(String lingua) {
        this.lingua = lingua;
    }
}
