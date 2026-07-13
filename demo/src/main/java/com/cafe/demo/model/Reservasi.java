package com.cafe.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "trx_billing_pc")
public class Reservasi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relasi ke tabel Pelanggan (Bisa null jika tamu anonim)
    @ManyToOne
    @JoinColumn(name = "pelanggan_id")
    private Pelanggan pelanggan;

    // Nama tamu jika yang main bukan member
    private String namaPemesanTamu;

    // Relasi ke tabel Komputer
    @ManyToOne
    @JoinColumn(name = "komputer_id")
    private Komputer komputer;

    private LocalDateTime waktuMulai;
    private LocalDateTime waktuSelesai;

    // Jenis Paket (Contoh: "Reguler", "Paket Malam", "Paket Pagi")
    private String jenisPaket;

    // Durasi dalam hitungan jam (jika memilih main Reguler)
    private int durasiJam;

    // Total tagihan yang harus dibayar
    private double totalHarga;

    // Status bermain: "Aktif" atau "Selesai"
    private String statusBermain = "Aktif";

    // Penanda apakah poin sudah diberikan ke member (mencegah poin ganda)
    private boolean poinSudahDiklaim = false;

    // === FITUR BARU: Kode unik khusus untuk login pelanggan umum/tamu ===
    private String kodeAkses;

    public Reservasi() {
    }

    // ==========================================
    // GETTER & SETTER
    // ==========================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pelanggan getPelanggan() {
        return pelanggan;
    }

    public void setPelanggan(Pelanggan pelanggan) {
        this.pelanggan = pelanggan;
    }

    public String getNamaPemesanTamu() {
        return namaPemesanTamu;
    }

    public void setNamaPemesanTamu(String namaPemesanTamu) {
        this.namaPemesanTamu = namaPemesanTamu;
    }

    public Komputer getKomputer() {
        return komputer;
    }

    public void setKomputer(Komputer komputer) {
        this.komputer = komputer;
    }

    public LocalDateTime getWaktuMulai() {
        return waktuMulai;
    }

    public void setWaktuMulai(LocalDateTime waktuMulai) {
        this.waktuMulai = waktuMulai;
    }

    public LocalDateTime getWaktuSelesai() {
        return waktuSelesai;
    }

    public void setWaktuSelesai(LocalDateTime waktuSelesai) {
        this.waktuSelesai = waktuSelesai;
    }

    public String getJenisPaket() {
        return jenisPaket;
    }

    public void setJenisPaket(String jenisPaket) {
        this.jenisPaket = jenisPaket;
    }

    public int getDurasiJam() {
        return durasiJam;
    }

    public void setDurasiJam(int durasiJam) {
        this.durasiJam = durasiJam;
    }

    public double getTotalHarga() {
        return totalHarga;
    }

    public void setTotalHarga(double totalHarga) {
        this.totalHarga = totalHarga;
    }

    public String getStatusBermain() {
        return statusBermain;
    }

    public void setStatusBermain(String statusBermain) {
        this.statusBermain = statusBermain;
    }

    public boolean isPoinSudahDiklaim() {
        return poinSudahDiklaim;
    }

    public void setPoinSudahDiklaim(boolean poinSudahDiklaim) {
        this.poinSudahDiklaim = poinSudahDiklaim;
    }

    // === GETTER & SETTER UNTUK KODE AKSES ===
    public String getKodeAkses() {
        return kodeAkses;
    }

    public void setKodeAkses(String kodeAkses) {
        this.kodeAkses = kodeAkses;
    }
}