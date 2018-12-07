package dataaccess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import system.Appointment;

public class AppointmentDAO extends DAO{
	public static String RESULT_PATIENT_SUCCESS = "success";
	public static String RESULT_PATIENT_EXISTS = "exists";
	public static String RESULT_PATIENT_UNKNOWN = "unknown";
	
	public String insertAppointment(Appointment appointment) {
		try {
			//Manual commits
			connection.setAutoCommit(false);
			
			//Client insertion SQL
			String statement = "INSERT INTO consulta (cod_funcionario, cod_cliente, data_consulta) "
					+ "VALUES (?, ?, ?, ?)";
			
			//Creates statement
			PreparedStatement preparedStatement = connection.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
			preparedStatement.setInt(1, appointment.getDoctor().getCode());
			preparedStatement.setInt(2, appointment.getPatient().getCode());
			LocalDate localDate = appointment.getDate();
			LocalTime localTime = appointment.getTime();
			LocalDateTime localDateTime = localDate.atTime(localTime);
			preparedStatement.setString(3, localDateTime.toString());
			preparedStatement.setInt(4, appointment.getType().getCode());
			
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
	
	public Appointment getAppointment(int code) {
		String query = "SELECT * FROM consulta "
				+ "WHERE cod_consulta = ?";
		PreparedStatement preparedStatement;
		Appointment appointment = null;
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, code);
			ResultSet rs = preparedStatement.executeQuery();
			if(rs.next()) {
				appointment = new Appointment();
				appointment.setCode(rs.getInt("cod_consulta"));
				appointment.setDoctor(rs.getInt("cod_funcionario"));
				appointment.setPatient(rs.getInt("cod_cliente"));
				appointment.setDateTime(rs.getTimestamp("data_consulta"));
				appointment.setStatus(rs.getBoolean("status_consulta"));
				appointment.setType(rs.getInt("tipo_consulta"));
				appointment.setPayment();
								
				return appointment;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
