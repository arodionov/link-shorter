package shorter.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;
import shorter.util.LinkUtil;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Setter
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Link {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @NaturalId
    @Column(nullable = false)
    @EqualsAndHashCode.Include
    private String link;
    @OneToOne(mappedBy = "link")
    private ShortedLink shortedLink;

    public Link(String link) {
        LinkUtil.check(link);
        this.link = link;
    }

    public static Link linkTo(String link) {
        return new Link(link);
    }

    public static Link HTTPLinkTo(String path) {
        return new Link("http://" + path);
    }

    public String getPath() {
        return LinkUtil.getPath(link);
    }

    public String link() {
        return link;
    }

}