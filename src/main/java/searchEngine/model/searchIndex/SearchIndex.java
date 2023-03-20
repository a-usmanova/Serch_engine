package searchEngine.model.searchIndex;

import lombok.Getter;
import lombok.Setter;
import searchEngine.model.lemma.Lemma;
import searchEngine.model.page.Page;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "search_index")
public class SearchIndex {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "page_id")
    private Page page;
    @ManyToOne
    @JoinColumn(name = "lemma_id")
    private Lemma lemma;
    @Column(name = "search_rank", nullable = false)
    private float rank;
}
