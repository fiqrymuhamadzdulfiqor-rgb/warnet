package com.cafe.demo.controller;

import com.cafe.demo.model.Komputer;
import com.cafe.demo.service.KomputerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/komputer")
public class KomputerController {

    @Autowired
    private KomputerService service;

    @GetMapping
    public List<Komputer> getAll() {
        return service.getAll();
    }

    @PostMapping
    public Komputer create(@RequestBody Komputer komputer) {
        return service.save(komputer);
    }
}