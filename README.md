# NexaNet Cafe

Aplikasi manajemen warnet (internet cafe) berbasis web, dibangun dengan **Spring Boot** dan **Thymeleaf**. Aplikasi ini menangani reservasi komputer, sesi billing, pemesanan makanan & minuman (F&B), manajemen pelanggan/member, poin reward, hingga pembayaran, lengkap dengan panel admin.

## Fitur Utama

- **Manajemen Komputer**: menambah, mengedit, menghapus, serta mengatur durasi dan sesi pemakaian komputer.
- **Reservasi & Sesi**: pelanggan dapat memilih komputer, melakukan reservasi, dan mengakhiri sesi.
- **Login & Registrasi Member**: pelanggan dapat login sebagai member atau menggunakan mode umum.
- **Dashboard Member**: menampilkan status billing aktif, riwayat transaksi, dan poin.
- **Pemesanan Makanan & Minuman (F&B)**: katalog menu, proses pemesanan, dan nota digital untuk tamu.
- **Tukar Poin**: penukaran poin member dengan barang/reward.
- **Panel Admin**: kelola pelanggan, komputer, transaksi, menu F&B, dan pesanan F&B yang masuk.
- **Pembayaran**: proses pembayaran transaksi hingga status transaksi selesai.

## Tech Stack

| Komponen         | Teknologi                          |
|-------------------|-------------------------------------|
| Bahasa            | Java 17                             |
| Framework         | Spring Boot (Spring Web, Spring Data JPA) |
| Template Engine   | Thymeleaf                           |
| Database          | PostgreSQL (Supabase)               |
| Build Tool        | Maven (Maven Wrapper disertakan)    |
| Frontend          | HTML5, CSS3 (Vanilla), JavaScript (ES6) |
| Library tambahan  | Lombok                              |

## Struktur Proyek

```
cafe/
├── demo/                                   # Aplikasi Spring Boot utama
│   ├── src/main/java/com/cafe/demo/
│   │   ├── controller/                     # Endpoint HTTP (Home, Admin, Komputer, Pelanggan, Transaksi, FoodBeverage)
│   │   ├── model/                          # Entity JPA (Admin, Komputer, Pelanggan, Transaksi, dll.)
│   │   ├── repository/                     # Interface Spring Data JPA
│   │   ├── service/                        # Business logic
│   │   └── DemoApplication.java            # Entry point aplikasi
│   ├── src/main/resources/
│   │   ├── application.properties          # Konfigurasi aplikasi & database
│   │   ├── static/css/                     # Aset statis (CSS)
│   │   └── templates/                      # Halaman Thymeleaf (.html)
│   └── pom.xml                             # Konfigurasi Maven & dependensi
└── docs/                                   # Dokumentasi proyek (Class Diagram, ERD, Panduan Instalasi, dll.)
```

## Persyaratan

- Java 17
- Maven (atau gunakan Maven Wrapper `mvnw` / `mvnw.cmd` yang sudah disediakan)
- Akses ke database PostgreSQL (proyek ini dikonfigurasi menggunakan Supabase)

## Konfigurasi

Pengaturan aplikasi berada di `demo/src/main/resources/application.properties`, meliputi:

- `spring.application.name` — nama aplikasi
- `server.port` — port server (default `1234`)
- `spring.datasource.url`, `spring.datasource.username`, `spring.datasource.password` — koneksi ke database PostgreSQL
- `spring.jpa.hibernate.ddl-auto=update` — skema database diperbarui otomatis mengikuti entity

> Sesuaikan kredensial database dengan environment Anda sendiri sebelum menjalankan aplikasi, dan hindari menyimpan kredensial produksi langsung di file ini.

## Cara Menjalankan

Dari dalam folder `demo/`:

```bash
# Linux / macOS
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

Setelah berjalan, aplikasi dapat diakses di `http://localhost:1234` (sesuai `server.port` pada konfigurasi).

Untuk membangun file JAR:

```bash
./mvnw clean package
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

## Dokumentasi Tambahan

Dokumentasi lebih lanjut tersedia di folder `docs/`:

- `FRONTEND.md` — dokumentasi sistem frontend (arsitektur, routing halaman, design system)
- `id-Class Diagram.pdf`
- `id-Entity Relationship Diagram.pdf`
- `id-Dokumentasi Proyek.pdf`
- `id-Installation Guide.pdf`