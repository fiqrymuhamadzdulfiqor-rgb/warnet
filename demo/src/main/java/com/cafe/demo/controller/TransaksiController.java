package com.cafe.demo.controller;

import com.cafe.demo.model.Transaksi;
import com.cafe.demo.service.TransaksiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transaksi")
public class TransaksiController {

    @Autowired
    private TransaksiService service;

    @GetMapping
    public List<Transaksi> getAll() {
        return service.getAll();
    }

    @PostMapping
    public Transaksi create(@RequestBody Transaksi transaksi) {
        return service.save(transaksi);
    }
}