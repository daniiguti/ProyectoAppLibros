package com.example.pruebarecviewbasedatos.Modelo;

public class Libro {
    //Atributos
    private String id;
    private String titulo;
    private String autor;
    private String imageURL;

    //Constructor
    public Libro(String id, String titulo, String autor, String imgURL) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.imageURL = imgURL;
    }

    //Getters y Setters
    public String getId() { return id; }
    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }
    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getImagePortada() { return imageURL; }
    public void setImagePortada(String imageURL) { this.imageURL = imageURL; }

    //toString
    @Override
    public String toString() {
        return "Libro{" +
                "id='" + id + '\'' +
                ", titulo='" + titulo + '\'' +
                ", autor='" + autor + '\'' +
                "}";
    }
}
