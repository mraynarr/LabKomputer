package projeklab;

import java.util.List;
import java.util.Scanner;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;

public class menuUser {
    private static LabDAO labDAO = new LabDAO();
    private static SesiDAO sesiDAO = new SesiDAO();
    private static ReservasiDAO reservasiDAO = new ReservasiDAO();
    private static MatkulDAO matkulDAO = new MatkulDAO();

    public static void tampil(User user, Scanner scanner) {
        while (true) {
            System.out.println("\n== Menu User ==");
            System.out.println("1. Pesan Lab");
            System.out.println("2. Log Aktivitas");
            System.out.println("3. Exit");
            System.out.print("Pilih menu: ");
            int pilih = scanner.nextInt();
            scanner.nextLine();

            switch (pilih) {
                case 1:
                    pesanLab(scanner, user);
                    break;
                case 2:
                    logAktivitasUser(user);
                    break;
                case 3:
                    System.out.println("Terima kasih!");
                    return;
                default:
                    System.out.println("Pilihan tidak valid.");
            }
        }
    }

    private static void pesanLab(Scanner scanner, User user) {
        System.out.println("\n-- Pesan Lab --");

        List<Lab> labs = labDAO.getAllLabs();
        if (labs.isEmpty()) {
            System.out.println("Belum ada lab yang tersedia.");
            return;
        }

        System.out.println("Pilih Lab:");
        for (int i = 0; i < labs.size(); i++) {
            System.out.println((i + 1) + ". " + labs.get(i).getNamaLab() + " (Kapasitas: " + labs.get(i).getKapasitas() + ")");
        }
        System.out.print("Masukkan nomor lab: ");
        int labIndex = scanner.nextInt() - 1;
        scanner.nextLine();

        if (labIndex < 0 || labIndex >= labs.size()) {
            System.out.println("Pilihan lab tidak valid.");
            return;
        }

        Lab lab = labs.get(labIndex);

        System.out.print("Masukkan tahun (yyyy): ");
        int tahun = scanner.nextInt();
        System.out.print("Masukkan bulan (1-12): ");
        int bulan = scanner.nextInt();
        System.out.print("Masukkan tanggal (1-31): ");
        int tanggal = scanner.nextInt();
        scanner.nextLine();

        Date sqlDate;
        try {
            Calendar cal = Calendar.getInstance();
            cal.set(tahun, bulan - 1, tanggal, 0, 0, 0);
            sqlDate = new Date(cal.getTimeInMillis());
        } catch (Exception e) {
            System.out.println("Tanggal tidak valid.");
            return;
        }

        List<Sesi> allSesi = sesiDAO.getAllSesi();
        if (allSesi.isEmpty()) {
            System.out.println("Belum ada sesi yang tersedia.");
            return;
        }

        List<Integer> reservedSesiIds = reservasiDAO.getReservedSesi(lab.getIdLab(), sqlDate);
        System.out.println("Sesi tersedia pada tanggal " + sqlDate.toString() + ":");
        for (Sesi sesi : allSesi) {
            String status = reservedSesiIds.contains(sesi.getIdSesi()) ? "Terpakai" : "Tersedia";
            System.out.println(sesi.getIdSesi() + ". [" + sesi.getJamMulai() + " - " + sesi.getJamSelesai() + "] - " + status);
        }

        System.out.println("Masukkan ID sesi yang ingin dipesan, pisahkan dengan koma (contoh: 1,3):");
        String sesiInput = scanner.nextLine();
        String[] sesiIdStrs = sesiInput.split(",");
        List<Integer> sesiToBook = new ArrayList<>();
        for (String s : sesiIdStrs) {
            try {
                int sesiId = Integer.parseInt(s.trim());
                boolean sesiExists = allSesi.stream().anyMatch(sess -> sess.getIdSesi() == sesiId);
                if (!reservedSesiIds.contains(sesiId) && sesiExists) {
                    sesiToBook.add(sesiId);
                } else {
                    System.out.println("Sesi ID " + sesiId + " tidak tersedia atau sudah dipesan.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Input sesi tidak valid: " + s);
            }
        }

        if (sesiToBook.isEmpty()) {
            System.out.println("Tidak ada sesi yang dipilih untuk dipesan.");
            return;
        }

        System.out.println("Konfirmasi pemesanan lab " + lab.getNamaLab() + " pada tanggal " + sqlDate + " untuk sesi:");
        sesiToBook.forEach(id -> {
            Sesi ses = allSesi.stream().filter(ses1 -> ses1.getIdSesi() == id).findFirst().orElse(null);
            if (ses != null) System.out.println("- [" + ses.getJamMulai() + " - " + ses.getJamSelesai() + "]");
        });
        System.out.print("Ketik Y untuk konfirmasi: ");
        String konfirmasi = scanner.nextLine();
        if (!konfirmasi.equalsIgnoreCase("Y")) {
            System.out.println("Pemesanan dibatalkan.");
            return;
        }
        
        List<Matkul> daftarMatkul = matkulDAO.getAllMatkul();
        if (daftarMatkul.isEmpty()) {
        System.out.println("Belum ada data mata kuliah.");
        return;
        }

        System.out.println("\nPilih Mata Kuliah:");
        for (int i = 0; i < daftarMatkul.size(); i++) {
            System.out.println((i + 1) + ". " + daftarMatkul.get(i).getNama());
        }
        System.out.print("Masukkan nomor mata kuliah: ");
        int matkulIndex = scanner.nextInt() - 1;
        scanner.nextLine();

        if (matkulIndex < 0 || matkulIndex >= daftarMatkul.size()) {
            System.out.println("Pilihan mata kuliah tidak valid.");
            return;
        }
        int idMatkul = daftarMatkul.get(matkulIndex).getId();

        System.out.print("Masukkan keterangan (opsional): ");
        String keterangan = scanner.nextLine();

        Reservasi reservasi = new Reservasi(0, user.getId(), lab.getIdLab(), idMatkul, sqlDate, keterangan);

        boolean sukses = reservasiDAO.insertReservasi(reservasi, sesiToBook);
        if (sukses) {
            System.out.println("Pemesanan berhasil!");
        } else {
            System.out.println("Pemesanan gagal.");
        }
    }

    private static void logAktivitasUser(User user) {
    System.out.println("\n-- Log Aktivitas Reservasi --");
    List<Reservasi> reservasiList = reservasiDAO.getReservasiByUser(user.getId());
    if (reservasiList.isEmpty()) {
        System.out.println("Belum ada aktivitas reservasi.");
        return;
    }
    for (Reservasi r : reservasiList) {
        System.out.println("ID Reservasi: " + r.getId() +
                           ", Lab ID: " + r.getIdLab() +
                           ", ID Matkul: " + r.getIdMatkul() +
                           ", Tanggal: " + r.getTanggalReservasi() +
                           ", Keterangan: " + r.getKeterangan());
    }
}

}
