package ma.ac.uir.projets8.model;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import ma.ac.uir.projets8.model.enums.TransactionStatus;

import java.util.Date;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Inheritance(strategy = InheritanceType.JOINED)
public class Transaction {

    @Id
    @SequenceGenerator(
            name = "transaction_id_sequence",
            sequenceName = "transaction_id_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "transaction_id_sequence"
    )
    private Long idTransaction;

    @ManyToOne
    private Event event;

    private Date date;

    private double valeur;

    @OneToMany
    private Set<Document> preuve;

    @JsonIgnore
    @ManyToOne
    private Budget budget;

    private TransactionStatus status;



}
