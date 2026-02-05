-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Waktu pembuatan: 05 Feb 2026 pada 16.50
-- Versi server: 10.4.32-MariaDB
-- Versi PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `do_system`
--

-- --------------------------------------------------------

--
-- Struktur dari tabel `admin_profiles`
--

CREATE TABLE `admin_profiles` (
  `id` bigint(20) NOT NULL,
  `admin_code` varchar(30) NOT NULL,
  `position` varchar(100) NOT NULL,
  `user_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `admin_profiles`
--

INSERT INTO `admin_profiles` (`id`, `admin_code`, `position`, `user_id`) VALUES
(1, 'shjcj', 'fbah', 1);

-- --------------------------------------------------------

--
-- Struktur dari tabel `customers`
--

CREATE TABLE `customers` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `user_id` bigint(20) UNSIGNED DEFAULT NULL,
  `name` varchar(150) NOT NULL,
  `phone` varchar(50) DEFAULT NULL,
  `email` varchar(150) DEFAULT NULL,
  `active` tinyint(1) NOT NULL DEFAULT 1,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `company_name` varchar(255) DEFAULT NULL,
  `customer_code` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data untuk tabel `customers`
--

INSERT INTO `customers` (`id`, `user_id`, `name`, `phone`, `email`, `active`, `created_at`, `updated_at`, `company_name`, `customer_code`) VALUES
(1, 3, 'Customer', NULL, 'customer@do.local', 1, '2026-02-05 15:50:36', '2026-02-05 15:50:36', 'dnjad', NULL);

-- --------------------------------------------------------

--
-- Struktur dari tabel `customer_addresses`
--

CREATE TABLE `customer_addresses` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `customer_id` bigint(20) UNSIGNED NOT NULL,
  `label` varchar(100) NOT NULL,
  `recipient_name` varchar(150) DEFAULT NULL,
  `phone` varchar(50) DEFAULT NULL,
  `address_line` text NOT NULL,
  `city` varchar(100) DEFAULT NULL,
  `province` varchar(100) DEFAULT NULL,
  `postal_code` varchar(20) DEFAULT NULL,
  `notes` text DEFAULT NULL,
  `is_default` tinyint(1) NOT NULL DEFAULT 0,
  `address` text DEFAULT NULL,
  `receiver_phone` varchar(255) DEFAULT NULL,
  `receiver_name` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data untuk tabel `customer_addresses`
--

INSERT INTO `customer_addresses` (`id`, `customer_id`, `label`, `recipient_name`, `phone`, `address_line`, `city`, `province`, `postal_code`, `notes`, `is_default`, `address`, `receiver_phone`, `receiver_name`) VALUES
(1, 1, 's bsda', NULL, NULL, 'JL Sakura no 2 kec tanjung senang kel way kandis', 'Kota Bandar Lampung', 'Lampung', '35143', NULL, 1, 'JL Sakura no 2 kec tanjung senang kel way kandis', '088276477014', 'Muhammad Rzki Pratama');

-- --------------------------------------------------------

--
-- Struktur dari tabel `customer_profiles`
--

CREATE TABLE `customer_profiles` (
  `id` bigint(20) NOT NULL,
  `customer_code` varchar(30) NOT NULL,
  `user_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `customer_profiles`
--

INSERT INTO `customer_profiles` (`id`, `customer_code`, `user_id`) VALUES
(1, 'ajbda', 3);

-- --------------------------------------------------------

--
-- Struktur dari tabel `delivery_orders`
--

CREATE TABLE `delivery_orders` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `customer_id` bigint(20) UNSIGNED NOT NULL,
  `ship_to_name` varchar(150) DEFAULT NULL,
  `ship_to_phone` varchar(50) DEFAULT NULL,
  `ship_to_address` text DEFAULT NULL,
  `ship_to_city` varchar(100) DEFAULT NULL,
  `ship_to_province` varchar(100) DEFAULT NULL,
  `ship_to_postal_code` varchar(20) DEFAULT NULL,
  `status` varchar(30) DEFAULT NULL,
  `warehouse_status` varchar(30) DEFAULT NULL,
  `shipment_status` varchar(30) DEFAULT NULL,
  `order_date` date DEFAULT NULL,
  `delivery_date` date DEFAULT NULL,
  `approved_by` varchar(120) DEFAULT NULL,
  `approved_at` datetime DEFAULT NULL,
  `assigned_driver` varchar(120) DEFAULT NULL,
  `assigned_driver_id` bigint(20) UNSIGNED DEFAULT NULL,
  `assigned_at` datetime DEFAULT NULL,
  `notes` text DEFAULT NULL,
  `confirmed_at` datetime(6) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `do_number` varchar(255) DEFAULT NULL,
  `order_note` varchar(500) DEFAULT NULL,
  `proof_image_data` longtext DEFAULT NULL,
  `receiver_name` varchar(255) DEFAULT NULL,
  `receiver_note` varchar(500) DEFAULT NULL,
  `signature_data` longtext DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `customer_address_id` bigint(20) NOT NULL,
  `driver_id` bigint(20) DEFAULT NULL,
  `order_number` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data untuk tabel `delivery_orders`
--

INSERT INTO `delivery_orders` (`id`, `customer_id`, `ship_to_name`, `ship_to_phone`, `ship_to_address`, `ship_to_city`, `ship_to_province`, `ship_to_postal_code`, `status`, `warehouse_status`, `shipment_status`, `order_date`, `delivery_date`, `approved_by`, `approved_at`, `assigned_driver`, `assigned_driver_id`, `assigned_at`, `notes`, `confirmed_at`, `created_at`, `do_number`, `order_note`, `proof_image_data`, `receiver_name`, `receiver_note`, `signature_data`, `updated_at`, `customer_address_id`, `driver_id`, `order_number`) VALUES
(1, 1, NULL, NULL, NULL, NULL, NULL, NULL, 'DRAFT', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2026-02-05 22:48:14.000000', 'DO-20260205-644684', '', NULL, NULL, NULL, NULL, '2026-02-05 22:48:14.000000', 1, NULL, 'DO-20260205-644684');

-- --------------------------------------------------------

--
-- Struktur dari tabel `delivery_order_items`
--

CREATE TABLE `delivery_order_items` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `delivery_order_id` bigint(20) UNSIGNED NOT NULL,
  `product_id` bigint(20) UNSIGNED NOT NULL,
  `product_name` varchar(150) DEFAULT NULL,
  `unit` varchar(50) DEFAULT NULL,
  `unit_price` decimal(18,2) DEFAULT NULL,
  `quantity` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Struktur dari tabel `driver_profiles`
--

CREATE TABLE `driver_profiles` (
  `id` bigint(20) NOT NULL,
  `driver_code` varchar(30) NOT NULL,
  `user_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `driver_profiles`
--

INSERT INTO `driver_profiles` (`id`, `driver_code`, `user_id`) VALUES
(1, 'bscbs', 2);

-- --------------------------------------------------------

--
-- Struktur dari tabel `notification_logs`
--

CREATE TABLE `notification_logs` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `delivery_order_id` bigint(20) UNSIGNED DEFAULT NULL,
  `type` varchar(30) DEFAULT NULL,
  `message` text NOT NULL,
  `created_at` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Struktur dari tabel `order_items`
--

CREATE TABLE `order_items` (
  `id` bigint(20) NOT NULL,
  `quantity` int(11) NOT NULL,
  `order_id` bigint(20) NOT NULL,
  `product_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `order_items`
--

INSERT INTO `order_items` (`id`, `quantity`, `order_id`, `product_id`) VALUES
(1, 1, 1, 1);

-- --------------------------------------------------------

--
-- Struktur dari tabel `owners`
--

CREATE TABLE `owners` (
  `id` bigint(20) NOT NULL,
  `company_name` varchar(255) DEFAULT NULL,
  `owner_code` varchar(255) DEFAULT NULL,
  `user_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struktur dari tabel `owner_profiles`
--

CREATE TABLE `owner_profiles` (
  `id` bigint(20) NOT NULL,
  `owner_code` varchar(30) NOT NULL,
  `user_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struktur dari tabel `products`
--

CREATE TABLE `products` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `sku` varchar(80) NOT NULL,
  `name` varchar(150) NOT NULL,
  `unit` varchar(50) NOT NULL,
  `price` decimal(38,2) DEFAULT NULL,
  `active` tinyint(1) NOT NULL DEFAULT 1,
  `is_active` bit(1) DEFAULT NULL,
  `stock` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data untuk tabel `products`
--

INSERT INTO `products` (`id`, `sku`, `name`, `unit`, `price`, `active`, `is_active`, `stock`) VALUES
(1, 'bhbcs', 'pensil', 'ds bds', 100000.00, 1, b'1', 211);

-- --------------------------------------------------------

--
-- Struktur dari tabel `project_control_profiles`
--

CREATE TABLE `project_control_profiles` (
  `id` bigint(20) NOT NULL,
  `pc_code` varchar(30) NOT NULL,
  `user_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struktur dari tabel `proof_of_delivery`
--

CREATE TABLE `proof_of_delivery` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `delivery_order_id` bigint(20) UNSIGNED NOT NULL,
  `photo_url` varchar(255) DEFAULT NULL,
  `signature_name` varchar(150) DEFAULT NULL,
  `notes` text DEFAULT NULL,
  `received_at` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Struktur dari tabel `qal_details`
--

CREATE TABLE `qal_details` (
  `id` bigint(20) NOT NULL,
  `document_name` varchar(200) NOT NULL,
  `document_type` varchar(80) NOT NULL,
  `received_date` date NOT NULL,
  `verification_status` varchar(40) NOT NULL,
  `qal_number` varchar(60) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `qal_details`
--

INSERT INTO `qal_details` (`id`, `document_name`, `document_type`, `received_date`, `verification_status`, `qal_number`) VALUES
(1, 'bhh', 'bn', '2026-02-17', 'Disetujui', '19291'),
(2, 'da nd', 'da bda', '2026-02-10', 'Disetujui', 'dbwdb');

-- --------------------------------------------------------

--
-- Struktur dari tabel `qal_records`
--

CREATE TABLE `qal_records` (
  `qal_number` varchar(60) NOT NULL,
  `admin_code` varchar(30) DEFAULT NULL,
  `admin_position` varchar(100) DEFAULT NULL,
  `approved_at` datetime(6) DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `customer_code` varchar(30) DEFAULT NULL,
  `customer_name` varchar(120) DEFAULT NULL,
  `driver_code` varchar(30) DEFAULT NULL,
  `driver_name` varchar(120) DEFAULT NULL,
  `qal_date` date NOT NULL,
  `signed_at` datetime(6) DEFAULT NULL,
  `status` enum('APPROVED','DRAFT','SIGNED') NOT NULL,
  `admin_user_id` bigint(20) NOT NULL,
  `customer_user_id` bigint(20) DEFAULT NULL,
  `driver_user_id` bigint(20) DEFAULT NULL,
  `spk_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `qal_records`
--

INSERT INTO `qal_records` (`qal_number`, `admin_code`, `admin_position`, `approved_at`, `created_at`, `customer_code`, `customer_name`, `driver_code`, `driver_name`, `qal_date`, `signed_at`, `status`, `admin_user_id`, `customer_user_id`, `driver_user_id`, `spk_id`) VALUES
('19291', 'shjcj', 'fbah', NULL, '2026-02-05 09:37:45.000000', 'ajbda', 'Customer', 'bscbs', 'Driver', '2026-02-13', NULL, 'DRAFT', 1, 3, 2, 3),
('dbwdb', 'shjcj', 'fbah', NULL, '2026-02-05 14:23:48.000000', 'ajbda', 'Muhammad Rzki Pratama', 'bscbs', 'Driver', '2026-02-05', NULL, 'DRAFT', 1, 3, 2, 4),
('dw s', 'shjcj', 'fbah', NULL, '2026-02-05 09:29:39.000000', 'ajbda', 'Customer', 'bscbs', 'Driver', '2026-02-05', NULL, 'DRAFT', 1, 3, 2, 2);

-- --------------------------------------------------------

--
-- Struktur dari tabel `qc_profiles`
--

CREATE TABLE `qc_profiles` (
  `id` bigint(20) NOT NULL,
  `position` varchar(100) NOT NULL,
  `qc_code` varchar(30) NOT NULL,
  `user_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struktur dari tabel `spk`
--

CREATE TABLE `spk` (
  `id` bigint(20) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `job_name` varchar(200) NOT NULL,
  `spk_number` varchar(60) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `spk`
--

INSERT INTO `spk` (`id`, `created_at`, `job_name`, `spk_number`) VALUES
(1, '2026-02-05 09:28:57.000000', 'dw', 'bwdw'),
(2, '2026-02-05 09:29:39.000000', 'dww', '102992'),
(3, '2026-02-05 09:37:45.000000', 'njjl', 'bb'),
(4, '2026-02-05 14:23:48.000000', 'dnaebdb', 'duehde');

-- --------------------------------------------------------

--
-- Struktur dari tabel `stocks`
--

CREATE TABLE `stocks` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `product_id` bigint(20) UNSIGNED NOT NULL,
  `warehouse_location` varchar(150) DEFAULT NULL,
  `quantity` int(11) NOT NULL DEFAULT 0,
  `updated_at` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Struktur dari tabel `users`
--

CREATE TABLE `users` (
  `id` bigint(20) NOT NULL,
  `name` varchar(120) NOT NULL,
  `email` varchar(180) NOT NULL,
  `phone` varchar(30) DEFAULT NULL,
  `password_hash` varchar(255) NOT NULL,
  `role` enum('ADMIN','CUSTOMER','DRIVER') NOT NULL,
  `active` tinyint(1) NOT NULL DEFAULT 1,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `is_active` bit(1) NOT NULL DEFAULT b'1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data untuk tabel `users`
--

INSERT INTO `users` (`id`, `name`, `email`, `phone`, `password_hash`, `role`, `active`, `created_at`, `updated_at`, `is_active`) VALUES
(1, 'Admin', 'admin@do.local', NULL, '$2a$10$ysm9V1DPMeiRVu1ohmKHaemT5Y5XDNGWyQOV1ADsUVjZeqAyl7B9C', 'ADMIN', 1, '2026-02-05 15:50:36', '2026-02-05 15:50:36', b'1'),
(2, 'Driver', 'driver@do.local', NULL, '$2a$10$ysm9V1DPMeiRVu1ohmKHaemT5Y5XDNGWyQOV1ADsUVjZeqAyl7B9C', 'DRIVER', 1, '2026-02-05 15:50:36', '2026-02-05 15:50:36', b'1'),
(3, 'Muhammad Rzki Pratama', 'customer@do.local', '088276477014', '$2a$10$ysm9V1DPMeiRVu1ohmKHaemT5Y5XDNGWyQOV1ADsUVjZeqAyl7B9C', 'CUSTOMER', 1, '2026-02-05 15:50:36', '2026-02-05 15:50:36', b'1'),
(4, 'Muhammad Rzki Pratama', 'rizki.qq05@gmail.com', '088276477014', '$2a$10$Hdik3IV5AviGVnN78kZoQOGWyQHIHLoE80oJr9hMF6YPuCHk2OWdC', 'CUSTOMER', 1, NULL, NULL, b'1');

--
-- Indexes for dumped tables
--

--
-- Indeks untuk tabel `admin_profiles`
--
ALTER TABLE `admin_profiles`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK_cnnjw6ph2mg34exhbrbw33erq` (`admin_code`),
  ADD UNIQUE KEY `UK_ss7cv5tvqvefq889gxgns5g2y` (`user_id`);

--
-- Indeks untuk tabel `customers`
--
ALTER TABLE `customers`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_customer_user` (`user_id`);

--
-- Indeks untuk tabel `customer_addresses`
--
ALTER TABLE `customer_addresses`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_addr_customer` (`customer_id`);

--
-- Indeks untuk tabel `customer_profiles`
--
ALTER TABLE `customer_profiles`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK_rti64m2a55hfol8xnj3oiaojr` (`customer_code`),
  ADD UNIQUE KEY `UK_cw536j6opmbegf01k4hykv2vs` (`user_id`);

--
-- Indeks untuk tabel `delivery_orders`
--
ALTER TABLE `delivery_orders`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK_bxe1y04t7n7c5f4ng4b5pohlo` (`do_number`),
  ADD KEY `fk_order_customer` (`customer_id`),
  ADD KEY `fk_order_driver` (`driver_id`);

--
-- Indeks untuk tabel `delivery_order_items`
--
ALTER TABLE `delivery_order_items`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_item_order` (`delivery_order_id`),
  ADD KEY `fk_item_product` (`product_id`);

--
-- Indeks untuk tabel `driver_profiles`
--
ALTER TABLE `driver_profiles`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK_pw4gvu9aoua1gwj9h3h9m9ylq` (`driver_code`),
  ADD UNIQUE KEY `UK_jefqm229den1fl199p9tt5ao0` (`user_id`);

--
-- Indeks untuk tabel `notification_logs`
--
ALTER TABLE `notification_logs`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_notif_order` (`delivery_order_id`);

--
-- Indeks untuk tabel `order_items`
--
ALTER TABLE `order_items`
  ADD PRIMARY KEY (`id`);

--
-- Indeks untuk tabel `owners`
--
ALTER TABLE `owners`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK_f5l871r0yr9dyilb3ls5p48os` (`user_id`);

--
-- Indeks untuk tabel `owner_profiles`
--
ALTER TABLE `owner_profiles`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK_o256g17ired73dtac7iumygoy` (`owner_code`),
  ADD UNIQUE KEY `UK_altmo9jf92tpan6fmv0rmdm2j` (`user_id`);

--
-- Indeks untuk tabel `products`
--
ALTER TABLE `products`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `sku` (`sku`);

--
-- Indeks untuk tabel `project_control_profiles`
--
ALTER TABLE `project_control_profiles`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK_69cq32wr3b83i5hwudovgwrws` (`pc_code`),
  ADD UNIQUE KEY `UK_lrpknkm6je5exdkp846mx8hnc` (`user_id`);

--
-- Indeks untuk tabel `proof_of_delivery`
--
ALTER TABLE `proof_of_delivery`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uk_proof_order` (`delivery_order_id`);

--
-- Indeks untuk tabel `qal_details`
--
ALTER TABLE `qal_details`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKchgvs0nb2ou33y2igyonh0ql0` (`qal_number`);

--
-- Indeks untuk tabel `qal_records`
--
ALTER TABLE `qal_records`
  ADD PRIMARY KEY (`qal_number`),
  ADD KEY `FKmxh5jnkg7nb7vd50j2odsfec4` (`spk_id`),
  ADD KEY `FKf1aox8e6frhabep3faw3acy7i` (`admin_user_id`),
  ADD KEY `FKo8krcsc16flcbgcan0p3bio3h` (`customer_user_id`),
  ADD KEY `FK9b1h9hhnxp8fx1bgu4ww9svbd` (`driver_user_id`);

--
-- Indeks untuk tabel `qc_profiles`
--
ALTER TABLE `qc_profiles`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK_tp8bfb8a22soaea0xio9dl6sk` (`qc_code`),
  ADD UNIQUE KEY `UK_fvtt0h23qa8w7tyliqegpyv3r` (`user_id`);

--
-- Indeks untuk tabel `spk`
--
ALTER TABLE `spk`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK_lrhdy8aku1rw27qgqh5911o84` (`spk_number`);

--
-- Indeks untuk tabel `stocks`
--
ALTER TABLE `stocks`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_stock_product` (`product_id`);

--
-- Indeks untuk tabel `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT untuk tabel yang dibuang
--

--
-- AUTO_INCREMENT untuk tabel `admin_profiles`
--
ALTER TABLE `admin_profiles`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT untuk tabel `customers`
--
ALTER TABLE `customers`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT untuk tabel `customer_addresses`
--
ALTER TABLE `customer_addresses`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT untuk tabel `customer_profiles`
--
ALTER TABLE `customer_profiles`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT untuk tabel `delivery_orders`
--
ALTER TABLE `delivery_orders`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT untuk tabel `delivery_order_items`
--
ALTER TABLE `delivery_order_items`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT untuk tabel `driver_profiles`
--
ALTER TABLE `driver_profiles`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT untuk tabel `notification_logs`
--
ALTER TABLE `notification_logs`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT untuk tabel `order_items`
--
ALTER TABLE `order_items`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT untuk tabel `owners`
--
ALTER TABLE `owners`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT untuk tabel `owner_profiles`
--
ALTER TABLE `owner_profiles`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT untuk tabel `products`
--
ALTER TABLE `products`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT untuk tabel `project_control_profiles`
--
ALTER TABLE `project_control_profiles`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT untuk tabel `proof_of_delivery`
--
ALTER TABLE `proof_of_delivery`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT untuk tabel `qal_details`
--
ALTER TABLE `qal_details`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT untuk tabel `qc_profiles`
--
ALTER TABLE `qc_profiles`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT untuk tabel `spk`
--
ALTER TABLE `spk`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT untuk tabel `stocks`
--
ALTER TABLE `stocks`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT untuk tabel `users`
--
ALTER TABLE `users`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- Ketidakleluasaan untuk tabel pelimpahan (Dumped Tables)
--

--
-- Ketidakleluasaan untuk tabel `admin_profiles`
--
ALTER TABLE `admin_profiles`
  ADD CONSTRAINT `FKrvt8dm2pcs4e6iuijv1ny4sfa` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Ketidakleluasaan untuk tabel `customer_addresses`
--
ALTER TABLE `customer_addresses`
  ADD CONSTRAINT `fk_addr_customer` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`id`) ON DELETE CASCADE;

--
-- Ketidakleluasaan untuk tabel `customer_profiles`
--
ALTER TABLE `customer_profiles`
  ADD CONSTRAINT `FK69orkdj1un5rh845ngvvmd1xs` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Ketidakleluasaan untuk tabel `delivery_orders`
--
ALTER TABLE `delivery_orders`
  ADD CONSTRAINT `fk_order_customer` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_order_driver` FOREIGN KEY (`driver_id`) REFERENCES `users` (`id`) ON DELETE SET NULL;

--
-- Ketidakleluasaan untuk tabel `delivery_order_items`
--
ALTER TABLE `delivery_order_items`
  ADD CONSTRAINT `fk_item_order` FOREIGN KEY (`delivery_order_id`) REFERENCES `delivery_orders` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_item_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE;

--
-- Ketidakleluasaan untuk tabel `driver_profiles`
--
ALTER TABLE `driver_profiles`
  ADD CONSTRAINT `FKqn29tvl9im463s414dfe9rctk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Ketidakleluasaan untuk tabel `notification_logs`
--
ALTER TABLE `notification_logs`
  ADD CONSTRAINT `fk_notif_order` FOREIGN KEY (`delivery_order_id`) REFERENCES `delivery_orders` (`id`) ON DELETE SET NULL;

--
-- Ketidakleluasaan untuk tabel `proof_of_delivery`
--
ALTER TABLE `proof_of_delivery`
  ADD CONSTRAINT `fk_proof_order` FOREIGN KEY (`delivery_order_id`) REFERENCES `delivery_orders` (`id`) ON DELETE CASCADE;

--
-- Ketidakleluasaan untuk tabel `qal_details`
--
ALTER TABLE `qal_details`
  ADD CONSTRAINT `FKchgvs0nb2ou33y2igyonh0ql0` FOREIGN KEY (`qal_number`) REFERENCES `qal_records` (`qal_number`);

--
-- Ketidakleluasaan untuk tabel `qal_records`
--
ALTER TABLE `qal_records`
  ADD CONSTRAINT `FK9b1h9hhnxp8fx1bgu4ww9svbd` FOREIGN KEY (`driver_user_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `FKf1aox8e6frhabep3faw3acy7i` FOREIGN KEY (`admin_user_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `FKmxh5jnkg7nb7vd50j2odsfec4` FOREIGN KEY (`spk_id`) REFERENCES `spk` (`id`),
  ADD CONSTRAINT `FKo8krcsc16flcbgcan0p3bio3h` FOREIGN KEY (`customer_user_id`) REFERENCES `users` (`id`);

--
-- Ketidakleluasaan untuk tabel `stocks`
--
ALTER TABLE `stocks`
  ADD CONSTRAINT `fk_stock_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
