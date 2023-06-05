package ma.ac.uir.projets8.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Inheritance(strategy = InheritanceType.JOINED)
public class Budget {
    @Id
    @SequenceGenerator(
            name = "budget_id_sequence",
            sequenceName = "budget_id_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "budget_id_sequence"
    )
    private Long idBudget;

    @OneToMany(mappedBy = "budget")
    private Set<Transaction> transactions;

    private double budget_initial;
    private  double budget_restant;

}
