package com.ueb.katalog.web;

import com.ueb.katalog.domain.Course;
import com.ueb.katalog.domain.CourseCategory;
import com.ueb.katalog.service.CourseService;
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

// Faqet e kurseve: lista, detajet, krijimi, ndryshimi dhe fshirja
@Controller
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;
    private final InstructorService instructorService;

    public CourseController(CourseService courseService, InstructorService instructorService) {
        this.courseService = courseService;
        this.instructorService = instructorService;
    }

    // Lista e të gjitha kurseve
    @GetMapping
    public String list(Model model) {
        model.addAttribute("courses", courseService.findAll());
        return "courses/list";
    }

    // Detajet e një kursi
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("course", courseService.findById(id));
        return "courses/detail";
    }

    // Forma bosh për kurs të ri
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("course", new Course());
        addFormData(model);
        return "courses/form";
    }

    // Ruajtja e kursit të ri
    @PostMapping
    public String create(@Valid @ModelAttribute("course") Course course,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        // Kurs i ri: id nuk merret kurrë nga forma
        course.setId(null);
        if (bindingResult.hasErrors()) {
            addFormData(model);
            return "courses/form";
        }
        courseService.save(course);
        redirectAttributes.addFlashAttribute("successMessage", "Kursi u shtua me sukses.");
        return "redirect:/courses";
    }

    // Forma e mbushur me të dhënat e kursit
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("course", courseService.findById(id));
        addFormData(model);
        return "courses/form";
    }

    // Ruajtja e ndryshimeve
    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("course") Course course,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        // 404 nëse kursi nuk ekziston
        courseService.findById(id);
        // id merret nga URL-ja, që të ndryshohet rreshti ekzistues e jo të krijohet i ri
        course.setId(id);
        if (bindingResult.hasErrors()) {
            addFormData(model);
            return "courses/form";
        }
        courseService.save(course);
        redirectAttributes.addFlashAttribute("successMessage", "Kursi u ndryshua me sukses.");
        return "redirect:/courses";
    }

    // Fshirja (vetëm me POST, nga butoni Fshij)
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        courseService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Kursi u fshi me sukses.");
        return "redirect:/courses";
    }

    // Lista e instruktorëve dhe e kategorive për <select>; duhen edhe kur forma kthehet me gabime
    private void addFormData(Model model) {
        model.addAttribute("instructors", instructorService.findAll());
        model.addAttribute("categories", CourseCategory.values());
    }
}
