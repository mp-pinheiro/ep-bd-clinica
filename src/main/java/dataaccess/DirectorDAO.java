package dataaccess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import system.Director;

public class DirectorDAO extends DAO {
	public ArrayList<Director> getDirectors(){
		ArrayList<Director> directorArray = new ArrayList<>();
		String query = "SELECT * FROM diretor NATURAL JOIN funcionario ";
		PreparedStatement preparedStatement;
		try {
			preparedStatement = connection.prepareStatement(query);
			ResultSet rs = preparedStatement.executeQuery();
			while(rs.next()) {
				Director director = new Director();
				director.setCode(rs.getInt("cod_funcionario"));
				director.setName(rs.getString("nome_funcionario"));
				directorArray.add(director);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return directorArray;
	}
	
	public Director getDirector(int code) {
		String query = "SELECT * FROM diretor NATURAL JOIN funcionario "
				+ "WHERE cod_funcionario = ?";
		PreparedStatement preparedStatement;
		Director director = null;
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, code);
			ResultSet rs = preparedStatement.executeQuery();
			if(rs.next()) {
				director = new Director();
				director.setCode(rs.getInt("cod_funcionario"));
				director.setName(rs.getString("nome_funcionario"));
								
				return director;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
