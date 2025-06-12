
package projeklab;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdiDAO {
    public List<Prodi> getAllProdi() {
        List<Prodi> list = new ArrayList<>();
        String sql = "SELECT * FROM prodi";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new Prodi(
                    rs.getInt("id_prodi"),
                    rs.getString("nama_prodi")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}

