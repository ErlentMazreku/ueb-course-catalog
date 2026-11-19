package com.ueb.katalog.web;

import com.ueb.katalog.domain.Instructor;
import com.ueb.katalog.service.InstructorService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// Faqet e instruktorëve: lista, krijimi, ndryshimi dhe fshirja
@Controller
@RequestMapping("/instructors")
public class InstructorController {

    private final InstructorService instructorService;

    public InstructorController(InstructorService instructorService) {
        this.instructorService = instructorService;
    }

    // Lista e të gjithë instruktorëve
    @GetMapping
    public String list(Model model) {
        model.addAttribute("instructors", instructorService.findAll());
        return "instructors/list";
    }

    // Forma bosh për instruktor të ri
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("instructor", new Instructor());
        return "instructors/form";
    }

    // Ruajtja e instruktorit të ri
    @PostMapping
    public String create(@Valid @ModelAttribute("instructor") Instructor instructor,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        // Instruktor i ri: id nuk merret kurrë nga forma
        instructor.setId(null);
        checkEmail(instructor, bindingResult);
        if (bindingResult.hasErrors()) {
            return "instructors/form";
        }
        instructorService.save(instructor);
        redirectAttributes.addFlashAttribute("successMessage", "Instruktori u shtua me sukses.");
        return "redirect:/instructors";
    }

    // Forma e mbushur me të dhënat e instruktorit
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("instructor", instructorService.findById(id));
        return "instructors/form";
    }

    // Ruajtja e ndryshimeve
    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("instructor") Instructor instructor,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        // 404 nëse instruktori nuk ekziston
        instructorService.findById(id);
        // id merret nga URL-ja, që të ndryshohet rreshti ekzistues e jo të krijohet i ri
        instructor.setId(id);
        checkEmail(instructor, bindingResult);
        if (bindingResult.hasErrors()) {
            return "instructors/form";
        }
        instructorService.save(instructor);
        redirectAttributes.addFlashAttribute("successMessage", "Instruktori u ndryshua me sukses.");
        return "redirect:/instructors";
    }

    // Fshirja (vetëm me POST, nga butoni Fshij)
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        // Instruktori me kurse mbetet; kurset do të mbeteshin pa instruktor
        if (instructorService.hasCourses(id)) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Ky instruktor ka kurse. Fshijini ose ndërroni instruktorin e tyre fillimisht.");
            return "redirect:/instructors";
        }
        instructorService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Instruktori u fshi me sukses.");
        return "redirect:/instructors";
    }

    // E-maili i dyfishtë shfaqet si gabim pranë fushës email, jo si gabim i databazës
    private void checkEmail(Instructor instructor, BindingResult bindingResult) {
        if (bindingResult.hasFieldErrors("email")) {
            return;
        }
        if (instructorService.isEmailTaken(instructor.getEmail(), instructor.getId())) {
            bindingResult.rejectValue("email", "duplicate", "Ky e-mail përdoret nga një instruktor tjetër.");
        }
    }
}
