package searchEngine.repositories.searchIndex;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import searchEngine.model.page.Page;
import searchEngine.model.searchIndex.SearchIndex;

import java.util.List;

@Repository
public interface SearchIndexRepository extends JpaRepository<SearchIndex, Integer> {
    @Transactional
    void deleteByPage(Page page);
    List<SearchIndex> findAllByPageId(int pageId);
}
