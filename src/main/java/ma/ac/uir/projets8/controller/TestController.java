package ma.ac.uir.projets8.controller;

import lombok.RequiredArgsConstructor;
import ma.ac.uir.projets8.service.ClubService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor
public class TestController {

   private final ClubService clubService;

    @PutMapping
    public ResponseEntity<String> abandonClub(){
        return clubService.abandonClub(1);
    }
}
