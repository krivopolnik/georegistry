package com.borschevski.georegistry.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class CastObce {
    @Id
    private int kod;
    private String nazev;
    private int kodObec;

    public int getKod() {
        return kod;
    }

    public void setKod(int kod) {
        this.kod = kod;
    }

    public String getNazev() {
        return nazev;
    }

    public void setNazev(String nazev) {
        this.nazev = nazev;
    }

    public int getKodObec() {
        return kodObec;
    }

    public void setKodObec(int kodObec) {
        this.kodObec = kodObec;
    }
}
