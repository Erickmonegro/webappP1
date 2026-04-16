package com.webapp.webappstudents.model;



import java.util.List;
import java.util.ArrayList;

public class DiaCalendario {
    private int numero;
    private boolean esMesActual;
    private List<Tarea> tareasDelDia = new ArrayList<>();

    // Constructor, Getters y Setters
    public DiaCalendario(int numero, boolean esMesActual) {
        this.numero = numero;
        this.esMesActual = esMesActual;
    }

    public int getNumero() { return numero; }
    public boolean isEsMesActual() { return esMesActual; }
    public List<Tarea> getTareasDelDia() { return tareasDelDia; }
    public void addTarea(Tarea t) { this.tareasDelDia.add(t); }
}
