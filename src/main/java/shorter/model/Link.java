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
        check(link);
        this.link = link;
    }

    public static Link linkTo(String link) {
        return new Link(link);
    }

    public static Link HTTPLinkTo(String path) {
        return new Link("http://" + path);
    }

    private void check(String link) {
    }

    public String getPath() {
        return link.substring(link.indexOf("//") + 2);
    }

    public String link() {
        return link;
    }

}