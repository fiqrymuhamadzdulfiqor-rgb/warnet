package com.cafe.demo.controller;

import com.cafe.demo.model.MenuFB;
import com.cafe.demo.model.Pelanggan;
import com.cafe.demo.model.PesananFB;
import com.cafe.demo.model.DetailPesananFB;
import com.cafe.demo.service.MenuFBService;
import com.cafe.demo.service.PelangganService;
import com.cafe.demo.service.PesananFBService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class FoodBeverageController {

    @Autowired
    private MenuFBService menuFBService;

    @Autowired
    private PesananFBService pesananFBService;

    @Autowired
    private PelangganService pelangganService;

    // ================= HALAMAN PESAN MAKANAN =================
    @GetMapping("/pesan-makan")
    public String pesanMakanPage(HttpSession session, Model model) {
        Pelanggan member = (Pelanggan) session.getAttribute("user");
        Pelanggan umum = (Pelanggan) session.getAttribute("guestUser");

        if (member != null) {
            model.addAttribute("currentUser", pelangganService.getById(member.getId()));
        } else if (umum != null) {
            model.addAttribute("currentUser", umum);
        } else {
            Pelanggan anonim = new Pelanggan();
            anonim.setStatus("UMUM");
            anonim.setNama("Guest"); 
            model.addAttribute("currentUser", anonim);
        }

        model.addAttribute("daftarMenu", menuFBService.getAllMenu());
        return "pesan-makan";
    }

    // ================= PROSES SIMPAN KERANJANG =================
    @PostMapping("/proses-pesan-makan")
    public String prosesPesanMakan(
            @RequestParam(required = false) List<Long> menuIds,
            @RequestParam(required = false) List<Integer> jumlahs,
            @RequestParam String metodePembayaran,
            @RequestParam(required = false) String namaManual,
            HttpSession session,
            Model model) {

        Pelanggan member = (Pelanggan) session.getAttribute("user");
        Pelanggan umum = (Pelanggan) session.getAttribute("guestUser");
        
        Pelanggan pembeliUntukView = new Pelanggan();
        PesananFB strukBaru = new PesananFB();
        strukBaru.setMetodePembayaran(metodePembayaran);
        strukBaru.setStatusPesanan("Diproses");

        // CEK SIAPA YANG BELI 
        if (member != null) {
            pembeliUntukView = pelangganService.getById(member.getId());
            strukBaru.setPelanggan(pembeliUntukView);
        } else if (umum != null) {
            pembeliUntukView = umum;
            strukBaru.setPelanggan(umum);
        } else {
            strukBaru.setNamaPemesanTamu(namaManual != null && !namaManual.isEmpty() ? namaManual : "Umum (PC Anonim)");
            pembeliUntukView.setStatus("UMUM");
            pembeliUntukView.setNama("Guest");
        }

        // PROSES ISI KERANJANG
        double totalHarga = 0;
        int totalPoin = 0;
        int totalItemDibeli = 0;

        if (menuIds != null && jumlahs != null) {
            for (int i = 0; i < menuIds.size(); i++) {
                MenuFB menu = menuFBService.getById(menuIds.get(i));
                int qty = jumlahs.get(i);

                if (menu != null && qty > 0) {
                    DetailPesananFB detail = new DetailPesananFB();
                    detail.setMenuFb(menu);
                    detail.setJumlah(qty);
                    detail.setSubTotalRupiah(menu.getHargaRupiah() * qty);
                    detail.setSubTotalPoin(menu.getHargaPoin() * qty);

                    strukBaru.tambahDetail(detail);

                    totalHarga += detail.getSubTotalRupiah();
                    totalPoin += detail.getSubTotalPoin();
                    totalItemDibeli += qty; 
                }
            }
        }

        // ========================================================
        // 🛡️ 3 LAPIS VALIDASI KEAMANAN
        // ========================================================
        if (totalItemDibeli == 0) {
            model.addAttribute("errorPoin", "Keranjang masih kosong! Silakan pilih makanan minimal 1.");
            model.addAttribute("daftarMenu", menuFBService.getAllMenu());
            model.addAttribute("currentUser", pembeliUntukView);
            return "pesan-makan";
        }

        if ("Tukar Poin".equalsIgnoreCase(metodePembayaran) && member == null) {
            model.addAttribute("errorPoin", "Maaf, fitur Tukar Poin khusus untuk Member Aktif!");
            model.addAttribute("daftarMenu", menuFBService.getAllMenu());
            model.addAttribute("currentUser", pembeliUntukView);
            return "pesan-makan";
        }

        if ("Tukar Poin".equalsIgnoreCase(metodePembayaran) && member != null) {
            if (pembeliUntukView.getPoint() < totalPoin) {
                model.addAttribute("errorPoin", "Poin Anda tidak cukup! Total butuh: " + totalPoin + " PTS.");
                model.addAttribute("daftarMenu", menuFBService.getAllMenu());
                model.addAttribute("currentUser", pembeliUntukView);
                return "pesan-makan";
            } 
        }
        // ========================================================

        strukBaru.setTotalHargaRupiah(totalHarga);
        strukBaru.setTotalPoinDigunakan(totalPoin);

        try {
            pesananFBService.prosesPesanan(strukBaru);
            
            // 🚀 LOGIKA REDIRECT YANG BENAR
            if (member != null) {
                return "redirect:/member-dashboard"; // Member ke dashboard
            }
            return "redirect:/nota-makanan/" + strukBaru.getId(); // Tamu Umum ke halaman Nota
            
        } catch (Exception e) { 
            System.out.println("=========== ERROR DATABASE KASIR ===========");
            e.printStackTrace();
            System.out.println("============================================");
            
            model.addAttribute("errorPoin", "Sistem Error: " + e.getMessage());
            model.addAttribute("daftarMenu", menuFBService.getAllMenu());
            model.addAttribute("currentUser", pembeliUntukView);
            return "pesan-makan";
        }
    }

    // ================= HALAMAN NOTA (BARU) =================
    @GetMapping("/nota-makanan/{id}")
    public String halamanNotaMakanan(@PathVariable Long id, Model model) {
        PesananFB pesanan = pesananFBService.getById(id);
        if (pesanan == null) {
            return "redirect:/"; // Jika ID tidak ditemukan, kembalikan ke home
        }
        
        model.addAttribute("nota", pesanan);
        return "nota-makanan"; // Memanggil file nota-makanan.html yang sudah kita buat
    }

    // ================= KELOLA MENU F&B (ADMIN) =================
    @GetMapping("/admin/kelola-menu")
    public String kelolaMenuPage(HttpSession session, Model model) {
        Boolean login = (Boolean) session.getAttribute("adminLogin");
        if (login == null || !login) return "redirect:/login";

        model.addAttribute("menuBaru", new MenuFB());
        model.addAttribute("daftarMenu", menuFBService.getAllMenu());
        return "admin-menu-fb";
    }

    @PostMapping("/admin/simpan-menu-fb")
    public String saveMenuFB(@ModelAttribute MenuFB menu) {
        menuFBService.save(menu);
        return "redirect:/admin/kelola-menu"; 
    }

    @GetMapping("/admin/delete-menu/{id}")
    public String deleteMenuFB(@PathVariable Long id) {
        menuFBService.delete(id);
        return "redirect:/admin/kelola-menu";
    }

    // ================= KELOLA PESANAN F&B (ADMIN) =================
    @GetMapping("/admin/pesanan-fb")
    public String daftarPesananFB(HttpSession session, Model model) {
        Boolean login = (Boolean) session.getAttribute("adminLogin");
        if (login == null || !login) return "redirect:/login";

        model.addAttribute("daftarPesanan", pesananFBService.getAll());
        return "admin-pesanan-fb";
    }

    @GetMapping("/admin/selesaikan-pesanan-fb/{id}")
    public String selesaikanPesananFB(@PathVariable Long id, HttpSession session) {
        Boolean login = (Boolean) session.getAttribute("adminLogin");
        if (login == null || !login) return "redirect:/login";

        PesananFB pesanan = pesananFBService.getById(id);
        if(pesanan != null) {
            pesanan.setStatusPesanan("Selesai");
            pesananFBService.save(pesanan);
        }
        return "redirect:/admin/pesanan-fb";
    }
}