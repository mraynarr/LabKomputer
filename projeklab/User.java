
package projeklab;

public class User {
    private int id;
    private String nama;
    private String email;
    private String password;
    private String role;
    private int idProdi;

    public User(int id, String nama, String email, String password, String role, int idProdi) {
        this.id = id;
        this.nama = nama;
        this.email = email;
        this.password = password;
        this.role = role;
        this.idProdi = idProdi;
    }

    public User(String nama, String email, String password, String role, int idProdi) {
        this.nama = nama;
        this.email = email;
        this.password = password;
        this.role = role;
        this.idProdi = idProdi;
    }

    public String getNama() { return nama; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public int getIdProdi() { return idProdi; }
    public int getId() { return id; }
}