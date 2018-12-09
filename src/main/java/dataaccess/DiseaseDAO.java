package dataaccess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import system.Diagnosis;
import system.Disease;

public class DiseaseDAO extends DAO {
	public void insertDiagnosisDisease(Diagnosis diagnosis, Disease disease) {
		try {
			//Manual commits
			connection.setAutoCommit(false);
			
			//Client insertion SQL
			String statement = "INSERT INTO doenca_diagnostico (cod_diagnostico, cod_doenca) "
					+ "VALUES (?, ?)";
			
			//Creates statement
			PreparedStatement preparedStatement = connection.prepareStatement(statement);
			preparedStatement.setInt(1, diagnosis.getCode());
			preparedStatement.setInt(2, disease.getCode());
			
			//Inserts client
			preparedStatement.execute();
			
			//Commits and returns
			connection.commit();
			connection.setAutoCommit(true);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ArrayList<Disease> getDiseases(){
		ArrayList<Disease> list = new ArrayList<>();
		String query = "SELECT * FROM doenca ";
		PreparedStatement preparedStatement;
		try {
			preparedStatement = connection.prepareStatement(query);
			ResultSet rs = preparedStatement.executeQuery();
			while(rs.next()) {
				Disease disease = new Disease();
				disease.setCode(rs.getInt("cod_doenca"));
				disease.setIdentifier(rs.getString("cod_nacional_doenca"));
				disease.setName(rs.getString("nome_doenca"));
				list.add(disease);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
}
