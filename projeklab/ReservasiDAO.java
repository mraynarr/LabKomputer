package projeklab;

import java.sql.*;
import java.util.*;

public class ReservasiDAO {

    // Cek sesi yang sudah dipesan pada lab dan tanggal tertentu
    public List<Integer> getReservedSesi(int idLab, java.util.Date tanggal) {
        List<Integer> reservedSesi = new ArrayList<>();
        String sql = "SELECT rd.id_sesi FROM reservasi r " +
                     "JOIN reservasi_detail rd ON r.id_reservasi = rd.id_reservasi " +
                     "WHERE r.id_lab = ? AND r.tanggal_reservasi = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idLab);
            stmt.setDate(2, new java.sql.Date(tanggal.getTime()));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                reservedSesi.add(rs.getInt("id_sesi"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservedSesi;
    }

    // Simpan reservasi baru
    public boolean insertReservasi(Reservasi reservasi, List<Integer> sesiIds) {
        // --- PERBAIKAN DI SINI: id_user diubah menjadi id sesuai skema DB Anda ---
        String sqlInsertReservasi = "INSERT INTO reservasi(id, id_lab, id_matkul, tanggal_reservasi, keterangan) " +
                                    "VALUES (?, ?, ?, ?, ?)";
        String sqlInsertDetail = "INSERT INTO reservasi_detail(id_reservasi, id_sesi) VALUES (?, ?)";

        Connection conn = null;
        PreparedStatement stmtReservasi = null;
        PreparedStatement stmtDetail = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Mulai transaksi

            stmtReservasi = conn.prepareStatement(sqlInsertReservasi, Statement.RETURN_GENERATED_KEYS);
            // Menggunakan getId() dari objek Reservasi, karena di Reservasi.java Anda pakai id bukan idUser
            stmtReservasi.setInt(1, reservasi.getIdUser()); // Ambil ID user dari objek Reservasi
            stmtReservasi.setInt(2, reservasi.getIdLab());
            stmtReservasi.setInt(3, reservasi.getIdMatkul());
            stmtReservasi.setDate(4, reservasi.getTanggalReservasi());
            stmtReservasi.setString(5, reservasi.getKeterangan());
            int affectedRows = stmtReservasi.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Membuat reservasi gagal, tidak ada baris yang terpengaruh.");
            }

            int idReservasi = 0;
            rs = stmtReservasi.getGeneratedKeys();
            if (rs.next()) {
                idReservasi = rs.getInt(1);
            } else {
                throw new SQLException("Membuat reservasi gagal, tidak ada ID yang diperoleh.");
            }

            stmtDetail = conn.prepareStatement(sqlInsertDetail);
            for (Integer sesiId : sesiIds) {
                stmtDetail.setInt(1, idReservasi);
                stmtDetail.setInt(2, sesiId);
                stmtDetail.addBatch(); // Tambahkan ke batch
            }
            stmtDetail.executeBatch(); // Eksekusi semua batch

            conn.commit(); // Commit transaksi
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    System.err.print("Transaksi dibatalkan. Pesan error: ");
                    conn.rollback(); // Rollback transaksi jika terjadi kesalahan
                } catch (SQLException excep) {
                    excep.printStackTrace();
                }
            }
            return false;
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (stmtReservasi != null) try { stmtReservasi.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (stmtDetail != null) try { stmtDetail.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Kembalikan ke autocommit
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Dapatkan log aktivitas user
    public List<Reservasi> getReservasiByUser(int idUser) {
        // --- PERBAIKAN DI SINI: id_user diubah menjadi id sesuai skema DB Anda ---
        List<Reservasi> list = new ArrayList<>();
        String sql = "SELECT * FROM reservasi WHERE id = ? ORDER BY tanggal_reservasi DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUser);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new Reservasi(
                    rs.getInt("id_reservasi"),
                    rs.getInt("id"), // --- PERBAIKAN DI SINI ---
                    rs.getInt("id_lab"),
                    rs.getInt("id_matkul"),
                    rs.getDate("tanggal_reservasi"),
                    rs.getString("keterangan")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Dapatkan semua log aktivitas reservasi (admin)
    public List<Reservasi> getAllReservasi() {
        List<Reservasi> list = new ArrayList<>();
        String sql = "SELECT * FROM reservasi ORDER BY tanggal_reservasi DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new Reservasi(
                    rs.getInt("id_reservasi"),
                    rs.getInt("id"), // --- PERBAIKAN DI SINI ---
                    rs.getInt("id_lab"),
                    rs.getInt("id_matkul"),
                    rs.getDate("tanggal_reservasi"),
                    rs.getString("keterangan")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}