package projeklab;

import java.util.List;
import java.util.Scanner;
// InputMismatchException tidak lagi dibutuhkan di sini karena sudah pindah ke DAO
// import java.util.InputMismatchException;

public class menuAdmin {
    // DAO yang masih dibutuhkan oleh menuAdmin secara langsung untuk menu utama
    // Dependencies ini sekarang akan memanggil method UI/alur dari DAO masing-masing
    private static UserDAO userDAO = new UserDAO();
    private static LabDAO labDAO = new LabDAO();
    private static SesiDAO sesiDAO = new SesiDAO();
    private static ReservasiDAO reservasiDAO = new ReservasiDAO(); // Tetap di sini untuk log reservasi

    public static void tampil(Scanner scanner, User admin) {
        while (true) {
            System.out.println("\n== Menu Admin ==");
            System.out.println("1. Kelola Admin & User");
            System.out.println("2. Kelola Lab");
            System.out.println("3. Kelola Jadwal (Sesi)");
            System.out.println("4. Log Aktivitas Reservasi");
            System.out.println("5. Logout");
            System.out.print("Pilih menu: ");
            int pilihan = scanner.nextInt();
            scanner.nextLine();

            switch (pilihan) {
                case 1:
                    UserDAO.manageAdminAndUser(scanner); // Panggil method dari UserDAO
                    break;
                case 2:
                    LabDAO.manageLab(scanner); // Panggil method dari LabDAO
                    break;
                case 3:
                    SesiDAO.manageSesi(scanner); // Panggil method dari SesiDAO
                    break;
                case 4:
                    tampilkanLogReservasi();
                    break;
                case 5:
                    System.out.println("Anda telah logout dari akun admin.");
                    return;
                default:
                    System.out.println("Pilihan tidak valid.");
            }
        }
    }

    // Method tampilkanLogReservasi tetap di menuAdmin karena tidak ada ReservasiManager/DAO UI
    private static void tampilkanLogReservasi() {
        List<Reservasi> allRes = reservasiDAO.getAllReservasi();
        if (allRes.isEmpty()) {
            System.out.println("Belum ada aktivitas reservasi.");
            return;
        }
        System.out.println("== Log Aktivitas Reservasi ==");
        System.out.printf("%-5s %-7s %-7s %-8s %-15s %-30s\n",
                          "ID", "User", "Lab", "Matkul", "Tanggal", "Keterangan");
        System.out.println("----------------------------------------------------------------------------------");

        for (Reservasi r : allRes) {
            System.out.printf("%-5d %-7d %-7d %-8d %-15s %-30s\n",
                              r.getId(),
                              r.getIdUser(),
                              r.getIdLab(),
                              r.getIdMatkul(),
                              r.getTanggalReservasi(),
                              r.getKeterangan());
        }
    }
}