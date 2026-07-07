package com.cafe.demo.repository;

import com.cafe.demo.model.PesananFB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PesananFBRepository extends JpaRepository<PesananFB, Long> {
}