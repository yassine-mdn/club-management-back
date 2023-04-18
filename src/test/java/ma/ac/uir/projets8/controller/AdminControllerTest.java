package ma.ac.uir.projets8.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.ac.uir.projets8.model.Admin;
import ma.ac.uir.projets8.model.enums.Role;
import ma.ac.uir.projets8.repository.AdminRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@SpringBootTest
@WebAppConfiguration
@ContextConfiguration
class AdminControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private AdminRepository adminRepository;


    private MockMvc mockMvc;


    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation)).build();
    }

    @Test
    void addAdmin() throws Exception {

        AdminController.NewAdminRequest adminRequest = new AdminController.NewAdminRequest("lastname","firstname","test-email","password");
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(adminRequest);

        System.out.println(json);

        this.mockMvc.perform(post("/api/v1/admins").content(json).characterEncoding("utf-8"))
                .andDo(document("post",preprocessRequest(prettyPrint()),preprocessResponse(prettyPrint())));
    }
    @Test
    void getAllAdmins() throws Exception {

        Admin admin = new Admin();
        admin.setEmail("email");
        admin.setFirstName("yassine");
        admin.setLastName("mouddene");
        admin.setPassword("password");
        admin.setRoles(List.of(Role.ADMIN));

        Admin admin2 = new Admin();
        admin2.setEmail("email2");
        admin2.setFirstName("hamza");
        admin2.setLastName("messouab");
        admin2.setPassword("password");
        admin2.setRoles(List.of(Role.ADMIN));


        this.adminRepository.save(admin);
        this.adminRepository.save(admin2);

        this.mockMvc.perform(get("/api/v1/admins").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andDo(document("get all",preprocessRequest(prettyPrint()),preprocessResponse(prettyPrint())));
    }

    @Test
    void getAdminById() throws Exception {

        Admin admin = new Admin();
        admin.setEmail("email3");
        admin.setFirstName("yassine");
        admin.setLastName("mouddene");
        admin.setPassword("password");
        admin.setRoles(List.of(Role.ADMIN));


        this.adminRepository.save(admin);

        this.mockMvc.perform(get("/api/v1/admins/1").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andDo(document("index",preprocessRequest(prettyPrint()),preprocessResponse(prettyPrint())));
    }

    @Test
    void deleteAdmin() throws Exception {

        this.mockMvc.perform(delete("/api/v1/admins/1")).andExpect(status().isOk())
                .andDo(document("delete",preprocessRequest(prettyPrint()),preprocessResponse(prettyPrint())));
    }

    @Test
    void updateAdmin() throws Exception {

        this.mockMvc.perform(delete("/api/v1/admins/1")).andExpect(status().isOk())
                .andDo(document("delete",preprocessRequest(prettyPrint()),preprocessResponse(prettyPrint())));
    }
}