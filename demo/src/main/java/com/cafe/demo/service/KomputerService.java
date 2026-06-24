package com.cafe.demo.service;

import com.cafe.demo.model.Komputer;
import com.cafe.demo.repository.KomputerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KomputerService {

    @Autowired
    private KomputerRepository komputerRepository;

    public List<Komputer> getAll() {
        return komputerRepository.findAll();
    }

    public List<Komputer> getKomputerTersedia() {
        return komputerRepository.findByStatus("Tersedia");
    }

    public Komputer getById(Long id) {
        return komputerRepository.findById(id).orElse(null);
    }

    public Komputer save(Komputer komputer) {
        return komputerRepository.save(komputer);
    }

    public void delete(Long id) {
        komputerRepository.deleteById(id);
    }
}