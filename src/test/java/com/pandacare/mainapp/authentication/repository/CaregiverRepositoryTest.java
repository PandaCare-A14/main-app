package com.pandacare.mainapp.authentication.repository;

import com.pandacare.mainapp.authentication.model.Caregiver;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("CaregiverRepository Tests")
class CaregiverRepositoryTest {

    @Autowired
    private CaregiverRepository caregiverRepository;

    @Test
    @DisplayName("save a new Caregiver and retrieve it by ID")
    void saveAndFindById() {
        Caregiver cv = new Caregiver(
                "Alice",
                "1234567890",
                "0812345678",
                "Jl. Merdeka No.1",
                "General Practice"
        );
        // before save, no ID
        assertThat(cv.getId()).isNull();

        Caregiver saved = caregiverRepository.save(cv);
        assertThat(saved.getId()).isNotNull();

        UUID id = saved.getId();
        Optional<Caregiver> found = caregiverRepository.findById(id);

        assertThat(found)
                .isPresent()
                .get()
                .satisfies(c -> {
                    assertThat(c.getName()).isEqualTo("Alice");
                    assertThat(c.getNik()).isEqualTo("1234567890");
                    assertThat(c.getPhoneNumber()).isEqualTo("0812345678");
                    assertThat(c.getWorkAddress()).isEqualTo("Jl. Merdeka No.1");
                    assertThat(c.getSpeciality()).isEqualTo("General Practice");
                });
    }

    @Test
    @DisplayName("delete a Caregiver and verify it's gone")
    void deleteById() {
        Caregiver cv = new Caregiver(
                "Bob",
                "0987654321",
                "0898765432",
                "Jl. Sudirman 22",
                "Dermatology"
        );
        Caregiver saved = caregiverRepository.save(cv);
        UUID id = saved.getId();

        caregiverRepository.deleteById(id);
        assertThat(caregiverRepository.findById(id)).isEmpty();
    }
}