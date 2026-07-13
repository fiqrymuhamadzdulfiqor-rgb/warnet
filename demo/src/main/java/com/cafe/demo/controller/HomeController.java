package com.cafe.demo.controller;

import com.cafe.demo.model.Komputer;
import com.cafe.demo.model.Pelanggan;
import com.cafe.demo.model.Reservasi;
import com.cafe.demo.service.KomputerService;
import com.cafe.demo.service.PelangganService;
import com.cafe.demo.service.ReservasiService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    private PelangganService pelangganService;

    @Autowired
    private KomputerService komputerService;

    // KITA SEKARANG MENGGUNAKAN RESERVASI SERVICE YANG BARU
    @Autowired
    private ReservasiService reservasiService;

    // ================= HOME & FASILITAS =================

    @GetMapping("/")
    public String home() { return "index"; }

    @GetMapping("/fasilitas")
    public String fasilitasPage() { return "fasilitas"; }

    // ================= LOGIN & LOGOUT =================

    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        if (session.getAttribute("adminLogin") != null && (Boolean) session.getAttribute("adminLogin")) {
            return "redirect:/admin";
        }
        return "login";
    }

    @PostMapping("/login")
    public String loginAdmin(@RequestParam String username, @RequestParam String password, HttpSession session) {
        if(username.equals("admin") && password.equals("123")) {
            session.setAttribute("adminLogin", true);
            return "redirect:/admin";
        }
        return "redirect:/login?error";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); 
        return "redirect:/";
    }

    // ================= RESERVASI & DURASI (MEMBER & UMUM) =================

    @GetMapping("/reservasi")
    public String reservasiPage() { return "reservasi"; }

    @PostMapping("/proses-reservasi")
    public String prosesReservasi(
            @RequestParam String nama,
            @RequestParam(defaultValue = "Reguler") String jenisPaket, // TAMBAHAN FITUR PAKET
            @RequestParam(defaultValue = "1") int durasi,
            HttpSession session) {
        
        Pelanggan pembeli = (Pelanggan) session.getAttribute("user");
        
        if (pembeli == null) {
            Pelanggan guest = new Pelanggan();
            guest.setNama(nama);
            guest.setStatus("UMUM");
            pembeli = pelangganService.save(guest); 
            session.setAttribute("guestUser", pembeli); 
        }

        session.setAttribute("jenisPaket", jenisPaket);
        session.setAttribute("durasiMain", durasi);
        return "redirect:/pilih-komputer";
    }

    @GetMapping("/atur-durasi")
    public String aturDurasiPage(HttpSession session, Model model) {
        if(session.getAttribute("user") == null) return "redirect:/login-member";
        model.addAttribute("user", session.getAttribute("user"));
        return "atur-durasi";
    }

    @PostMapping("/proses-durasi")
    public String prosesDurasiMember(
            @RequestParam(defaultValue = "Reguler") String jenisPaket,
            @RequestParam(defaultValue = "1") int durasi, 
            HttpSession session) {
            
        session.setAttribute("jenisPaket", jenisPaket);
        session.setAttribute("durasiMain", durasi);
        return "redirect:/pilih-komputer";
    }

    // ================= MEMBER DASHBOARD =================

    @GetMapping("/login-member")
    public String loginMemberPage(HttpSession session) {
        if (session.getAttribute("user") != null) return "redirect:/member-dashboard"; 
        return "login-member";
    }

    @PostMapping("/login-member")
    public String loginMember(@RequestParam String username, @RequestParam String password, HttpSession session) {
        Pelanggan user = pelangganService.loginMember(username, password);
        if(user != null) {
            session.setAttribute("user", user);
            return "redirect:/member-dashboard";
        }
        return "redirect:/login-member?error";
    }

    @GetMapping("/register-member")
    public String registerMemberPage() { return "register-member"; }

    @PostMapping("/register-member")
    public String registerMember(Pelanggan pelanggan) {
        pelanggan.setStatus("MEMBER");
        pelangganService.save(pelanggan);
        return "redirect:/login-member";
    }

    @GetMapping("/member-dashboard")
    public String memberDashboard(HttpSession session, Model model) {
        // 1. Ambil data dari sesi (gunakan nama variabel berbeda agar aman)
        Pelanggan userSession = (Pelanggan) session.getAttribute("user");
        if(userSession == null) return "redirect:/login-member";

        // 2. Ambil data terbaru dari DB dan simpan di variabel user
        Pelanggan user = pelangganService.getById(userSession.getId());
        model.addAttribute("user", user);

        // 3. PERBAIKAN BARIS 145 & 150: Simpan ID ke dalam variabel final agar Java tidak protes
        final Long userId = user.getId(); 

        // 4. Ambil riwayat billing PC (MENGGUNAKAN RESERVASI)
        List<Reservasi> riwayatReservasi = reservasiService.getAll().stream()
                .filter(r -> r.getPelanggan() != null && r.getPelanggan().getId().equals(userId))
                .collect(Collectors.toList());
        
        // Tetap menggunakan nama variabel transaksiList agar HTML lamamu tidak error
        model.addAttribute("transaksiList", riwayatReservasi); 

        Reservasi transaksiAktif = riwayatReservasi.stream()
                .filter(r -> "Aktif".equalsIgnoreCase(r.getStatusBermain()))
                .findFirst().orElse(null);
        model.addAttribute("transaksiAktif", transaksiAktif);

        return "member-dashboard";
    }

    // ================= ALUR CHECKOUT & PEMBAYARAN =================

    @GetMapping("/pilih-komputer")
    public String pilihKomputer(Model model, HttpSession session) {
        if(session.getAttribute("user") == null && session.getAttribute("guestUser") == null) {
            return "redirect:/";
        }

        model.addAttribute("komputerList", komputerService.getKomputerTersedia()); 
        
        return "pilih-komputer";
    }

    @GetMapping("/simpan-komputer/{id}")
    public String simpanKomputer(@PathVariable Long id, HttpSession session) {
        Pelanggan pelanggan = (Pelanggan) session.getAttribute("user");
        if(pelanggan == null) pelanggan = (Pelanggan) session.getAttribute("guestUser");
        if(pelanggan == null) return "redirect:/";

        String jenisPaket = (String) session.getAttribute("jenisPaket");
        if (jenisPaket == null) jenisPaket = "Reguler";
        
        Integer durasi = (Integer) session.getAttribute("durasiMain");
        if (durasi == null) durasi = 1;

        Komputer komputer = komputerService.getById(id);
        
        // BUAT NOTA RESERVASI BARU
        Reservasi reservasi = new Reservasi();
        reservasi.setPelanggan(pelanggan);
        if ("UMUM".equals(pelanggan.getStatus())) {
            reservasi.setNamaPemesanTamu(pelanggan.getNama());
        }
        reservasi.setKomputer(komputer);
        reservasi.setJenisPaket(jenisPaket);
        reservasi.setDurasiJam(durasi);
        reservasi.setWaktuMulai(LocalDateTime.now());
        reservasi.setStatusBermain("Pending"); // Belum dibayar

        // --- LOGIKA HARGA CERDAS (PAKET vs REGULER BERDASARKAN GRADE) ---
        double totalHarga = 0;
        
        // 1. Cek dulu apa Grade komputernya untuk menentukan biaya tambahan
        double biayaTambahanGrade = 0;
        if ("VIP".equalsIgnoreCase(komputer.getGrade())) {
            biayaTambahanGrade = 10000; // Ekstra 10rb untuk VIP
        } else if ("VVIP".equalsIgnoreCase(komputer.getGrade())) {
            biayaTambahanGrade = 20000; // Ekstra 20rb untuk VVIP
        }
        
        // 2. Hitung total harga sesuai jenis paket + biaya grade
        if ("Paket Malam".equalsIgnoreCase(jenisPaket)) {
            totalHarga = 30000 + biayaTambahanGrade; 
        } else if ("Paket Pagi".equalsIgnoreCase(jenisPaket)) {
            totalHarga = 20000 + biayaTambahanGrade;
        } else {
            // Jika Reguler, Tarif Per Jam dikali Durasi (Sudah otomatis menyesuaikan tarif PC masing-masing)
            totalHarga = komputer.getTarifPerJam() * durasi; 
        }
        reservasi.setTotalHarga(totalHarga);
        // --------------------------------------------------------------

        if ("UMUM".equals(pelanggan.getStatus())) {
            // Membuat kode acak 6 karakter (Kombinasi huruf dan angka)
           String generateKode = java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
            reservasi.setKodeAkses(generateKode);
        } else {
            // Jika member, suruh login pakai akunnya
            reservasi.setKodeAkses("GUNAKAN AKUN MEMBER");
        }

        session.setAttribute("reservasiPending", reservasi);
        return "redirect:/pembayaran";
    }

    @GetMapping("/pembayaran")
    public String pembayaranPage(Model model, HttpSession session) {
        Reservasi reservasi = (Reservasi) session.getAttribute("reservasiPending");
        if(reservasi == null) return "redirect:/";
        
        // Tetap pakai nama variabel transaksi agar HTML-mu tidak error
        model.addAttribute("transaksi", reservasi);
        return "pembayaran";
    }

    @PostMapping("/pembayaran")
    public String prosesPembayaran(HttpSession session) {
        Reservasi reservasi = (Reservasi) session.getAttribute("reservasiPending");
        if(reservasi == null) return "redirect:/member-dashboard";

        session.removeAttribute("reservasiPending");

        // 1. Ubah status PC menjadi Dipakai
        Komputer pc = reservasi.getKomputer();
        pc.setStatus("Dipakai");
        komputerService.save(pc);

        // 2. Aktifkan Billing
        reservasi.setStatusBermain("Aktif");

        // ==============================================================
        // FITUR BARU: POIN LANGSUNG CAIR SAAT PEMBAYARAN SUKSES!
        // ==============================================================
        Pelanggan userSession = (Pelanggan) session.getAttribute("user");
        if (userSession != null && !reservasi.isPoinSudahDiklaim()) {
            // Ambil data user terbaru dari database
            Pelanggan user = pelangganService.getById(userSession.getId());
            
            // Tentukan Poin Dasar berdasarkan Grade PC
            int poinBase = 5; // Default Reguler
            if ("VVIP".equalsIgnoreCase(pc.getGrade())) {
                poinBase = 10;
            } else if ("VIP".equalsIgnoreCase(pc.getGrade())) {
                poinBase = 8;
            }
            
            // Total Poin = Poin Grade PC dikali Durasi Main
            int totalPoinDidapat = poinBase * reservasi.getDurasiJam();
            
            // Suntikkan poinnya ke akun member
            user.setPoint(user.getPoint() + totalPoinDidapat);
            pelangganService.save(user);
            
            // Update session agar poin di layar (dashboard) langsung berubah
            session.setAttribute("user", user); 
            
            // KUNCI: Tandai poin sudah diklaim agar tidak dobel saat Admin klik "Stop"
            reservasi.setPoinSudahDiklaim(true);
        }
        // ==============================================================

        reservasiService.save(reservasi);
        session.setAttribute("reservasiSelesai", reservasi);

        if(session.getAttribute("user") != null){
            return "redirect:/member-dashboard";
        }
        session.removeAttribute("guestUser");
        return "redirect:/transaksi-selesai";
    }

    @GetMapping("/transaksi-selesai")
    public String transaksiSelesai(HttpSession session, Model model) {
        Reservasi reservasi = (Reservasi) session.getAttribute("reservasiSelesai");
        if(reservasi == null) return "redirect:/";
        
        model.addAttribute("transaksi", reservasi); // Tetap pakai variabel transaksi
        return "transaksi-selesai";
    }

    // ================= TUKAR POIN =================
    @GetMapping("/tukar-poin")
    public String halamanTukarPoin(Model model, HttpSession session) {
        if(session.getAttribute("user") == null) return "redirect:/";
        Pelanggan user = pelangganService.getById(((Pelanggan) session.getAttribute("user")).getId()); 
        model.addAttribute("user", user);
        return "tukar-poin";
    }

    @PostMapping("/proses-tukar-poin")
    public String prosesTukarPoin(@RequestParam String hadiah, @RequestParam int hargaPoin, HttpSession session, Model model) {
        if(session.getAttribute("user") == null) return "redirect:/";
        Pelanggan user = pelangganService.getById(((Pelanggan) session.getAttribute("user")).getId());

        if (user.getPoint() >= hargaPoin) {
            user.setPoint(user.getPoint() - hargaPoin);
            pelangganService.save(user); 
            session.setAttribute("user", user); 
            return "redirect:/member-dashboard?sukses=tukar"; 
        } else {
            model.addAttribute("error", "Maaf, poin kamu tidak cukup!");
            model.addAttribute("user", user);
            return "tukar-poin";
        }
    }
}