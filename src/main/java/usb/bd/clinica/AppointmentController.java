package usb.bd.clinica;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import dataaccess.PatientDAO;
import system.Patient;

public class AppointmentController extends DefaultController {
	@PostMapping("/new_appointment")
	public String postAppointment(@ModelAttribute Patient patient, RedirectAttributes redirectAttributes) {
		PatientDAO patientDAO = new PatientDAO();
		String success = patientDAO.insertPatient(patient);
		patientDAO.closeConnection();
		redirectAttributes.addFlashAttribute("message", success);
		return "redirect:/";
	}
    
    @GetMapping("/new_appointment")
    public String newAppointment(Model model) {
    	model.addAttribute("appointment", new Patient());
        return "new_appointment";
    }
}
