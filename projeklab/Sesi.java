package projeklab;

public class Sesi {
    private int idSesi;
    private String jamMulai;
    private String jamSelesai;

    public Sesi(int idSesi, String jamMulai, String jamSelesai) {
        this.idSesi = idSesi;
        this.jamMulai = jamMulai;
        this.jamSelesai = jamSelesai;
    }

    public int getIdSesi() {
        return idSesi;
    }

    public String getJamMulai() {
        return jamMulai;
    }

    public String getJamSelesai() {
        return jamSelesai;
    }

    public void setJamMulai(String jamMulai) {
        this.jamMulai = jamMulai;
    }

    public void setJamSelesai(String jamSelesai) {
        this.jamSelesai = jamSelesai;
    }
}
