package com.example.listadeeventos.Models;

import java.util.Objects;

public class Evento {
    private String nombre;
    private String fecha;
    private String hora;

    public Evento() {
    }

    public Evento(String nombre, String fecha, String hora) {
        this.nombre = nombre;
        this.fecha = fecha;
        this.hora = hora;
    }

    public String getNombre() {
        return nombre;
    }

    public String getFecha() {
        return fecha;
    }

    public String getHora() {
        return hora;
    }


    public void setNombre(String nombre) {
        this.nombre = nombre;
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
        return Objects.equals(nombre, evento.nombre) && Objects.equals(fecha, evento.fecha) && Objects.equals(hora, evento.hora);
    }

    @Override
    public int hashCode() {return Objects.hash(nombre, fecha, hora);}
}
