package com.ueb.katalog;

import com.ueb.katalog.config.SecurityConfig;
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

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    void userCanAccessFormsAndDeleteWithCsrf() throws Exception {
        mvc.perform(get("/courses/new").with(user("user")))
                .andExpect(status().isOk());
        mvc.perform(get("/instructors/new").with(user("user")))
                .andExpect(status().isOk());
        for (String path : new String[]{"/courses/1/delete", "/instructors/1/delete"}) {
            mvc.perform(post(path).with(user("user")).with(csrf()))
                    .andExpect(status().is3xxRedirection());
            mvc.perform(post(path).with(user("user")))
                    .andExpect(status().isForbidden());
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
