package com.cafe.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "trx_detail_fb")
public class DetailPesananFB {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relasi balik ke Struk Utama
    @ManyToOne
    @JoinColumn(name = "pesanan_fb_id")
    private PesananFB pesananFb;

    // Makanan/Minuman yang dipesan
    @ManyToOne
    @JoinColumn(name = "menu_fb_id")
    private MenuFB menuFb;

    private int jumlah;
    private double subTotalRupiah;
    private int subTotalPoin;

    public DetailPesananFB() {}

    // --- GETTER & SETTER ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PesananFB getPesananFb() { return pesananFb; }
    public void setPesananFb(PesananFB pesananFb) { this.pesananFb = pesananFb; }

    public MenuFB getMenuFb() { return menuFb; }
    public void setMenuFb(MenuFB menuFb) { this.menuFb = menuFb; }

    public int getJumlah() { return jumlah; }
    public void setJumlah(int jumlah) { this.jumlah = jumlah; }

    public double getSubTotalRupiah() { return subTotalRupiah; }
    public void setSubTotalRupiah(double subTotalRupiah) { this.subTotalRupiah = subTotalRupiah; }

    public int getSubTotalPoin() { return subTotalPoin; }
    public void setSubTotalPoin(int subTotalPoin) { this.subTotalPoin = subTotalPoin; }
}