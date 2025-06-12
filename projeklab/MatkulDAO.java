package projeklab;

import java.sql.*;
import java.util.*;

public class MatkulDAO {
    public List<Matkul> getAllMatkul() {
        List<Matkul> list = new ArrayList<>();
        String sql = "SELECT * FROM mata_kuliah";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Matkul(rs.getInt("id_matkul"), rs.getString("nama_matkul")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
