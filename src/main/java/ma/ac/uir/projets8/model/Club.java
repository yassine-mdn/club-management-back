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
public class Club {
    @Id
    @SequenceGenerator(
            name = "club_id_sequence",
            sequenceName = "club_id_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "club_id_sequence"
    )
    private Long idClub;

    @ManyToMany
    private Set<Event> events;



//
//    Club
//-id: int {unique}
//-comites: Set<ComiteClub>
//-membres: Set<Etudiant>
//-nom: string
//-description: string
//-evenements: Set<Evenement>
//-budget: Budget
//+creer()
//+modifierStatut()
//+supprimer()
//+modifierMembresClub()
//+modifierInfo(
}
