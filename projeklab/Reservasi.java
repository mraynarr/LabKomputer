package projeklab;

import java.sql.Date;

public class Reservasi {
    private int id;
    private int idUser;
    private int idLab;
    private int idMatkul;
    private Date tanggalReservasi;
    private String keterangan;

    public Reservasi(int id, int idUser, int idLab, int idMatkul, Date tanggalReservasi, String keterangan) {
        this.id = id;
        this.idUser = idUser;
        this.idLab = idLab;
        this.idMatkul = idMatkul;
        this.tanggalReservasi = tanggalReservasi;
        this.keterangan = keterangan;
    }

    // Getter
    public int getId() { return id; }
    public int getIdUser() { return idUser; }
    public int getIdLab() { return idLab; }
    public int getIdMatkul() { return idMatkul; }
    public Date getTanggalReservasi() { return tanggalReservasi; }
    public String getKeterangan() { return keterangan; }
}
