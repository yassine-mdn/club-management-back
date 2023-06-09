package ma.ac.uir.projets8.service;

import lombok.RequiredArgsConstructor;
import ma.ac.uir.projets8.exception.DocumentNotFoundException;
import ma.ac.uir.projets8.model.Club;
import ma.ac.uir.projets8.model.Document;
import ma.ac.uir.projets8.repository.DocumentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;

    private final String CHEMIN_REPERTOIRE = "C:/Users/yassi/Bureau/projet-s8-file-transfer";

    public String uploadFichier(MultipartFile file, Club sender) throws IOException {
        String cheminComplet = CHEMIN_REPERTOIRE+ File.separator + sender.getName() +  File.separator +file.getOriginalFilename();
        Files.createDirectories(Paths.get(CHEMIN_REPERTOIRE+ File.separator + sender.getName()));

        long millis = System.currentTimeMillis();

        if (!documentRepository.existsByPath(cheminComplet)) {

            Document doc = documentRepository.save(Document.builder()
                    .name(file.getOriginalFilename())
                    .type(file.getContentType())
                    .dateUpload(new Date(millis))
                    .path(cheminComplet )
                    .size(file.getSize())
                    .sender(sender)
                    .build());


            file.transferTo(new File(cheminComplet));

            return "file uploaded successfully : " + cheminComplet;
        }
        file.transferTo(new File(cheminComplet));
        return "file modified successfully : " + cheminComplet;
    }

    public byte[] downloadDocument(Long id) throws IOException {
        Document infoFichier = documentRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Document Not Found"));
        String filePath = infoFichier.getPath();
        return Files.readAllBytes(new File(filePath).toPath());
    }


    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }

}

