package searchEngine.repositories.site;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import searchEngine.model.site.Site;
import searchEngine.model.site.StatusType;

import java.util.List;
import java.util.Optional;

@Repository
public interface SiteRepository extends JpaRepository<Site, Integer> {
    Optional<List<Site>> findByUrl(String url);
    Optional<List<Site>> findByStatus(StatusType status);
}
