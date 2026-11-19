package com.ueb.katalog;

import com.ueb.katalog.config.SecurityConfig;
import com.ueb.katalog.domain.Course;
import com.ueb.katalog.domain.CourseCategory;
import com.ueb.katalog.domain.Instructor;
import com.ueb.katalog.repository.InstructorRepository;
import com.ueb.katalog.service.CourseService;
import com.ueb.katalog.service.InstructorService;
import com.ueb.katalog.web.CourseController;
import com.ueb.katalog.web.HomeController;
import com.ueb.katalog.web.InstructorController;
import com.ueb.katalog.web.LoginController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Testet përdorin vetëm vlera testuese dhe nuk lidhen me databazën.
@WebMvcTest(controllers = {LoginController.class, HomeController.class,
        CourseController.class, InstructorController.class},
        properties = "spring.config.location=classpath:/application-mvc-test.properties")
@Import(SecurityConfig.class)
class KatalogApplicationTests {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private CourseService courseService;

    @MockitoBean
    private InstructorService instructorService;

    @MockitoBean
    private InstructorRepository instructorRepository;

    @Test
    void anonymousRequestsRequireLogin() throws Exception {
        for (String path : new String[]{"/", "/courses", "/instructors", "/courses/new"}) {
            mvc.perform(get(path))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/login"));
        }
        mvc.perform(get("/css/style.css")).andExpect(status().isOk());
    }

    @Test
    void loginPageRendersFormWithCsrfAndMessages() throws Exception {
        mvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(content().string(containsString("name=\"_csrf\"")))
                .andExpect(content().string(not(containsString(">Instruktorët</a>"))));
        mvc.perform(get("/login?error"))
                .andExpect(content().string(containsString("Përdoruesi ose fjalëkalimi është gabim.")));
        mvc.perform(get("/login?logout"))
                .andExpect(content().string(containsString("U çkyçët me sukses.")));
    }

