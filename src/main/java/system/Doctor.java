package system;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import dataaccess.DirectorDAO;
import dataaccess.SpecialtyDAO;

public class Doctor extends Employee {
	private int crm;
	private ArrayList<Specialty> specialtyList;
	private Director director;
	private LocalTime timeIn;
	private LocalTime timeOut;
	private int dayOff;
	private int commission;

	public int getCrm() {
		return crm;
	}

	public void setCrm(int crm) {
		this.crm = crm;
	}

	public ArrayList<Specialty> getSpecialtyList() {
		return specialtyList;
	}
	
	public void setSpecialtyList(ArrayList<Integer> specialtyList) {
		this.specialtyList = new ArrayList<>();
		
		SpecialtyDAO specialtyDAO = new SpecialtyDAO();
		for(Integer specCode : specialtyList) {
			this.specialtyList.add(specialtyDAO.getSpecialty(specCode));
		}
		specialtyDAO.closeConnection();
	}
	
	public void setSpecialtyListInstance(ArrayList<Specialty> specialtyList) {
		this.specialtyList = specialtyList;
	}

	public LocalTime getTimeIn() {
		return timeIn;
	}

	public void setTimeIn(String timeIn) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
		LocalTime dateTime = null;
		try{
			dateTime = LocalTime.parse(timeIn, formatter);
		} catch (DateTimeParseException e) {
			formatter = DateTimeFormatter.ofPattern("H:mm");
			try{
				dateTime = LocalTime.parse(timeIn, formatter);
			} catch (DateTimeParseException e2) {
				formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
				dateTime = LocalTime.parse(timeIn, formatter);
			}
		}
		this.timeIn = dateTime;
	}

	public LocalTime getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(String timeOut) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
		LocalTime dateTime = null;
		try{
			dateTime = LocalTime.parse(timeOut, formatter);
		} catch (DateTimeParseException e) {
			formatter = DateTimeFormatter.ofPattern("H:mm");
			try{
				dateTime = LocalTime.parse(timeOut, formatter);
			} catch (DateTimeParseException e2) {
				formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
				dateTime = LocalTime.parse(timeOut, formatter);
			}
		}
		this.timeOut = dateTime;
	}

	public int getDayOff() {
		return dayOff;
	}

	public void setDayOff(int dayOff) {
		this.dayOff = dayOff;
	}

	public Director getDirector() {
		return director;
	}

	public void setDirector(int director) {
		DirectorDAO directorDAO = new DirectorDAO();
		this.director = directorDAO.getDirector(director);
	}

	public int getCommission() {
		return commission;
	}

	public void setCommission(int commission) {
		this.commission = commission;
	}
}
