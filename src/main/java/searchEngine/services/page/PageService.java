package searchEngine.services.page;

import searchEngine.model.page.Page;
import searchEngine.model.site.Site;

import java.util.List;

public interface PageService {
    int addPage(Page page);

    void addPageList(List<Page> pageList);
    void deleteSitePages(List<Page> pageList);
    List<Page> getSitePages(int siteId);
    int countBySite(Site site);
}
