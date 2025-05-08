package com.pandacare.mainapp.doctor_profile.controller;

import com.pandacare.mainapp.doctor_profile.model.DoctorProfile;
import com.pandacare.mainapp.doctor_profile.service.DoctorProfileService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/doctors")
public class DoctorProfileController {

    private final DoctorProfileService doctorProfileService;

    public DoctorProfileController(DoctorProfileService doctorProfileService) {
        this.doctorProfileService = doctorProfileService;
    }

    // Display all doctors (search page)
    @GetMapping
    public String showAllDoctors(Model model) {
        List<DoctorProfile> doctors = doctorProfileService.findAll();
        model.addAttribute("doctors", doctors);
        model.addAttribute("searchType", "name"); // default search type
        return "doctors/search";
    }

    // Search doctors by different criteria
    @GetMapping("/search")
    public String searchDoctors(
            @RequestParam(required = false, defaultValue = "name") String searchType,
            @RequestParam(required = false) String keyword,
            Model model) {

        List<DoctorProfile> doctors;

        if (keyword == null || keyword.trim().isEmpty()) {
            // No keyword provided, fallback to all doctors
            doctors = doctorProfileService.findAll();
            model.addAttribute("error", "Please enter a keyword to search.");
        } else {
            try {
                doctors = doctorProfileService.searchDoctorProfile(searchType, keyword);
            } catch (IllegalArgumentException e) {
                doctors = doctorProfileService.findAll();
                model.addAttribute("error", "Invalid search criteria.");
            }
        }

        model.addAttribute("doctors", doctors);
        model.addAttribute("searchType", searchType);
        model.addAttribute("keyword", keyword);
        return "doctors/search";
    }

    // Show doctor details
    @GetMapping("/{id}")
    public String showDoctorDetails(@PathVariable String id, Model model) {
        if (id == null) {
            return "redirect:/doctors";
        }

        DoctorProfile doctor = doctorProfileService.findById(id);
        if (doctor == null) {
            return "redirect:/doctors";
        }
        model.addAttribute("doctor", doctor);
        return "doctors/detail";
    }

    // Add new doctor (form)
    @GetMapping("/new")
    public String showAddDoctorForm(Model model) {
        model.addAttribute("doctor", new DoctorProfile());
        return "doctors/form";
    }

    // Save new doctor
    @PostMapping("/new/save")
    public String saveDoctor(@ModelAttribute DoctorProfile doctorProfile, RedirectAttributes redirectAttributes) {
        DoctorProfile created = doctorProfileService.createProfile(doctorProfile);
        if (created == null) {
            redirectAttributes.addFlashAttribute("error", "Failed to create doctor profile");
            return "redirect:/doctors/new";
        }
        return "redirect:/doctors/" + created.getId();
    }

    // Edit doctor (form)
    @GetMapping("/{id}/edit")
    public String showEditDoctorForm(@PathVariable String id, Model model) {
        DoctorProfile doctor = doctorProfileService.findById(id);
        if (doctor == null) {
            return "redirect:/doctors";
        }
        model.addAttribute("doctor", doctor);
        return "doctors/form";
    }

    // Update doctor
    @PostMapping("/{id}/save")
    public String updateDoctor(@PathVariable String id, @ModelAttribute DoctorProfile doctorProfile, RedirectAttributes redirectAttributes) {
        if (id == null) {
            return "redirect:/doctors";
        }

        DoctorProfile updated = doctorProfileService.updateProfile(doctorProfile);
        if (updated == null) {
            redirectAttributes.addFlashAttribute("error", "Failed to update doctor profile");
            return "redirect:/doctors/" + id + "/edit";
        }
        return "redirect:/doctors/" + id;
    }

    // Delete doctor
    @PostMapping("/{id}/delete")
    public String deleteDoctor(@PathVariable String id) {
        DoctorProfile doctor = doctorProfileService.findById(id);
        if (doctor != null) {
            doctorProfileService.deleteProfile(doctor);
        }
        return "redirect:/doctors";
    }
}