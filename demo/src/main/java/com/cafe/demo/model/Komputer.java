package com.cafe.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "komputer")
public class Komputer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String namaKomputer;

    // Default otomatis Kosong saat PC baru didaftarkan
    private String status = "Tersedia"; 

    private double tarifPerJam;

    // === TAMBAHAN BARU: Klasifikasi PC ===
    private String grade; // Nanti isinya: Reguler, VIP, VVIP

    public Komputer() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNamaKomputer() {
        return namaKomputer;
    }

    public void setNamaKomputer(String namaKomputer) {
        this.namaKomputer = namaKomputer;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTarifPerJam() {
        return tarifPerJam;
    }

    public void setTarifPerJam(double tarifPerJam) {
        this.tarifPerJam = tarifPerJam;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
}