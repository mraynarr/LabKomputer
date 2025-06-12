package projeklab;

import java.sql.*;
import java.util.*;
import java.util.Scanner; // Import Scanner
import java.util.InputMismatchException; // Import InputMismatchException

public class SesiDAO {

    // Ambil semua sesi
    public List<Sesi> getAllSesi() { //
        List<Sesi> list = new ArrayList<>(); //
        String sql = "SELECT * FROM sesi"; //

        try (Connection conn = DBConnection.getConnection(); //
             PreparedStatement stmt = conn.prepareStatement(sql); //
             ResultSet rs = stmt.executeQuery()) { //

            while (rs.next()) { //
                list.add(new Sesi( //
                    rs.getInt("id_sesi"), //
                    rs.getString("jam_mulai"), //
                    rs.getString("jam_selesai") //
                ));
            }
        } catch (SQLException e) { //
            e.printStackTrace(); //
        }
        return list; //
    }

    // --- Method baru: Ambil Sesi berdasarkan ID ---
    public Sesi getSesiById(int idSesi) { //
        String sql = "SELECT * FROM sesi WHERE id_sesi = ?"; //
        try (Connection conn = DBConnection.getConnection(); //
             PreparedStatement stmt = conn.prepareStatement(sql)) { //
            stmt.setInt(1, idSesi); //
            try (ResultSet rs = stmt.executeQuery()) { //
                if (rs.next()) { //
                    return new Sesi(rs.getInt("id_sesi"), rs.getString("jam_mulai"), rs.getString("jam_selesai")); //
                }
            }
        } catch (SQLException e) { //
            e.printStackTrace(); //
        }
        return null; //
    }

    // Tambah sesi baru
    public boolean insertSesi(Sesi sesi) { //
        String sql = "INSERT INTO sesi(jam_mulai, jam_selesai) VALUES (?, ?)"; //
        try (Connection conn = DBConnection.getConnection(); //
             PreparedStatement stmt = conn.prepareStatement(sql)) { //
            stmt.setString(1, sesi.getJamMulai()); //
            stmt.setString(2, sesi.getJamSelesai()); //
            return stmt.executeUpdate() > 0; //
        } catch (SQLException e) { //
            e.printStackTrace(); //
        }
        return false; //
    }

    // Update sesi
    public boolean updateSesi(Sesi sesi) { //
        String sql = "UPDATE sesi SET jam_mulai = ?, jam_selesai = ? WHERE id_sesi = ?"; //
        try (Connection conn = DBConnection.getConnection(); //
             PreparedStatement stmt = conn.prepareStatement(sql)) { //
            stmt.setString(1, sesi.getJamMulai()); //
            stmt.setString(2, sesi.getJamSelesai()); //
            stmt.setInt(3, sesi.getIdSesi()); //
            return stmt.executeUpdate() > 0; //
        } catch (SQLException e) { //
            e.printStackTrace(); //
        }
        return false; //
    }

    // Hapus sesi
    public boolean deleteSesi(int idSesi) { //
        String sql = "DELETE FROM sesi WHERE id_sesi = ?"; //
        try (Connection conn = DBConnection.getConnection(); //
             PreparedStatement stmt = conn.prepareStatement(sql)) { //
            stmt.setInt(1, idSesi); //
            return stmt.executeUpdate() > 0; //
        } catch (SQLException e) { //
            e.printStackTrace(); //
        }
        return false; //
    }

    // --- UI/Alur Method untuk Kelola Sesi ---
    public static void manageSesi(Scanner scanner) {
        SesiDAO sesiDAO = new SesiDAO(); // Buat instance SesiDAO

        while (true) {
            System.out.println("\n--- Kelola Sesi ---");
            System.out.println("1. Tambah Sesi Baru");
            System.out.println("2. Lihat Semua Sesi");
            System.out.println("3. Edit Sesi (Update)");
            System.out.println("4. Hapus Sesi");
            System.out.println("5. Kembali ke Menu Admin");
            System.out.print("Pilih menu: ");
            int pilihan = scanner.nextInt();
            scanner.nextLine();

            switch (pilihan) {
                case 1:
                    sesiDAO.tambahSesi(scanner);
                    break;
                case 2:
                    sesiDAO.lihatSemuaSesi();
                    break;
                case 3:
                    sesiDAO.editSesi(scanner);
                    break;
                case 4:
                    sesiDAO.hapusSesi(scanner);
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Pilihan tidak valid.");
            }
        }
    }

