package com.cafe.demo.repository;

import com.cafe.demo.model.PesananFB;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PesananFBRepository extends JpaRepository<PesananFB, Long> {
    
    List<PesananFB> findByPelangganIdOrderByTanggalPesananDesc(Long pelangganId);
}