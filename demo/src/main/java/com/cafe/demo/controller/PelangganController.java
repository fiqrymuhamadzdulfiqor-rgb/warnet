package com.cafe.demo.controller;

import com.cafe.demo.model.Pelanggan;
import com.cafe.demo.service.PelangganService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pelanggan")
public class PelangganController {

    @Autowired
    private PelangganService service;

    @GetMapping
    public List<Pelanggan> getAll() {
        return service.getAll();
    }

    @PostMapping
    public Pelanggan create(@RequestBody Pelanggan pelanggan) {
        return service.save(pelanggan);
    }
}