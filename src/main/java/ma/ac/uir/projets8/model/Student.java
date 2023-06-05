package ma.ac.uir.projets8.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Student extends Account{
    //TODO : Add a Set of clubs

    @JsonIgnore
    @ManyToMany(mappedBy = "participants")
    private Set<Meeting> meetings;

    @JsonIgnore
    @ManyToMany(mappedBy = "members")
    private Set<Club> joinedClubs;  //smiya tbel l clubs li m9iyed fihoum

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_manged_club",nullable = true)
    private Club mangedClub;


}
