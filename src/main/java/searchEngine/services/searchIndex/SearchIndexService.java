package searchEngine.services.searchIndex;

import searchEngine.model.page.Page;
import searchEngine.model.searchIndex.SearchIndex;

import java.util.List;
import java.util.Set;

public interface SearchIndexService {
    void addAll(Set<SearchIndex> indexSet);
    List<SearchIndex> getIndexByPageId(int pageId);
    void deleteAll(List<SearchIndex> searchIndices);
    void deleteByPage(Page page);
    void addIndex(SearchIndex searchIndex);
}
