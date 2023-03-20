package searchEngine.services.page;

import searchEngine.model.page.Page;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchEngine.model.site.Site;
import searchEngine.repositories.page.PageRepository;

import java.util.List;

@Service
public class PageServiceImpl implements PageService {

    private final PageRepository pageRepository;

    @Autowired
    public PageServiceImpl(PageRepository pageRepository) {
        this.pageRepository = pageRepository;
    }

    @Override
    public int addPage(Page page) {
        pageRepository.save(page);
        return page.getId();
    }

    @Override
    public void addPageList(List<Page> pageList) {
        pageRepository.saveAll(pageList);
    }

    @Override
    public List<Page> getSitePages(int siteId) {
        return pageRepository.findAllBySiteId(siteId);
    }

    @Override
    public void deleteSitePages(List<Page> pageList) {
        pageRepository.deleteAll(pageList);
    }

    @Override
    public int countBySite(Site site) {
        return pageRepository.countBySite(site);
    }
}
