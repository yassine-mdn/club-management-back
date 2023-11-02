package ma.ac.uir.projets8.util;

import ma.ac.uir.projets8.model.Club;
import ma.ac.uir.projets8.model.enums.ClubStatus;
import ma.ac.uir.projets8.repository.ClubRepository;
import ma.ac.uir.projets8.service.ClubService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FileGeneratorTest {
    @Autowired
    private ClubService clubService;

    @Autowired
    private ClubRepository clubRepository;

    @Test
    void generateWorkSheet() {


    }
}