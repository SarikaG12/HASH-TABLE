import java.util.*;

public class hashtable {

    HashMap<String, Integer> pageViews = new HashMap<>();
    HashMap<String, HashSet<String>> uniqueVisitors = new HashMap<>();
    HashMap<String, Integer> trafficSources = new HashMap<>();

    void processEvent(String url, String userId, String source) {
        pageViews.put(url, pageViews.getOrDefault(url, 0) + 1);

        uniqueVisitors.putIfAbsent(url, new HashSet<>());
        uniqueVisitors.get(url).add(userId);

        trafficSources.put(source, trafficSources.getOrDefault(source, 0) + 1);
    }

    void getDashboard() {
        System.out.println("\nTop Pages:");

        List<Map.Entry<String, Integer>> list = new ArrayList<>(pageViews.entrySet());

        list.sort((a, b) -> b.getValue() - a.getValue());

        int i = 1;
        for (Map.Entry<String, Integer> e : list) {
            System.out.println(i + ". " + e.getKey() + " - " + e.getValue() +
                    " views (" + uniqueVisitors.get(e.getKey()).size() + " unique)");
            i++;
            if (i > 10) break;
        }

        System.out.println("\nTraffic Sources:");
        for (String s : trafficSources.keySet()) {
            System.out.println(s + " -> " + trafficSources.get(s));
        }
    }

    public static void main(String[] args) {

        hashtable obj = new hashtable();

        obj.processEvent("/news", "u1", "Google");
        obj.processEvent("/news", "u2", "Facebook");
        obj.processEvent("/sports", "u3", "Direct");
        obj.processEvent("/news", "u1", "Google");

        obj.getDashboard();
    }
}