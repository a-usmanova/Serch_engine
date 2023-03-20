package searchEngine.services.lemma;

import searchEngine.model.lemma.Lemma;
import searchEngine.model.site.Site;

import java.util.List;
import java.util.Set;

public interface LemmaService {
    void addLemma(Lemma lemma);
    void addAll(Set<Lemma> lemmaSet);
    Lemma getLemmaByLemmaAndSiteId(String lemma, int siteId);
    Lemma getLemmaByString(String lemma);
    List<Lemma> getLemmasBySiteId(int siteId);
    void deleteAll(List<Lemma> lemmaList);
    void deleteBySite(Site site);
    int countBySite(Site site);
}
