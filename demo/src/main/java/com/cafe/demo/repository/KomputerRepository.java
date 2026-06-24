package com.cafe.demo.repository;

import com.cafe.demo.model.Komputer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KomputerRepository extends JpaRepository<Komputer, Long> {

    List<Komputer> findByStatus(String status);

}