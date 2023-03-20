package searchEngine.services.site;

import searchEngine.dto.indexing.IndexingResponse;
import searchEngine.model.site.Site;
import searchEngine.model.site.StatusType;

import java.util.List;

public interface SiteService {
    int addSite(Site site);
    void deleteSite(Site site);
    List<Site> getSiteByUrl(String url);
    List<Site> getAll();
    List<Site> getSiteByStatus(StatusType status);

    IndexingResponse startIndexing(List<Site> siteList);
    IndexingResponse stopIndexing();
    IndexingResponse indexPage(String url);
}
