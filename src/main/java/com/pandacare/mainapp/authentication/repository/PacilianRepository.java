package com.pandacare.mainapp.authentication.repository;

import com.pandacare.mainapp.authentication.model.Pacilian;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PacilianRepository extends JpaRepository<Pacilian, UUID> {}