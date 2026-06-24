package com.cafe.demo.model;

import jakarta.persistence.*;

@Entity
public class Transaksi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pelanggan_id")
    private Pelanggan pelanggan;

    @ManyToOne
    @JoinColumn(name = "komputer_id")
    private Komputer komputer;

    private int jam;

    private double tarif;

    private double total;

    // Tambahan untuk alur Guest (Kode Akses Layar PC)
    private String kodeAkses;

    public Transaksi() {
    }

    public Long getId() {
        return id;
    }

    public Pelanggan getPelanggan() {
        return pelanggan;
    }

    public void setPelanggan(Pelanggan pelanggan) {
        this.pelanggan = pelanggan;
    }

    public Komputer getKomputer() {
        return komputer;
    }

    public void setKomputer(Komputer komputer) {
        this.komputer = komputer;
    }

    public int getJam() {
        return jam;
    }

    public void setJam(int jam) {
        this.jam = jam;
    }

    public double getTarif() {
        return tarif;
    }

    public void setTarif(double tarif) {
        this.tarif = tarif;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getKodeAkses() {
        return kodeAkses;
    }

    public void setKodeAkses(String kodeAkses) {
        this.kodeAkses = kodeAkses;
    }

    public void hitungTotal() {
        this.total = tarif * jam;
    }
}