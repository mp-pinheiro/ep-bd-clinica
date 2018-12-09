package system;

import java.time.LocalTime;
import java.util.ArrayList;

public class Diagnosis {
	private int code;
	private int appointmentCode;
	private String treatment;
	private String description;
	private ArrayList<Disease> diseaseList;
	private LocalTime time;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTreatment() {
		return treatment;
	}

	public void setTreatment(String treatment) {
		this.treatment = treatment;
	}

	public ArrayList<Disease> getDiseaseList() {
		return diseaseList;
	}

	public void setDiseaseList(ArrayList<Integer> diseaseList) {
		this.diseaseList = new ArrayList<>();
		for(Integer code : diseaseList) {
			Disease disease = new Disease();
			disease.setCode(code);
			this.diseaseList.add(disease);	
		}
	}
	
	public void setDiseaseListInstance(ArrayList<Disease> diseaseList) {
		this.diseaseList = diseaseList;
	}

	public int getAppointmentCode() {
		return appointmentCode;
	}

	public void setAppointmentCode(int appointmentCode) {
		this.appointmentCode = appointmentCode;
	}

	public LocalTime getTime() {
		return time;
	}

	public void setTime(LocalTime time) {
		this.time = time;
	}
}
