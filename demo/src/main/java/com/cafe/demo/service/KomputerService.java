package com.cafe.demo.service;

import com.cafe.demo.model.Komputer;
import com.cafe.demo.repository.KomputerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort; // PERHATIKAN: TAMBAHKAN IMPORT INI
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KomputerService {

    @Autowired
    private KomputerRepository komputerRepository;

    public List<Komputer> getAll() {
        // PERUBAHAN DI SINI: Menggunakan Sort.by untuk mengurutkan berdasarkan 'id' secara Ascending (1, 2, 3...)
        return komputerRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        
        // Alternatif jika ingin urut berdasarkan abjad nama komputernya:
        // return komputerRepository.findAll(Sort.by(Sort.Direction.ASC, "namaKomputer"));
    }

    public List<Komputer> getKomputerTersedia() {
        // Panggil fungsi baru yang sudah dilengkapi sorting
        return komputerRepository.findByStatusOrderByNamaKomputerAsc("Tersedia");
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