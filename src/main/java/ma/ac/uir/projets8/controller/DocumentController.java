package ma.ac.uir.projets8.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import ma.ac.uir.projets8.model.Club;
import ma.ac.uir.projets8.model.Document;
import ma.ac.uir.projets8.repository.DocumentRepository;
import ma.ac.uir.projets8.service.ClubService;
import ma.ac.uir.projets8.service.DocumentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/documents")
public class DocumentController {

    private final DocumentService documentService;
    private final DocumentRepository documentRepository;
    private final ClubService clubService;


    @Operation(summary = "Get all documents")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<Document>> getAllDocuments() {
        return ResponseEntity.ok(documentService.getAllDocuments());
    }

    @Operation(summary = "Get a document by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Document not found")
    })
    @PreAuthorize("hasAnyRole('ADMIN','PROF','PRESIDENT','VICE_PRESIDENT','TREASURER','SECRETARY')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getDocumentById(@PathVariable Long id) throws IOException {
        byte[] document = documentService.downloadDocument(id);
        Optional<Document> doc = documentRepository.findById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf(doc.get().getType()))
                .body(document);
    }

    @PreAuthorize("hasAnyRole('ADMIN','PROF','PRESIDENT','VICE_PRESIDENT','TREASURER','SECRETARY')")
    @PostMapping("/send/src/{club_id}")
    public ResponseEntity<?> sendDocument(@PathVariable("club_id") Integer club_id, @RequestParam("file") MultipartFile file) throws IOException {
        Club sender = clubService.getClubById(club_id).getBody();
        return ResponseEntity.ok(documentService.uploadFichier(file, sender));
    }



}
