package searchEngine.repositories.lemma;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import searchEngine.model.lemma.Lemma;
import searchEngine.model.site.Site;

import java.util.List;

@Repository
public interface LemmaRepository extends JpaRepository<Lemma, Integer>{
    void deleteAllBySite(Site site);
    @Transactional
    Integer deleteBySite(Site site);
    List<Lemma> findAllBySiteId(int siteId);
    Lemma findByLemmaAndSiteId(String lemma, int siteId);
    Lemma findByLemma(String lemma);
    int countBySite(Site site);
}
