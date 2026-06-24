package com.cafe.demo.repository;

import com.cafe.demo.model.Pelanggan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PelangganRepository extends JpaRepository<Pelanggan, Long> {

    Optional<Pelanggan> findByUsernameAndPassword(
            String username,
            String password
    );

    Optional<Pelanggan> findByUsername(
            String username
    );
}