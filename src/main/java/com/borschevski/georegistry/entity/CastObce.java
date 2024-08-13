package com.borschevski.georegistry.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("cast_obce")
public class CastObce {
    @Id
    private Integer kod;
    private String nazev;
    private Integer kodObec;

    public Integer getKod() {
        return kod;
    }

    public void setKodObce(Integer kod) {
        this.kod = kod;
    }

    public String getNazev() {
        return nazev;
    }

    public void setNazev(String nazev) {
        this.nazev = nazev;
    }

    public Integer getKodObec() {
        return kodObec;
    }

    public void setKodObec(Integer kodObec) {
        this.kodObec = kodObec;
    }
}
