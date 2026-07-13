package com.cafe.demo.repository;

import com.cafe.demo.model.DetailPesananFB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetailPesananFBRepository extends JpaRepository<DetailPesananFB, Long> {
}