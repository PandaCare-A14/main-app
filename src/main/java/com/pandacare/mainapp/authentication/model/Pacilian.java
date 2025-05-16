package com.pandacare.mainapp.authentication.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "caregivers")
@Setter
@Getter
public class Pacilian extends User {
    private String address;
    private String medicalHistory; // CHECK
}