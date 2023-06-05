package ma.ac.uir.projets8.model;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Inheritance(strategy = InheritanceType.JOINED)
public class Document {

    @Id
    @SequenceGenerator(
            name = "document_id_sequence",
            sequenceName = "document_id_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "document_id_sequence"
    )
    private Long idDocument;

    private String libelle;

    private String chemin;
    private Long size;
    private Date date;


}
