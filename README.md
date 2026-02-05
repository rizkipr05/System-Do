# DO-System

Aplikasi Delivery Order (DO) sederhana dengan backend Spring Boot dan frontend HTML/JS statis. Sistem ini memisahkan peran Customer, Admin, dan Driver.

## Struktur Proyek
- `backend/` — Spring Boot (Java 17, MySQL)
- `frontend/` — HTML/CSS/JS statis (Bootstrap)

## Prasyarat
- Java 17
- Maven
- MySQL

## Konfigurasi Database
Atur koneksi di `backend/src/main/resources/application.properties`:
```
server.port=8081
spring.datasource.url=jdbc:mysql://localhost:3306/do_system?useSSL=false&serverTimezone=Asia/Jakarta
spring.datasource.username=root
spring.datasource.password=
```

> DDL auto update sudah aktif: `spring.jpa.hibernate.ddl-auto=update`

## Menjalankan Backend
```
cd backend
mvn spring-boot:run
```
Backend berjalan di `http://localhost:8081`.

## Menjalankan Frontend
Frontend bersifat statis. Buka file HTML langsung di browser atau lewat server lokal.
Contoh halaman utama:
- `frontend/login-admin.html` (Admin)
- `frontend/login-customer.html` (Customer)
- `frontend/login-driver.html` (Driver)

## Akun & Role
Role yang tersedia:
- `ADMIN`
- `CUSTOMER`
- `DRIVER`

Registrasi customer tersedia di `frontend/register-customer.html`.

## Fitur Utama
### Customer
- Registrasi & login
- Manajemen profil (edit data diri & perusahaan)
- Kelola alamat pengiriman
- Buat pesanan / Delivery Order (pilih produk, input jumlah, pilih alamat, submit)
- Tracking status pengiriman
- Konfirmasi penerimaan (foto bukti, tanda tangan, catatan)
- Riwayat pesanan & detail barang

### Admin
- Manajemen master data customer
- Manajemen produk & stok (harga, satuan, stok gudang)
- Pembuatan DO manual + generate nomor DO otomatis
- Approval DO & update status (Draft → Approved → Packing → Ready To Ship)
- Assign driver & monitoring pengiriman
- Laporan (DO harian/bulanan, barang terlaris, customer aktif, status pengiriman)
- Cetak dokumen (Print DO, Surat Jalan)
- Notifikasi (simulasi)

### Driver
- Login driver
- Lihat DO yang ditugaskan
- Update status pengiriman (Dalam Perjalanan, Terkirim, Gagal Kirim)
- Upload bukti serah terima (foto, tanda tangan, catatan)

## Endpoint Ringkas
Base URL: `http://localhost:8081/api`
- Auth: `/auth/login`, `/auth/register`, `/auth/me`
- Customer: `/customer/me`, `/addresses`, `/orders`
- Admin: `/admin/*`

## Catatan
- Produk harus diisi dulu agar customer bisa membuat order.
- Stok akan berkurang saat status DO diubah ke `READY_TO_SHIP`.
- Notifikasi admin masih simulasi (belum integrasi email/WA).
- Perubahan role & nama tabel/kolom (Admin/Customer/Driver) bersifat breaking. Untuk database lama, lakukan migrasi data/rename kolom atau drop & recreate schema.

## Lisensi
Internal / private project.
