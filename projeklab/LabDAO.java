package projeklab;

import java.sql.*;
import java.util.*;
import java.util.Scanner; // Import Scanner
import java.util.InputMismatchException; // Import InputMismatchException

public class LabDAO {

    // Ambil semua data lab
    public List<Lab> getAllLabs() { //
        List<Lab> list = new ArrayList<>(); //
        String sql = "SELECT * FROM lab_komputer"; //

        try (Connection conn = DBConnection.getConnection(); //
             PreparedStatement stmt = conn.prepareStatement(sql); //
             ResultSet rs = stmt.executeQuery()) { //

            while (rs.next()) { //
                list.add(new Lab( //
                    rs.getInt("id_lab"), //
                    rs.getString("nama_lab"), //
                    rs.getInt("kapasitas") //
                ));
            }
        } catch (SQLException e) { //
            e.printStackTrace(); //
        }
        return list; //
    }

    // --- Method baru: Ambil Lab berdasarkan ID ---
    public Lab getLabById(int idLab) { //
        String sql = "SELECT * FROM lab_komputer WHERE id_lab = ?"; //
        try (Connection conn = DBConnection.getConnection(); //
             PreparedStatement stmt = conn.prepareStatement(sql)) { //
            stmt.setInt(1, idLab); //
            try (ResultSet rs = stmt.executeQuery()) { //
                if (rs.next()) { //
                    return new Lab( //
                        rs.getInt("id_lab"), //
                        rs.getString("nama_lab"), //
                        rs.getInt("kapasitas") //
                    );
                }
            }
        } catch (SQLException e) { //
            e.printStackTrace(); //
        }
        return null; //
    }

    // Tambah lab baru
    public boolean insertLab(Lab lab) { //
        String sql = "INSERT INTO lab_komputer(nama_lab, kapasitas) VALUES (?, ?)"; //
        try (Connection conn = DBConnection.getConnection(); //
             PreparedStatement stmt = conn.prepareStatement(sql)) { //
            stmt.setString(1, lab.getNamaLab()); //
            stmt.setInt(2, lab.getKapasitas()); //
            return stmt.executeUpdate() > 0; //
        } catch (SQLException e) { //
            e.printStackTrace(); //
        }
        return false; //
    }

    // Hapus lab berdasarkan ID
    public boolean deleteLab(int idLab) { //
        String sql = "DELETE FROM lab_komputer WHERE id_lab = ?"; //
        try (Connection conn = DBConnection.getConnection(); //
             PreparedStatement stmt = conn.prepareStatement(sql)) { //
            stmt.setInt(1, idLab); //
            return stmt.executeUpdate() > 0; //
        } catch (SQLException e) { //
            e.printStackTrace(); //
        }
        return false; //
    }

    // Update data lab
    public boolean updateLab(int idLab, String namaBaru, int kapasitasBaru) { //
        String sql = "UPDATE lab_komputer SET nama_lab = ?, kapasitas = ? WHERE id_lab = ?"; //
        try (Connection conn = DBConnection.getConnection(); //
             PreparedStatement stmt = conn.prepareStatement(sql)) { //
            stmt.setString(1, namaBaru); //
            stmt.setInt(2, kapasitasBaru); //
            stmt.setInt(3, idLab); //
            return stmt.executeUpdate() > 0; //
        } catch (SQLException e) { //
            e.printStackTrace(); //
        }
        return false; //
    }

    // --- UI/Alur Method untuk Kelola Lab ---
    public static void manageLab(Scanner scanner) {
        LabDAO labDAO = new LabDAO(); // Buat instance LabDAO

        while (true) {
            System.out.println("\n--- Kelola Lab ---");
            System.out.println("1. Tambah Lab Baru");
            System.out.println("2. Lihat Semua Lab");
            System.out.println("3. Edit Lab (Update)");
            System.out.println("4. Hapus Lab");
            System.out.println("5. Kembali ke Menu Admin");
            System.out.print("Pilih menu: ");
            int pilihan = scanner.nextInt();
            scanner.nextLine();

            switch (pilihan) {
                case 1:
                    labDAO.tambahLab(scanner);
                    break;
                case 2:
                    labDAO.lihatSemuaLab();
                    break;
                case 3:
                    labDAO.editLab(scanner);
                    break;
                case 4:
                    labDAO.hapusLab(scanner);
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Pilihan tidak valid.");
            }
        }
    }

