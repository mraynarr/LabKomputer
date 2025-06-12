package projeklab;

public class Lab {
    private int idLab;
    private String namaLab;
    private int kapasitas;

    public Lab(int idLab, String namaLab, int kapasitas) {
        this.idLab = idLab;
        this.namaLab = namaLab;
        this.kapasitas = kapasitas;
    }

    public int getIdLab() {
        return idLab;
    }

    public String getNamaLab() {
        return namaLab;
    }

    public int getKapasitas() {
        return kapasitas;
    }

    public void setNamaLab(String namaLab) {
        this.namaLab = namaLab;
    }

    public void setKapasitas(int kapasitas) {
        this.kapasitas = kapasitas;
    }
}
