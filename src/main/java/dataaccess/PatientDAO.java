package dataaccess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import system.Patient;

public class PatientDAO extends DAO{
	public static String RESULT_PATIENT_SUCCESS = "success";
	public static String RESULT_PATIENT_EXISTS = "exists";
	public static String RESULT_PATIENT_UNKNOWN = "unknown";
	
	public ArrayList<Patient> getPatients(){
		ArrayList<Patient> list = new ArrayList<>();
		String query = "SELECT * FROM paciente NATURAL JOIN cliente ";
		PreparedStatement preparedStatement;
		try {
			preparedStatement = connection.prepareStatement(query);
			ResultSet rs = preparedStatement.executeQuery();
			while(rs.next()) {
				Patient patient = new Patient();
				patient.setCode(rs.getInt("cod_cliente"));
				patient.setName(rs.getString("nome_cliente"));
				patient.setPhone(rs.getString("telefone_cliente"));
				patient.setCpf(rs.getString("cpf_paciente"));
				patient.setBirthDate(rs.getString("data_nasc_paciente"));
				patient.setSex(rs.getString("sexo_paciente"));
				list.add(patient);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	
	public boolean checkPatientExists(String cpf) {
		String query = "SELECT * FROM paciente "
				+ "WHERE cpf_paciente = ?";
		PreparedStatement preparedStatement;
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, cpf);
			ResultSet rs = preparedStatement.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public String insertPatient(Patient patient) {
		try {
			//Manual commits
			connection.setAutoCommit(false);
			
			String cpf = patient.getCpf().replace(".", "").replace("-", "");
			
			if(checkPatientExists(cpf)) return PatientDAO.RESULT_PATIENT_EXISTS;
			
			//Client insertion SQL
			String statement = "INSERT INTO cliente (nome_cliente, telefone_cliente) "
					+ "VALUES (?, ?)";
			
			//Creates statement
			PreparedStatement preparedStatement = connection.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
			preparedStatement.setString(1, patient.getName());
			preparedStatement.setString(2, patient.getPhone().replace("(", "").replace(")", "").replace(" ", ""));
			
			//Inserts client
			preparedStatement.execute();
			
			//Gets patient number
			int last_inserted_id = -1;
			ResultSet rs = preparedStatement.getGeneratedKeys();
			if(rs.next()) {
				last_inserted_id = rs.getInt(1);
            } else {
            	return PatientDAO.RESULT_PATIENT_UNKNOWN;
            }
			
			//Patient insertion SQL
			statement = "INSERT INTO paciente (cod_cliente, cpf_paciente, data_nasc_paciente, sexo_paciente) "
					+ "VALUES (?, ?, ?, ?)";
			
			//Creates statement
			preparedStatement = connection.prepareStatement(statement);
			preparedStatement.setInt(1, last_inserted_id);
			preparedStatement.setString(2, cpf);
			SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
			java.util.Date date;
			try {
				date = sdf1.parse(patient.getBirthDate());
				preparedStatement.setDate(3, new java.sql.Date(date.getTime()));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			preparedStatement.setString(4, patient.getSex());
			
			//Inserts client
			preparedStatement.execute();
			
			//Commits and returns
			connection.commit();
			connection.setAutoCommit(true);
			return PatientDAO.RESULT_PATIENT_SUCCESS;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return PatientDAO.RESULT_PATIENT_UNKNOWN;
	}
	
	public Patient getPatient(int code) {
		String query = "SELECT * FROM cliente NATURAL JOIN paciente "
				+ "WHERE cod_cliente = ?";
		PreparedStatement preparedStatement;
		Patient patient = null;
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, code);
			ResultSet rs = preparedStatement.executeQuery();
			if(rs.next()) {
				patient = new Patient();
				patient.setCode(rs.getInt("cod_cliente"));
				patient.setName(rs.getString("nome_cliente"));
				patient.setPhone(rs.getString("telefone_cliente"));
				patient.setCpf(rs.getString("cpf_paciente"));
				patient.setBirthDate(rs.getString("data_nasc_paciente"));
				patient.setSex(rs.getString("sexo_paciente"));
				
				return patient;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
