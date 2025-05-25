package com.pandacare.mainapp.authentication.repository;

import com.pandacare.mainapp.authentication.model.Caregiver;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CaregiverRepositoryTest {

    @Autowired
    private CaregiverRepository caregiverRepository;    @Test
    @DisplayName("save a new Caregiver and verify all fields")
    void saveAndVerifyFields() {
        Caregiver cv = new Caregiver(
                "Alice",
                "1234567890",
                "0812345678",
                "Jl. Merdeka No.1",
                "General Practice"
        );
        cv.setId(UUID.randomUUID()); // Set ID manually since no auto-generation
        cv.setEmail("alice@example.com");

        Caregiver saved = caregiverRepository.save(cv);

        assertThat(saved.getId())
                .isNotNull()
                .isInstanceOf(UUID.class);

        Optional<Caregiver> found = caregiverRepository.findById(saved.getId());
        assertThat(found)
                .isPresent()
                .get()
                .satisfies(c -> {
                    assertThat(c.getName()).isEqualTo("Alice");
                    assertThat(c.getNik()).isEqualTo("1234567890");
                    assertThat(c.getPhoneNumber()).isEqualTo("0812345678");
                    assertThat(c.getWorkAddress()).isEqualTo("Jl. Merdeka No.1");
                    assertThat(c.getSpeciality()).isEqualTo("General Practice");
                    assertThat(c.getEmail()).isEqualTo("alice@example.com");
                });
    }    @Test
    @DisplayName("update a Caregiver's information")
    void updateCaregiver() {
        Caregiver cv = new Caregiver(
                "Bob",
                "0987654321",
                "0898765432",
                "Jl. Sudirman 22",
                "Dermatology"
        );
        cv.setId(UUID.randomUUID()); // Set ID manually since no auto-generation
        Caregiver saved = caregiverRepository.save(cv);

        saved.setSpeciality("Pediatric Dermatology");
        Caregiver updated = caregiverRepository.save(saved);

        assertThat(updated.getSpeciality()).isEqualTo("Pediatric Dermatology");
    }    @Test
    @DisplayName("delete a Caregiver and verify deletion")
    void deleteCaregiver() {
        Caregiver cv = new Caregiver(
                "Charlie",
                "1122334455",
                "0811223344",
                "Jl. Thamrin 10",
                "Cardiology"
        );
        cv.setId(UUID.randomUUID()); // Set ID manually since no auto-generation
        Caregiver saved = caregiverRepository.save(cv);

        caregiverRepository.deleteById(saved.getId());
        assertThat(caregiverRepository.findById(saved.getId())).isEmpty();
    }
}