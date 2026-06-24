package com.cafe.demo.service;

import com.cafe.demo.model.Pelanggan;
import com.cafe.demo.repository.PelangganRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PelangganService {

    @Autowired
    private PelangganRepository pelangganRepository;

    public List<Pelanggan> getAll() {
        return pelangganRepository.findAll();
    }

    public Pelanggan getById(Long id) {
        return pelangganRepository.findById(id).orElse(null);
    }

    public Pelanggan save(Pelanggan pelanggan) {
        return pelangganRepository.save(pelanggan);
    }

    public void delete(Long id) {
        pelangganRepository.deleteById(id);
    }

    public Pelanggan loginMember(String username, String password) {

        return pelangganRepository
                .findByUsernameAndPassword(username, password)
                .orElse(null);
    }
}