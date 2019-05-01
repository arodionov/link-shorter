package shorter.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Getter
@Setter
@EqualsAndHashCode(of = "shortPath")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class LinkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String shortPath;

    private String fullPath;
}
