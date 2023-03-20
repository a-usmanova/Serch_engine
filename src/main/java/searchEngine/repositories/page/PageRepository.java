package searchEngine.repositories.page;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import searchEngine.model.page.Page;
import searchEngine.model.site.Site;

import java.util.List;

@Repository
public interface PageRepository extends JpaRepository<Page, Integer> {
    List<Page> findAllBySiteId(int siteId);
    List<Page> findAllByPath(String path);
    int countBySite(Site site);
}

