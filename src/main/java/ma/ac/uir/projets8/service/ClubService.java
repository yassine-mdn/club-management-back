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
import ma.ac.uir.projets8.model.enums.EventStatus;
import ma.ac.uir.projets8.model.enums.Role;
import ma.ac.uir.projets8.repository.ClubRepository;
import ma.ac.uir.projets8.repository.EventRepository;
import ma.ac.uir.projets8.repository.PersonnelRepository;
import ma.ac.uir.projets8.repository.StudentRepository;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.Worksheet;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class ClubService {

    private final ClubRepository clubRepository;

    private final EventRepository eventRepository;

    private final StudentRepository studentRepository;

    private final PersonnelRepository personnelRepository;

    private final AuthenticatedDetailsService authenticatedDetailsService;

    public ResponseEntity<List<Club>> getAllClubs() {

        return ResponseEntity.ok(clubRepository.findAll());
    }

    @CacheEvict(value = {"clubs", "clubsDetails"}, allEntries = true)
    public Club addClub(NewClubRequest request) {

        if (!ObjectUtils.allNotNull(request.name(), request.description(), request.committeeIds()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid request");
        Club club = new Club();
        club.setName(request.name());
        club.setDescription(request.description());
        club.setType(request.supervisorId() == null ? ClubType.NORMAL : ClubType.ACADEMIC);
        if (request.supervisorId() != null)
            club.addSupervisor(personnelRepository.findById(request.supervisorId())
                    .orElseThrow(AccountNotFoundException::new));
        club.addCommitteeMembers(studentRepository.findAllById(request.committeeIds()));
        club.setStatus(request.status() == null ? ClubStatus.CREATION_STEP_1 : request.status());
        club.setFeatured(false);
        club.addClubDetails(new ClubDetails());

        // after update, club have set<budget> instead of budget attribute
        // the budget instance will be created by an admin user
        // the mapping with the club will be done automatically

//        Budget budget = new Budget();
//        budget.setClub(club);
//        budget.setBudget_initial(0.0);
//        budget.setBudget_restant(0.0);
//        club.setBudget(budget);

        club.setCreationDate(Instant.now());
        return clubRepository.save(club);
    }

    public Club getClubById(Integer id) {
        return clubRepository.findById(id).orElseThrow(() -> new ClubNotFoundException(id));
    }

    @CacheEvict(value = {"clubs", "clubsDetails"}, allEntries = true)
    public Club updateClub(Integer id, NewClubRequest request) {
        return clubRepository.findById(id).map(club -> {
                    if (request.name() != null)
                        club.setName(request.name());
                    if (request.description() != null)
                        club.setDescription(request.description());
                    if (request.committeeIds() != null)
                        club.addCommitteeMembers(studentRepository.findAllById(request.committeeIds()));
                    if (request.supervisorId() != null)
                        club.addSupervisor(personnelRepository.findById(request.supervisorId())
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid supervisor id request")));
                    if (request.featured() != null)
                        club.setFeatured(request.featured());
                    return clubRepository.save(club);
                }
        ).orElseThrow(() -> new ClubNotFoundException(id));
    }

    public Club updateClubStatus(Integer id, ClubStatus status) {

        Account account = authenticatedDetailsService.getAuthenticatedAccount();

        return clubRepository.findById(id).map(club -> {
                    if (club.getType().equals(ClubType.ACADEMIC) || !Objects.equals(club.getSupervisor().getIdA(), account.getIdA()))
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "you are not allowed to change this club status");
                    club.setStatus(status);
                    return clubRepository.save(club);
                }
        ).orElseThrow(() -> new ClubNotFoundException(id));

    }

    //TODO: add service for club president to make a president change request (email ytsafet l admin)
    //  gha tgeneri email fih lien (za3ma end point) l admin bach yaccepti l request
    public Club changeClubPresident(Integer clubId, Integer newPresidentId) {
        Account account = authenticatedDetailsService.getAuthenticatedAccount();
        if (!CollectionUtils.containsAny(account.getRoles(), List.of(Role.PROF, Role.ADMIN))) {
            //TODO : forward to admin or prof
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "you are not allowed to change this club president request has been forwarded to admin/prof");
        }
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new ClubNotFoundException(clubId));
        Student newPresident = studentRepository.findById(newPresidentId)
                .orElseThrow(AccountNotFoundException::new);
        Student oldPresident = studentRepository.findById(clubId)
                .orElseThrow(AccountNotFoundException::new);
        newPresident.setRoles(List.of(Role.PRESIDENT));
        oldPresident.setRoles(List.of(Role.STUDENT));
        newPresident.setMangedClub(club);
        oldPresident.setMangedClub(null);
        club.getCommitteeMembers().remove(oldPresident);
        club.getCommitteeMembers().add(newPresident);
        studentRepository.save(newPresident);
        studentRepository.save(oldPresident);
        return clubRepository.save(club);
    }

    //TODO : add service for club president to make a club name change request (email ytsafet l admin)
    //  gha tgeneri email fih lien (za3ma end point) l admin bach yaccepti l request


    public Club abandonClub(Integer id) {
        return clubRepository.findById(id).map(club -> {
                    club.setStatus(ClubStatus.ABANDONED);
                    club.setMembers(new HashSet<>());
                    club.getCommitteeMembers().forEach(student -> {
                        student.setMangedClub(null);
                        student.setRoles(List.of(Role.STUDENT));
                        studentRepository.save(student);
                    });
                    return clubRepository.save(club);
                }
        ).orElseThrow(() -> new ClubNotFoundException(id));
    }

    @CacheEvict(value = {"clubs", "clubsDetails"}, allEntries = true)
    public void deleteClub(Integer id) {

        try {
            clubRepository.deleteById(id);
        } catch (RuntimeException e) {
            throw new ClubNotFoundException(id);
        }
    }

    public Page<Club> getClubsByStatus(ClubStatus status, Integer pageNumber, Integer pageSize) {
        return clubRepository.findAllByStatus(status, PageRequest.of(pageNumber, pageSize));
    }

    public Page<Event> getClubEvents(Integer id, Integer pageNumber, Integer pageSize, String searchKeyword, List<EventStatus> statusList) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("date").descending());
        return eventRepository.findAllByFilterByOrganisateurIdc(id, statusList, searchKeyword, pageable);
    }

    public Page<Student> getClubMembers(Integer id, Integer pageNumber, Integer pageSize) {

        Club club = clubRepository.findById(id).orElseThrow(() -> new ClubNotFoundException(id));
        return studentRepository.findAllByJoinedClubs(club, PageRequest.of(pageNumber, pageSize));
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
                ws.value(0, 0, "Student id");
                ws.value(0, 1, "first name");
                ws.value(0, 2, "last name");
                ws.value(0, 3, "email");
                ws.value(0, 4, "major");
                ws.value(0, 5, "level");
                for (int i = 0; i < members.size(); i++) {
                    ws.value(i + 1, 0, members.get(i).getFirstName());
                    ws.value(i + 1, 1, members.get(i).getLastName());
                    ws.value(i + 1, 2, members.get(i).getEmail());
                    ws.value(i + 1, 3, members.get(i).getStudentId());
                    ws.value(i + 1, 4, members.get(i).getMajor());
                    ws.value(i + 1, 5, members.get(i).getLevel());
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

    public void addMembersFromFile(MultipartFile excelFile, Integer id) throws IOException {

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
    }


    @Cacheable(value = "clubs")
    public ResponseEntity<List<Club>> getClubsPage(Integer pageNumber, Integer pageSize) {

        if (pageNumber < 0 || pageSize < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid request");
        Page<Club> resultPage = clubRepository.findAll(PageRequest.of(pageNumber, pageSize));
        if (pageNumber > resultPage.getTotalPages()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, new PageOutOfBoundsException(pageNumber).getMessage());
        }
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("total-pages", String.valueOf(resultPage.getTotalPages()));
        return new ResponseEntity<>(resultPage.getContent(), responseHeaders, HttpStatus.OK);
    }


    public Page<Club> getCubsPageFiltered(
            String searchKeyWord,
            Integer pageNumber,
            Integer pageSize,
            List<ClubStatus> statusList,
            List<ClubType> clubTypes) {

        return clubRepository.findAllFiltered(
                clubTypes,
                statusList,
                searchKeyWord,
                PageRequest.of(pageNumber, pageSize)
        );
    }

    public ResponseEntity<List<Club>> getPendingClubs(Integer pageNumber, Integer pageSize) {
        if (pageNumber < 0 || pageSize < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid request");
        Page<Club> resultPage = clubRepository.findAllByStatusIn(
                List.of(ClubStatus.CREATION_STEP_2, ClubStatus.CREATION_STEP_1, ClubStatus.CREATION_STEP_3),
                PageRequest.of(pageNumber, pageSize).withSort(Sort.by(Sort.Direction.DESC, "creationDate")));
        if (pageNumber > resultPage.getTotalPages()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, new PageOutOfBoundsException(pageNumber).getMessage());
        }
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("total-pages", String.valueOf(resultPage.getTotalPages()));
        return new ResponseEntity<>(resultPage.getContent(), responseHeaders, HttpStatus.OK);
    }

    public ResponseEntity<List<Club>> getFeaturedClubs(Integer pageNumber, Integer pageSize) {
        if (pageNumber < 0 || pageSize < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid request");
        Page<Club> resultPage = clubRepository.findAllByFeatured(true, PageRequest.of(pageNumber, pageSize));
        if (pageNumber > resultPage.getTotalPages()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, new PageOutOfBoundsException(pageNumber).getMessage());
        }
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("total-pages", String.valueOf(resultPage.getTotalPages()));
        return new ResponseEntity<>(resultPage.getContent(), responseHeaders, HttpStatus.OK);
    }

    public List<Budget> getClubBudgets(Integer id) {

        Club club = clubRepository.findById(id).orElseThrow(() -> new ClubNotFoundException(id));
        return new ArrayList<>(club.getBudgets());
    }


    public Page<Club> getMyClubs(Integer pageNumber, Integer pageSize) {
        Account account = authenticatedDetailsService.getAuthenticatedAccount();
        return clubRepository.findAllManagedClubs(account.getIdA(), PageRequest.of(pageNumber, pageSize));
    }
}
