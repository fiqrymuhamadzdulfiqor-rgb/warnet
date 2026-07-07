package com.cafe.demo.service;

import com.cafe.demo.model.MenuFB;
import com.cafe.demo.repository.MenuFBRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuFBService {

    @Autowired
    private MenuFBRepository menuFBRepository;

    public List<MenuFB> getAllMenu() {
        return menuFBRepository.findAll();
    }

    public MenuFB getById(Long id) {
        return menuFBRepository.findById(id).orElse(null);
    }

    public MenuFB save(MenuFB menu) {
        return menuFBRepository.save(menu);
    }

    public void delete(Long id) {
        menuFBRepository.deleteById(id);
    }
}