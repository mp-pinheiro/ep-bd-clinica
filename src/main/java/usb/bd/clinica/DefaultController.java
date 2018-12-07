package usb.bd.clinica;

import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import dataaccess.AppointmentDAO;
import dataaccess.DirectorDAO;
import dataaccess.DoctorDAO;
import dataaccess.PatientDAO;
import dataaccess.PaymentDAO;
import dataaccess.SpecialtyDAO;
import system.Appointment;
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

	// Patient
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

	// Doctor
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

	// Appointment
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
		model.addAttribute("specialties", specialtyDAO.getSpecialties());
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
		int timeIn = Integer.parseInt(appointment.getDoctor().getTimeIn().subSequence(0, 2).toString());
		int timeOut = Integer.parseInt(appointment.getDoctor().getTimeOut().subSequence(0, 2).toString());

		ArrayList<String> unTimes = doctorDAO.getUnavailableTimes(appointment.getDoctor().getCode(),
				appointment.getDate());
		for (int i = timeIn; i <= timeOut; i++) {
			String curTime = i + ":00:00";
			if (i / 10 < 1)
				curTime = "0" + curTime;
			if (!unTimes.contains(curTime))
				timeList.add(curTime);
		}

		redirectAttributes.addFlashAttribute("times", timeList);
		doctorDAO.closeConnection();

		return "redirect:/set_time";
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
		return "redirect:/view_patient";
	}

	@GetMapping("/view_patient")
	public String viewPatient(Model model) {
		return "view_patient";
	}
	
	@GetMapping("/view_doctor/{id}")
	public String getDoctorById(@PathVariable int id, RedirectAttributes redirectAttributes) {
		DoctorDAO doctorDAO = new DoctorDAO();
		redirectAttributes.addFlashAttribute("doctor", doctorDAO.getDoctor(id, true));
		doctorDAO.closeConnection();
		return "redirect:/view_doctor";
	}

	@GetMapping("/view_doctor")
	public String viewDoctor(Model model) {
		return "view_doctor";
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
		model.addAttribute("payment", new Payment());
		return "view_appointment";
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
}