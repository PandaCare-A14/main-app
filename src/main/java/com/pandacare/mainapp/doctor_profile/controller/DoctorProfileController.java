package com.pandacare.mainapp.doctor_profile.controller;

import com.pandacare.mainapp.doctor_profile.model.DoctorProfile;
import com.pandacare.mainapp.doctor_profile.service.DoctorProfileService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
            @RequestParam String searchType,
            @RequestParam String keyword,
            Model model) {

        List<DoctorProfile> doctors;

        try {
            doctors = doctorProfileService.searchDoctorProfile(searchType, keyword);
        } catch (IllegalArgumentException e) {
            // If invalid search type, fall back to showing all doctors
            doctors = doctorProfileService.findAll();
            model.addAttribute("error", "Invalid search criteria");
        }

        model.addAttribute("doctors", doctors);
        model.addAttribute("searchType", searchType);
        model.addAttribute("keyword", keyword);
        return "doctors/search";
    }

    // Show doctor details
    @GetMapping("/{id}")
    public String showDoctorDetails(@PathVariable String id, Model model) {
        DoctorProfile doctor = doctorProfileService.findById(id);
        if (doctor == null) {
            // Handle doctor not found case
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
    @PostMapping
    public String saveDoctor(@ModelAttribute DoctorProfile doctorProfile) {
        DoctorProfile created = doctorProfileService.createProfile(doctorProfile);
        if (created == null) {
            // Handle creation failure (e.g., duplicate ID)
            return "redirect:/doctors/new?error";
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
    @PostMapping("/{id}")
    public String updateDoctor(@PathVariable String id, @ModelAttribute DoctorProfile doctorProfile) {
        DoctorProfile updated = doctorProfileService.updateProfile(doctorProfile);
        if (updated == null) {
            // Handle update failure
            return "redirect:/doctors/" + id + "/edit?error";
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