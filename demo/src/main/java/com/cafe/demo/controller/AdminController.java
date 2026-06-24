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
    public String adminPage(HttpSession session) {

        Boolean login =
                (Boolean) session.getAttribute("adminLogin");

        if(login == null || !login){
            return "redirect:/login";
        }

        return "admin";
    }

    // ================= PELANGGAN =================

    @GetMapping("/pelanggan-page")
    public String pelangganPage(Model model){

        model.addAttribute(
                "pelanggan",
                new Pelanggan());

        model.addAttribute(
                "pelangganList",
                pelangganService.getAll());

        return "pelanggan";
    }

    @PostMapping("/save-pelanggan")
    public String savePelanggan(
            Pelanggan pelanggan){

        pelangganService.save(pelanggan);

        return "redirect:/pelanggan-page";
    }

    @GetMapping("/edit-pelanggan/{id}")
    public String editPelanggan(
            @PathVariable Long id,
            Model model){

        model.addAttribute(
                "pelanggan",
                pelangganService.getById(id));

        model.addAttribute(
                "pelangganList",
                pelangganService.getAll());

        return "pelanggan";
    }

    @GetMapping("/delete-pelanggan/{id}")
    public String deletePelanggan(
            @PathVariable Long id){

        pelangganService.delete(id);

        return "redirect:/pelanggan-page";
    }

    // ================= KOMPUTER =================

    @GetMapping("/komputer-page")
    public String komputerPage(Model model){

        model.addAttribute(
                "komputer",
                new Komputer());

        model.addAttribute(
                "komputerList",
                komputerService.getAll());

        return "komputer";
    }

    @PostMapping("/save-komputer")
    public String saveKomputer(
            Komputer komputer){

        komputerService.save(komputer);

        return "redirect:/komputer-page";
    }

    @GetMapping("/edit-komputer/{id}")
    public String editKomputer(
            @PathVariable Long id,
            Model model){

        model.addAttribute(
                "komputer",
                komputerService.getById(id));

        model.addAttribute(
                "komputerList",
                komputerService.getAll());

        return "komputer";
    }

    @GetMapping("/delete-komputer/{id}")
    public String deleteKomputer(
            @PathVariable Long id){

        komputerService.delete(id);

        return "redirect:/komputer-page";
    }

    // ================= TRANSAKSI =================

    @GetMapping("/transaksi-page")
public String transaksiPage(Model model){

    model.addAttribute(
            "transaksi",
            new com.cafe.demo.model.Transaksi());

    model.addAttribute(
            "pelangganList",
            pelangganService.getAll());

    model.addAttribute(
            "komputerList",
            komputerService.getAll());

    model.addAttribute(
            "transaksiList",
            transaksiService.getAll());

    return "transaksi";
}

    // ================= PEMBAYARAN =================

    @GetMapping("/pembayaran-page")
    public String pembayaranPage(
            Model model){

        model.addAttribute(
                "pembayaranList",
                pembayaranService.getAll());

        return "pembayaran-admin";
    }

    
}