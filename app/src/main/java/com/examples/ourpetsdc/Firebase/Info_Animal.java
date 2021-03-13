package com.examples.ourpetsdc.Firebase;

public class Info_Animal {

    String nomePet, DataNasc, tipoAnimal, raca, chip, pelagem, formato_pelo, cauda, sexo, boletim, imagem, key;

    public Info_Animal(){}

    public Info_Animal(String nomePet, String dataNasc, String tipoAnimal, String raca, String chip, String pelagem, String formato_pelo, String cauda, String sexo, String boletim, String imagem, String key) {
        this.nomePet = nomePet;
        DataNasc = dataNasc;
        this.tipoAnimal = tipoAnimal;
        this.raca = raca;
        this.chip = chip;
        this.pelagem = pelagem;
        this.formato_pelo = formato_pelo;
        this.cauda = cauda;
        this.sexo = sexo;
        this.boletim = boletim;
        this.imagem = imagem;
        this.key = key;
    }

    public String getNomePet() {
        return nomePet;
    }

    public void setNomePet(String nomePet) {
        this.nomePet = nomePet;
    }

    public String getDataNasc() {
        return DataNasc;
    }

    public void setDataNasc(String dataNasc) {
        DataNasc = dataNasc;
    }

    public String getTipoAnimal() {
        return tipoAnimal;
    }

    public void setTipoAnimal(String tipoAnimal) {
        this.tipoAnimal = tipoAnimal;
    }

    public String getRaca() {
        return raca;
    }

    public void setRaca(String raca) {
        this.raca = raca;
    }

    public String getChip() {
        return chip;
    }

    public void setChip(String chip) {
        this.chip = chip;
    }

    public String getPelagem() {
        return pelagem;
    }

    public void setPelagem(String pelagem) {
        this.pelagem = pelagem;
    }

    public String getFormato_pelo() {
        return formato_pelo;
    }

    public void setFormato_pelo(String formato_pelo) {
        this.formato_pelo = formato_pelo;
    }

    public String getCauda() {
        return cauda;
    }

    public void setCauda(String cauda) {
        this.cauda = cauda;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getBoletim() {
        return boletim;
    }

    public void setBoletim(String boletim) {
        this.boletim = boletim;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
