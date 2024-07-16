package hu.progmasters.fundraiser.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String url;

    @ManyToOne
    @JoinColumn(name = "fund_id")
    private Fund fund;

    public Image(String imageUrl, String originalFilename) {
        this.url = imageUrl;
        this.name = originalFilename;
    }

    @Override
    public String toString() {
        return url;
    }
}
