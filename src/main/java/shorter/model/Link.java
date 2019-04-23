package shorter.model;

import javax.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@EqualsAndHashCode(of = {"fullLink","shortLink"})
@NoArgsConstructor
@ToString
@Entity
public class Link {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;

	@Column(nullable = false, unique = true)
	private String fullLink;

	@Column(nullable = false, unique = true)
	private String shortLink;

	public Link(String fullLink) {
		this.fullLink = fullLink;
	}

	public Link(String fullLink, String shortLink) {
		this.fullLink = fullLink;
		this.shortLink = shortLink;
	}
}
