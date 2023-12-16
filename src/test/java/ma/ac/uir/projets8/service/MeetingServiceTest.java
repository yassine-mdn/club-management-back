package ma.ac.uir.projets8.service;

import ma.ac.uir.projets8.controller.MeetingController;
import ma.ac.uir.projets8.model.Personnel;
import ma.ac.uir.projets8.model.Meeting;
import ma.ac.uir.projets8.model.Student;
import ma.ac.uir.projets8.model.enums.Role;
import ma.ac.uir.projets8.repository.PersonnelRepository;
import ma.ac.uir.projets8.repository.MeetingRepository;
import ma.ac.uir.projets8.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class MeetingServiceTest {

    @Autowired
    private MeetingService meetingService;
    @Autowired
    private PersonnelRepository personnelRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private MeetingRepository meetingRepository;

    private Personnel personnel = new Personnel();

    private Student student1 = new Student();

    private Student student2 = new Student();

    private Meeting testMeeting = new Meeting();

    @BeforeEach
    public void setUp() {
        personnel.setEmail("testAdmin@mail.com");
        personnel.setFirstName("test");
        personnel.setLastName("testAdmin");
        personnel.setPassword("test");
        personnel.setRoles(List.of(Role.ADMIN));
        personnelRepository.save(personnel);

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
        testMeeting.setOrganiser(personnel);
        testMeeting.setParticipants(Set.of(student1, student2));
        meetingRepository.save(testMeeting);

    }

    @Test
    void getAllMeetings() {
        System.out.println();
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
        assertEquals(meeting.getOrganiser().getLastName(), personnel.getLastName());
    }

    @Test
    void deleteMeeting() {
        meetingService.deleteMeeting(1);
        assertThat(meetingRepository.findAll()).isEmpty();
    }
}