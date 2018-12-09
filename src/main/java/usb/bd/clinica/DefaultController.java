package usb.bd.clinica;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import dataaccess.AppointmentDAO;
import dataaccess.DiagnosisDAO;
import dataaccess.DirectorDAO;
import dataaccess.DiseaseDAO;
import dataaccess.DoctorDAO;
import dataaccess.PatientDAO;
import dataaccess.PaymentDAO;
import dataaccess.SpecialtyDAO;
import system.Appointment;
import system.Diagnosis;
import system.Doctor;
import system.Patient;
import system.Payment;

@Controller
public class DefaultController {
	protected void setMessageAttributes(RedirectAttributes redirectAttributes, String success, String type) {
		redirectAttributes.addFlashAttribute("message", success);
		redirectAttributes.addFlashAttribute("type", type);
	}

	@GetMapping("/")
	public String index(Model model) {
		return "index";
	}

	@GetMapping("/index")
	public String indexRemap(Model model) {
		return "index";
	}

	@PostMapping("/new_patient")
	public String postPatient(@ModelAttribute Patient patient, RedirectAttributes redirectAttributes) {
		PatientDAO patientDAO = new PatientDAO();
		String success = patientDAO.insertPatient(patient);
		patientDAO.closeConnection();
		setMessageAttributes(redirectAttributes, success, "paciente");
		return "redirect:/";
	}

	@GetMapping("/new_patient")
	public String newPatient(Model model) {
		model.addAttribute("patient", new Patient());
		return "new_patient";
	}

	@PostMapping("/new_doctor")
	public String postDoctor(@ModelAttribute Doctor doctor, RedirectAttributes redirectAttributes) {
		DoctorDAO doctorDAO = new DoctorDAO();
		String success = doctorDAO.insertDoctor(doctor);
		doctorDAO.closeConnection();
		setMessageAttributes(redirectAttributes, success, "médico");
		return "redirect:/";
	}

	@GetMapping("/new_doctor")
	public String newDoctor(Model model) {
		model.addAttribute("doctor", new Doctor());

		// Possible specialties
		SpecialtyDAO specialtyDAO = new SpecialtyDAO();
		model.addAttribute("specialties", specialtyDAO.getSpecialties());
		specialtyDAO.closeConnection();

		// Possible directors
		DirectorDAO directorDAO = new DirectorDAO();
		model.addAttribute("directors", directorDAO.getDirectors());
		directorDAO.closeConnection();

		return "new_doctor";
	}

	@PostMapping("/new_appointment")
	public String postAppointment(@ModelAttribute Appointment appointment, RedirectAttributes redirectAttributes) {
		redirectAttributes.addFlashAttribute("appointment", appointment);
		DoctorDAO doctorDAO = new DoctorDAO();
		redirectAttributes.addFlashAttribute("doctors",
				doctorDAO.getSpecializedDoctors(appointment.getType().getCode()));
		return "redirect:/set_doctor";
	}

	@GetMapping("/new_appointment")
	public String newAppointment(Model model) {
		model.addAttribute("appointment", new Appointment());

		// Possible specialties
		SpecialtyDAO specialtyDAO = new SpecialtyDAO();
		model.addAttribute("specialties", specialtyDAO.getSpecialties(true));
		specialtyDAO.closeConnection();

		// Possible patients
		PatientDAO patientDAO = new PatientDAO();
		model.addAttribute("patients", patientDAO.getPatients());
		patientDAO.closeConnection();

		return "new_appointment";
	}

	@PostMapping("/set_doctor")
	public String postAppointmentDoctor(@ModelAttribute Appointment appointment,
			RedirectAttributes redirectAttributes) {
		redirectAttributes.addFlashAttribute("appointment", appointment);

		return "redirect:/set_date";
	}

	@GetMapping("/set_doctor")
	public String newAppointmentDoctor(Model model, RedirectAttributes redirectAttributes) {
		model.addAttribute("appointment_patient", new Appointment());
		return "set_doctor";
	}

