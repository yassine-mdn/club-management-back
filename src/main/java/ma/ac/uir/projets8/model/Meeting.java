package ma.ac.uir.projets8.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Meeting {

    @Id
    @SequenceGenerator(
            name = "meeting_id_sequence",
            sequenceName = "meeting_id_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "meeting_id_sequence"
    )
    private Integer idM;

    private Date date;
    private String title;
    private String description;
    private String location;
    private Integer lengthInMinutes;

    @ManyToOne
    @JoinColumn(name = "id_organiser",nullable = false)
    private Account organiser;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "participant",
            joinColumns =@JoinColumn(name = "id_meeting"),
            inverseJoinColumns = @JoinColumn(name = "id_participant")
    )
    private Set<Student> participants;

}
