package com.pandacare.mainapp.authentication.repository;

import com.pandacare.mainapp.authentication.model.Pacillian;
import com.pandacare.mainapp.config.TestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestConfig.class)  // This is the key change
class PacillianRepositoryTest {

    @Autowired
    private PacillianRepository pacilianRepository;

    @Autowired
    private TestConfig.TestIdGenerator testIdGenerator;

    @Test
    @DisplayName("save a new Pacillian and retrieve it by ID")
    void saveAndFindById() {
        Pacillian p = new Pacillian(
                "Jane Doe",
                "9876543210",
                "081298765432",
                "Bandung",
                "Asthma"
        );

        // Verify ID is null before setting
        assertThat(p.getId()).isNull();

        p.setId(testIdGenerator.generateId());

        Pacillian saved = pacilianRepository.save(p);
        assertThat(saved.getId()).isNotNull();

        Optional<Pacillian> found = pacilianRepository.findById(saved.getId());
        assertThat(found).isPresent()
                .get()
                .satisfies(pc -> {
                    assertThat(pc.getName()).isEqualTo("Jane Doe");
                    assertThat(pc.getNik()).isEqualTo("9876543210");
                    assertThat(pc.getPhoneNumber()).isEqualTo("081298765432");
                    assertThat(pc.getAddress()).isEqualTo("Bandung");
                    assertThat(pc.getMedicalHistory()).isEqualTo("Asthma");
                });
    }


    @Test
    @DisplayName("delete a Pacillian and verify it's gone")
    void deleteById() {
        Pacillian p = new Pacillian(
                "John Smith",
                "1122334455",
                "08111222333",
                "Jakarta",
                "None"
        );
        p.setId(testIdGenerator.generateId());
        Pacillian saved = pacilianRepository.save(p);
        UUID id = saved.getId();

        pacilianRepository.deleteById(id);
        assertThat(pacilianRepository.findById(id)).isEmpty();
    }
}
