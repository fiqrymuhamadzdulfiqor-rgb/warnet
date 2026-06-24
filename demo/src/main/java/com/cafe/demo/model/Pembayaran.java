package com.cafe.demo.model;

import jakarta.persistence.*;

@Entity
public class Pembayaran {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String metode;

    private double jumlahBayar;

    @OneToOne
    @JoinColumn(name = "transaksi_id")
    private Transaksi transaksi;

    public Pembayaran() {
    }

    public Long getId() {
        return id;
    }

    public String getMetode() {
        return metode;
    }

    public void setMetode(String metode) {
        this.metode = metode;
    }

    public double getJumlahBayar() {
        return jumlahBayar;
    }

    public void setJumlahBayar(double jumlahBayar) {
        this.jumlahBayar = jumlahBayar;
    }

    public Transaksi getTransaksi() {
        return transaksi;
    }

    public void setTransaksi(Transaksi transaksi) {
        this.transaksi = transaksi;
    }
}