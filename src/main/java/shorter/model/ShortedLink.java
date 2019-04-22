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
public class ShortedLink {
    @Id
    private Long linkId;
    @NaturalId
    @Column(nullable = false)
    @EqualsAndHashCode.Include
    private String shortedLink;
    @MapsId
    @OneToOne
    @JoinColumn(name = "link_id")
    private Link link;

    public ShortedLink(String shortedLink, Link link) {
        LinkUtil.check(shortedLink);
        this.shortedLink = shortedLink;
        this.link = link;
        this.linkId = this.link.getId();
    }

    public String getPath() {
        return LinkUtil.getPath(shortedLink);
    }

    public String link() {
        return shortedLink;
    }

}
