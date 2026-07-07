package com.cafe.demo.service;

import com.cafe.demo.model.Pelanggan;
import com.cafe.demo.model.PesananFB;
import com.cafe.demo.repository.PesananFBRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    // FUNGSI INTI PEMROSESAN PESANAN KANTIN (Dari User)
    public PesananFB prosesPesanan(PesananFB pesanan, Pelanggan pembeli) {
        pesanan.setPelanggan(pembeli);
        pesanan.setStatusPesanan("Diproses");

        if ("Tukar Poin".equalsIgnoreCase(pesanan.getMetodePembayaran())) {
            if (pembeli.getPoint() < pesanan.getTotalPoinDigunakan()) {
                throw new RuntimeException("Gagal: Poin kamu tidak cukup! Butuh " + pesanan.getTotalPoinDigunakan() + " pts.");
            }
            pembeli.setPoint(pembeli.getPoint() - pesanan.getTotalPoinDigunakan());
            pelangganService.save(pembeli); 
            pesanan.setTotalHargaRupiah(0);
        } else {
            pesanan.setTotalPoinDigunakan(0);
        }

        return pesananFBRepository.save(pesanan);
    }
}