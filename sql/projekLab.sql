create database labkomputer;
use labkomputer;

CREATE TABLE prodi (
    id_prodi INT AUTO_INCREMENT PRIMARY KEY,
    nama_prodi VARCHAR(100) NOT NULL
);

-- data dummy prodi
INSERT INTO prodi (id_prodi, nama_prodi) 
VALUES (1, 'Sistem Informasi'), (2, 'Informatika');
 
CREATE TABLE user (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nama VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    id_prodi INT,
    role ENUM('admin', 'user') NOT NULL DEFAULT 'user',
    FOREIGN KEY (id_prodi) REFERENCES prodi(id_prodi)
);

-- data dummy admin
INSERT INTO user (id, nama, email, password, role) VALUES (1, 'admin', 'admin@gmail.com', 'admin123', 'admin');

CREATE TABLE lab_komputer (
    id_lab INT AUTO_INCREMENT PRIMARY KEY,
    nama_lab VARCHAR(100) NOT NULL,
    kapasitas INT NOT NULL
);

CREATE TABLE mata_kuliah (
    id_matkul INT AUTO_INCREMENT PRIMARY KEY,
    nama_matkul VARCHAR(100) NOT NULL,
    kode_matkul VARCHAR(20) NOT NULL,
    id_prodi INT,
    FOREIGN KEY (id_prodi) REFERENCES prodi(id_prodi)
);

-- data dummy matkul
INSERT INTO mata_kuliah (id_matkul, nama_matkul, kode_matkul, id_prodi) 
VALUES (1, 'Pemrograman Dasar', '01', 1), (2, 'Basis Data', '02', 1), (3, 'Matematika Komputasi', '03', 1), (4, 'Pengantar Informatika', '01', 2), (5, 'Pemrograman Web', '02', 2);

CREATE TABLE sesi (
    id_sesi INT AUTO_INCREMENT PRIMARY KEY,
    nama_sesi VARCHAR(50),      -- contoh: "Sesi 1"
    jam_mulai TIME NOT NULL,
    jam_selesai TIME NOT NULL
);


CREATE TABLE reservasi (
    id_reservasi INT AUTO_INCREMENT PRIMARY KEY,
    id INT NOT NULL,
    id_lab INT not null,
    id_matkul INT NOT NULL,
    tanggal_reservasi DATE NOT NULL,
    keterangan VARCHAR(50),
    FOREIGN KEY (id) REFERENCES user(id),
    FOREIGN KEY (id_matkul) REFERENCES mata_kuliah(id_matkul),
    FOREIGN key (id_lab) REFERENCES lab_komputer(id_lab)
);


CREATE TABLE reservasi_detail (
    id_detail INT AUTO_INCREMENT PRIMARY KEY,
    id_reservasi INT NOT NULL,
    id_sesi INT NOT NULL,
    FOREIGN KEY (id_reservasi) REFERENCES reservasi(id_reservasi),
    FOREIGN KEY (id_sesi) REFERENCES sesi(id_sesi)
);

DELIMITER //

CREATE TRIGGER cek_konflik_reservasi
BEFORE INSERT ON reservasi_detail
FOR EACH ROW
BEGIN
    DECLARE konflik INT;

    SELECT COUNT(*) INTO konflik
    FROM reservasi_detail rd
    JOIN reservasi r ON rd.id_reservasi = r.id_reservasi
    JOIN sesi s1 ON rd.id_sesi = s1.id_sesi
    JOIN reservasi r_new ON r_new.id_reservasi = NEW.id_reservasi
    JOIN sesi s2 ON NEW.id_sesi = s2.id_sesi
    WHERE r.id_lab = r_new.id_lab
      AND r.tanggal_reservasi = r_new.tanggal_reservasi
      AND (
          s1.jam_mulai < s2.jam_selesai AND
          s1.jam_selesai > s2.jam_mulai
      );

    IF konflik > 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Konflik jadwal ditemukan pada lab yang sama.';
    END IF;
end;

DELIMITER ;


DELIMITER //

CREATE PROCEDURE cetak_laporan_reservasi(
    IN tanggal_mulai DATE,
    IN tanggal_akhir DATE,
    IN id_prodi_param INT
)
BEGIN
    SELECT 
        r.id_reservasi,
        u.nama_user,
        mk.nama_matkul,
        r.tanggal_reservasi,
        s.jam_mulai,
        s.jam_selesai,
        lk.nama_lab
    FROM reservasi r
    JOIN reservasi_detail rd ON r.id_reservasi = rd.id_reservasi
    JOIN sesi s ON rd.id_sesi = s.id_sesi
    JOIN user u ON r.id = u.id
    JOIN mata_kuliah mk ON r.id_matkul = mk.id_matkul
    JOIN lab_komputer lk ON r.id_lab = lk.id_lab
    WHERE mk.id_prodi = id_prodi_param
      AND r.tanggal_reservasi BETWEEN tanggal_mulai AND tanggal_akhir
    ORDER BY r.tanggal_reservasi;
end;

DELIMITER ;





