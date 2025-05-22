package com.pandacare.mainapp.authentication.model;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@MappedSuperclass
@Setter
@Getter
public abstract class User {
    @Id
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