package ma.ac.uir.projets8.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class ClubDetails {
    @JsonIgnore
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

    @OneToOne
    @MapsId
    @JoinColumn(name = "idC")
    private Club club;

    private String logo;
    private String cover;

    private String description;

    private String email;
    private String phone;
    private String aboutUs;
    /*
    @ElementCollection
    private List<String> socials;
    @ElementCollection
    private List<String> medias;
    */
}
