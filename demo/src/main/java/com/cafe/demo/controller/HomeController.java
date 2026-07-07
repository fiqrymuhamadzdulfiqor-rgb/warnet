package com.cafe.demo.controller;

import com.cafe.demo.model.Komputer;
import com.cafe.demo.model.Pelanggan;
import com.cafe.demo.model.Pembayaran;
import com.cafe.demo.model.Transaksi;
import com.cafe.demo.service.KomputerService;
import com.cafe.demo.service.PelangganService;
import com.cafe.demo.service.PembayaranService;
import com.cafe.demo.service.TransaksiService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List; // TAMBAHAN IMPORT UNTUK LIST TRANSAKSI

@Controller
public class HomeController {

    @Autowired
    private PelangganService pelangganService;

    @Autowired
    private KomputerService komputerService;

    @Autowired
    private TransaksiService transaksiService;

    @Autowired
    private PembayaranService pembayaranService;

    // ================= HOME =================

    @GetMapping("/")
    public String home() {
        return "index";
    }

    // ================= FASILITAS =================
    @GetMapping("/fasilitas")
    public String fasilitasPage() {
        return "fasilitas"; // Membuka file fasilitas.html
    }

    // ================= LOGIN ADMIN =================

    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        if (session.getAttribute("adminLogin") != null && (Boolean) session.getAttribute("adminLogin")) {
            return "redirect:/admin";
        }
        return "login";
    }

    @PostMapping("/login")
    public String loginAdmin(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session) {

        if(username.equals("admin") && password.equals("123")) {
            session.setAttribute("adminLogin", true);
            return "redirect:/admin";
        }
        return "redirect:/login?error";
    }

    // ================= LOGOUT =================

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // Menghapus seluruh sesi
        session.invalidate(); 
        return "redirect:/";
    }

    // ================= RESERVASI UMUM =================

    @GetMapping("/reservasi")
    public String reservasiPage() {
        return "reservasi";
    }

    @PostMapping("/proses-reservasi")
    public String prosesReservasi(
            @RequestParam String nama,
            @RequestParam int durasi,
            HttpSession session) {
        
        // Cek apakah yang booking ini Member yang sedang login
        Pelanggan pembeli = (Pelanggan) session.getAttribute("user");
        
        // Jika dia BUKAN member (Pelanggan Umum), buatkan data baru di database
        if (pembeli == null) {
            Pelanggan guest = new Pelanggan();
            guest.setNama(nama);
            guest.setStatus("UMUM");
            pembeli = pelangganService.save(guest); // Simpan ke database
            session.setAttribute("guestUser", pembeli); // Ingat dia sebagai Guest
        }

        // Simpan durasi main yang diketik ke memori (akan dipakai saat milih PC)
        session.setAttribute("durasiMain", durasi);
        
        return "redirect:/pilih-komputer";
    }

    // ================= LOGIN MEMBER =================

    @GetMapping("/login-member")
    public String loginMemberPage(HttpSession session) {
        if (session.getAttribute("user") != null) {
            return "redirect:/member-dashboard"; 
        }
        return "login-member";
    }

    @PostMapping("/login-member")
    public String loginMember(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session) {

        Pelanggan user = pelangganService.loginMember(username, password);

        if(user != null) {
            session.setAttribute("user", user);
            return "redirect:/member-dashboard";
        }
        return "redirect:/login-member?error";
    }

    // ================= REGISTER MEMBER =================

    @GetMapping("/register-member")
    public String registerMemberPage() {
        return "register-member";
    }

    @PostMapping("/register-member")
    public String registerMember(Pelanggan pelanggan) {
        pelanggan.setStatus("MEMBER");
        pelangganService.save(pelanggan);
        return "redirect:/login-member";
    }

    // ================= MEMBER DASHBOARD =================

    @GetMapping("/member-dashboard")
    public String memberDashboard(HttpSession session, Model model) {
        Pelanggan user = (Pelanggan) session.getAttribute("user");

        if(user == null) {
            return "redirect:/login-member";
        }

        // Dapatkan data user terbaru dari DB agar poinnya selalu update
        user = pelangganService.getById(user.getId());
        model.addAttribute("user", user);

        // Ambil riwayat transaksi
        List<Transaksi> riwayatTransaksi = transaksiService.getByPelanggan(user);
        model.addAttribute("transaksiList", riwayatTransaksi);

        // --- LOGIKA MENCARI BILLING AKTIF YANG DITAMBAHKAN ---
        Transaksi transaksiAktif = null;
        for (Transaksi t : riwayatTransaksi) {
            // Jika komputer pada transaksi ini statusnya masih "Dipakai", berarti ini billing yang sedang jalan
            if (t.getKomputer() != null && "Dipakai".equalsIgnoreCase(t.getKomputer().getStatus())) {
                transaksiAktif = t;
                break; // Ketemu yang aktif, langsung stop pencarian
            }
        }
        model.addAttribute("transaksiAktif", transaksiAktif);
        // ----------------------------------------------------

        return "member-dashboard";
    }

    // ================= PILIH KOMPUTER =================

    @GetMapping("/pilih-komputer")
    public String pilihKomputer(Model model, HttpSession session) {
        if(session.getAttribute("user") == null && session.getAttribute("guestUser") == null) {
            return "redirect:/";
        }

        model.addAttribute("komputerList", komputerService.getAll());
        return "pilih-komputer";
    }

    // ================= CHECKOUT (PILIH PC BARU) =================

    @GetMapping("/simpan-komputer/{id}")
    public String simpanKomputer(@PathVariable Long id, HttpSession session) {
        // Ambil data pelanggan (Member atau Umum)
        Pelanggan pelanggan = (Pelanggan) session.getAttribute("user");
        if(pelanggan == null) {
            pelanggan = (Pelanggan) session.getAttribute("guestUser");
        }
        
        if(pelanggan == null) {
            return "redirect:/"; // Tendang ke home kalau nyasar
        }

        // Ambil durasi main yang sudah disimpan dari halaman reservasi tadi
        Integer durasi = (Integer) session.getAttribute("durasiMain");
        if (durasi == null) durasi = 1; // Default 1 jam untuk jaga-jaga

        // Proses pembuatan nota Transaksi
        Komputer komputer = komputerService.getById(id);
        Transaksi transaksi = new Transaksi();
        transaksi.setPelanggan(pelanggan);
        transaksi.setKomputer(komputer);
        transaksi.setJam(durasi);

        // --- MENGHITUNG HARGA TOTAL ---
        transaksi.setTarif(komputer.getTarifPerJam()); 
        transaksi.hitungTotal(); 
        // ------------------------------

        transaksi = transaksiService.save(transaksi);
        session.setAttribute("transaksi", transaksi); // Bawa nota ini ke halaman pembayaran

        return "redirect:/pembayaran";
    }

    // ================= PEMBAYARAN =================

    @GetMapping("/pembayaran")
    public String pembayaranPage(Model model, HttpSession session) {
        Transaksi transaksi = (Transaksi) session.getAttribute("transaksi");

        if(transaksi == null) {
            return "redirect:/";
        }

        model.addAttribute("transaksi", transaksi);
        return "pembayaran";
    }

    @PostMapping("/pembayaran")
    public String prosesPembayaran(@RequestParam String metode, HttpSession session) {
        // 1. Ambil transaksi dari sesi
        Transaksi transaksi = (Transaksi) session.getAttribute("transaksi");

        // 2. JIKA TERJADI DOUBLE-CLICK, PERINTAH KEDUA AKAN LANGSUNG KENA BLOK DI SINI
        if(transaksi == null) {
            return "redirect:/member-dashboard";
        }

        // --- PINDAHKAN HAPUS SESI KE BARIS ATAS SINI (KUNCI ANTI-DOBEL) ---
        session.removeAttribute("transaksi");
        session.setAttribute("transaksiSelesai", transaksi);
        // ------------------------------------------------------------------

        // 3. Simpan data pembayaran ke database
        Pembayaran pembayaran = new Pembayaran();
        pembayaran.setMetode(metode);
        pembayaran.setJumlahBayar(transaksi.getTotal());
        pembayaran.setTransaksi(transaksi);
        pembayaranService.save(pembayaran);

        // 4. Update poin member
        if(session.getAttribute("user") != null){
            Pelanggan user = (Pelanggan) session.getAttribute("user");
            user = pelangganService.getById(user.getId());
            
            int poinTambahan = transaksi.getJam() * 10; 
            int poinSekarang = user.getPoint();

            // Log CCTV tetap dipertahankan untuk memantau
            System.out.println("=== LAPORAN DEBUG POIN ===");
            System.out.println("Jam Bermain: " + transaksi.getJam());
            System.out.println("Poin Sebelumnya: " + poinSekarang);
            System.out.println("Poin yang Ditambahkan: " + poinTambahan);
            System.out.println("==========================");

            user.setPoint(poinSekarang + poinTambahan);
            pelangganService.save(user); 
            session.setAttribute("user", user); 
        }

        // 5. Langsung redirect (baris hapus session yang di bawah sudah dibuang karena pindah ke atas)
        if(session.getAttribute("user") != null){
            return "redirect:/member-dashboard";
        }
        session.removeAttribute("guestUser");
        return "redirect:/transaksi-selesai";
    }

    // ================= TRANSAKSI SELESAI =================

    @GetMapping("/transaksi-selesai")
    public String transaksiSelesai(HttpSession session, Model model) {
        Transaksi transaksi = (Transaksi) session.getAttribute("transaksiSelesai");

        if(transaksi == null){
            return "redirect:/";
        }

        model.addAttribute("transaksi", transaksi);
        return "transaksi-selesai";
    }

    // 1. Menampilkan Halaman Tukar Poin
    @GetMapping("/tukar-poin")
    public String halamanTukarPoin(Model model, HttpSession session) {
        // Pastikan yang akses hanya member yang sudah login
        if(session.getAttribute("user") == null){
            return "redirect:/";
        }
        
        Pelanggan user = (Pelanggan) session.getAttribute("user");
        // Ambil data terbaru dari database biar poinnya akurat
        user = pelangganService.getById(user.getId()); 
        
        model.addAttribute("user", user);
        return "tukar-poin"; // Kita akan buat file tukar-poin.html setelah ini
    }

    // 2. Memproses Penukaran Poin
    @PostMapping("/proses-tukar-poin")
    public String prosesTukarPoin(@RequestParam String hadiah, @RequestParam int hargaPoin, HttpSession session, Model model) {
        if(session.getAttribute("user") == null){
            return "redirect:/";
        }

        Pelanggan user = (Pelanggan) session.getAttribute("user");
        user = pelangganService.getById(user.getId()); // Update data terbaru

        // Cek apakah poinnya cukup?
        if (user.getPoint() >= hargaPoin) {
            // Poin cukup -> Kurangi poin
            user.setPoint(user.getPoint() - hargaPoin);
            pelangganService.save(user); // Simpan sisa poin ke database
            
            session.setAttribute("user", user); // Update sesi
            
            // Catatan: Di sini kamu juga bisa nge-save ke tabel 'RiwayatPenukaran' kalau ada
            
            return "redirect:/member-dashboard?sukses=tukar"; 
        } else {
            // Poin kurang -> Kembalikan ke halaman tukar poin dengan pesan error
            model.addAttribute("error", "Maaf, poin kamu tidak cukup untuk menukar " + hadiah);
            model.addAttribute("user", user);
            return "tukar-poin";
        }
    }

}