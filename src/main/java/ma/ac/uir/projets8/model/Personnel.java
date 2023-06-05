package ma.ac.uir.projets8.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Personnel extends Account{

    @JsonIgnore
    @OneToMany(mappedBy = "supervisor",fetch = FetchType.LAZY)
    private Set<Club> managedClubs;
}
