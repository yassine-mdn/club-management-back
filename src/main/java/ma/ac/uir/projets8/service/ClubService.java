package ma.ac.uir.projets8.service;

import lombok.RequiredArgsConstructor;
import ma.ac.uir.projets8.exception.ClubNotFoundException;
import ma.ac.uir.projets8.exception.DocumentNotFoundException;
import ma.ac.uir.projets8.model.Club;
import ma.ac.uir.projets8.repository.ClubRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ClubService {

    private final ClubRepository clubRepository;



    public Club createClub(Club club) {
        return clubRepository.save(club);
    }

    public Optional<Club> getClubById(Long id) {
        return clubRepository.findById(id);
    }

    public List<Club> getAllClubs() {
        return clubRepository.findAll();
    }

    public Club updateClub(Club club) {
        if (!clubRepository.existsById(club.getIdClub())) {
            throw new ClubNotFoundException(club.getIdClub());
        }
        return clubRepository.save(club);
    }

    public void deleteClub(Long id) {
        if (!clubRepository.existsById(id)) {
            throw new ClubNotFoundException(id);
        }
        clubRepository.deleteById(id);
    }
}

