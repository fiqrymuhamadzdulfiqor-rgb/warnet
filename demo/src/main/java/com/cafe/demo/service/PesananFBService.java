package com.cafe.demo.service;

import com.cafe.demo.model.Pelanggan;
import com.cafe.demo.model.PesananFB;
import com.cafe.demo.repository.PesananFBRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // <-- JANGAN LUPA IMPORT INI

import java.util.List;

@Service
public class PesananFBService {

    @Autowired
    private PesananFBRepository pesananFBRepository;

    @Autowired
    private PelangganService pelangganService;

    // --- FUNGSI UNTUK ADMIN ---
    public List<PesananFB> getAll() {
        return pesananFBRepository.findAll();
    }

    public PesananFB getById(Long id) {
        return pesananFBRepository.findById(id).orElse(null);
    }

    public PesananFB save(PesananFB pesanan) {
        return pesananFBRepository.save(pesanan);
    }
    // --------------------------

    // FUNGSI INTI PEMROSESAN PESANAN KANTIN (Mendukung Keranjang)
    @Transactional // <-- INI DIA KUNCI UNDO OTOMATISNYA! JANGAN DIHAPUS YA
    public PesananFB prosesPesanan(PesananFB pesanan) {
        // Ambil data pelanggan langsung dari struk pesanan
        Pelanggan pembeli = pesanan.getPelanggan();

        // Cek jika metode pembayarannya Tukar Poin
        if ("Tukar Poin".equalsIgnoreCase(pesanan.getMetodePembayaran())) {
            
            // Proteksi: Cegah tamu/umum menggunakan poin
            if (pembeli == null) {
                throw new RuntimeException("Gagal: Pelanggan umum/tamu tidak bisa menggunakan fitur Tukar Poin!");
            }
            
            // Proteksi: Cek apakah poin cukup
            if (pembeli.getPoint() < pesanan.getTotalPoinDigunakan()) {
                throw new RuntimeException("Gagal: Poin kamu tidak cukup! Butuh " + pesanan.getTotalPoinDigunakan() + " pts.");
            }
            
            // Kurangi poin member dan simpan ke database pelanggan
            pembeli.setPoint(pembeli.getPoint() - pesanan.getTotalPoinDigunakan());
            pelangganService.save(pembeli); 
            
            // Set harga rupiah jadi 0 karena sudah lunas pakai poin
            pesanan.setTotalHargaRupiah(0);
            
        } else {
            // Jika bayar tunai/QRIS, pastikan poin yang digunakan dicatat 0
            pesanan.setTotalPoinDigunakan(0);
        }

        // Simpan struk pesanan. Karena ada aturan CascadeType.ALL di Entity,
        // semua daftar makanan (DetailPesananFB) akan otomatis ikut tersimpan!
        return pesananFBRepository.save(pesanan);
    }
}