# PandaCare A-14

## Anggota Kelompok
Muhammad Satrio Haryo Kusumo - 2006597771

Thorbert Anson Shi - 2306221900

Daffa Aqil Mahmud - 2306245056

Agus Tini Sridewi - 2306276004

Freia Arianti Zulaika - 2306152254

Muhammad Faizi Ismady Supardjo - 2306244955


## Module 9 - Software Architecture

**Container Diagram**

![container](https://github.com/user-attachments/assets/046b5468-85c6-41dc-a002-511bb7f8430a)

**Deployment Diagram**

![deployment-diagram](https://github.com/user-attachments/assets/c406082c-f6d8-4fc4-9468-605d0db57741)

**Context Diagram**

![context](https://github.com/user-attachments/assets/0da4304c-c497-403c-a6c6-3ed159b7c667)

**Future Architecture**

![Image](https://github.com/user-attachments/assets/c97cbe0c-8c84-4fba-a4c0-078d4919ca8f)

## Penjelasan Future Architecture

### Identifikasi Risiko Arsitektur 
- Aplikasi Utama Monolitik: Saat ini, main app menangani hampir seluruh fitur bisnis dalam satu monolit sehingga dapat menyebabkan:

    a. Kompleksitas kode yang meningkat saat fitur baru ditambahkan
    
    b. Deployment ulang seluruh sistem untuk perubahan minor
    
    c.Bottleneck performa saat semua permintaan harus diproses oleh satu aplikasi

- Keterbatasan Skalabilitas Railway:

    a. Fleksibilitas scaling saat traffic meningkat
    
    b. Keterbatasan dalam konfigurasi resource dan kemampuan diagnostik

- Single Point of Failure pada Database:
    a. Risiko kegagalan sistem total jika database mengalami masalah
    
    b. Potensi kehilangan data tanpa replikasi atau failover otomatis

    c. Keterbatasan performa saat beban query meningkat

### Solusi Risiko Arsitektur

Untuk mengatasi risiko-risiko tersebut, kami merencanakan untuk menerapkan arsitektur berikut:
- Dekomposisi ke Microservices:
   
    a. User Service untuk manajemen profil dan akun

    b. Schedule Service untuk penjadwalan dan menghubungkan caregiver-pacilian

    c. Review Service untuk pengelolaan ulasan dan feedback

- Solusi untuk Keterbatasan Railway:
    a. Containerization dengan Docker

    b. Implementasi API Gateway untuk load balancing

    c. Strategi multi-cloud untuk mengurangi ketergantungan pada satu platform

- Peningkatan Ketahanan Database:

    a. PostgreSQL Cluster

    b. Read/write separation untuk mengoptimalkan performa

### Risk Sampling

Kami menerapkan teknik Risk Storming untuk mengidentifikasi potensi risiko arsitektur pada PandaCare. Setelah melakukan diskusi, kelompok menganalisis komponen sistem dan menilai tingkat risiko dengan skala 1-10. Dari proses ini, kami mengidentifikasi tiga risiko utama yaitu arsitektur monolitik pada aplikasi utama (nilai risiko 8), keterbatasan platform untuk menangani lonjakan traffic (nilai risiko 7), dan kerentanan single point of failure pada database PostgreSQL (nilai risiko 9).

Dengan menggunakan teknik ini, kami menyadari bahwa dalam pengembangan suatu aplikasi harus mempertimbangkan ketahanan suatu sistem untuk berjalan, terutama jika aplikasi mencapai skala yang lebih besar. Oleh karena itu, kami merancang arsitektur yang lebih baik dengan pendekatan microservices, strategi multi-cloud, dan peningkatan ketahanan database.