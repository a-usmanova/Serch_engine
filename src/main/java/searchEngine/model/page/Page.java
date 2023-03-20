package searchEngine.model.page;

import lombok.Getter;
import lombok.Setter;
import searchEngine.model.site.Site;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "page", indexes = {@Index(columnList = "path", name = "path_index")})
public class Page {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id")
    private Site site;
    @Column(columnDefinition = "VARCHAR(255)", nullable = false)
    private String path;
    @Column(nullable = false)
    private int code;
    @Column(columnDefinition = "MEDIUMTEXT", nullable = false)
    private String content;

    @Override
    public String toString() {
        return site.getId() + " - " + path + " - " + code;
    }
}
