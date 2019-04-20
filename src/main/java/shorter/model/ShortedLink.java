package shorter.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Setter
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ShortedLink {
    @Id
    private Long linkId;
    @NaturalId
    @Column(nullable = false)
    @EqualsAndHashCode.Include
    private String name;
    @MapsId
    @OneToOne
    @JoinColumn(name = "link_id")
    private Link link;
}
