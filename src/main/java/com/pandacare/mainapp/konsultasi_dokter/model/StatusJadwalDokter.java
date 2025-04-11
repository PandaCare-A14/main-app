package com.pandacare.mainapp.konsultasi_dokter.model;

public enum StatusJadwalDokter {
    AVAILABLE {
        @Override
        public boolean isAvailable() {
            return true;
        }
    },

    REQUESTED {
        @Override
        public boolean isAvailable() {
            return false;
        }
    },

    APPROVED {
        @Override
        public boolean isAvailable() {
            return false;
        }
    },

    REJECTED {
        @Override
        public boolean isAvailable() {
            return false;
        }
    },

    CHANGE_SCHEDULE {
        @Override
        public boolean isAvailable() {
            return false;
        }
    };

    public abstract boolean isAvailable();
}