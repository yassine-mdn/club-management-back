package ma.ac.uir.projets8.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Builder
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

    private String name;
    private String type;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String path;
    private Long size;
    private Date dateUpload;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "sender_id",nullable = false)
    private Club sender;


}
