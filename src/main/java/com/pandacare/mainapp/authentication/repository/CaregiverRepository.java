package com.pandacare.mainapp.authentication.repository;

import com.pandacare.mainapp.authentication.model.Caregiver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CaregiverRepository extends JpaRepository<Caregiver, UUID> {}