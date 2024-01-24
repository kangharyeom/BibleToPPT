package bulletin.bulletin.domain.bible.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "BIBLES")
public class Bible {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bibleId;
    @Column
    private String title;
    @Column
    private String chapter;

    @Column
    private String verse;

    @Column(columnDefinition = "varchar(1000)")
    private String content;

    public Bible(String content) {
        this.content = content;
    }
}
