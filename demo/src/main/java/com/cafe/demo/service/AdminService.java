package com.cafe.demo.service;

import com.cafe.demo.model.Admin;
import com.cafe.demo.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    public Admin login(String username, String password) {
        return adminRepository.findByUsernameAndPassword(
                username,
                password
        );
    }

    public Admin save(Admin admin) {
        return adminRepository.save(admin);
    }
}