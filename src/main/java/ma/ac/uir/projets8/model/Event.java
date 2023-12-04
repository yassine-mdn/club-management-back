package ma.ac.uir.projets8.model;

import jakarta.persistence.*;
import lombok.*;
import ma.ac.uir.projets8.model.enums.EventStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;
import java.util.List;
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

    @JsonIgnore
    @OneToMany(mappedBy = "event")
    private Set<Transaction> transcations;

    @ManyToOne
    @JoinColumn(name = "club_id",nullable = true)
    private Club organisateur;

    private Date date;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "event_participant",
            joinColumns = @JoinColumn(name = "id_event"),
            inverseJoinColumns = @JoinColumn(name = "id_participant")
    )
    private Set<Student> participants;

    private String cover;

    private EventStatus status;
    public void addTransactions(List<Transaction> transactions) {
        for (Transaction transaction : transactions) {
            transaction.setEvent(this);
            this.transcations.add(transaction);

        }
    }



}
