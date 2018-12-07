package dataaccess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

import system.Appointment;
import system.Doctor;
import system.Patient;
import system.Payment;
import system.Specialty;

public class AppointmentDAO extends DAO{
	public static String RESULT_PATIENT_SUCCESS = "success";
	public static String RESULT_PATIENT_EXISTS = "exists";
	public static String RESULT_PATIENT_UNKNOWN = "unknown";
	
	public String insertAppointment(Appointment appointment) {
		try {
			//Manual commits
			connection.setAutoCommit(false);
			
			//Client insertion SQL
			String statement = "INSERT INTO consulta (cod_funcionario, cod_cliente, data_consulta, tipo_consulta) "
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
			
			//Inserts appointment
			preparedStatement.execute();
			//Gets patient number
			int last_inserted_id = -1;
			ResultSet rs = preparedStatement.getGeneratedKeys();
			if(rs.next()) {
				last_inserted_id = rs.getInt(1);
            } else {
            	return PatientDAO.RESULT_PATIENT_UNKNOWN;
            }
			connection.commit();
			
			//Inserts payment
			PaymentDAO paymentDAO = new PaymentDAO();
			Payment payment = new Payment();
			payment.setCode(last_inserted_id);
			TaxDAO taxDAO = new TaxDAO();
			float tax = taxDAO.getTax(appointment.getType().getCode(), appointment.getDate().getMonthValue()) / 100f;
			taxDAO.closeConnection();
			float commission = appointment.getDoctor().getCommission() / 100f;
			payment.setValue(Math.round(appointment.getType().getValue() * commission * tax));
			payment.setType(0);
			payment.setStatus(false);
			paymentDAO.insertPayment(payment);
			
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

	public ArrayList<Appointment> getAppointments(){
		String query = "SELECT * FROM consulta NATURAL JOIN cliente NATURAL JOIN funcionario JOIN especialidade ON (especialidade.cod_especialidade = consulta.tipo_consulta)";
		PreparedStatement preparedStatement;
		ArrayList<Appointment> appointmentList = new ArrayList<>();
		try {
			preparedStatement = connection.prepareStatement(query);
			ResultSet rs = preparedStatement.executeQuery();
			while(rs.next()) {
				Appointment appointment = new Appointment();
				appointment.setCode(rs.getInt("cod_consulta"));
				Doctor doctor = new Doctor();
				doctor.setName(rs.getString("nome_funcionario"));
				doctor.setCode(rs.getInt("cod_funcionario"));
				appointment.setDoctorInstance(doctor);
				Patient patient = new Patient();
				patient.setCode(rs.getInt("cod_cliente"));
				patient.setName(rs.getString("nome_cliente"));
				appointment.setPatientInstance(patient);
				appointment.setDateTime(rs.getTimestamp("data_consulta"));
				appointment.setStatus(rs.getBoolean("status_consulta"));
				Specialty specialty = new Specialty();
				specialty.setCode(rs.getInt("tipo_consulta"));
				specialty.setName(rs.getString("nome_especialidade"));
				appointment.setTypeInstance(specialty);
				appointment.setPayment();
								
				appointmentList.add(appointment);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return appointmentList;
	}
}
