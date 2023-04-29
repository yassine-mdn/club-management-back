package ma.ac.uir.projets8.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
    @ManyToMany(mappedBy = "participants")
    private Set<Meeting> meetings;
}
