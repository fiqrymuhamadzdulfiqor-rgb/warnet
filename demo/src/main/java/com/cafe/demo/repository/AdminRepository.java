package com.cafe.demo.repository;

import com.cafe.demo.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    Admin findByUsernameAndPassword(
            String username,
            String password
    );
}