package searchEngine.parser;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import searchEngine.model.page.Page;
import searchEngine.model.site.Site;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

public class SiteParser extends RecursiveTask<Page> {
    private Page page;
    private Site site;
    private static HashSet<String> links = new HashSet<>();
    private static List<Page> pageList = new ArrayList<>();

    public SiteParser(Page page, Site site) {
        this.page = page;
        this.site = site;
    }
    @Override
    protected Page compute() {
        Set<SiteParser> taskList = new HashSet<>();
        List<SiteParser> children = getPage(page);

        for (SiteParser child : children) {
            taskList.add(child);
        }

        for (SiteParser task : taskList) {
            task.fork();
        }
        for (SiteParser task : taskList) {
            task.join();
        }
        return page;
    }

    public List<SiteParser> getPage(Page page) {
        List<SiteParser> children = new ArrayList<>();
        String url = page.getSite().getUrl().concat(page.getPath());
        addUrlToLinks(url);
        try {
            Connection.Response response = Jsoup.connect(url)
                    .userAgent("Opera")
                    .timeout(100000).ignoreHttpErrors(true)
                    .execute();
            Document document = response.parse();//Jsoup.connect(url).ignoreHttpErrors(true).timeout(1000).get();
            page.setCode(response.statusCode());
            page.setContent(document.toString().replaceAll("'", ""));
            pageList.add(page);
            Elements elements = document.select("a[href]");

            for (Element element : elements) {
                String childUrl = element.attr("abs:href");
                if (links.contains(childUrl)) {continue;}
                if (isTrueLink(childUrl, url)) {
                    Page child = new Page();
                    child.setSite(site);
                    child.setPath(childUrl.replace(site.getUrl(), ""));
                    children.add(new SiteParser(child, site));
                }
            }
            System.out.println(page);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.out.println(url);
        }
        return children;
    }

    private boolean isTrueLink(String childUrl, String url) {
        return childUrl.contains(url) && !childUrl.equals(url)
                && !childUrl.contains("#")
                && !childUrl.contains(".pdf")
                && !childUrl.contains(".svg")
                && !childUrl.contains(".xlsx");
    }
    public static void addUrlToLinks(String url) {
        links.add(url);
    }

    public static List<Page> getPageList(Site site) {
        return pageList.stream()
                .filter(page -> page.getSite().equals(site))
                .collect(Collectors.toList());
    }
}
