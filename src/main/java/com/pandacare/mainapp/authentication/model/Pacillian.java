package com.pandacare.mainapp.authentication.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "pacillians")
@Setter
@Getter
public class Pacillian extends User {
    private String address;
    private String medicalHistory; // CHECK

    public Pacillian() {}

    public Pacillian(String name, String nik, String phoneNumber,
                     String address, String medicalHistory) {
        super(name, nik, phoneNumber);
        this.address = address;
        this.medicalHistory = medicalHistory;
    }
}