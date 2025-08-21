package com.example.listadeeventos.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Objects;

public class Evento implements Serializable {
    private String id;
    private String titulo;
    private String fecha;
    private String hora;

    public Evento() {
    }

    public Evento(String id ,String nombre, String fecha, String hora) {
        this.id = id;
        this.titulo = nombre;
        this.fecha = fecha;
        this.hora = hora;
    }
    public String getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getFecha() {
        return fecha;
    }

    public String getHora() {
        return hora;
    }


    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }


    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Evento evento = (Evento) object;
        return Objects.equals(titulo, evento.titulo) && Objects.equals(fecha, evento.fecha) && Objects.equals(hora, evento.hora);
    }

    @Override
    public int hashCode() {return Objects.hash(titulo, fecha, hora);}
}
