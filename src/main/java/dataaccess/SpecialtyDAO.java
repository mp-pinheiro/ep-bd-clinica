package dataaccess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import system.Specialty;

public class SpecialtyDAO extends DAO {
	public Specialty getSpecialty(int code){
		String query = "SELECT * FROM especialidade "
				+ "WHERE cod_especialidade = ?";
		PreparedStatement preparedStatement;
		Specialty specialty = null;
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, code);
			ResultSet rs = preparedStatement.executeQuery();
			if(rs.next()) {
				specialty = new Specialty();
				specialty.setCode(rs.getInt("cod_especialidade"));
				specialty.setName(rs.getString("nome_especialidade"));
				specialty.setValue(rs.getInt("valor_especialidade"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return specialty;
	}
	
	public ArrayList<Specialty> getSpecialties(boolean onlyAvailable){
		ArrayList<Specialty> specialtyArray = new ArrayList<>();
		PreparedStatement preparedStatement;
		if(!onlyAvailable) {
			String query = "SELECT * FROM especialidade ";
			try {
				preparedStatement = connection.prepareStatement(query);
				ResultSet rs = preparedStatement.executeQuery();
				while(rs.next()) {
					Specialty specialty = new Specialty();
					specialty.setCode(rs.getInt("cod_especialidade"));
					specialty.setName(rs.getString("nome_especialidade"));
					specialty.setValue(rs.getInt("valor_especialidade"));
					specialtyArray.add(specialty);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return specialtyArray;
		} else {
			String query = "SELECT * FROM especialidade NATURAL JOIN medico_especialidade ";
			try {
				preparedStatement = connection.prepareStatement(query);
				ResultSet rs = preparedStatement.executeQuery();
				while(rs.next()) {
					Specialty specialty = new Specialty();
					specialty.setCode(rs.getInt("cod_especialidade"));
					specialty.setName(rs.getString("nome_especialidade"));
					specialty.setValue(rs.getInt("valor_especialidade"));
					specialtyArray.add(specialty);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return specialtyArray;
		}
	}
	
	public ArrayList<Specialty> getSpecialties(){
		return getSpecialties(false);
	}
}
