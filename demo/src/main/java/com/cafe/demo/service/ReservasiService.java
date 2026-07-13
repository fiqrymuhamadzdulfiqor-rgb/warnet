package com.cafe.demo.service;

import com.cafe.demo.model.Komputer;
import com.cafe.demo.model.Pelanggan;
import com.cafe.demo.model.Reservasi;
import com.cafe.demo.repository.KomputerRepository;
import com.cafe.demo.repository.ReservasiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservasiService {

    @Autowired
    private ReservasiRepository reservasiRepository;

    @Autowired
    private KomputerRepository komputerRepository;

    @Autowired
    private PelangganService pelangganService;

    public List<Reservasi> getAll() {
        return reservasiRepository.findAll();
    }

    public Reservasi save(Reservasi reservasi) {
        return reservasiRepository.save(reservasi);
    }

    // ==============================================================
    // LOGIKA SELESAI BERMAIN & PEMBAGIAN POIN (SANGAT KETAT)
    // ==============================================================
    @Transactional
    public void selesaikanBermain(Long reservasiId) {
        Reservasi reservasi = reservasiRepository.findById(reservasiId)
                .orElseThrow(() -> new RuntimeException("Data Billing tidak ditemukan!"));

        if ("Selesai".equalsIgnoreCase(reservasi.getStatusBermain())) {
            throw new RuntimeException("Sesi PC ini sudah diselesaikan sebelumnya.");
        }

        Komputer pc = reservasi.getKomputer();
        Pelanggan user = reservasi.getPelanggan();

        // 1. CEK STATUS MEMBER & BAGIKAN POIN BERDASARKAN GRADE PC
        // Syarat Ketat: User tidak boleh null, status WAJIB "MEMBER", dan poin belum diklaim
        if (user != null && "MEMBER".equalsIgnoreCase(user.getStatus()) && !reservasi.isPoinSudahDiklaim()) {
            
            int poinBonus = 0;
            String gradePc = (pc != null && pc.getGrade() != null) ? pc.getGrade() : "Reguler";

            // Klasifikasi Poin
            if ("VVIP".equalsIgnoreCase(gradePc)) {
                poinBonus = 10;
            } else if ("VIP".equalsIgnoreCase(gradePc)) {
                poinBonus = 8;
            } else {
                poinBonus = 5; // Default Reguler
            }

            // Suntikkan poin ke akun member
            user.setPoint(user.getPoint() + poinBonus);
            pelangganService.save(user);

            // Kunci agar tidak bisa diklaim dua kali
            reservasi.setPoinSudahDiklaim(true);
        }

        // 2. BEBASKAN PC (Ubah status jadi Kosong)
        if (pc != null) {
            pc.setStatus("Kosong");
            komputerRepository.save(pc);
        }

        // 3. TUTUP BILLING
        reservasi.setStatusBermain("Selesai");
        // Catat jam selesai aktual (jika selesainya lebih cepat/lambat dari jadwal)
        reservasi.setWaktuSelesai(LocalDateTime.now()); 
        
        reservasiRepository.save(reservasi);
    }
}