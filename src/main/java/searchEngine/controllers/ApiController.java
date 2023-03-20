package searchEngine.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import searchEngine.config.SitesList;
import searchEngine.dto.indexing.IndexingResponse;
import searchEngine.dto.statistics.StatisticsResponse;
import searchEngine.services.site.SiteService;
import searchEngine.services.statistic.StatisticsService;

import java.util.HashMap;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class ApiController {
    private final StatisticsService statisticsService;
    private final SiteService siteService;

    @Autowired
    private SitesList siteList;

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> statistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }

    @GetMapping("/startIndexing")
    public ResponseEntity<IndexingResponse> startIndexing() {
        return ResponseEntity.ok(siteService.startIndexing(siteList.getSites()));
    }

    @GetMapping("/stopIndexing")
    public ResponseEntity<IndexingResponse> stopIndexing() {
         return ResponseEntity.ok(siteService.stopIndexing());
    }
    @PostMapping("/indexPage")
    public ResponseEntity<IndexingResponse> indexPage(String url) {
        return ResponseEntity.ok(siteService.indexPage(url));
    }
    @GetMapping("/search")
    public HashMap<String, Object> search() {
        HashMap<String, Object> response = new HashMap<>();
        return response;
    }
}
