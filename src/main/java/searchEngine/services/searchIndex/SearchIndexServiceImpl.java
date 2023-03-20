package searchEngine.services.searchIndex;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchEngine.model.page.Page;
import searchEngine.model.searchIndex.SearchIndex;
import searchEngine.repositories.searchIndex.SearchIndexRepository;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class SearchIndexServiceImpl implements SearchIndexService{
    private final SearchIndexRepository searchIndexRepository;

//    @Autowired
//    public SearchIndexServiceImpl(SearchIndexRepository searchIndexRepository) {
//        this.searchIndexRepository = searchIndexRepository;
//    }

    @Override
    public void addAll(Set<SearchIndex> indexSet) {
        searchIndexRepository.saveAll(indexSet);
    }

    @Override
    public List<SearchIndex> getIndexByPageId(int pageId) {
        return searchIndexRepository.findAllByPageId(pageId);
    }

    @Override
    public void deleteAll(List<SearchIndex> searchIndices) {
        searchIndexRepository.deleteAll(searchIndices);
    }

    @Override
    public void deleteByPage(Page page) {
        searchIndexRepository.deleteByPage(page);
    }

    @Override
    public void addIndex(SearchIndex searchIndex) {
        searchIndexRepository.save(searchIndex);
    }
}
