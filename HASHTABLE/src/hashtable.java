import java.util.*;

public class hashtable {

    // n-gram -> set of document ids
    private HashMap<String, Set<String>> ngramIndex;

    // document id -> list of n-grams
    private HashMap<String, List<String>> documentNgrams;

    // constructor
    public hashtable() {
        ngramIndex = new HashMap<>();
        documentNgrams = new HashMap<>();
    }

    // generate n-grams
    public List<String> generateNGrams(String text, int n) {
        List<String> ngrams = new ArrayList<>();

        String[] words = text.toLowerCase().replaceAll("[^a-z0-9 ]", "").split("\\s+");

        for (int i = 0; i <= words.length - n; i++) {
            StringBuilder sb = new StringBuilder();

            for (int j = 0; j < n; j++) {
                sb.append(words[i + j]);
                if (j != n - 1) {
                    sb.append(" ");
                }
            }

            ngrams.add(sb.toString());
        }

        return ngrams;
    }

    // add document to database
    public void addDocument(String docId, String text, int n) {
        List<String> ngrams = generateNGrams(text, n);
        documentNgrams.put(docId, ngrams);

        for (String gram : ngrams) {
            ngramIndex.putIfAbsent(gram, new HashSet<String>());
            ngramIndex.get(gram).add(docId);
        }
    }

    // analyze a new document
    public void analyzeDocument(String docId, String text, int n) {
        List<String> newDocNgrams = generateNGrams(text, n);

        HashMap<String, Integer> matchCount = new HashMap<>();

        for (String gram : newDocNgrams) {
            if (ngramIndex.containsKey(gram)) {
                for (String existingDoc : ngramIndex.get(gram)) {
                    matchCount.put(existingDoc, matchCount.getOrDefault(existingDoc, 0) + 1);
                }
            }
        }

        System.out.println("Analyzing Document: " + docId);
        System.out.println("Extracted " + newDocNgrams.size() + " n-grams");

        if (matchCount.isEmpty()) {
            System.out.println("No matching documents found");
            return;
        }

        String mostSimilarDoc = "";
        int maxMatches = 0;

        for (Map.Entry<String, Integer> entry : matchCount.entrySet()) {
            String existingDoc = entry.getKey();
            int matches = entry.getValue();

            double similarity = (matches * 100.0) / newDocNgrams.size();

            System.out.println("Found " + matches + " matching n-grams with \""
                    + existingDoc + "\"");
            System.out.println("Similarity: " + String.format("%.2f", similarity) + "%");

            if (similarity >= 60) {
                System.out.println("PLAGIARISM DETECTED");
            } else if (similarity >= 15) {
                System.out.println("Suspicious");
            } else {
                System.out.println("Low similarity");
            }

            System.out.println();

            if (matches > maxMatches) {
                maxMatches = matches;
                mostSimilarDoc = existingDoc;
            }
        }

        double bestSimilarity = (maxMatches * 100.0) / newDocNgrams.size();
        System.out.println("Most similar document: " + mostSimilarDoc);
        System.out.println("Best similarity: " + String.format("%.2f", bestSimilarity) + "%");
    }

    public static void main(String[] args) {
        hashtable obj = new hashtable();

        String doc1 = "Artificial intelligence is transforming education by enabling personalized learning and automated assessment for students.";
        String doc2 = "Artificial intelligence is transforming education by enabling personalized learning and automated assessment in universities.";
        String doc3 = "Cloud computing provides scalable resources and services over the internet for organizations and businesses.";

        obj.addDocument("essay_089.txt", doc1, 5);
        obj.addDocument("essay_092.txt", doc2, 5);
        obj.addDocument("essay_050.txt", doc3, 5);

        String newEssay = "Artificial intelligence is transforming education by enabling personalized learning and automated assessment for students in modern universities.";

        obj.analyzeDocument("essay_123.txt", newEssay, 5);
    }
}