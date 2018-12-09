package dataaccess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import system.Diagnosis;
import system.Disease;

public class DiagnosisDAO extends DAO {
	public void insertDiagnosis(Diagnosis diagnosis) {
		try {
			//Manual commits
			connection.setAutoCommit(false);
			
			//Client insertion SQL
			String statement = "INSERT INTO diagnostico (descricao_diagnostico, tratamento_diagnostico) "
					+ "VALUES (?, ?)";
			
			//Creates statement
			PreparedStatement preparedStatement = connection.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
			preparedStatement.setString(1, diagnosis.getDescription());
			preparedStatement.setString(2, diagnosis.getTreatment());
			
			//Inserts diagnosis
			preparedStatement.execute();
			connection.commit();
			
			// Gets diagnosis number
			ResultSet rs = preparedStatement.getGeneratedKeys();
			if (rs.next()) {
				diagnosis.setCode(rs.getInt(1));
			}
			
			ArrayList<Disease> diseases = diagnosis.getDiseaseList();
			DiseaseDAO diseaseDAO = new DiseaseDAO();
			for(Disease disease : diseases) {
				diseaseDAO.insertDiagnosisDisease(diagnosis, disease);
			}
			diseaseDAO.closeConnection();
			
			//Update appointment
			statement = "UPDATE consulta "
					+ "SET cod_diagnostico = ?, "
					+ "    status_consulta = 1 "
					+ "WHERE cod_consulta = ? ";
			
			//Creates statement
			preparedStatement = connection.prepareStatement(statement);
			preparedStatement.setInt(1, diagnosis.getCode());
			preparedStatement.setInt(2, diagnosis.getAppointmentCode());
			
			//Executes update
			preparedStatement.executeUpdate();
			connection.commit();
			
			//Commits and returns
			connection.setAutoCommit(true);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Diagnosis getDiagnosisByAppointment(int code) {
		String query = "SELECT * FROM diagnostico NATURAL JOIN consulta "
				+ "WHERE cod_consulta = ? ";
		PreparedStatement preparedStatement;
		Diagnosis diagnosis = null;
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, code);
			ResultSet rs = preparedStatement.executeQuery();
			if(rs.next()) {
				diagnosis = new Diagnosis();
				diagnosis.setCode(rs.getInt("cod_diagnostico"));
				diagnosis.setAppointmentCode(rs.getInt("cod_consulta"));
				diagnosis.setDescription(rs.getString("descricao_diagnostico"));
				diagnosis.setTreatment(rs.getString("tratamento_diagnostico"));
				diagnosis.setTime(rs.getTime("horario_diagnostico").toLocalTime());
				
				query = "SELECT * FROM diagnostico NATURAL JOIN doenca_diagnostico NATURAL JOIN doenca "
						+ "WHERE cod_diagnostico = ? ";
				preparedStatement = connection.prepareStatement(query);
				preparedStatement.setInt(1, diagnosis.getCode());
				
				rs = preparedStatement.executeQuery();
				ArrayList<Disease> diseaseList = new ArrayList<>();
				while(rs.next()) {
					Disease disease = new Disease();
					disease.setCode(rs.getInt("cod_doenca"));
					disease.setIdentifier(rs.getString("cod_nacional_doenca"));
					disease.setName(rs.getString("nome_doenca"));
					diseaseList.add(disease);
				}
				diagnosis.setDiseaseListInstance(diseaseList);
				return diagnosis;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
