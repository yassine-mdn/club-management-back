package ma.ac.uir.projets8.service;


import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import ma.ac.uir.projets8.controller.ClubController.NewClubRequest;
import ma.ac.uir.projets8.exception.AccountNotFoundException;
import ma.ac.uir.projets8.exception.ClubNotFoundException;
import ma.ac.uir.projets8.exception.PageOutOfBoundsException;
import ma.ac.uir.projets8.model.*;
import ma.ac.uir.projets8.model.enums.ClubStatus;
import ma.ac.uir.projets8.model.enums.ClubType;
import ma.ac.uir.projets8.repository.ClubRepository;
import ma.ac.uir.projets8.repository.PersonnelRepository;
import ma.ac.uir.projets8.repository.StudentRepository;
import ma.ac.uir.projets8.util.NullChecker;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.dhatim.fastexcel.Color;
import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.Worksheet;
import org.dhatim.fastexcel.reader.Cell;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.*;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class ClubService {

    private final ClubRepository clubRepository;

    private final StudentRepository studentRepository;

    private final PersonnelRepository personnelRepository;

    public ResponseEntity<List<Club>> getAllClubs() {

        return ResponseEntity.ok(clubRepository.findAll());
    }

    public ResponseEntity<String> addClub(NewClubRequest request) {

        if (!ObjectUtils.allNotNull(request.name(), request.description(), request.committeeIds()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid request");
        Club club = new Club();
        club.setName(request.name());
        club.setDescription(request.description());
        club.setType(request.supervisorId() == null ? ClubType.NORMAL : ClubType.ACADEMIC);
        if (request.supervisorId() != null)
            club.addSupervisor(personnelRepository.findById(request.supervisorId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid supervisor id request")));
        club.addCommitteeMembers(studentRepository.findAllById(request.committeeIds()));
        club.setStatus(request.status() == null ? ClubStatus.CREATION_STEP_1 : request.status());
        club.addClubDetails(new ClubDetails());
        Budget budget = new Budget();
        budget.setClub(club);
        budget.setBudget_initial(0.0);
        budget.setBudget_restant(0.0);
        club.setBudget(budget);
        clubRepository.save(club);
        return new ResponseEntity<>("Club successfully added to database", HttpStatus.CREATED);
    }

    public ResponseEntity<Club> getClubById(Integer id) {

        try {
            return ResponseEntity.ok(clubRepository.findById(id).orElseThrow(() -> new ClubNotFoundException(id)));
        } catch (ClubNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    public ResponseEntity<String> updateClub(Integer id, NewClubRequest request) {
        clubRepository.findById(id).map(club -> {
                    if (!request.name().isEmpty())
                        club.setName(request.name());
                    if (!request.description().isEmpty())
                        club.setDescription(request.description());
                    if (!request.committeeIds().isEmpty())
                        club.addCommitteeMembers(studentRepository.findAllById(request.committeeIds()));
                    if (request.supervisorId() != null)
                        club.addSupervisor(personnelRepository.findById(request.supervisorId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid supervisor id request")));
                    if (request.status() != null)
                        club.setStatus(request.status());
                    return clubRepository.save(club);
                }
        ).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, new ClubNotFoundException(id).getMessage()));
        return new ResponseEntity<>("Club account with " + id + " successfully updated", HttpStatus.ACCEPTED);
    }

    public ResponseEntity<String> deleteClub(Integer id) {

        try {
            clubRepository.deleteById(id);
            return new ResponseEntity<>("Club successfully deleted", HttpStatus.OK);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, new ClubNotFoundException(id).getMessage());
        }
    }

    public ResponseEntity<List<Club>> getClubsByStatus(ClubStatus status) {

        return ResponseEntity.ok(clubRepository.findAllByStatus(status));
    }

    public ResponseEntity<List<Event>> getClubEvents(Integer id) {

        try {
            return ResponseEntity.ok(new ArrayList<>(clubRepository.findById(id).orElseThrow(() -> new ClubNotFoundException(id)).getEvents()));
        } catch (ClubNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    public ResponseEntity<List<Student>> getClubMembers(Integer id) {

        try {
            return ResponseEntity.ok(new ArrayList<>(clubRepository.findById(id).orElseThrow(() -> new ClubNotFoundException(id)).getMembers()));

        } catch (ClubNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    public void getClubMembersFile(Integer id, HttpServletResponse response) {

        try {
            List<Student> members = new ArrayList<>(clubRepository.findById(id).orElseThrow(() -> new ClubNotFoundException(id)).getMembers());
            String path = "C:/Users/yassi/Bureau/projet-s8-file-transfer/temp/rapport-"
                    + new SimpleDateFormat("dd-MM-yyyy").format(new Date()) + ".xlsx";
            try (OutputStream os = new FileOutputStream(path)) {

                Workbook wb = new Workbook(os, "MyApplication", "1.0");
                Worksheet ws = wb.newWorksheet("sheet 1");
                ws.range(0, 0, members.size(), 3).createTable()
                        .setName("test")
                        .setDisplayName("display name")
                        .styleInfo();
                ws.value(0, 0, "first name");
                ws.value(0, 1, "last name");
                ws.value(0, 2, "email");
                ws.value(0, 3, "Student id");
                for (int i = 0; i < members.size(); i++) {
                    ws.value(i + 1, 0, members.get(i).getFirstName());
                    ws.value(i + 1, 1, members.get(i).getLastName());
                    ws.value(i + 1, 2, members.get(i).getEmail());
                    ws.value(i + 1, 3, members.get(i).getStudentId());
                }
                ws.finish();
                wb.finish();


                File file = new File(path);
                response.setContentType("application/vnd.ms-excel");
                response.setHeader("Content-disposition", "attachment; filename=" + file.getName());

                OutputStream out = response.getOutputStream();
                FileInputStream in = new FileInputStream(file);

                // copy from in to out
                IOUtils.copy(in, out);

                out.close();
                in.close();
                os.close();
                file.delete();

            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
            }

        } catch (ClubNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    public String addMembersFromFile(MultipartFile excelFile, Integer id) throws IOException {

        try {

            Club club = clubRepository.findById(id).orElseThrow(() -> new ClubNotFoundException(id));
            excelFile.transferTo(new File("C:/Users/yassi/Bureau/projet-s8-file-transfer/temp/" + excelFile.getOriginalFilename()));
            try (FileInputStream file = new FileInputStream("C:/Users/yassi/Bureau/projet-s8-file-transfer/temp/" + excelFile.getOriginalFilename());
                 ReadableWorkbook wb = new ReadableWorkbook(file)) {
                Sheet sheet = wb.getFirstSheet();
                try (Stream<Row> rows = sheet.openStream()) {
                    rows.forEach(row -> {
                        if (row.getRowNum() != 1) {
                            Student student = new Student();
                            student.setFirstName(row.getCell(0).getRawValue());
                            student.setLastName(row.getCell(1).getRawValue());
                            student.setEmail(row.getCell(2).getRawValue());
                            student.setStudentId(Integer.valueOf(row.getCell(3).getRawValue()));
                            student.getJoinedClubs().add(club);
                            studentRepository.save(student);
                            club.getMembers().add(student);
                            clubRepository.save(club);
                        }
                    });
                }

            }
            //TODO: delete file
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
        return "members added successfully";
    }

    public ResponseEntity<List<Club>> getClubsPage(Integer pageNumber, Integer size) {

        if (pageNumber < 0 || size < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid request");
        Page<Club> resultPage = clubRepository.findAll(PageRequest.of(pageNumber, size));
        if (pageNumber > resultPage.getTotalPages()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, new PageOutOfBoundsException(pageNumber).getMessage());
        }
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("total-pages", String.valueOf(resultPage.getTotalPages()));
        return new ResponseEntity<>(resultPage.getContent(), responseHeaders, HttpStatus.OK);
    }


    public ResponseEntity<List<Club>> getCubsPageBySearch(String searchKeyWord, Integer pageNumber, Integer size) {
        if (pageNumber < 0 || size < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid request");
        Page<Club> resultPage = clubRepository.findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(searchKeyWord, searchKeyWord, PageRequest.of(pageNumber, size));
        if (pageNumber > resultPage.getTotalPages()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, new PageOutOfBoundsException(pageNumber).getMessage());
        }
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("total-pages", String.valueOf(resultPage.getTotalPages()));
        return new ResponseEntity<>(resultPage.getContent(), responseHeaders, HttpStatus.OK);
    }
}
