package com.pandacare.mainapp.authentication.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Getter
@Setter
public abstract class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id; // Sync with id from auth service
    private String email;
    private String name;
    private String nik;
    private String phoneNumber;

    protected User() {}

    protected User(String name, String nik, String phoneNumber) {
        this.name = name;
        this.nik = nik;
        this.phoneNumber = phoneNumber;
    }
}