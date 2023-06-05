package ma.ac.uir.projets8.model;

import jakarta.persistence.*;
import lombok.*;
import ma.ac.uir.projets8.model.enums.EventStatus;

import java.util.Date;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Inheritance(strategy = InheritanceType.JOINED)
public class Event {

    @Id
    @SequenceGenerator(
            name = "event_id_sequence",
            sequenceName = "event_id_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "event_id_sequence"
    )
    private Long idEvent;

    private String name;

    private String description;

    @OneToMany(mappedBy = "event")
    private Set<Transaction> budget;

    @ManyToMany
    @JoinTable(
            name = "event_participant",
            joinColumns = @JoinColumn(name = "id_event"),
            inverseJoinColumns = @JoinColumn(name = "id_participant")
    )
    private Set<Account> participants;

    @Enumerated(EnumType.STRING)
    private EventStatus status;

    @ManyToMany
    @JoinTable(
            name = "event_club",
            joinColumns = @JoinColumn(name = "id_event"),
            inverseJoinColumns = @JoinColumn(name = "id_club")
    )
    private Set<Club> organisateurs;

    private Date date;

}
