package ma.ac.uir.projets8.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
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

    @ManyToMany(mappedBy = "participants")
    private Set<Meeting> meetings;
}
