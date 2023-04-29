package ma.ac.uir.projets8.service;

import lombok.RequiredArgsConstructor;
import ma.ac.uir.projets8.controller.AdminController.*;
import ma.ac.uir.projets8.exception.AccountNotFoundException;
import ma.ac.uir.projets8.exception.PageOutOfBoundsException;
import ma.ac.uir.projets8.model.Admin;
import ma.ac.uir.projets8.model.Meeting;
import ma.ac.uir.projets8.repository.AdminRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import static ma.ac.uir.projets8.model.enums.Role.ADMIN;

@RequiredArgsConstructor
@Service
public class AdminService {

    private final AdminRepository adminRepository;

    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    public void addAdmin(@RequestBody NewAdminRequest request) {
        Admin admin = new Admin();
        admin.setLastName(request.lastName());
        admin.setFirstName(request.firstName());
        admin.setEmail(request.email());
        admin.setPassword(request.password());
        admin.setRoles(List.of(ADMIN));
        adminRepository.save(admin);
    }

    public Admin getAdminById(Integer id) {
        return adminRepository.findById(id).orElseThrow(() -> new AccountNotFoundException(id));
    }

    public void updateAdmin(Integer id, NewAdminRequest request) {
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
                );
        //TODO:Add case of recieving an invalid id
    }

    public void deleteAdmin(Integer id) {

        adminRepository.deleteById(id);
    }

    public ResponseEntity<List<Admin>> getAdminsPage(int pageNumber, int size) {
        Page<Admin> resultPage = adminRepository.findAll(PageRequest.of(pageNumber, size));
        if (pageNumber > resultPage.getTotalPages()) {
            throw new PageOutOfBoundsException(pageNumber);
        }
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("total-pages", String.valueOf(resultPage.getTotalPages()));
        return new ResponseEntity<>(resultPage.getContent(), responseHeaders, HttpStatus.OK);
    }

}
