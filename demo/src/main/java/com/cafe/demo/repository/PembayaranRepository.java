package com.cafe.demo.repository;

import com.cafe.demo.model.Pembayaran;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PembayaranRepository extends JpaRepository<Pembayaran, Long> {
}