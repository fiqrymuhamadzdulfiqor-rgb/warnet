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

// ================= LOGIN ADMIN =================

@GetMapping("/login")
public String loginPage() {
    return "login";
}

@PostMapping("/login")
public String loginAdmin(
        @RequestParam String username,
        @RequestParam String password,
        HttpSession session) {

    if(username.equals("admin")
            && password.equals("123")) {

        session.setAttribute("adminLogin", true);

        return "redirect:/admin";
    }

    return "redirect:/login?error";
}

// ================= LOGOUT =================

@GetMapping("/logout")
public String logout(HttpSession session) {

    session.removeAttribute("user");
    session.removeAttribute("guestUser");
    session.removeAttribute("transaksi");
    session.removeAttribute("transaksiSelesai");

    return "redirect:/";
}

// ================= RESERVASI UMUM =================

@GetMapping("/reservasi")
public String reservasiPage(Model model) {

    model.addAttribute("pelanggan",
            new Pelanggan());

    return "reservasi";
}

@PostMapping("/reservasi")
public String reservasi(
        Pelanggan pelanggan,
        HttpSession session) {

    pelanggan.setStatus("UMUM");

    Pelanggan saved =
            pelangganService.save(pelanggan);

    session.setAttribute(
            "guestUser",
            saved
    );

    return "redirect:/pilih-komputer";
}

// ================= LOGIN MEMBER =================

@GetMapping("/login-member")
public String loginMemberPage() {
    return "login-member";
}

@PostMapping("/login-member")
public String loginMember(
        @RequestParam String username,
        @RequestParam String password,
        HttpSession session) {

    Pelanggan user =
            pelangganService.loginMember(
                    username,
                    password);

    if(user != null) {

        session.setAttribute(
                "user",
                user);

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
public String registerMember(
        Pelanggan pelanggan) {

    pelanggan.setStatus("MEMBER");

    pelangganService.save(pelanggan);

    return "redirect:/login-member";
}

// ================= MEMBER DASHBOARD =================

@GetMapping("/member-dashboard")
public String memberDashboard(
        HttpSession session,
        Model model) {

    Pelanggan user =
            (Pelanggan) session.getAttribute("user");

    if(user == null) {
        return "redirect:/login-member";
    }

    model.addAttribute("user", user);

    model.addAttribute(
            "transaksiList",
            transaksiService.getByPelanggan(user)
    );

    return "member-dashboard";
}

// ================= PILIH KOMPUTER =================

@GetMapping("/pilih-komputer")
public String pilihKomputer(
        Model model,
        HttpSession session) {

    if(session.getAttribute("user") == null
            && session.getAttribute("guestUser") == null) {

        return "redirect:/";
    }

    model.addAttribute(
            "komputerList",
            komputerService.getKomputerTersedia()
    );

    return "pilih-komputer";
}

// ================= CHECKOUT =================

@PostMapping("/checkout")
public String checkout(
        @RequestParam Long komputerId,
        @RequestParam int jam,
        HttpSession session) {

    Pelanggan pelanggan =
            (Pelanggan) session.getAttribute("user");

    if(pelanggan == null) {

        pelanggan =
                (Pelanggan) session.getAttribute("guestUser");
    }

    if(pelanggan == null) {
        return "redirect:/";
    }

    Komputer komputer =
            komputerService.getById(komputerId);

    Transaksi transaksi =
            new Transaksi();

    transaksi.setPelanggan(pelanggan);
    transaksi.setKomputer(komputer);
    transaksi.setJam(jam);

    transaksi =
            transaksiService.save(transaksi);

    session.setAttribute(
            "transaksi",
            transaksi
    );

    return "redirect:/pembayaran";
}

// ================= PEMBAYARAN =================

@GetMapping("/pembayaran")
public String pembayaranPage(
        Model model,
        HttpSession session) {

    Transaksi transaksi =
            (Transaksi) session.getAttribute("transaksi");

    if(transaksi == null) {
        return "redirect:/";
    }

    model.addAttribute(
            "transaksi",
            transaksi
    );

    return "pembayaran";
}

@PostMapping("/pembayaran")
public String prosesPembayaran(
        @RequestParam String metode,
        HttpSession session) {

    Transaksi transaksi =
            (Transaksi) session.getAttribute("transaksi");

    if(transaksi == null) {
        return "redirect:/";
    }

    Pembayaran pembayaran =
            new Pembayaran();

    pembayaran.setMetode(metode);

    pembayaran.setJumlahBayar(
            transaksi.getTotal()
    );

    pembayaran.setTransaksi(transaksi);

    pembayaranService.save(pembayaran);

    if(session.getAttribute("user") != null){

    Pelanggan user =
            (Pelanggan) session.getAttribute("user");

    user = pelangganService.getById(
            user.getId());

    session.setAttribute(
            "user",
            user);
        }

    session.setAttribute(
        "transaksiSelesai",
        transaksi
    );

    session.removeAttribute("transaksi");

    if(session.getAttribute("user") != null){

        return "redirect:/member-dashboard";
    }

    session.removeAttribute("guestUser");

    return "redirect:/transaksi-selesai";
}

// ================= TRANSAKSI SELESAI =================

@GetMapping("/transaksi-selesai")
public String transaksiSelesai(
        HttpSession session,
        Model model) {

    Transaksi transaksi =
            (Transaksi) session.getAttribute(
                    "transaksiSelesai"
            );

    if(transaksi == null){
        return "redirect:/";
    }

    model.addAttribute(
            "transaksi",
            transaksi
    );

    return "transaksi-selesai";
}


}
