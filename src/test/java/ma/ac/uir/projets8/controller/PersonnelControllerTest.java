package ma.ac.uir.projets8.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.ac.uir.projets8.model.Personnel;
import ma.ac.uir.projets8.model.enums.Role;
import ma.ac.uir.projets8.repository.PersonnelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@SpringBootTest
@WebAppConfiguration
@ContextConfiguration
class PersonnelControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private PersonnelRepository personnelRepository;


    private MockMvc mockMvc;


    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation)).build();
    }

    @Test
    void addPersonnel() throws Exception {

        PersonnelController.NewPersonnelRequest personnelRequest = new PersonnelController.NewPersonnelRequest("lastname","firstname","test-email",Role.ADMIN,"password");
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(personnelRequest);

        System.out.println(json);

        this.mockMvc.perform(post("/api/v1/personnels").content(json).characterEncoding("utf-8"))
                .andDo(document("post",preprocessRequest(prettyPrint()),preprocessResponse(prettyPrint())));
    }
    @Test
    void getAllPersonnels() throws Exception {

        Personnel personnel = new Personnel();
        personnel.setEmail("email");
        personnel.setFirstName("yassine");
        personnel.setLastName("mouddene");
        personnel.setPassword("password");
        personnel.setRoles(List.of(Role.ADMIN));

        Personnel personnel2 = new Personnel();
        personnel2.setEmail("email2");
        personnel2.setFirstName("hamza");
        personnel2.setLastName("messouab");
        personnel2.setPassword("password");
        personnel2.setRoles(List.of(Role.ADMIN));


        this.personnelRepository.save(personnel);
        this.personnelRepository.save(personnel2);

        this.mockMvc.perform(get("/api/v1/personnels").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andDo(document("get all",preprocessRequest(prettyPrint()),preprocessResponse(prettyPrint())));
    }

    @Test
    void getPersonnelById() throws Exception {

        Personnel personnel = new Personnel();
        personnel.setEmail("email3");
        personnel.setFirstName("yassine");
        personnel.setLastName("mouddene");
        personnel.setPassword("password");
        personnel.setRoles(List.of(Role.ADMIN));


        this.personnelRepository.save(personnel);

        this.mockMvc.perform(get("/api/v1/personnels/1").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andDo(document("index",preprocessRequest(prettyPrint()),preprocessResponse(prettyPrint())));
    }

    @Test
    void deletePersonnel() throws Exception {

        this.mockMvc.perform(delete("/api/v1/personnels/1")).andExpect(status().isOk())
                .andDo(document("delete",preprocessRequest(prettyPrint()),preprocessResponse(prettyPrint())));
    }

    @Test
    void updatePersonnel() throws Exception {

        this.mockMvc.perform(delete("/api/v1/personnels/1")).andExpect(status().isOk())
                .andDo(document("delete",preprocessRequest(prettyPrint()),preprocessResponse(prettyPrint())));
    }
}