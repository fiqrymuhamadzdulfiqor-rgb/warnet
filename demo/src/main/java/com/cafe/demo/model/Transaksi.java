package com.cafe.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

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
    private String kodeAkses;

    // --- FITUR BARU: Pencatatan Waktu Otomatis ---
    @Column(updatable = false)
    private LocalDateTime tanggalTransaksi;

    @PrePersist
    protected void onCreate() {
        this.tanggalTransaksi = LocalDateTime.now();
    }

    public Transaksi() {
    }

    // --- GETTER & SETTER ---
    public Long getId() { return id; }
    
    public Pelanggan getPelanggan() { return pelanggan; }
    public void setPelanggan(Pelanggan pelanggan) { this.pelanggan = pelanggan; }

    public Komputer getKomputer() { return komputer; }
    public void setKomputer(Komputer komputer) { this.komputer = komputer; }

    public int getJam() { return jam; }
    public void setJam(int jam) { this.jam = jam; }

    public double getTarif() { return tarif; }
    public void setTarif(double tarif) { this.tarif = tarif; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public String getKodeAkses() { return kodeAkses; }
    public void setKodeAkses(String kodeAkses) { this.kodeAkses = kodeAkses; }

    public LocalDateTime getTanggalTransaksi() { return tanggalTransaksi; }
    public void setTanggalTransaksi(LocalDateTime tanggalTransaksi) { this.tanggalTransaksi = tanggalTransaksi; }

    public void hitungTotal() {
        this.total = tarif * jam;
    }
}