package dataaccess;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.TimeZone;

import system.Doctor;
import system.Specialty;

public class DoctorDAO extends DAO {
	public static String RESULT_DOCTOR_SUCCESS = "success";
	public static String RESULT_DOCTOR_EXISTS = "exists";
	public static String RESULT_DOCTOR_UNKNOWN = "unknown";

	public ArrayList<String> getUnavailableTimes(int code, LocalDate date) {
		ArrayList<String> dateArray = new ArrayList<>();
		String query = "SELECT * FROM consulta " + "WHERE cod_funcionario = ? " + "AND data_consulta like ?";
		PreparedStatement preparedStatement;
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, code);
			preparedStatement.setString(2, Date.valueOf(date).toString() + "%");
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				dateArray.add(rs.getString("data_consulta").split(" ")[1]);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dateArray;
	}

	public ArrayList<Doctor> getSpecializedDoctors(int specialtyCode) {
		ArrayList<Doctor> doctorArray = new ArrayList<>();
		String query = "SELECT * FROM medico NATURAL JOIN funcionario NATURAL JOIN medico_especialidade "
				+ "WHERE cod_especialidade = ?";
		PreparedStatement preparedStatement;
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, specialtyCode);
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				doctorArray.add(getDoctor(rs));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return doctorArray;
	}
	
	public ArrayList<Doctor> getDoctors() {
		ArrayList<Doctor> doctorArray = new ArrayList<>();
		String query = "SELECT * FROM medico NATURAL JOIN funcionario ";
		PreparedStatement preparedStatement;
		try {
			preparedStatement = connection.prepareStatement(query);
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				doctorArray.add(getDoctor(rs));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return doctorArray;
	}

	public boolean checkDoctorExists(int crm) {
		String query = "SELECT * FROM medico " + "WHERE crm_medico = ?";
		PreparedStatement preparedStatement;
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, crm);
			ResultSet rs = preparedStatement.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public String insertDoctor(Doctor doctor) {
		try {
			// Manual commits
			connection.setAutoCommit(false);

			if (checkDoctorExists(doctor.getCrm()))
				return PatientDAO.RESULT_PATIENT_EXISTS;

			// Employee insertion SQL
			String statement = "INSERT INTO funcionario (nome_funcionario) " + "VALUES (?)";

			// Creates statement
			PreparedStatement preparedStatement = connection.prepareStatement(statement,
					Statement.RETURN_GENERATED_KEYS);
			preparedStatement.setString(1, doctor.getName());

			// Inserts employee
			preparedStatement.execute();

			// Gets doctor number
			int lastInsertedId = -1;
			ResultSet rs = preparedStatement.getGeneratedKeys();
			if (rs.next()) {
				lastInsertedId = rs.getInt(1);
			} else {
				return RESULT_DOCTOR_EXISTS;
			}

			// Doctor insertion SQL
			statement = "INSERT INTO medico (cod_funcionario, crm_medico, horario_entrada_medico, horario_saida_medico, dia_folga_medico, cod_diretor, comissao_medico) "
					+ "VALUES (?, ?, ?, ?, ?, ?, ?)";

			// Creates statement
			preparedStatement = connection.prepareStatement(statement);
			preparedStatement.setInt(1, lastInsertedId);
			preparedStatement.setInt(2, doctor.getCrm());
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
			simpleDateFormat.setTimeZone(TimeZone.getTimeZone("BRT"));
			preparedStatement.setTime(3, new Time(simpleDateFormat.parse(doctor.getTimeIn()).getTime()));
			preparedStatement.setTime(4, new Time(simpleDateFormat.parse(doctor.getTimeOut()).getTime()));
			preparedStatement.setInt(5, doctor.getDayOff());
			preparedStatement.setInt(6, doctor.getDirector().getCode());
			preparedStatement.setInt(7, doctor.getCommission());

			// Inserts doctor
			preparedStatement.execute();

			// Inserts specialties
			ArrayList<Specialty> specList = doctor.getSpecialtyList();
			for (Specialty specialty : specList) {
				int specCode = specialty.getCode();
				// Patient insertion SQL
				statement = "INSERT INTO medico_especialidade (cod_funcionario, cod_especialidade) " + "VALUES (?, ?)";

				// Creates statement
				preparedStatement = connection.prepareStatement(statement);
				preparedStatement.setInt(1, lastInsertedId);
				preparedStatement.setInt(2, specCode);

				// Inserts specialties
				preparedStatement.execute();
			}

			// Commits and returns
			connection.commit();
			connection.setAutoCommit(true);
			return RESULT_DOCTOR_SUCCESS;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return RESULT_DOCTOR_UNKNOWN;
	}

	public Doctor getDoctor(int doctorCode) {
		return getDoctor(doctorCode, false);
	}

	public Doctor getDoctor(int doctorCode, boolean addSpecialties) {
		Doctor doctor = null;
		try {
			PreparedStatement preparedStatement;
			String query = "";
			ArrayList<Specialty> specialtyList = new ArrayList<>();
			if (addSpecialties) {
				query = "SELECT * FROM medico NATURAL JOIN funcionario NATURAL JOIN medico_especialidade NATURAL JOIN especialidade " 
						+ "WHERE cod_funcionario = ? ";
			} else {
				query = "SELECT * FROM medico NATURAL JOIN funcionario " + "WHERE cod_funcionario = ?";
			}

			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, doctorCode);
			ResultSet rs = preparedStatement.executeQuery();
			while(rs.next()) {
				if(doctor==null) {
					doctor = getDoctor(rs);
				}
				if(addSpecialties) {
					Specialty specialty = new Specialty();
					specialty.setCode(rs.getInt("cod_especialidade"));
					specialty.setName(rs.getString("nome_especialidade"));
					specialty.setValue(rs.getInt("valor_especialidade"));
					specialtyList.add(specialty);
				}
			}
			
			if(addSpecialties) {
				doctor.setSpecialtyListInstance(specialtyList);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return doctor;
	}

	private Doctor getDoctor(ResultSet rs) throws SQLException {
		Doctor doctor = new Doctor();
		doctor = new Doctor();
		doctor.setCode(rs.getInt("cod_funcionario"));
		doctor.setName(rs.getString("nome_funcionario"));
		doctor.setCrm(rs.getInt("crm_medico"));
		doctor.setTimeIn(rs.getString("horario_entrada_medico"));
		doctor.setTimeOut(rs.getString("horario_saida_medico"));
		doctor.setDayOff(rs.getInt("dia_folga_medico"));
		doctor.setCommission(rs.getInt("comissao_medico"));
		doctor.setDirector(rs.getInt("cod_diretor"));
		return doctor;
	}
}
