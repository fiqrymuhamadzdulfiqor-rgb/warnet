package com.cafe.demo.service;

import com.cafe.demo.model.Komputer;
import com.cafe.demo.model.Pelanggan;
import com.cafe.demo.model.Transaksi;
import com.cafe.demo.repository.TransaksiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TransaksiService {

    @Autowired
    private TransaksiRepository transaksiRepository;

    @Autowired
    private KomputerService komputerService;

    @Autowired
    private PelangganService pelangganService;

    public List<Transaksi> getAll() {
        return transaksiRepository.findAll();
    }

    public Transaksi getById(Long id) {
        return transaksiRepository.findById(id).orElse(null);
    }

    public Transaksi save(Transaksi transaksi) {

        Komputer komputer = komputerService.getById(transaksi.getKomputer().getId());
        Pelanggan pelanggan = pelangganService.getById(transaksi.getPelanggan().getId());

        transaksi.setKomputer(komputer);
        transaksi.setPelanggan(pelanggan);

        // 1. Hitung Harga Dasar
        double hargaDasar = transaksi.getTarif() * transaksi.getJam();

        // 2. Pemisahan Hak Akses Pelanggan
        if ("MEMBER".equalsIgnoreCase(pelanggan.getStatus())) {
            // Benefit Member: Diskon 15%
            transaksi.setTotal(hargaDasar * 0.85);

            // Benefit Member: Tambah 10 Poin per Jam
            int tambahanPoint = transaksi.getJam() * 10;
            pelanggan.setPoint(pelanggan.getPoint() + tambahanPoint);
            pelangganService.save(pelanggan);

            transaksi.setKodeAkses("MEMBER-LOGIN"); 
        } else {
            // Pengguna Umum: Bayar Harga Normal
            transaksi.setTotal(hargaDasar);

            // Pengguna Umum: Generate Kode Akses Acak 6 Karakter
            String kodeUnik = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
            transaksi.setKodeAkses(kodeUnik);
        }

        // 3. Validasi & Ubah Status Komputer
        if ("Dipakai".equalsIgnoreCase(komputer.getStatus())) {
            throw new IllegalStateException("Maaf, komputer ini sedang digunakan!");
        }
        
        komputer.setStatus("Dipakai");
        komputerService.save(komputer);

        return transaksiRepository.save(transaksi);
    }

    public void delete(Long id) {
        Transaksi transaksi = getById(id);
        if (transaksi != null) {
            Komputer komputer = transaksi.getKomputer();
            komputer.setStatus("Tersedia");
            komputerService.save(komputer);
            transaksiRepository.deleteById(id);
        }
    }

    public List<Transaksi> getByPelanggan(Pelanggan pelanggan) {
        return transaksiRepository.findByPelanggan(pelanggan);
    }
}