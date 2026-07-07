package com.cafe.demo.repository;

import com.cafe.demo.model.MenuFB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuFBRepository extends JpaRepository<MenuFB, Long> {
}