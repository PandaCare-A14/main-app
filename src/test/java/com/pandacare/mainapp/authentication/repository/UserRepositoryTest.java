package com.pandacare.mainapp.authentication.repository;

import com.pandacare.mainapp.authentication.model.Caregiver;
import com.pandacare.mainapp.authentication.model.Pacillian;
import com.pandacare.mainapp.authentication.model.User;
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
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("existsByEmail returns true when a user with that email is saved")
    void existsByEmailTrue() {
        Pacillian pac = new Pacillian(
                "Pacient",
                "5566778899",
                "081255667788",
                "Yogyakarta",
                "Diabetes"
        );
        pac.setEmail("patient@example.com");
        userRepository.save(pac);

        assertThat(userRepository.existsByEmail("patient@example.com")).isTrue();
    }

    @Test
    @DisplayName("existsByEmail returns false for unknown email")
    void existsByEmailFalse() {
        assertThat(userRepository.existsByEmail("nobody@nowhere.test")).isFalse();
    }

    @Test
    @DisplayName("CR UD cycle on UserRepository via a subclass entity")
    void crudViaSubclass() {
        Caregiver cv = new Caregiver(
                "Dr. Who",
                "0001112223",
                "081300011122",
                "Tardis",
                "Time Travel"
        );
        cv.setEmail("thedoctor@tardis.test");
        User saved = userRepository.save(cv);

        // ID assigned
        UUID id = saved.getId();
        assertThat(id).isNotNull();

        // Read
        Optional<User> found = userRepository.findById(id);
        assertThat(found)
                .isPresent()
                .get()
                .extracting(User::getEmail)
                .isEqualTo("thedoctor@tardis.test");

        // Delete
        userRepository.delete(saved);
        assertThat(userRepository.findById(id)).isEmpty();
    }
}