import java.util.*;

public class hashtable {

    class TrieNode {
        HashMap<Character, TrieNode> children;
        boolean isEnd;
        String word;

        TrieNode() {
            children = new HashMap<>();
            isEnd = false;
            word = null;
        }
    }

    private TrieNode root;
    private HashMap<String, Integer> frequencyMap;

    public hashtable() {
        root = new TrieNode();
        frequencyMap = new HashMap<>();
    }

    // insert query into trie
    public void insert(String query) {
        TrieNode current = root;

        for (char ch : query.toCharArray()) {
            current.children.putIfAbsent(ch, new TrieNode());
            current = current.children.get(ch);
        }

        current.isEnd = true;
        current.word = query;
    }

    // update frequency when query is searched
    public void updateFrequency(String query) {
        if (!frequencyMap.containsKey(query)) {
            insert(query);
        }
        frequencyMap.put(query, frequencyMap.getOrDefault(query, 0) + 1);
    }

    // search top 10 suggestions for prefix
    public void search(String prefix) {
        TrieNode current = root;

        for (char ch : prefix.toCharArray()) {
            if (!current.children.containsKey(ch)) {
                System.out.println("No suggestions found");
                return;
            }
            current = current.children.get(ch);
        }

        ArrayList<String> matches = new ArrayList<>();
        collectWords(current, matches);

        Collections.sort(matches, new Comparator<String>() {
            public int compare(String a, String b) {
                return frequencyMap.get(b) - frequencyMap.get(a);
            }
        });

        System.out.println("search(\"" + prefix + "\") ->");
        int limit = Math.min(10, matches.size());

        for (int i = 0; i < limit; i++) {
            String query = matches.get(i);
            System.out.println((i + 1) + ". " + query + " (" + frequencyMap.get(query) + " searches)");
        }
    }

    // collect all words from trie node
    private void collectWords(TrieNode node, ArrayList<String> matches) {
        if (node.isEnd) {
            matches.add(node.word);
        }

        for (TrieNode child : node.children.values()) {
            collectWords(child, matches);
        }
    }

    // simple typo correction
    public void suggestCorrections(String input) {
        ArrayList<String> corrections = new ArrayList<>();

        for (String query : frequencyMap.keySet()) {
            if (isCloseMatch(input, query)) {
                corrections.add(query);
            }
        }

        Collections.sort(corrections, new Comparator<String>() {
            public int compare(String a, String b) {
                return frequencyMap.get(b) - frequencyMap.get(a);
            }
        });

        System.out.println("Corrections for \"" + input + "\" ->");
        if (corrections.isEmpty()) {
            System.out.println("No close matches found");
            return;
        }

        int limit = Math.min(5, corrections.size());
        for (int i = 0; i < limit; i++) {
            String query = corrections.get(i);
            System.out.println((i + 1) + ". " + query + " (" + frequencyMap.get(query) + " searches)");
        }
    }

    // simple close match logic
    private boolean isCloseMatch(String a, String b) {
        if (Math.abs(a.length() - b.length()) > 2) {
            return false;
        }

        int mismatches = 0;
        int len = Math.min(a.length(), b.length());

        for (int i = 0; i < len; i++) {
            if (a.charAt(i) != b.charAt(i)) {
                mismatches++;
            }
        }

        mismatches += Math.abs(a.length() - b.length());

        return mismatches <= 2;
    }

    public static void main(String[] args) {
        hashtable obj = new hashtable();

        // adding search queries
        obj.updateFrequency("java tutorial");
        obj.updateFrequency("javascript");
        obj.updateFrequency("java download");
        obj.updateFrequency("java tutorial");
        obj.updateFrequency("java tutorial");
        obj.updateFrequency("java 21 features");
        obj.updateFrequency("java 21 features");
        obj.updateFrequency("java stream api");
        obj.updateFrequency("java spring boot");
        obj.updateFrequency("java interview questions");
        obj.updateFrequency("javascript");
        obj.updateFrequency("javascript");
        obj.updateFrequency("java download");
        obj.updateFrequency("java hashmap");
        obj.updateFrequency("java hashset");
        obj.updateFrequency("java collections");

        // search suggestions
        obj.search("jav");
        System.out.println();

        // update frequency
        obj.updateFrequency("java 21 features");
        obj.updateFrequency("java 21 features");
        obj.updateFrequency("java 21 features");

        System.out.println("After updating frequency:");
        obj.search("jav");
        System.out.println();

        // typo correction
        obj.suggestCorrections("jvaa");
    }
}