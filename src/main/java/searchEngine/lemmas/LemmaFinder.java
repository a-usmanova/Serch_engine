package searchEngine.lemmas;

import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import searchEngine.model.lemma.Lemma;
import searchEngine.model.site.Site;

import java.io.IOException;
import java.util.*;

public class LemmaFinder {
    private final LuceneMorphology luceneMorphology;
    private static final String[] particlesNames = new String[]{"МЕЖД", "ПРЕДЛ", "СОЮЗ"};

    public static LemmaFinder getInstance() {

        LuceneMorphology morphology;
        try {
            morphology = new RussianLuceneMorphology();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new LemmaFinder(morphology);
    }

    private LemmaFinder(LuceneMorphology luceneMorphology) {
        this.luceneMorphology = luceneMorphology;
    }

//    public Map<String, Integer> collectLemmas(String text) {
//        String[] words = arrayContainsWords(text);
//        HashMap<String, Integer> lemmas = new HashMap<>();
//        for (String word: words) {
//            if (word.isBlank()) {
//                continue;
//            }
//            List<String> wordBaseForms = luceneMorphology.getMorphInfo(word);
//            if (wordBaseBelongToParticle(wordBaseForms)) {
//                continue;
//            }
//            List<String> normalForms = luceneMorphology.getNormalForms(word);
//            if (normalForms.isEmpty()) {
//                continue;
//            }
//            String normalWord = normalForms.get(0);
//            if (lemmas.containsKey(normalWord)) {
//                lemmas.put(normalWord, lemmas.get(normalWord) + 1);
//            } else {
//                lemmas.put(normalWord, 1);
//            }
//        }
//        return lemmas;
//    }
    public HashMap<Lemma, Integer> collectLemmas(String text, Site site) {
        String[] words = arrayContainsWords(text);
        HashMap<Lemma, Integer> lemmas = new HashMap<>();
        for (String word: words) {
            if (word.isBlank()) {
                continue;
            }
            List<String> wordBaseForms = luceneMorphology.getMorphInfo(word);
            if (wordBaseBelongToParticle(wordBaseForms)) {
                continue;
            }
            List<String> normalForms = luceneMorphology.getNormalForms(word);
            if (normalForms.isEmpty()) {
                continue;
            }
            String normalWord = normalForms.get(0);
            Lemma lemma = createLemma(normalWord, site);
            if (lemmas.containsKey(lemma)) {
                lemmas.put(lemma, lemmas.get(lemma) + 1);
            } else {
                lemmas.put(lemma, lemma.getFrequency());
            }
        }
        return lemmas;
    }
    private String[] arrayContainsWords(String text) {
        return text.toLowerCase(Locale.ROOT)
                .replaceAll("[^а-я\\s]", "")
                .trim()
                .split("\\s+");
    }
    private boolean wordBaseBelongToParticle(List<String> wordBaseForms) {
        return wordBaseForms.stream().anyMatch(this::hasParticleProperty);
    }

    private boolean hasParticleProperty(String wordBase) {
        for (String property: particlesNames) {
            if (wordBase.toUpperCase().contains(property))
                return true;
        }
        return false;
    }

    public String stripHtml(String content) {
        return content.replaceAll("<p .*?>", "\r\n")
                .replaceAll("<br\\s*/?>", "\r\n")
                .replaceAll("<.*?>", "");
    }

    private Lemma createLemma(String word, Site site) {
        Lemma lemma = new Lemma();
        lemma.setSite(site);
        lemma.setLemma(word);
        lemma.setFrequency(1);

        return lemma;
    }
}
