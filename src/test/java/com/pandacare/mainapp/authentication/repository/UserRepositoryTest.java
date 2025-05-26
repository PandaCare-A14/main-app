package com.pandacare.mainapp.authentication.repository;

import com.pandacare.mainapp.authentication.model.Caregiver;
import com.pandacare.mainapp.authentication.model.Pacillian;
import com.pandacare.mainapp.authentication.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;    @Test
    @DisplayName("findByEmail returns user when email exists")
    void findByEmailSuccess() {
        Pacillian pac = new Pacillian(
                "Patient",
                "5566778899",
                "081255667788",
                "Yogyakarta",
                "Diabetes"
        );
        pac.setId(UUID.randomUUID()); // Set ID manually since no auto-generation
        pac.setEmail("patient@example.com");
        userRepository.save(pac);

        Optional<User> found = userRepository.findByEmail("patient@example.com");
        assertThat(found)
                .isPresent()
                .get()
                .extracting(User::getName)
                .isEqualTo("Patient");
    }    @Test
    @DisplayName("findAll returns both Caregivers and Pacillians")
    void findAllReturnsBothTypes() {
        Caregiver caregiver = new Caregiver(
                "Dr. Smith",
                "1112223334",
                "0811112222",
                "Hospital",
                "Neurology"
        );
        caregiver.setId(UUID.randomUUID()); // Set ID manually since no auto-generation
        userRepository.save(caregiver);

        Pacillian pacillian = new Pacillian(
                "Patient",
                "4445556667",
                "0844455566",
                "Clinic",
                "Hypertension"
        );
        pacillian.setId(UUID.randomUUID()); // Set ID manually since no auto-generation
        userRepository.save(pacillian);

        List<User> users = userRepository.findAll();
        assertThat(users).hasSize(2);
        assertThat(users.stream().anyMatch(u -> u instanceof Caregiver)).isTrue();
        assertThat(users.stream().anyMatch(u -> u instanceof Pacillian)).isTrue();
    }    @Test
    @DisplayName("CRUD operations work through base User repository")
    void userRepositoryCrudOperations() {
        // Create
        Caregiver cv = new Caregiver(
                "Dr. Who",
                "0001112223",
                "081300011122",
                "Tardis",
                "Time Travel"
        );
        cv.setId(UUID.randomUUID()); // Set ID manually since no auto-generation
        User saved = userRepository.save(cv);

        // Read
        Optional<User> found = userRepository.findById(saved.getId());
        assertThat(found).isPresent();

        // Update
        cv.setEmail("doctor@who.test");
        User updated = userRepository.save(cv);
        assertThat(updated.getEmail()).isEqualTo("doctor@who.test");

        // Delete
        userRepository.delete(updated);
        assertThat(userRepository.findById(updated.getId())).isEmpty();
    }
}