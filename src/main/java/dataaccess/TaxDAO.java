package dataaccess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TaxDAO extends DAO {
	public int getTax(int type, int month) {
		String query = "SELECT * FROM taxa "
				+ "WHERE cod_especialidade = ? "
				+ "AND mes_taxa = ?";
		PreparedStatement preparedStatement;
		int tax = 0;
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, type);
			preparedStatement.setInt(2, month);
			ResultSet rs = preparedStatement.executeQuery();
			if(rs.next()) {
				tax = rs.getInt("valor_mes_taxa");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tax;
	}
}