	@PostMapping("/set_date")
	public String postAppointmentDate(@ModelAttribute Appointment appointment, RedirectAttributes redirectAttributes) {
		redirectAttributes.addFlashAttribute("appointment", appointment);

		// Get available dates
		DoctorDAO doctorDAO = new DoctorDAO();

		ArrayList<String> timeList = new ArrayList<>();
		int timeIn = Integer.parseInt(appointment.getDoctor().getTimeIn().toString().subSequence(0, 2).toString());
		int timeOut = Integer.parseInt(appointment.getDoctor().getTimeOut().toString().subSequence(0, 2).toString());

		ArrayList<String> unTimes = doctorDAO.getUnavailableTimes(appointment.getDoctor().getCode(),
				appointment.getDate());
		for (int i = timeIn; i <= timeOut; i++) {
			String curTime = i + ":00:00";

			if (i / 10 < 1)
				curTime = "0" + curTime;

			if (LocalDate.now().isEqual(appointment.getDate())) {
				LocalTime temp = LocalTime.parse(curTime, DateTimeFormatter.ofPattern("HH:mm:ss"));
				System.out.println("curtime " + temp);
				System.out.println("now " + LocalTime.now());
				if (LocalTime.now().isAfter(temp)) {
					System.out.println("after");
					continue;
				}
			}
			
			System.out.println(unTimes.toString());
			if (!unTimes.contains(curTime))
				timeList.add(curTime);
		}

		doctorDAO.closeConnection();
		if (timeList.size() > 0) {
			redirectAttributes.addFlashAttribute("times", timeList);
			return "redirect:/set_time";
		} else {
			redirectAttributes.addFlashAttribute("appointment", appointment);
			redirectAttributes.addFlashAttribute("message", "not_available");
			return "redirect:/set_date";
		}
	}

	@GetMapping("/set_date")
	public String newAppointmentDate(Model model, RedirectAttributes redirectAttributes) {
		model.addAttribute("appointment_doctor", new Appointment());
		return "set_date";
	}

	@PostMapping("/set_time")
	public String postAppointmentTime(@ModelAttribute Appointment appointment, RedirectAttributes redirectAttributes) {
		AppointmentDAO appointmentDAO = new AppointmentDAO();
		String success = appointmentDAO.insertAppointment(appointment);
		appointmentDAO.closeConnection();
		setMessageAttributes(redirectAttributes, success, "consulta");
		return "redirect:/";
	}

	@GetMapping("/set_time")
	public String newAppointmentTime(Model model, RedirectAttributes redirectAttributes) {
		model.addAttribute("appointment_date", new Appointment());
		return "set_time";
	}

	@GetMapping("/view_patient/{id}")
	public String getPatientById(@PathVariable int id, RedirectAttributes redirectAttributes) {
		PatientDAO patientDAO = new PatientDAO();
		redirectAttributes.addFlashAttribute("patient", patientDAO.getPatient(id));
		patientDAO.closeConnection();
		
		AppointmentDAO appointmentDAO = new AppointmentDAO();
		redirectAttributes.addFlashAttribute("appointments", appointmentDAO.getAppointmentsFromPatient(id));
		appointmentDAO.closeConnection();
		
		return "redirect:/view_patient";
	}

	@GetMapping("/view_patient")
	public String viewPatient(Model model, RedirectAttributes redirectAttributes) {
		if(model.asMap().containsKey("patient"))
			return "view_patient";
		else
			return "redirect:/view_patients";
	}

	@GetMapping("/view_doctor/{id}")
	public String getDoctorById(@PathVariable int id, RedirectAttributes redirectAttributes) {
		DoctorDAO doctorDAO = new DoctorDAO();
		redirectAttributes.addFlashAttribute("doctor", doctorDAO.getDoctor(id, true));
		doctorDAO.closeConnection();
		
		AppointmentDAO appointmentDAO = new AppointmentDAO();
		redirectAttributes.addFlashAttribute("appointments", appointmentDAO.getAppointmentsFromDoctor(id));
		appointmentDAO.closeConnection();
		
		return "redirect:/view_doctor";
	}

