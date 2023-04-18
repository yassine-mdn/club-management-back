package ma.ac.uir.projets8.service;

import lombok.RequiredArgsConstructor;
import ma.ac.uir.projets8.controller.AdminController.*;
import ma.ac.uir.projets8.exception.CompteNotFoundException;
import ma.ac.uir.projets8.model.Admin;
import ma.ac.uir.projets8.repository.AdminRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
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

    public Admin getAdminById(Integer id){
        return adminRepository.findById(id).orElseThrow(() -> new CompteNotFoundException(id));
    }

    public void updateAdmin(Integer id, NewAdminRequest request) {
        adminRepository.findById(id)
                .map(admin -> {
                            admin.setFirstName(request.firstName());
                            admin.setLastName(request.lastName());
                            admin.setEmail(request.email());
                            admin.setPassword(request.password());
                            return adminRepository.save(admin);
                        }
                );
        //TODO:Add case of recieving an invalid id
    }

    public void deleteAdmin(Integer id){

        adminRepository.deleteById(id);
    }

}
