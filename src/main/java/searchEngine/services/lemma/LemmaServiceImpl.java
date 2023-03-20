package searchEngine.services.lemma;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchEngine.model.lemma.Lemma;
import searchEngine.model.site.Site;
import searchEngine.repositories.lemma.LemmaRepository;

import java.util.List;
import java.util.Set;

@Service
public class LemmaServiceImpl implements LemmaService {
    private final LemmaRepository lemmaRepository;

    @Autowired
    LemmaServiceImpl(LemmaRepository lemmaRepository) {
        this.lemmaRepository = lemmaRepository;
    }

    @Override
    public void addLemma(Lemma lemma) {
        lemmaRepository.save(lemma);
    }

    @Override
    public void addAll(Set<Lemma> lemmaSet) {
        lemmaRepository.saveAll(lemmaSet);
    }

    @Override
    public Lemma getLemmaByLemmaAndSiteId(String lemma, int siteId) {
        return lemmaRepository.findByLemmaAndSiteId(lemma, siteId);
    }

    @Override
    public Lemma getLemmaByString(String lemma) {
        return lemmaRepository.findByLemma(lemma);
    }

    @Override
    public List<Lemma> getLemmasBySiteId(int siteId) {
        return lemmaRepository.findAllBySiteId(siteId);
    }

    @Override
    public void deleteAll(List<Lemma> lemmaList) {
        lemmaRepository.deleteAll(lemmaList);
    }

    @Override
    public void deleteBySite(Site site) {
        lemmaRepository.deleteBySite(site);
    }

    @Override
    public int countBySite(Site site) {
        return lemmaRepository.countBySite(site);
    }
}
