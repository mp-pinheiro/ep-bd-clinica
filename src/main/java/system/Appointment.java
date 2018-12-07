package system;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import dataaccess.DoctorDAO;
import dataaccess.PatientDAO;
import dataaccess.PaymentDAO;
import dataaccess.SpecialtyDAO;
import dataaccess.TaxDAO;

public class Appointment {
	private int code;
	private Doctor doctor;
	private Patient patient;
	private LocalDate date;
	private LocalTime time;
	private Specialty type;
	private boolean status;
	private Payment payment;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public void setDoctor(int doctorCode) {
		DoctorDAO doctorDAO = new DoctorDAO();
		doctor = doctorDAO.getDoctor(doctorCode);
		doctorDAO.closeConnection();
	}

	public Doctor getDoctor() {
		return doctor;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(int patientCode) {
		PatientDAO patientDAO = new PatientDAO();
		patient = patientDAO.getPatient(patientCode);
		patientDAO.closeConnection();
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(String date) {
		LocalDate dateTime = null;
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			dateTime = LocalDate.parse(date, formatter);
		} catch (DateTimeParseException e) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			dateTime = LocalDate.parse(date, formatter);
		}
		this.date = dateTime;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public Specialty getType() {
		return type;
	}

	public void setType(int typeCode) {
		SpecialtyDAO specialtyDAO = new SpecialtyDAO();
		type = specialtyDAO.getSpecialty(typeCode);
		specialtyDAO.closeConnection();
	}

	public LocalTime getTime() {
		return time;
	}

	public void setTime(String time) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
		LocalTime dateTime = LocalTime.parse(time, formatter);
		this.time = dateTime;
	}
	
	public void setDateTime(Timestamp dateTime) {
		this.date = dateTime.toLocalDateTime().toLocalDate();
		this.time = dateTime.toLocalDateTime().toLocalTime();
	}

	public Payment getPayment() {
		return payment;
	}

	public void setPayment() {
		PaymentDAO paymentDAO = new PaymentDAO();
		this.payment = paymentDAO.getPayment(getCode());
		paymentDAO.closeConnection();
	}
	
	public void makePayment(int type) {
		Payment payment = new Payment();
		payment.setCode(getCode());
		TaxDAO taxDAO = new TaxDAO();
		float tax = taxDAO.getTax(getType().getCode(), getDate().getMonthValue()) / 100f;
		taxDAO.closeConnection();
		float commission = getDoctor().getCommission() / 100f;
		payment.setValue(Math.round(getType().getValue() * commission * tax));
		payment.setType(type);
		payment.setStatus(true);
		
		PaymentDAO paymentDAO = new PaymentDAO();
		paymentDAO.insertPayment(payment);
		paymentDAO.closeConnection();
	}
}
