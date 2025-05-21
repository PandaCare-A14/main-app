package com.pandacare.mainapp.doctor_profile.dto.response;

import lombok.Getter;
import java.util.Objects;

public record ErrorResponse(String message) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ErrorResponse that = (ErrorResponse) o;
        return Objects.equals(message, that.message);
    }

}