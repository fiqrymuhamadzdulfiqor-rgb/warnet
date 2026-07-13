package com.cafe.demo.repository;

import com.cafe.demo.model.Komputer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface KomputerRepository extends JpaRepository<Komputer, Long> {
    
    // Perintah baru: Cari berdasarkan status, lalu urutkan nama komputernya (Ascending/A-Z)
    List<Komputer> findByStatusOrderByNamaKomputerAsc(String status);
}