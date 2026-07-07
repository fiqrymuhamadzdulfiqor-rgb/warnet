package com.cafe.demo.controller;

import com.cafe.demo.model.MenuFB;
import com.cafe.demo.model.Pelanggan;
import com.cafe.demo.model.PesananFB;
import com.cafe.demo.service.MenuFBService;
import com.cafe.demo.service.PelangganService;
import com.cafe.demo.service.PesananFBService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class FoodBeverageController {

    @Autowired
    private MenuFBService menuFBService;

    @Autowired
    private PesananFBService pesananFBService;

    @Autowired
    private PelangganService pelangganService;

    // ================= HALAMAN PESAN MAKANAN (SIAPA PUN BISA AKSES) =================
    @GetMapping("/pesan-makan")
    public String pesanMakanPage(HttpSession session, Model model) {
        Pelanggan member = (Pelanggan) session.getAttribute("user");
        Pelanggan umum = (Pelanggan) session.getAttribute("guestUser");

        if (member != null) {
            model.addAttribute("currentUser", pelangganService.getById(member.getId()));
        } else if (umum != null) {
            model.addAttribute("currentUser", umum);
        } else {
            // JIKA BELUM LOGIN/RESERVASI (Pelanggan Umum Bebas dari Home)
            Pelanggan anonim = new Pelanggan();
            anonim.setStatus("UMUM");
            anonim.setNama("Guest"); 
            model.addAttribute("currentUser", anonim);
        }

        model.addAttribute("daftarMenu", menuFBService.getAllMenu());
        model.addAttribute("pesanan", new PesananFB());
        return "pesan-makan";
    }

    @PostMapping("/proses-pesan-makan")
    public String prosesPesanMakan(
            @ModelAttribute PesananFB pesanan,
            @RequestParam Long menuId,
            @RequestParam(required = false) String namaManual, // Tangkap input Nama/No PC jika umum
            HttpSession session,
            Model model) {

        Pelanggan member = (Pelanggan) session.getAttribute("user");
        Pelanggan umum = (Pelanggan) session.getAttribute("guestUser");
        
        Pelanggan pembeli = null;

        if (member != null) {
            pembeli = pelangganService.getById(member.getId());
        } else if (umum != null) {
            pembeli = umum;
        } else {
            // JIKA UMUM TANPA SESSION: Otomatis daftarkan data UMUM baru sesuai nomor PC/Nama yang diinput
            Pelanggan umumBaru = new Pelanggan();
            umumBaru.setNama(namaManual != null && !namaManual.isEmpty() ? namaManual : "Umum (PC Anonim)");
            umumBaru.setStatus("UMUM");
            pembeli = pelangganService.save(umumBaru); // Simpan ke DB agar punya ID relasi
        }

        MenuFB menu = menuFBService.getById(menuId);
        pesanan.setMenuFb(menu);
        pesanan.setTotalHargaRupiah(menu.getHargaRupiah() * pesanan.getJumlah());
        pesanan.setTotalPoinDigunakan(menu.getHargaPoin() * pesanan.getJumlah());

        try {
            pesananFBService.prosesPesanan(pesanan, pembeli);
            
            if (member != null) {
                session.setAttribute("user", pelangganService.getById(member.getId()));
                return "redirect:/member-dashboard";
            }
            
            // Jika umum, lempar ke halaman depan dengan status sukses jajan
            return "redirect:/?suksesMakan"; 
            
        } catch (RuntimeException e) {
            model.addAttribute("currentUser", pembeli);
            model.addAttribute("daftarMenu", menuFBService.getAllMenu());
            model.addAttribute("errorPoin", e.getMessage());
            return "pesan-makan";
        }
    }

    // ================= KELOLA MENU F&B (ADMIN) =================
    @GetMapping("/admin/kelola-menu")
    public String kelolaMenuPage(HttpSession session, Model model) {
        Boolean login = (Boolean) session.getAttribute("adminLogin");
        if (login == null || !login) {
            return "redirect:/login";
        }

        model.addAttribute("menuBaru", new MenuFB());
        model.addAttribute("daftarMenu", menuFBService.getAllMenu());
        return "admin-menu-fb";
    }

    @PostMapping("/admin/save-menu")
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
        if (login == null || !login) {
            return "redirect:/login";
        }

        // Kirim semua data pesanan ke halaman admin
        model.addAttribute("daftarPesanan", pesananFBService.getAll());
        return "admin-pesanan-fb";
    }

    @GetMapping("/admin/selesaikan-pesanan-fb/{id}")
    public String selesaikanPesananFB(@PathVariable Long id, HttpSession session) {
        Boolean login = (Boolean) session.getAttribute("adminLogin");
        if (login == null || !login) {
            return "redirect:/login";
        }

        PesananFB pesanan = pesananFBService.getById(id);
        if(pesanan != null) {
            pesanan.setStatusPesanan("Selesai"); // Ubah status pesanan
            pesananFBService.save(pesanan);
        }
        
        return "redirect:/admin/pesanan-fb";
    }
}