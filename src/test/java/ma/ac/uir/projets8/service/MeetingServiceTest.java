package ma.ac.uir.projets8.service;

import ma.ac.uir.projets8.controller.MeetingController;
import ma.ac.uir.projets8.model.Account;
import ma.ac.uir.projets8.model.Admin;
import ma.ac.uir.projets8.model.Meeting;
import ma.ac.uir.projets8.model.Student;
import ma.ac.uir.projets8.model.enums.Role;
import ma.ac.uir.projets8.repository.AdminRepository;
import ma.ac.uir.projets8.repository.MeetingRepository;
import ma.ac.uir.projets8.repository.StudentRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class MeetingServiceTest {

    @Autowired
    private MeetingService meetingService;
    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private MeetingRepository meetingRepository;

    private Admin admin = new Admin();

    private Student student1 = new Student();

    private Student student2 = new Student();

    private Meeting testMeeting = new Meeting();

    @BeforeEach
    public void setUp() {
        admin.setEmail("testAdmin@mail.com");
        admin.setFirstName("test");
        admin.setLastName("testAdmin");
        admin.setPassword("test");
        admin.setRoles(List.of(Role.ADMIN));
        adminRepository.save(admin);

        student1.setEmail("sudent1@mail.com");
        student1.setFirstName("test");
        student1.setLastName("testStudent1");
        student1.setPassword("test");
        student1.setRoles(List.of(Role.STUDENT, Role.PRESIDENT));
        studentRepository.save(student1);

        student2.setEmail("sudent2@mail.com");
        student2.setFirstName("test");
        student2.setLastName("testStudent2");
        student2.setPassword("test");
        student2.setRoles(List.of(Role.STUDENT, Role.VICE_PRESIDENT));
        studentRepository.save(student2);

        testMeeting.setTitle("test");
        testMeeting.setDescription("test");
        testMeeting.setLengthInMinutes(60);
        testMeeting.setOrganiser(admin);
        testMeeting.setParticipants(Set.of(student1, student2));
        meetingRepository.save(testMeeting);

    }

    @Test
    void getAllMeetings() {
        System.out.println();
    }

    @Test
    void addMeeting() {
        MeetingController.NewMeetingRequest meeting = new MeetingController.NewMeetingRequest(
                "test",
                null,
                "test",
                60,
                admin.getIdA(),
                List.of(student1.getIdA(), student2.getIdA())
        );
        meetingService.addMeeting(meeting);
        Meeting meeting2 = meetingService.getMeetingById(2);
        assertThat(meeting2).isNotNull();
        assertEquals(meeting2.getOrganiser().getLastName(), admin.getLastName());

    }

    @Test
    void getMeetingById() {
        Meeting meeting = meetingService.getMeetingById(1);
        assertThat(meeting).isNotNull();
        System.out.println(meeting.getTitle());
    }

    @Test
    void updateMeetingParticipants() {
        MeetingController.NewMeetingRequest request = new MeetingController.NewMeetingRequest(
                "",
                null,
                "",
                null,
                null,
                List.of(student1.getIdA())
        );
        meetingService.updateMeeting(1, request);
        Meeting meeting = meetingService.getMeetingById(1);
        assertEquals(meeting.getParticipants().size(), 1);
    }

    @Test
    void getMeetingParticipants() {
        Meeting meeting = meetingService.getMeetingById(1);
        assertEquals(meeting.getParticipants().size(), 2);
    }

    @Test
    void getMeetingOrganiser() {
        Meeting meeting = meetingService.getMeetingById(1);
        assertEquals(meeting.getOrganiser().getLastName(), admin.getLastName());
    }

    @Test
    void deleteMeeting() {
        meetingService.deleteMeeting(1);
        assertThat(meetingRepository.findAll()).isEmpty();
    }
}