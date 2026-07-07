package com.cafe.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class PesananFB {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pelanggan_id")
    private Pelanggan pelanggan;

    @ManyToOne
    @JoinColumn(name = "menu_fb_id")
    private MenuFB menuFb;

    private int jumlah;
    private double totalHargaRupiah; // Jika bayar pakai uang
    private int totalPoinDigunakan; // Jika bayar pakai poin

    private String metodePembayaran; // "Tunai", "QRIS", atau "Tukar Poin"
    private String statusPesanan; // "Diproses" atau "Selesai"

    @Column(updatable = false)
    private LocalDateTime tanggalPesanan;

    @PrePersist
    protected void onCreate() {
        this.tanggalPesanan = LocalDateTime.now();
    }

    public PesananFB() {}

    // --- GETTER & SETTER ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Pelanggan getPelanggan() { return pelanggan; }
    public void setPelanggan(Pelanggan pelanggan) { this.pelanggan = pelanggan; }

    public MenuFB getMenuFb() { return menuFb; }
    public void setMenuFb(MenuFB menuFb) { this.menuFb = menuFb; }

    public int getJumlah() { return jumlah; }
    public void setJumlah(int jumlah) { this.jumlah = jumlah; }

    public double getTotalHargaRupiah() { return totalHargaRupiah; }
    public void setTotalHargaRupiah(double totalHargaRupiah) { this.totalHargaRupiah = totalHargaRupiah; }

    public int getTotalPoinDigunakan() { return totalPoinDigunakan; }
    public void setTotalPoinDigunakan(int totalPoinDigunakan) { this.totalPoinDigunakan = totalPoinDigunakan; }

    public String getMetodePembayaran() { return metodePembayaran; }
    public void setMetodePembayaran(String metodePembayaran) { this.metodePembayaran = metodePembayaran; }

    public String getStatusPesanan() { return statusPesanan; }
    public void setStatusPesanan(String statusPesanan) { this.statusPesanan = statusPesanan; }

    public LocalDateTime getTanggalPesanan() { return tanggalPesanan; }
    public void setTanggalPesanan(LocalDateTime tanggalPesanan) { this.tanggalPesanan = tanggalPesanan; }
}