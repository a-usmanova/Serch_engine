package searchEngine.services.site;

import lombok.RequiredArgsConstructor;
import searchEngine.dto.indexing.IndexingResponse;
import searchEngine.lemmas.LemmaFinder;
import searchEngine.model.lemma.Lemma;
import searchEngine.model.page.Page;
import searchEngine.model.searchIndex.SearchIndex;
import searchEngine.model.site.Site;

import org.springframework.stereotype.Service;
import searchEngine.model.site.StatusType;
import searchEngine.parser.SiteParser;
import searchEngine.repositories.site.SiteRepository;
import searchEngine.services.lemma.LemmaService;
import searchEngine.services.page.PageService;
import searchEngine.services.searchIndex.SearchIndexService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ForkJoinPool;

@RequiredArgsConstructor
@Service
public class SiteServiceImpl implements SiteService {
    private final SiteRepository siteRepository;
    private final PageService pageService;
    private final LemmaService lemmaService;
    private final SearchIndexService indexService;
    private HashMap<Site, ForkJoinPool> threadMap = new HashMap<>();

    @Override
    public int addSite(Site site) {
        siteRepository.save(site);
        return site.getId();
    }

    @Override
    public void deleteSite(Site site) {
        siteRepository.delete(site);
    }

    @Override
    public List<Site> getSiteByUrl(String url) {
        return siteRepository.findByUrl(url).orElse(new ArrayList<>());
    }

    @Override
    public List<Site> getAll() {
        return siteRepository.findAll();
    }

    @Override
    public List<Site> getSiteByStatus(StatusType status) {
        return siteRepository.findByStatus(status).orElse(new ArrayList<>());
    }

    public void deleteDependencies(Site site) {
        List<Site> siteDBList = getSiteByUrl(site.getUrl());

        siteDBList.forEach(siteDB -> {
            List<Page> pageList = pageService.getSitePages(siteDB.getId());
            pageList.forEach(page ->
                    indexService.deleteAll(indexService.getIndexByPageId(page.getId())));
            lemmaService.deleteAll(lemmaService.getLemmasBySiteId(siteDB.getId()));
            pageService.deleteSitePages(pageService.getSitePages(siteDB.getId()));
            deleteSite(siteDB);
        });
    }

    @Override
    public IndexingResponse stopIndexing() {
        threadMap.forEach((site, pool) -> {
            if (site.getStatus().equals(StatusType.INDEXING)) {
                pool.shutdownNow();
                changeSiteStatus(site, StatusType.FAILED, "Индексация остановлена пользователем");
            }
        });
        return getResponse(false, "Индексация остановлена пользователем");
    }

    //-----------------------START_INDEXING----------------------------
    @Override
    public IndexingResponse startIndexing(List<Site> siteList) {
        if (isStarted()) {
            getResponse(false, "Индексация уже запущена");
        }
        siteList.forEach(site -> {
            deleteDependencies(site);
            // try {
            changeSiteStatus(site, StatusType.INDEXING, "");
            parseSite(site);
            List<Page> pageList = SiteParser.getPageList(site);
            pageService.addPageList(pageList);
            changeSiteStatus(site, StatusType.INDEXED, "");
//            } catch (Exception ex) {
//                changeSiteStatus(site, StatusType.FAILED, ex.getMessage());
//            }
        });
        return getResponse(true, "");
    }

    private boolean isStarted() {
        List<Site> siteList = getSiteByStatus(StatusType.INDEXING);
        return siteList.size() > 0;
    }

    private void parseSite(Site site){
        Page page = new Page();
        page.setSite(site);
        page.setPath("/");
        ForkJoinPool pool = new ForkJoinPool();
        threadMap.put(site, pool);
        pool.invoke(new SiteParser(page, site));
    }
    //-----------------------START_INDEXING----------------------------