    @Test
    void authenticatedUserIsRedirectedToLastVisitedPageInsteadOfLogin() throws Exception {
        MockHttpSession session = new MockHttpSession();
        mvc.perform(get("/instructors").session(session).with(user("user")))
                .andExpect(status().isOk());

        mvc.perform(get("/login").session(session).with(user("user")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/instructors"));
    }

    @Test
    void authenticatedUserWithoutPreviousPageIsRedirectedToCourses() throws Exception {
        mvc.perform(get("/login").with(user("user")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/courses"));
    }

    @Test
    void bothUsersLoginAndAlwaysRedirectToCourses() throws Exception {
        for (String username : new String[]{"user", "admin"}) {
            MockHttpSession session = new MockHttpSession();
            mvc.perform(get("/instructors").session(session));
            mvc.perform(post("/login").session(session).with(csrf())
                            .param("username", username)
                            .param("password", "test-only-" + username + "-password"))
                    .andExpect(authenticated().withUsername(username))
                    .andExpect(redirectedUrl("/courses"));
        }
    }

    @Test
    void wrongPasswordRedirectsToError() throws Exception {
        mvc.perform(formLogin().user("user").password("test-only-wrong-password"))
                .andExpect(unauthenticated())
                .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    void authenticatedNavbarContainsUsernameAndLogoutForm() throws Exception {
        mvc.perform(get("/courses").with(user("user")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(">Instruktorët</a>")))
                .andExpect(content().string(containsString(">user</span>")))
                .andExpect(content().string(containsString("action=\"/logout\"")))
                .andExpect(content().string(containsString("name=\"_csrf\"")));
    }

    @Test
    void userCanReadListsButCannotAccessManagementRoutes() throws Exception {
        Instructor instructor = new Instructor("Arta", "Krasniqi", "arta@example.com", "Java");
        instructor.setId(1L);
        Course course = new Course("Java bazë", null, CourseCategory.PROGRAMMING,
                new BigDecimal("10.00"), 10, LocalDate.now(), LocalDate.now().plusDays(30), instructor);
        course.setId(1L);
        when(courseService.findAll()).thenReturn(List.of(course));
        when(courseService.findById(1L)).thenReturn(course);
        when(instructorService.findAll()).thenReturn(List.of(instructor));

        mvc.perform(get("/courses").with(user("user")))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("href=\"/courses/new\""))))
                .andExpect(content().string(not(containsString("Veprimet"))))
                .andExpect(content().string(not(containsString("/courses/1/edit"))))
                .andExpect(content().string(not(containsString("/courses/1/delete"))));
        mvc.perform(get("/instructors").with(user("user")))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("href=\"/instructors/new\""))))
                .andExpect(content().string(not(containsString("Veprimet"))))
                .andExpect(content().string(not(containsString("/instructors/1/edit"))))
                .andExpect(content().string(not(containsString("/instructors/1/delete"))));
        mvc.perform(get("/courses/1").with(user("user")))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("/courses/1/edit"))));

        for (String path : new String[]{
                "/courses/new", "/courses/1/edit",
                "/instructors/new", "/instructors/1/edit"}) {
            mvc.perform(get(path).with(user("user")))
                    .andExpect(status().isForbidden());
        }
        for (String path : new String[]{
                "/courses", "/courses/1/edit", "/courses/1/delete",
                "/instructors", "/instructors/1/edit", "/instructors/1/delete"}) {
            mvc.perform(post(path).with(user("user")).with(csrf()))
                    .andExpect(status().isForbidden());
        }
    }

    @Test
    void adminCanAccessManagementFormsAndActions() throws Exception {
        Instructor instructor = new Instructor("Arta", "Krasniqi", "arta@example.com", "Java");
        instructor.setId(1L);
        Course course = new Course("Java bazë", null, CourseCategory.PROGRAMMING,
                new BigDecimal("10.00"), 10, LocalDate.now(), LocalDate.now().plusDays(30), instructor);
        course.setId(1L);
        when(courseService.findAll()).thenReturn(List.of(course));
        when(courseService.findById(1L)).thenReturn(course);
        when(instructorService.findAll()).thenReturn(List.of(instructor));
        when(instructorService.findById(1L)).thenReturn(instructor);

        mvc.perform(get("/courses").with(user("admin").roles("ADMIN", "USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("href=\"/courses/new\"")))
                .andExpect(content().string(containsString("/courses/1/edit")))
                .andExpect(content().string(containsString("/courses/1/delete")));
        mvc.perform(get("/instructors").with(user("admin").roles("ADMIN", "USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("href=\"/instructors/new\"")))
                .andExpect(content().string(containsString("/instructors/1/edit")))
                .andExpect(content().string(containsString("/instructors/1/delete")));
        mvc.perform(get("/courses/1").with(user("admin").roles("ADMIN", "USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("/courses/1/edit")));

        for (String path : new String[]{
                "/courses/new", "/courses/1/edit",
                "/instructors/new", "/instructors/1/edit"}) {
            mvc.perform(get(path).with(user("admin").roles("ADMIN", "USER")))
                    .andExpect(status().isOk());
        }
        for (String path : new String[]{"/courses", "/courses/1/edit", "/instructors", "/instructors/1/edit"}) {
            mvc.perform(post(path).with(user("admin").roles("ADMIN", "USER")).with(csrf()))
                    .andExpect(status().isOk());
        }
        for (String path : new String[]{"/courses/1/delete", "/instructors/1/delete"}) {
            mvc.perform(post(path).with(user("admin").roles("ADMIN", "USER")).with(csrf()))
                    .andExpect(status().is3xxRedirection());
        }
    }

    @Test
    void anonymousLogoutRequiresLoginEvenWithCsrf() throws Exception {
        mvc.perform(post("/logout").with(csrf()))
                .andExpect(unauthenticated())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void logoutRequiresCsrfAndInvalidatesSession() throws Exception {
        MockHttpSession session = (MockHttpSession) mvc.perform(
                        formLogin().user("user").password("test-only-user-password"))
                .andReturn().getRequest().getSession(false);
        mvc.perform(post("/logout").session(session))
                .andExpect(status().isForbidden());
        mvc.perform(post("/logout").session(session).with(csrf()))
                .andExpect(unauthenticated())
                .andExpect(redirectedUrl("/login?logout"));
        assertTrue(session.isInvalid());
        mvc.perform(get("/courses"))
                .andExpect(redirectedUrl("/login"));
    }
}
