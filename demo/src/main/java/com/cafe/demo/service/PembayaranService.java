package com.cafe.demo.service;

import com.cafe.demo.model.Pembayaran;
import com.cafe.demo.repository.PembayaranRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PembayaranService {

    @Autowired
    private PembayaranRepository repository;

    public List<Pembayaran> getAll() {
        return repository.findAll();
    }

    public Pembayaran save(Pembayaran pembayaran) {
        return repository.save(pembayaran);
    }
}