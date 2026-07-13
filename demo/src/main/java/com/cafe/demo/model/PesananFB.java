package com.cafe.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "trx_kasir_fb")
public class PesananFB {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // SOLUSI UNTUK ARIF/PC-01: Disimpan sebagai string biasa, tidak masuk ke database Pelanggan
    private String namaPemesanTamu;

    // Relasi ini sekarang Boleh Kosong (nullable = true) kalau yang beli orang umum/tamu
    @ManyToOne
    @JoinColumn(name = "pelanggan_id", nullable = true)
    private Pelanggan pelanggan;

    // FITUR KERANJANG: Satu pesanan punya banyak detail makanan
    @OneToMany(mappedBy = "pesananFb", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetailPesananFB> daftarMenu = new ArrayList<>();

    private double totalHargaRupiah;
    private int totalPoinDigunakan;
    private String metodePembayaran; 
    private String statusPesanan; 

    @Column(updatable = false)
    private LocalDateTime tanggalPesanan;

    @PrePersist
    protected void onCreate() {
        this.tanggalPesanan = LocalDateTime.now();
    }

    public PesananFB() {}

    // Method pembantu untuk keranjang
    public void tambahDetail(DetailPesananFB detail) {
        daftarMenu.add(detail);
        detail.setPesananFb(this);
    }

    // --- GETTER & SETTER ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNamaPemesanTamu() { return namaPemesanTamu; }
    public void setNamaPemesanTamu(String namaPemesanTamu) { this.namaPemesanTamu = namaPemesanTamu; }

    public Pelanggan getPelanggan() { return pelanggan; }
    public void setPelanggan(Pelanggan pelanggan) { this.pelanggan = pelanggan; }

    public List<DetailPesananFB> getDaftarMenu() { return daftarMenu; }
    public void setDaftarMenu(List<DetailPesananFB> daftarMenu) { this.daftarMenu = daftarMenu; }

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