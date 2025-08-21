package com.example.listadeeventos.Models;

import java.util.Objects;

public class Persona {
    private String nombre;
    private String apellido;
    private String edad;

    public Persona() {
    }

    public Persona(String nombre, String pellido, String edad) {
        this.nombre = nombre;
        this.apellido = pellido;
        this.edad = edad;
    }

    public String getNombre() {
        return nombre;
    }

    public String getAPellido() {
        return apellido;
    }

    public String getEdad() {
        return edad;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setPellido(String pellido) {
        this.apellido = pellido;
    }

    public void setEdad(String edad) {
        this.edad = edad;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Persona persona = (Persona) object;
        return Objects.equals(nombre, persona.nombre) && Objects.equals(apellido, persona.apellido) && Objects.equals(edad, persona.edad);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre, apellido, edad);
    }
}
