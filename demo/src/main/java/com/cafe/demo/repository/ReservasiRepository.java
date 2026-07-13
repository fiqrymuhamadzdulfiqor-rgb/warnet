package com.cafe.demo.repository;

import com.cafe.demo.model.Reservasi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservasiRepository extends JpaRepository<Reservasi, Long> {
    // Mencari komputer yang sedang dipakai
    List<Reservasi> findByStatusBermain(String statusBermain);
}