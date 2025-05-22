package com.pandacare.mainapp.authentication.repository;

import com.pandacare.mainapp.authentication.model.Pacillian;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PacillianRepository extends JpaRepository<Pacillian, UUID> {
}