    private void tambahLab(Scanner scanner) {
        System.out.println("\n-- Tambah Lab Baru --");
        System.out.print("Nama Lab: ");
        String namaLab = scanner.nextLine();

        System.out.print("Kapasitas Lab: ");
        int kapasitas = 0;
        try {
            kapasitas = scanner.nextInt();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("Input kapasitas tidak valid. Harap masukkan angka.");
            scanner.nextLine();
            return;
        }

        Lab newLab = new Lab(0, namaLab, kapasitas);
        boolean berhasil = insertLab(newLab); // Memanggil method insertLab yang sudah ada di kelas ini
        if (berhasil) {
            System.out.println("Lab berhasil ditambahkan.");
        } else {
            System.out.println("Gagal menambahkan lab.");
        }
    }

    private void lihatSemuaLab() {
        List<Lab> labs = getAllLabs(); // Memanggil method getAllLabs yang sudah ada di kelas ini
        if (labs.isEmpty()) {
            System.out.println("Belum ada lab yang terdaftar.");
            return;
        }
        System.out.println("\n== Daftar Semua Lab ==");
        System.out.printf("%-5s %-20s %-10s\n", "ID", "Nama Lab", "Kapasitas");
        System.out.println("------------------------------------");
        for (Lab lab : labs) {
            System.out.printf("%-5d %-20s %-10d\n", lab.getIdLab(), lab.getNamaLab(), lab.getKapasitas());
        }
    }

    private void editLab(Scanner scanner) {
        lihatSemuaLab();
        if (getAllLabs().isEmpty()) { // Memanggil method getAllLabs yang sudah ada di kelas ini
            return;
        }

        System.out.print("\nMasukkan ID lab yang ingin diedit: ");
        int labId = 0;
        try {
            labId = scanner.nextInt();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("Input ID tidak valid. Harap masukkan angka.");
            scanner.nextLine();
            return;
        }

        Lab existingLab = getLabById(labId); // Memanggil method getLabById yang sudah ada di kelas ini
        if (existingLab == null) {
            System.out.println("Lab dengan ID " + labId + " tidak ditemukan.");
            return;
        }

        System.out.println("Mengedit Lab: " + existingLab.getNamaLab() + " (Kapasitas: " + existingLab.getKapasitas() + ")");
        System.out.print("Nama lab baru (kosongkan jika tidak ingin mengubah): ");
        String namaBaru = scanner.nextLine();
        if (namaBaru.isEmpty()) {
            namaBaru = existingLab.getNamaLab();
        }

        System.out.print("Kapasitas baru (masukkan 0 jika tidak ingin mengubah, atau angka): ");
        int kapasitasBaru = 0;
        String kapasitasInput = scanner.nextLine();
        if (kapasitasInput.isEmpty() || kapasitasInput.equals("0")) {
            kapasitasBaru = existingLab.getKapasitas();
            System.out.println("Kapasitas tidak diubah.");
        } else {
            try {
                kapasitasBaru = Integer.parseInt(kapasitasInput);
            } catch (NumberFormatException e) {
                System.out.println("Input kapasitas tidak valid. Kapasitas tidak diubah.");
                kapasitasBaru = existingLab.getKapasitas();
            }
        }

        boolean berhasil = updateLab(labId, namaBaru, kapasitasBaru); // Memanggil method updateLab yang sudah ada di kelas ini
        if (berhasil) {
            System.out.println("Lab berhasil diperbarui.");
        } else {
            System.out.println("Gagal memperbarui lab (pastikan ID benar).");
        }
    }

    private void hapusLab(Scanner scanner) {
        lihatSemuaLab();
        if (getAllLabs().isEmpty()) { // Memanggil method getAllLabs yang sudah ada di kelas ini
            return;
        }

        System.out.print("\nMasukkan ID lab yang ingin dihapus: ");
        int labId = 0;
        try {
            labId = scanner.nextInt();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("Input ID tidak valid. Harap masukkan angka.");
            scanner.nextLine();
            return;
        }

        System.out.print("Anda yakin ingin menghapus lab dengan ID " + labId + "? (y/n): ");
        String konfirmasi = scanner.nextLine().trim().toLowerCase();

        if (konfirmasi.equals("y")) {
            boolean berhasil = deleteLab(labId); // Memanggil method deleteLab yang sudah ada di kelas ini
            if (berhasil) {
                System.out.println("Lab berhasil dihapus.");
            } else {
                System.out.println("Gagal menghapus lab (pastikan ID benar dan tidak ada ketergantungan).");
            }
        } else {
            System.out.println("Penghapusan dibatalkan.");
        }
    }
}