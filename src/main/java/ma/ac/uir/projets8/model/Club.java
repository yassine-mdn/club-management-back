package ma.ac.uir.projets8.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.ac.uir.projets8.model.enums.ClubStatus;
import ma.ac.uir.projets8.model.enums.ClubType;

import java.time.Instant;
import java.util.*;

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
    private Integer idC;

    private String name;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "is_member",
            joinColumns =@JoinColumn(name = "id_culb"),
            inverseJoinColumns = @JoinColumn(name = "id_member")
    )
    private Set<Student> members;


    @OneToMany(mappedBy = "mangedClub")
    private Set<Student> committeeMembers;

    private String description;

    private ClubType type;

    private ClubStatus status;

    private Boolean featured;

    @JsonIgnore
    private Instant creationDate;

    @ManyToOne
    @JoinColumn(name = "id_supervisor")
    private Personnel supervisor;

    @JsonIgnore
    @OneToOne(mappedBy = "club",cascade = CascadeType.ALL,fetch = FetchType.EAGER,optional = false)
    @PrimaryKeyJoinColumn
    private ClubDetails clubDetails;

    @JsonIgnore
    @OneToMany(mappedBy = "organisateur",fetch = FetchType.LAZY)
    private Set<Event> events;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "sender")
    private Set<Document> documents;


    @OneToMany( mappedBy = "club",cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private Set<Budget> budgets;


    public void addMember(List<Student> students){
        for (Student student : students) {
            members.add(student);
            student.getJoinedClubs().add(this);
        }
    }

    public void addClubDetails(ClubDetails clubDetails){
        this.clubDetails = clubDetails;
        clubDetails.setClub(this);
    }

    public void addCommitteeMembers(List<Student> students){
        for (Student student : students) {
            committeeMembers.add(student);
            student.setMangedClub(this);
        }
    }

    public void addSupervisor(Personnel personnel){
        this.supervisor = personnel;
        personnel.getManagedClubs().add(this);
    }



}
