# Wallet Ledger Service - Coding Test

Selamat datang di coding test untuk software engineer di OCBC.

Ini adalah hands-on exercise untuk mid-level backend engineer. Yang ingin kami lihat adalah cara kamu problem solving, buat query database, handle transaction & concurrency, dan memahami konsep dasar web di Spring Boot.

## Skenario
Kamu akan membangun backend service untuk **User Loyalty Point Wallet**.
Sistem perlu support hal-hal berikut:
1. Lihat saldo wallet user.
2. Earn points (kalau wallet belum ada, otomatis dibuat).
3. Spend points (dengan validasi, limit, dan cek saldo cukup).
4. Ambil transaction summary (total points per type).
5. Lihat transaction history yang sudah diurutkan.
6. Generate **High Spenders Report** untuk transaksi bernilai tinggi.

---

## Tech Stack
- **Java 21** (required environment, tapi penggunaan fitur sintaks khusus Java 21 itu opsional. Kamu tetap boleh pakai gaya Java 8/11/17).
- **Spring Boot 3.3.0**
- **Maven**
- **H2 In-Memory Database** (Spring Data JPA / Hibernate)
- **JUnit 5 & Mockito**

---

## Tugas yang Harus Diselesaikan

Ada 5 exercise utama yang harus diselesaikan. Codebase sudah bisa di-compile dari awal, tapi test-nya masih ada yang fail. Semua task sudah ditandai dengan komentar `// TODO` di kode.

### Tugas 1: Binding Konfigurasi (`WalletProperties.java`)
- Bind parameter konfigurasi dengan prefix `wallet` di `application.properties` (contoh: `min-spend-amount`, `max-spend-amount`, `tier-multipliers`) ke class `WalletProperties`.
- Pastikan `WalletProperties` ter-register sebagai Spring Bean supaya bisa di-inject ke service.

### Tugas 2: Logika Kondisional & Perhitungan Biaya (`WalletService.java`)
- Lengkapi `calculateProcessingFee(TransactionRequest request)`.
- Implement logic fee berdasarkan tipe `TransactionRequest`:
  - `EarnRequest`: selalu `0`.
  - `SpendRequest`: kalau amount > `100`, fee = `1%` dari amount. Kalau tidak, flat `0.50`.
  - `RefundRequest`: fee = `2%` dari amount.
- *Catatan*: Kamu boleh pakai Java 21 switch pattern matching (record pattern/type guard) atau struktur standar `if-else / instanceof`. Dua-duanya acceptable.

### Tugas 3: Streams dan Agregasi Data (`WalletService.java`)
- Lengkapi `getTransactionSummary(String userId)`.
- Gunakan **Java Streams API** untuk ambil transaksi user dari database, group by `TransactionType`, lalu sum amount-nya.
- Return `Map<TransactionType, BigDecimal>` sebagai summary total.

### Tugas 4: API, Transaksi, dan Konkurensi (`WalletService.java`, `WalletRepository.java`, `WalletController.java`, `GlobalExceptionHandler.java`)
- **Controller & Validation**: Lengkapi endpoint di `WalletController` dan pastikan validasi request body aktif (Jakarta/Hibernate validation).
- **Global Exception Handling**: Konfigurasikan `GlobalExceptionHandler` untuk intercept custom exception (`WalletNotFoundException`, `InsufficientFundsException`, `WalletBlockedException`, `IllegalArgumentException`) dan kembalikan HTTP status + JSON error yang sesuai. Pastikan validation error (`MethodArgumentNotValidException`) juga ditangani rapi.
- **Transaction & Concurrency**: Lengkapi service method `earnPoints` dan `spendPoints`.
  - Terapkan boundary transaksi dengan `@Transactional`.
  - Bereskan issue concurrency. Kalau beberapa thread spend/earn points bersamaan di wallet yang sama, bisa terjadi race condition atau "lost update". Gunakan database locking (contoh: Pessimistic Write lock di repository) untuk serialize operation. Cek hint di `WalletRepository.java`.

### Tugas 5: Query Database Kompleks (`TransactionRepository.java`, `WalletService.java`, `WalletController.java`)
- Lengkapi query di `TransactionRepository.java` menggunakan anotasi JPA `@Query`.
- Tulis **custom JPQL query** yang:
  - Join tabel Wallet dan Transaction.
  - Filter berdasarkan category, date range, dan spending threshold (pakai klausa `HAVING` pada `SUM(amount) >= :threshold`).
  - Group by `wallet.userId`.
  - Order by total spent descending.
  - Return list projection DTO `UserSpendingReport`.
- Sambungkan query ini ke service dan controller endpoint dengan mapping REST request yang benar.

---

## Cara Menjalankan & Verifikasi

1. Jalankan seluruh test:
   ```bash
   mvn clean test
   ```
2. Pada boilerplate awal, beberapa test memang akan gagal. Target kamu adalah buat semua test di `WalletServiceTest` dan `WalletControllerTest` jadi pass.

Semoga berhasil.