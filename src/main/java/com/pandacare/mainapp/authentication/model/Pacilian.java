package com.pandacare.mainapp.authentication.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "pacilians")
@Setter
@Getter
public class Pacilian extends User {
    private String address;
    private String medicalHistory;

    public Pacilian(String name, String nik, String phoneNumber,
                     String address, String medicalHistory) {
        super(name, nik, phoneNumber);
        this.address = address;
        this.medicalHistory = medicalHistory;
    }

    public Pacilian() {

    }
}