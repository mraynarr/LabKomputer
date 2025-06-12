package projeklab;

import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;

public class Main {
    private static UserDAO userDAO = new UserDAO();
    private static ProdiDAO prodiDAO = new ProdiDAO();
    private static LabDAO labDAO = new LabDAO();
    private static SesiDAO sesiDAO = new SesiDAO();
    private static ReservasiDAO reservasiDAO = new ReservasiDAO();  

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== Menu ===");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Pilih menu: ");

            int pilihan = scanner.nextInt();
            scanner.nextLine();

            if (pilihan == 1) {
                login(scanner);
            } else if (pilihan == 2) {
                register(scanner);
            } else if (pilihan == 3) {
                System.out.println("Keluar program.");
                break;
            } else {
                System.out.println("Pilihan tidak valid.");
            }
        }
        scanner.close();
    }

    private static void login(Scanner scanner) {
    System.out.println("\n-- Login --");
    System.out.print("Email: ");
    String email = scanner.nextLine();
    System.out.print("Password: ");
    String password = scanner.nextLine();

    User user = userDAO.getUserByEmailAndPassword(email, password);
    if (user != null) {
        System.out.println("Login berhasil! Selamat datang, " + user.getNama());
        if ("admin".equals(user.getRole())) {
            menuAdmin.tampil(scanner, user);   // Panggil method tampil di menuAdmin
        } else {
            menuUser.tampil(user, scanner);   // Panggil method tampil di menuUser
        }
    } else {
        System.out.println("Email atau password salah!");
    }
}


    private static void register(Scanner scanner) {
        System.out.println("\n-- Register --");
        System.out.print("Nama lengkap: ");
        String nama = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        List<Prodi> daftarProdi = prodiDAO.getAllProdi();
        System.out.println("Pilih Prodi:");
        for (int i = 0; i < daftarProdi.size(); i++) {
            System.out.println((i + 1) + ". " + daftarProdi.get(i).getNama());
        }
        System.out.print("Masukkan nomor prodi: ");
        int index = scanner.nextInt() - 1;
        scanner.nextLine();

        if (index >= 0 && index < daftarProdi.size()) {
            int idProdi = daftarProdi.get(index).getId();
            User user = new User(nama, email, password, "user", idProdi);
            if (userDAO.insertUser(user)) {
                System.out.println("Registrasi berhasil, silakan login.");
            } else {
                System.out.println("Registrasi gagal.");
            }
        } else {
            System.out.println("Prodi tidak valid.");
        }
    }

}