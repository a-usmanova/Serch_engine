package searchEngine.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import searchEngine.model.site.Site;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "indexing-settings")
@Getter
@Setter
public class SitesList {
    private List<Site> sites;
}