package com.cafe.demo.repository;

import com.cafe.demo.model.Pelanggan;
import com.cafe.demo.model.Transaksi;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransaksiRepository extends JpaRepository<Transaksi, Long> {

    List<Transaksi> findByPelanggan(
            Pelanggan pelanggan
    );

}