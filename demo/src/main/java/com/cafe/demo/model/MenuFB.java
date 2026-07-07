package com.cafe.demo.model;

import jakarta.persistence.*;

@Entity
public class MenuFB {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String namaMenu;
    
    private String kategori; // "Makanan" atau "Minuman"
    
    private double hargaRupiah;
    
    private int hargaPoin; // Berapa poin yang dibutuhkan untuk ditukar

    public MenuFB() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNamaMenu() { return namaMenu; }
    public void setNamaMenu(String namaMenu) { this.namaMenu = namaMenu; }

    public String getKategori() { return kategori; }
    public void setKategori(String kategori) { this.kategori = kategori; }

    public double getHargaRupiah() { return hargaRupiah; }
    public void setHargaRupiah(double hargaRupiah) { this.hargaRupiah = hargaRupiah; }

    public int getHargaPoin() { return hargaPoin; }
    public void setHargaPoin(int hargaPoin) { this.hargaPoin = hargaPoin; }
}