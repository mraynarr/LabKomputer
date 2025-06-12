package projeklab;

public class ReservasiDetail {
    private int id;
    private int idReservasi;
    private int idSesi;

    public ReservasiDetail(int id, int idReservasi, int idSesi) {
        this.id = id;
        this.idReservasi = idReservasi;
        this.idSesi = idSesi;
    }

    public int getId() { return id; }
    public int getIdReservasi() { return idReservasi; }
    public int getIdSesi() { return idSesi; }
}
