package searchEngine.services.statistic;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchEngine.model.site.Site;
import searchEngine.dto.statistics.DetailedStatisticsItem;
import searchEngine.dto.statistics.StatisticsData;
import searchEngine.dto.statistics.StatisticsResponse;
import searchEngine.dto.statistics.TotalStatistics;
import searchEngine.services.lemma.LemmaService;
import searchEngine.services.page.PageService;
import searchEngine.services.site.SiteService;

import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {
    private final SiteService siteService;
    private final PageService pageService;
    private final LemmaService lemmaService;

    @Override
    public StatisticsResponse getStatistics() {

        List<DetailedStatisticsItem> detailed = new ArrayList<>();
        List<Site> sitesList = siteService.getAll();

        TotalStatistics total = new TotalStatistics();
        total.setSites(sitesList.size());
        total.setIndexing(true);

        sitesList.forEach(site -> {
            DetailedStatisticsItem item = new DetailedStatisticsItem();
            item.setName(site.getName());
            item.setUrl(site.getUrl());
            item.setPages(pageService.countBySite(site));
            item.setLemmas(lemmaService.countBySite(site));
            item.setStatus(site.getStatus().name());
            item.setError(site.getLastError() != null? site.getLastError():"");
            item.setStatusTime(site.getStatusTime().getLong(ChronoField.CLOCK_HOUR_OF_DAY));
            total.setPages(total.getPages() + item.getPages());
            total.setLemmas(total.getLemmas() + item.getLemmas());
            detailed.add(item);
        });

        StatisticsResponse response = new StatisticsResponse();
        StatisticsData data = new StatisticsData();
        data.setTotal(total);
        data.setDetailed(detailed);
        response.setStatistics(data);
        response.setResult(true);
        return response;
    }
}