    private void tambahSesi(Scanner scanner) {
        System.out.println("\n-- Tambah Sesi Baru --");
        System.out.print("Jam mulai (HH:mm:ss): ");
        String jamMulai = scanner.nextLine();
        System.out.print("Jam selesai (HH:mm:ss): ");
        String jamSelesai = scanner.nextLine();

        Sesi newSesi = new Sesi(0, jamMulai, jamSelesai);
        boolean berhasil = insertSesi(newSesi); // Memanggil method insertSesi yang sudah ada di kelas ini
        if (berhasil) {
            System.out.println("Sesi berhasil ditambahkan.");
        } else {
            System.out.println("Gagal menambahkan sesi.");
        }
    }

    private void lihatSemuaSesi() {
        List<Sesi> sesiList = getAllSesi(); // Memanggil method getAllSesi yang sudah ada di kelas ini
        if (sesiList.isEmpty()) {
            System.out.println("Belum ada sesi yang terdaftar.");
            return;
        }
        System.out.println("\n== Daftar Semua Sesi ==");
        System.out.printf("%-5s %-15s %-15s\n", "ID", "Jam Mulai", "Jam Selesai");
        System.out.println("------------------------------------");
        for (Sesi s : sesiList) {
            System.out.printf("%-5d %-15s %-15s\n", s.getIdSesi(), s.getJamMulai(), s.getJamSelesai());
        }
    }

    private void editSesi(Scanner scanner) {
        lihatSemuaSesi();
        if (getAllSesi().isEmpty()) { // Memanggil method getAllSesi yang sudah ada di kelas ini
            return;
        }

        System.out.print("Masukkan ID sesi yang ingin diedit: ");
        int sesiId = 0;
        try {
            sesiId = scanner.nextInt();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("Input ID tidak valid. Harap masukkan angka.");
            scanner.nextLine();
            return;
        }

        Sesi existingSesi = getSesiById(sesiId); // Memanggil method getSesiById yang sudah ada di kelas ini
        if (existingSesi == null) {
            System.out.println("Sesi dengan ID " + sesiId + " tidak ditemukan.");
            return;
        }

        System.out.println("Mengedit Sesi: [" + existingSesi.getJamMulai() + " - " + existingSesi.getJamSelesai() + "]");
        System.out.print("Jam mulai baru (HH:mm:ss, kosongkan jika tidak ingin mengubah): ");
        String mulai = scanner.nextLine();
        if (mulai.isEmpty()) {
            mulai = existingSesi.getJamMulai();
            System.out.println("Jam mulai tidak diubah.");
        }

        System.out.print("Jam selesai baru (HH:mm:ss, kosongkan jika tidak ingin mengubah): ");
        String selesai = scanner.nextLine();
        if (selesai.isEmpty()) {
            selesai = existingSesi.getJamSelesai();
            System.out.println("Jam selesai tidak diubah.");
        }

        Sesi sesiBaru = new Sesi(sesiId, mulai, selesai);
        boolean berhasil = updateSesi(sesiBaru); // Memanggil method updateSesi yang sudah ada di kelas ini
        if (berhasil) {
            System.out.println("Sesi berhasil diperbarui.");
        } else {
            System.out.println("Gagal memperbarui sesi.");
        }
    }

    private void hapusSesi(Scanner scanner) {
        lihatSemuaSesi();
        if (getAllSesi().isEmpty()) { // Memanggil method getAllSesi yang sudah ada di kelas ini
            return;
        }

        System.out.print("Masukkan ID sesi yang ingin dihapus: ");
        int sesiId = 0;
        try {
            sesiId = scanner.nextInt();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("Input ID tidak valid. Harap masukkan angka.");
            scanner.nextLine();
            return;
        }

        System.out.print("Anda yakin ingin menghapus sesi dengan ID " + sesiId + "? (y/n): ");
        String konfirmasi = scanner.nextLine().trim().toLowerCase();

        if (konfirmasi.equals("y")) {
            boolean berhasil = deleteSesi(sesiId); // Memanggil method deleteSesi yang sudah ada di kelas ini
            if (berhasil) {
                System.out.println("Sesi berhasil dihapus.");
            } else {
                System.out.println("Gagal menghapus sesi (pastikan ID benar dan tidak ada ketergantungan).");
            }
        } else {
            System.out.println("Penghapusan dibatalkan.");
        }
    }
}