package ma.ac.uir.projets8.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Student extends Account{

    @JsonIgnore
    @ManyToMany(mappedBy = "participants")
    private Set<Meeting> meetings;

    @JsonIgnore
    @ManyToMany(mappedBy = "members")
    private Set<Club> joinedClubs = new HashSet<>();

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_manged_club",nullable = true)
    private Club mangedClub;

    @Column(unique = true)
    private Integer studentId; // Hiya numero etudiant

    private String major;

    private Integer level;


}
