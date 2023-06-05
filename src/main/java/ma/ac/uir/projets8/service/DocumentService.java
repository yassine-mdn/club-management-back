package ma.ac.uir.projets8.service;

import lombok.RequiredArgsConstructor;
import ma.ac.uir.projets8.exception.DocumentNotFoundException;
import ma.ac.uir.projets8.model.Document;
import ma.ac.uir.projets8.repository.DocumentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;


    public Document createDocument(Document document) {
        return documentRepository.save(document);
    }

    public Optional<Document> getDocumentById(Long id) {
        return documentRepository.findById(id);
    }

    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }

    public Document updateDocument(Document document) {
        if (!documentRepository.existsById(document.getIdDocument())) {
            throw new DocumentNotFoundException(document.getIdDocument());
        }
        return documentRepository.save(document);
    }

    public void deleteDocument(Long id) {
        if (!documentRepository.existsById(id)) {
            throw new DocumentNotFoundException(id);
        }
        documentRepository.deleteById(id);
    }
}

