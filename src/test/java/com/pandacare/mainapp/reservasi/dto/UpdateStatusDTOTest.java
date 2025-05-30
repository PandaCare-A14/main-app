package com.pandacare.mainapp.reservasi.dto;

import com.pandacare.mainapp.reservasi.enums.StatusReservasiKonsultasi;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UpdateStatusDTOTest {

    @Test
    void testEquals() {
        UUID uuid = UUID.randomUUID();
        UpdateStatusDTO dto1 = new UpdateStatusDTO(StatusReservasiKonsultasi.APPROVED, uuid);
        UpdateStatusDTO dto2 = new UpdateStatusDTO(StatusReservasiKonsultasi.APPROVED, uuid);
        UpdateStatusDTO dto3 = new UpdateStatusDTO(StatusReservasiKonsultasi.REJECTED, uuid);

        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertNotEquals(dto1, null);
        assertNotEquals(dto1, new Object());
    }

    @Test
    void testHashCode() {
        UUID uuid = UUID.randomUUID();
        UpdateStatusDTO dto1 = new UpdateStatusDTO(StatusReservasiKonsultasi.APPROVED, uuid);
        UpdateStatusDTO dto2 = new UpdateStatusDTO(StatusReservasiKonsultasi.APPROVED, uuid);

        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testCanEqual() {
        UpdateStatusDTO dto1 = new UpdateStatusDTO();
        UpdateStatusDTO dto2 = new UpdateStatusDTO();
        Object other = new Object();

        assertTrue(dto1.canEqual(dto2));
        assertFalse(dto1.canEqual(other));
    }

    @Test
    void testToString() {
        UUID uuid = UUID.randomUUID();
        UpdateStatusDTO dto = new UpdateStatusDTO(StatusReservasiKonsultasi.APPROVED, uuid);
        String result = dto.toString();

        assertTrue(result.contains("UpdateStatusDTO"));
        assertTrue(result.contains("status=APPROVED"));
        assertTrue(result.contains("newScheduleId=" + uuid));
    }

    @Test
    void testSetStatus() {
        UpdateStatusDTO dto = new UpdateStatusDTO();
        dto.setStatus(StatusReservasiKonsultasi.APPROVED);

        assertEquals(StatusReservasiKonsultasi.APPROVED, dto.getStatus());
    }

    @Test
    void testSetNewScheduleId() {
        UpdateStatusDTO dto = new UpdateStatusDTO();
        UUID uuid = UUID.randomUUID();
        dto.setNewScheduleId(uuid);

        assertEquals(uuid, dto.getNewScheduleId());
    }

    @Test
    void testGetStatus() {
        UpdateStatusDTO dto = new UpdateStatusDTO(StatusReservasiKonsultasi.WAITING, null);

        assertEquals(StatusReservasiKonsultasi.WAITING, dto.getStatus());
    }

    @Test
    void testGetNewScheduleId() {
        UUID uuid = UUID.randomUUID();
        UpdateStatusDTO dto = new UpdateStatusDTO(null, uuid);

        assertEquals(uuid, dto.getNewScheduleId());
    }
}