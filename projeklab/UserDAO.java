package projeklab;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner; // Import Scanner
import java.util.InputMismatchException; // Import InputMismatchException

public class UserDAO {
    // Dependencies
    private static ProdiDAO prodiDAO = new ProdiDAO(); // ProdiDAO dibutuhkan di sini

    // Method insertUser yang sudah ada:
    public boolean insertUser(User user) {
        // Menggunakan "user" sebagai nama tabel, dan "id" sebagai nama kolom ID
        String sql = "INSERT INTO user(nama, email, password, role, id_prodi) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getNama());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword()); // Pastikan password di-hash di aplikasi nyata!
            stmt.setString(4, user.getRole());
            stmt.setInt(5, user.getIdProdi());
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Method getUserByEmailAndPassword yang sudah ada:
    public User getUserByEmailAndPassword(String email, String password) {
        // Menggunakan "user" sebagai nama tabel, dan "id" sebagai nama kolom ID
        String sql = "SELECT * FROM user WHERE email = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Menggunakan "id" untuk kolom ID
                return new User(
                    rs.getInt("id"),
                    rs.getString("nama"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("role"),
                    rs.getInt("id_prodi")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // --- METHOD BARU UNTUK KELOLA ADMIN/USER (TERMASUK UI/alur) ---

    // Mengambil semua user (termasuk admin lain)
    public List<User> getAllUsers() { //
        List<User> userList = new ArrayList<>(); //
        // Menggunakan "user" sebagai nama tabel
        String sql = "SELECT * FROM user"; //
        try (Connection conn = DBConnection.getConnection(); //
             PreparedStatement stmt = conn.prepareStatement(sql); //
             ResultSet rs = stmt.executeQuery()) { //
            while (rs.next()) { //
                // Menggunakan "id" untuk kolom ID
                userList.add(new User( //
                    rs.getInt("id"), //
                    rs.getString("nama"), //
                    rs.getString("email"), //
                    rs.getString("password"), //
                    rs.getString("role"), //
                    rs.getInt("id_prodi") //
                ));
            }
        } catch (SQLException e) { //
            e.printStackTrace(); //
        }
        return userList; //
    }

    // Mengambil user berdasarkan ID
    public User getUserById(int idUser) { // Parameter tetap idUser, tapi nanti mapping ke kolom "id"
        // Menggunakan "user" sebagai nama tabel, dan "id" sebagai nama kolom ID
        String sql = "SELECT * FROM user WHERE id = ?"; //
        try (Connection conn = DBConnection.getConnection(); //
             PreparedStatement stmt = conn.prepareStatement(sql)) { //
            stmt.setInt(1, idUser); //
            try (ResultSet rs = stmt.executeQuery()) { //
                if (rs.next()) { //
                    // Menggunakan "id" untuk kolom ID
                    return new User( //
                        rs.getInt("id"), //
                        rs.getString("nama"), //
                        rs.getString("email"), //
                        rs.getString("password"), //
                        rs.getString("role"), //
                        rs.getInt("id_prodi") //
                    );
                }
            }
        } catch (SQLException e) { //
            e.printStackTrace(); //
        }
        return null; //
    }

    // Update user (bisa untuk nama, email, password, role, id_prodi)
    public boolean updateUser(User user) { //
        // Menggunakan "user" sebagai nama tabel, dan "id" sebagai nama kolom ID
        String sql = "UPDATE user SET nama = ?, email = ?, password = ?, role = ?, id_prodi = ? WHERE id = ?"; //
        try (Connection conn = DBConnection.getConnection(); //
             PreparedStatement stmt = conn.prepareStatement(sql)) { //
            stmt.setString(1, user.getNama()); //
            stmt.setString(2, user.getEmail()); //
            stmt.setString(3, user.getPassword()); //
            stmt.setString(4, user.getRole()); //
            stmt.setInt(5, user.getIdProdi()); //
            stmt.setInt(6, user.getId()); // Menggunakan user.getId() sesuai User.java Anda
            return stmt.executeUpdate() > 0; //
        } catch (SQLException e) { //
            e.printStackTrace(); //
        }
        return false; //
    }

    // Hapus user berdasarkan ID
    public boolean deleteUser(int idUser) { // Parameter tetap idUser, tapi mapping ke kolom "id"
        // Menggunakan "user" sebagai nama tabel, dan "id" sebagai nama kolom ID
        String sql = "DELETE FROM user WHERE id = ?"; //
        try (Connection conn = DBConnection.getConnection(); //
             PreparedStatement stmt = conn.prepareStatement(sql)) { //
            stmt.setInt(1, idUser); //
            return stmt.executeUpdate() > 0; //
        } catch (SQLException e) { //
            e.printStackTrace(); //
        }
        return false; //
    }

    // --- UI/Alur Method untuk Kelola Admin & User ---
    public static void manageAdminAndUser(Scanner scanner) {
        while (true) {
            System.out.println("\n--- Kelola Admin & User ---");
            System.out.println("1. Tambah User Baru");
            System.out.println("2. Lihat Semua User");
            System.out.println("3. Edit User");
            System.out.println("4. Hapus User");
            System.out.println("5. Kembali ke Menu Admin Utama");
            System.out.print("Pilih menu: ");
            int pilihan = scanner.nextInt();
            scanner.nextLine();

            UserDAO userDAO = new UserDAO(); // Membuat instance UserDAO

            switch (pilihan) {
                case 1:
                    userDAO.tambahUser(scanner);
                    break;
                case 2:
                    userDAO.lihatSemuaUser();
                    break;
                case 3:
                    userDAO.editUser(scanner);
                    break;
                case 4:
                    userDAO.hapusUser(scanner);
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Pilihan tidak valid.");
            }
        }
    }

    private void tambahUser(Scanner scanner) {
        System.out.println("\n-- Tambah User Baru --");
        System.out.print("Nama lengkap: ");
        String nama = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        System.out.println("Pilih Role:");
        System.out.println("1. user");
        System.out.println("2. admin");
        System.out.print("Masukkan nomor role: ");
        int rolePilihan = 0;
        try {
            rolePilihan = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Input tidak valid. Role diatur sebagai 'user' default.");
            scanner.nextLine();
            rolePilihan = 1;
        }
        scanner.nextLine();
        String role = (rolePilihan == 2) ? "admin" : "user";

        List<Prodi> daftarProdi = prodiDAO.getAllProdi();
        System.out.println("Pilih Prodi:");
        for (int i = 0; i < daftarProdi.size(); i++) {
            System.out.println((i + 1) + ". " + daftarProdi.get(i).getNama());
        }
        System.out.print("Masukkan nomor prodi (0 jika tidak ada prodi terkait): ");
        int indexProdi = 0;
        try {
            indexProdi = scanner.nextInt() - 1;
        } catch (InputMismatchException e) {
            System.out.println("Input tidak valid. Prodi diatur ke 0 (Tidak ada prodi).");
            scanner.nextLine();
            indexProdi = -1;
        }
        scanner.nextLine();

        int idProdi = 0;
        if (indexProdi >= 0 && indexProdi < daftarProdi.size()) {
            idProdi = daftarProdi.get(indexProdi).getId();
        } else {
            System.out.println("Prodi tidak valid atau tidak dipilih. User akan tanpa prodi.");
        }

        User newUser = new User(nama, email, password, role, idProdi);
        if (insertUser(newUser)) { // Memanggil method insertUser yang sudah ada di kelas ini
            System.out.println("User/Admin berhasil ditambahkan.");
        } else {
            System.out.println("Gagal menambahkan user/admin. Email mungkin sudah terdaftar.");
        }
    }

    private void lihatSemuaUser() {
        List<User> userList = getAllUsers(); // Memanggil method getAllUsers yang sudah ada di kelas ini
        if (userList.isEmpty()) {
            System.out.println("Belum ada user yang terdaftar.");
            return;
        }
        System.out.println("\n== Daftar Semua User ==");
        System.out.printf("%-5s %-20s %-25s %-10s %-10s\n", "ID", "Nama", "Email", "Role", "ID Prodi");
        System.out.println("----------------------------------------------------------------------");
        for (User user : userList) {
            System.out.printf("%-5d %-20s %-25s %-10s %-10d\n",
                              user.getId(), user.getNama(), user.getEmail(), user.getRole(), user.getIdProdi());
        }
    }

    private void editUser(Scanner scanner) {
        lihatSemuaUser();
        if (getAllUsers().isEmpty()) { // Memanggil method getAllUsers yang sudah ada di kelas ini
            return;
        }

        System.out.print("\nMasukkan ID user yang ingin diedit: ");
        int userId = 0;
        try {
            userId = scanner.nextInt();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("Input ID tidak valid. Harap masukkan angka.");
            scanner.nextLine();
            return;
        }

        User existingUser = getUserById(userId); // Memanggil method getUserById yang sudah ada di kelas ini
        if (existingUser == null) {
            System.out.println("User dengan ID " + userId + " tidak ditemukan.");
            return;
        }

        System.out.println("Mengedit User: " + existingUser.getNama() + " (ID: " + existingUser.getId() + ")");

        System.out.print("Nama lengkap baru (kosongkan jika tidak ingin mengubah): ");
        String namaBaru = scanner.nextLine();
        if (namaBaru.isEmpty()) {
            namaBaru = existingUser.getNama();
        }

        System.out.print("Email baru (kosongkan jika tidak ingin mengubah): ");
        String emailBaru = scanner.nextLine();
        if (emailBaru.isEmpty()) {
            emailBaru = existingUser.getEmail();
        }

        System.out.print("Password baru (kosongkan jika tidak ingin mengubah): ");
        String passwordBaru = scanner.nextLine();
        if (passwordBaru.isEmpty()) {
            passwordBaru = existingUser.getPassword();
        }

        System.out.println("Role saat ini: " + existingUser.getRole());
        System.out.println("Pilih Role baru:");
        System.out.println("1. user");
        System.out.println("2. admin");
        System.out.print("Masukkan nomor role baru (kosongkan jika tidak ingin mengubah): ");
        String roleInput = scanner.nextLine();
        String roleBaru = existingUser.getRole();
        if (!roleInput.isEmpty()) {
            try {
                int rolePilihan = Integer.parseInt(roleInput);
                if (rolePilihan == 1) {
                    roleBaru = "user";
                } else if (rolePilihan == 2) {
                    roleBaru = "admin";
                } else {
                    System.out.println("Pilihan role tidak valid. Role tidak diubah.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Input role tidak valid. Role tidak diubah.");
            }
        }

        int idProdiBaru = existingUser.getIdProdi();
        List<Prodi> daftarProdi = prodiDAO.getAllProdi();
        System.out.println("Prodi saat ini: " + (existingUser.getIdProdi() == 0 ? "Tidak ada" : getProdiNamaById(existingUser.getIdProdi())));
        System.out.println("Pilih Prodi baru:");
        for (int i = 0; i < daftarProdi.size(); i++) {
            System.out.println((i + 1) + ". " + daftarProdi.get(i).getNama());
        }
        System.out.print("Masukkan nomor prodi baru (0 jika tidak ada prodi terkait, atau kosongkan jika tidak ingin mengubah): ");
        String prodiInput = scanner.nextLine();

        if (!prodiInput.isEmpty()) {
            try {
                int indexProdi = Integer.parseInt(prodiInput) - 1;
                if (indexProdi == -1) {
                    idProdiBaru = 0;
                } else if (indexProdi >= 0 && indexProdi < daftarProdi.size()) {
                    idProdiBaru = daftarProdi.get(indexProdi).getId();
                } else {
                    System.out.println("Pilihan prodi tidak valid. Prodi tidak diubah.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Input prodi tidak valid. Prodi tidak diubah.");
            }
        }

        User updatedUser = new User(userId, namaBaru, emailBaru, passwordBaru, roleBaru, idProdiBaru);
        boolean berhasil = updateUser(updatedUser); // Memanggil method updateUser yang sudah ada di kelas ini
        if (berhasil) {
            System.out.println("User berhasil diperbarui.");
        } else {
            System.out.println("Gagal memperbarui user. Email mungkin sudah terdaftar.");
        }
    }

    private void hapusUser(Scanner scanner) {
        lihatSemuaUser();
        if (getAllUsers().isEmpty()) { // Memanggil method getAllUsers yang sudah ada di kelas ini
            return;
        }

        System.out.print("\nMasukkan ID user yang ingin dihapus: ");
        int userId = 0;
        try {
            userId = scanner.nextInt();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("Input ID tidak valid. Harap masukkan angka.");
            scanner.nextLine();
            return;
        }

        System.out.print("Anda yakin ingin menghapus user dengan ID " + userId + "? (y/n): ");
        String konfirmasi = scanner.nextLine().trim().toLowerCase();

        if (konfirmasi.equals("y")) {
            boolean berhasil = deleteUser(userId); // Memanggil method deleteUser yang sudah ada di kelas ini
            if (berhasil) {
                System.out.println("User berhasil dihapus.");
            } else {
                System.out.println("Gagal menghapus user (pastikan ID benar dan tidak ada ketergantungan).");
            }
        } else {
            System.out.println("Penghapusan dibatalkan.");
        }
    }

    // Dipindahkan dari menuAdmin.java
    private String getProdiNamaById(int idProdi) {
        List<Prodi> daftarProdi = prodiDAO.getAllProdi();
        for (Prodi p : daftarProdi) {
            if (p.getId() == idProdi) {
                return p.getNama();
            }
        }
        return "Tidak Diketahui";
    }
}