    //-----------------------INDEX_PAGE----------------------------
    @Override
    public IndexingResponse indexPage(String url) {
        LemmaFinder lemmaFinder = LemmaFinder.getInstance();

        List<Site> siteList = getSiteByUrl(url);
        if (siteList.isEmpty()) {
            return getResponse(false, """
                    Данная страница находится за пределами сайтов,\s
                    указанных в конфигурационном файле
                    """);
        }
        siteList.forEach(site -> {
            Set<Lemma> lemmaSet = new HashSet<>();
            Set<SearchIndex> indexSet = new HashSet<>();
            List<Page> pageList = pageService.getSitePages(site.getId());
            pageList.forEach(page -> {
                indexService.deleteByPage(page);
                String content = lemmaFinder.stripHtml(page.getContent());
                Map<Lemma, Integer> lemmas = lemmaFinder.collectLemmas(content, site);
                updateLemmaSet(lemmaSet, lemmas.keySet().stream().toList());
                indexSet.addAll(createIndexSet(lemmas, page, site.getId()));
            });
            lemmaService.deleteBySite(site);
            lemmaService.addAll(lemmaSet);
            updateIndexSet(indexSet, site.getId());
            indexService.addAll(indexSet);
        });
        return getResponse(true, "");
    }

    private void updateLemmaSet(Set<Lemma> lemmaSet, List<Lemma> lemmaList) {
        lemmaList.forEach(lemma -> {
            if (lemmaSet.contains(lemma)) {
                Lemma lemmaFromSet = lemmaSet.stream().filter(lemma1 -> lemma1.equals(lemma)).findFirst().get();
                lemmaFromSet.setFrequency(lemmaFromSet.getFrequency() + 1);
                lemmaSet.add(lemmaFromSet);
            } else {
                lemmaSet.add(lemma);
            }
        });
    }

    private void updateIndexSet(Set<SearchIndex> indexSet, int siteId){
        for(SearchIndex index: indexSet) {
            Lemma lemma = lemmaService.getLemmaByLemmaAndSiteId(index.getLemma().getLemma(), siteId);
            index.setLemma(lemma);
        }
    }
    private void updateLemma(String lemmaString, Site site) {
        Lemma lemma = lemmaService.getLemmaByLemmaAndSiteId(lemmaString, site.getId());
        if (lemma != null) {
            lemma.setFrequency(lemma.getFrequency() + 1);
            lemmaService.addLemma(lemma);
        }
        lemma = new Lemma();
        lemma.setLemma(lemmaString);
        lemma.setSite(site);
        lemma.setFrequency(1);
        lemmaService.addLemma(lemma);
    }

    private Set<SearchIndex> createIndexSet(Map<Lemma, Integer> lemmas, Page page, int siteId) {
        Set<SearchIndex> indexSet = new HashSet<>();
        lemmas.forEach((lemma, rank) -> {
            SearchIndex index = new SearchIndex();
            index.setPage(page);
            index.setLemma(lemma);
            index.setRank(rank);
            indexSet.add(index);
        });
        return indexSet;
    }
//    private void updateIndex(Map<String, Integer> lemmas, Page page, int siteId) {
//        lemmas.forEach((lemmaString, rank) -> {
//            Lemma lemma = lemmaService.getLemmaByLemmaAndSiteId(lemmaString, siteId);
//            SearchIndex searchIndex = new SearchIndex();
//            searchIndex.setPage(page);
//            searchIndex.setLemma(lemma);
//            searchIndex.setRank(rank);
//            indexService.addIndex(searchIndex);
//        });
//    }

    //-----------------------INDEX_PAGE----------------------------

    private IndexingResponse getResponse(boolean result, String error) {
        IndexingResponse response = new IndexingResponse();
        response.setResult(result);
        response.setError(error);
        return response;
    }

    private void changeSiteStatus(Site site, StatusType status, String error) {
        site.setStatus(status);
        site.setStatusTime(LocalDateTime.now());
        if (status.equals(StatusType.FAILED)) {
            site.setLastError(error);
        }
        addSite(site);
    }
}