	@GetMapping("/view_doctor")
	public String viewDoctor(Model model) {
		if(model.asMap().containsKey("doctor"))
			return "view_doctor";
		else
			return "redirect:/view_doctors";
	}

	@PostMapping("/pay_appointment")
	public String postPayment(@ModelAttribute Payment payment, RedirectAttributes redirectAttributes) {
		PaymentDAO paymentDAO = new PaymentDAO();
		String success = paymentDAO.makePayment(payment.getCode());
		paymentDAO.closeConnection();
		setMessageAttributes(redirectAttributes, success, "paciente");
		return "redirect:/view_appointment/" + payment.getCode();
	}

	@GetMapping("/view_appointment/{id}")
	public String getAppointmentById(@PathVariable int id, RedirectAttributes redirectAttributes) {
		AppointmentDAO appointmentDAO = new AppointmentDAO();
		redirectAttributes.addFlashAttribute("appointment", appointmentDAO.getAppointment(id));
		appointmentDAO.closeConnection();
		return "redirect:/view_appointment";
	}

	@GetMapping("/view_appointment")
	public String viewAppointment(Model model) {
		if(model.asMap().containsKey("appointment")) {
			model.addAttribute("payment", new Payment());
			model.addAttribute("temporary", new Appointment());
			return "view_appointment";
		} else {
			return "redirect:/view_appointments";
		}
			
	}

	@GetMapping("/view_appointments")
	public String viewAppointments(Model model) {
		AppointmentDAO appointmentDAO = new AppointmentDAO();
		ArrayList<Appointment> appointments = appointmentDAO.getAppointments();
		model.addAttribute("appointments", appointments);
		appointmentDAO.closeConnection();
		return "view_appointments";
	}

	@GetMapping("/view_doctors")
	public String viewDoctors(Model model) {
		DoctorDAO doctorDAO = new DoctorDAO();
		ArrayList<Doctor> doctors = doctorDAO.getDoctors();
		model.addAttribute("doctors", doctors);
		doctorDAO.closeConnection();
		return "view_doctors";
	}

	@GetMapping("/view_patients")
	public String viewPatients(Model model) {
		PatientDAO patientDAO = new PatientDAO();
		ArrayList<Patient> patients = patientDAO.getPatients();
		model.addAttribute("patients", patients);
		patientDAO.closeConnection();
		return "view_patients";
	}

	@PostMapping("/set_diagnosis")
	public String postDiagnosisAppointment(@ModelAttribute Appointment appointment,
			RedirectAttributes redirectAttributes) {
		redirectAttributes.addFlashAttribute("temporary", appointment);
		return "redirect:/new_diagnosis";
	}

	@PostMapping("/new_diagnosis")
	public String postDiagnosis(@ModelAttribute Diagnosis diagnosis, RedirectAttributes redirectAttributes) {
		DiagnosisDAO diagnosisDAO = new DiagnosisDAO();
		diagnosisDAO.insertDiagnosis(diagnosis);
		diagnosisDAO.closeConnection();
		return "redirect:/view_diagnosis/" + diagnosis.getAppointmentCode();
	}

	@GetMapping("/new_diagnosis")
	public String newDiagnosis(Model model) {
		model.addAttribute("diagnosis", new Diagnosis());

		// Possible diseases
		DiseaseDAO specialtyDAO = new DiseaseDAO();
		model.addAttribute("diseases", specialtyDAO.getDiseases());
		specialtyDAO.closeConnection();

		return "new_diagnosis";
	}

	@GetMapping("/view_diagnosis/{id}")
	public String getDiagnosisByAppointmentId(@PathVariable int id, RedirectAttributes redirectAttributes) {
		DiagnosisDAO diagnosisDAO = new DiagnosisDAO();
		redirectAttributes.addFlashAttribute("diagnosis", diagnosisDAO.getDiagnosisByAppointment(id));
		diagnosisDAO.closeConnection();
		return "redirect:/view_diagnosis";
	}

	@GetMapping("/view_diagnosis")
	public String viewDiagnosis(Model model) {
		if(model.asMap().containsKey("diagnosis"))
			return "view_diagnosis";
		else
			return "redirect:/view_appointments";
	}
}