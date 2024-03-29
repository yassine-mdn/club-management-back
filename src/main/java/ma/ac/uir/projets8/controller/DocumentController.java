package ma.ac.uir.projets8.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import ma.ac.uir.projets8.exception.PageOutOfBoundsException;
import ma.ac.uir.projets8.model.Club;
import ma.ac.uir.projets8.model.Document;
import ma.ac.uir.projets8.repository.DocumentRepository;
import ma.ac.uir.projets8.service.ClubService;
import ma.ac.uir.projets8.service.DocumentService;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

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


    @Operation(summary = "Get all documents", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<Document>> getAllDocuments() {
        return ResponseEntity.ok(documentService.getAllDocuments());
    }

    @Operation(summary = "Get a document by ID", security = @SecurityRequirement(name = "bearerAuth"))
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

    @Operation(summary = "save document", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAnyRole('ADMIN','PROF','PRESIDENT','VICE_PRESIDENT','TREASURER','SECRETARY')")
    @PostMapping("/send/src/{club_id}")
    public ResponseEntity<?> sendDocument(@PathVariable("club_id") Integer club_id, @RequestParam("file") MultipartFile file) throws IOException {
        Club sender = clubService.getClubById(club_id);
        return ResponseEntity.ok(documentService.uploadFichier(file, sender));
    }


    @Operation(summary = "get domcuments by sender id", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAnyRole('ADMIN','PROF','PRESIDENT','VICE_PRESIDENT')")
    @GetMapping("/sender/{idC}")
    public ResponseEntity<List<Document>> getDocumentsBySenderId(
            @PathVariable("idC") Integer idC,
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "25") Integer pageSize) {

        if (pageNumber < 0 || pageSize < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid request");
        Page<Document> docs = documentService.getDocumentPageBySenderId(idC, pageNumber, pageSize);
        if (pageNumber > docs.getTotalPages()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, new PageOutOfBoundsException(pageNumber).getMessage());
        }
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("total-pages", String.valueOf(docs.getTotalPages()));
        return new ResponseEntity<>(docs.getContent(), responseHeaders, HttpStatus.OK);
    }


}
