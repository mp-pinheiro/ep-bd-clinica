package system;

import java.util.ArrayList;

import dataaccess.DirectorDAO;
import dataaccess.SpecialtyDAO;

public class Doctor extends Employee {
	private int crm;
	private ArrayList<Specialty> specialtyList;
	private Director director;
	private String timeIn;
	private String timeOut;
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

	public String getTimeIn() {
		return timeIn;
	}

	public void setTimeIn(String timeIn) {
		this.timeIn = timeIn;
	}

	public String getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(String timeOut) {
		this.timeOut = timeOut;
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
