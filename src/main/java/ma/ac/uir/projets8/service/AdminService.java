package ma.ac.uir.projets8.service;

import lombok.RequiredArgsConstructor;
import ma.ac.uir.projets8.controller.AdminController.*;
import ma.ac.uir.projets8.exception.AccountNotFoundException;
import ma.ac.uir.projets8.exception.PageOutOfBoundsException;
import ma.ac.uir.projets8.model.Admin;
import ma.ac.uir.projets8.model.Meeting;
import ma.ac.uir.projets8.repository.AdminRepository;
import ma.ac.uir.projets8.util.NullChecker;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static ma.ac.uir.projets8.model.enums.Role.ADMIN;

@RequiredArgsConstructor
@Service
public class AdminService {

    private final AdminRepository adminRepository;

    public ResponseEntity<List<Admin>> getAllAdmins() {
        return ResponseEntity.ok(adminRepository.findAll());
    }

    public ResponseEntity<String> addAdmin(@RequestBody NewAdminRequest request) {

        if (NullChecker.hasNull(request))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid request");
        if (adminRepository.existsByEmail(request.email()))
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Email already exists");
        Admin admin = new Admin();
        admin.setLastName(request.lastName());
        admin.setFirstName(request.firstName());
        admin.setEmail(request.email());
        admin.setPassword(request.password());
        admin.setRoles(List.of(ADMIN));
        adminRepository.save(admin);
        return new ResponseEntity<>("Admin account successfully created", HttpStatus.CREATED);
    }

    public ResponseEntity<Admin> getAdminById(Integer id) {
        try {
            return ResponseEntity.ok(adminRepository.findById(id).orElseThrow(() -> new AccountNotFoundException(id)));
        } catch (AccountNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }

    }

    public ResponseEntity<String> updateAdmin(Integer id, NewAdminRequest request) {
        adminRepository.findById(id)
                .map(admin -> {
                            if (!request.firstName().isEmpty())
                                admin.setFirstName(request.firstName());
                            if (!request.lastName().isEmpty())
                                admin.setLastName(request.lastName());
                            if (!request.email().isEmpty())
                                admin.setEmail(request.email());
                            if (!request.password().isEmpty())
                                admin.setPassword(request.password());
                            return adminRepository.save(admin);
                        }
                ).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, new AccountNotFoundException(id).getMessage()));
        return new ResponseEntity<>("Admin account with " + id + " successfully updated", HttpStatus.ACCEPTED);
    }

    public ResponseEntity<String> deleteAdmin(Integer id) {
        try {

            adminRepository.deleteById(id);
            return ResponseEntity.ok("deleted successfully");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, new AccountNotFoundException(id).getMessage());
        }
    }

    public ResponseEntity<List<Admin>> getAdminsPage(int pageNumber, int size) {
        Page<Admin> resultPage = adminRepository.findAll(PageRequest.of(pageNumber, size));
        if (pageNumber > resultPage.getTotalPages()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, new PageOutOfBoundsException(pageNumber).getMessage());
        }
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("total-pages", String.valueOf(resultPage.getTotalPages()));
        return new ResponseEntity<>(resultPage.getContent(), responseHeaders, HttpStatus.OK);
    }

}
