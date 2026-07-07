package com.cafe.demo.controller;

import com.cafe.demo.model.Komputer;
import com.cafe.demo.model.Pelanggan;
import com.cafe.demo.service.KomputerService;
import com.cafe.demo.service.PelangganService;
import com.cafe.demo.service.PembayaranService;
import com.cafe.demo.service.TransaksiService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AdminController {

    @Autowired
    private PelangganService pelangganService;

    @Autowired
    private KomputerService komputerService;

    @Autowired
    private TransaksiService transaksiService;

    @Autowired
    private PembayaranService pembayaranService;

    // ================= ADMIN DASHBOARD =================
    @GetMapping("/admin")
    public String adminPage(HttpSession session, Model model) {
        Boolean login = (Boolean) session.getAttribute("adminLogin");
        if (login == null || !login) {
            return "redirect:/login";
        }

        // Hitung data real-time untuk Dashboard
        double totalPendapatan = transaksiService.getAll().stream()
                .mapToDouble(transaksi -> transaksi.getTotal())
                .sum();

        long pcAktif = komputerService.getAll().stream()
                .filter(k -> "Dipakai".equalsIgnoreCase(k.getStatus()))
                .count();
        int totalPc = komputerService.getAll().size();

        long totalMember = pelangganService.getAll().stream()
                .filter(p -> "MEMBER".equalsIgnoreCase(p.getStatus()))
                .count();

        model.addAttribute("totalPendapatan", totalPendapatan);
        model.addAttribute("pcAktif", pcAktif);
        model.addAttribute("totalPc", totalPc);
        model.addAttribute("totalMember", totalMember);

        return "admin";
    }

    // ================= PELANGGAN =================
    @GetMapping("/pelanggan-page")
    public String pelangganPage(Model model) {
        model.addAttribute("pelangganList", pelangganService.getAll());
        return "pelanggan";
    }

    @GetMapping("/delete-pelanggan/{id}")
    public String deletePelanggan(@PathVariable Long id) {
        pelangganService.delete(id);
        return "redirect:/pelanggan-page";
    }

    // ================= KOMPUTER =================
    @GetMapping("/komputer-page")
    public String komputerPage(Model model) {
        model.addAttribute("komputer", new Komputer());
        model.addAttribute("komputerList", komputerService.getAll());
        return "komputer";
    }

    @PostMapping("/save-komputer")
    public String saveKomputer(@ModelAttribute Komputer komputer) {
        if (komputer.getStatus() == null || komputer.getStatus().isEmpty()) {
            komputer.setStatus("Tersedia");
        }
        komputerService.save(komputer);
        return "redirect:/komputer-page";
    }

    @GetMapping("/edit-komputer/{id}")
    public String editKomputer(@PathVariable Long id, Model model) {
        model.addAttribute("komputer", komputerService.getById(id));
        model.addAttribute("komputerList", komputerService.getAll());
        return "komputer";
    }

    @GetMapping("/delete-komputer/{id}")
    public String deleteKomputer(@PathVariable Long id) {
        komputerService.delete(id);
        return "redirect:/komputer-page";
    }

    // --- FITUR BARU: AKHIRI SESI (STOP BILLING) ---
    @GetMapping("/akhiri-sesi/{id}")
    public String akhiriSesi(@PathVariable Long id) {
        Komputer komputer = komputerService.getById(id);
        if (komputer != null) {
            komputer.setStatus("Tersedia"); // Kembalikan status komputer menjadi kosong/tersedia
            komputerService.save(komputer);
        }
        return "redirect:/komputer-page";
    }

    // ================= TRANSAKSI =================
    @GetMapping("/transaksi-page")
    public String transaksiPage(Model model) {
        model.addAttribute("transaksiList", transaksiService.getAll());
        return "transaksi";
    }
}