import java.util.*;

public class hashtable {

    static class VideoData {
        String videoId;
        String content;
        int version;

        VideoData(String videoId, String content, int version) {
            this.videoId = videoId;
            this.content = content;
            this.version = version;
        }

        public String toString() {
            return "VideoData{id='" + videoId + "', version=" + version + "}";
        }
    }

    static class LRUCache<K, V> extends LinkedHashMap<K, V> {
        private int capacity;

        LRUCache(int capacity) {
            super(16, 0.75f, true);
            this.capacity = capacity;
        }

        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() > capacity;
        }
    }

    private LRUCache<String, VideoData> l1Cache;
    private LRUCache<String, VideoData> l2Cache;
    private HashMap<String, VideoData> l3Database;
    private HashMap<String, Integer> accessCount;

    private int l1Hits;
    private int l2Hits;
    private int l3Hits;
    private int totalRequests;

    public hashtable() {
        l1Cache = new LRUCache<>(10000);
        l2Cache = new LRUCache<>(100000);
        l3Database = new HashMap<>();
        accessCount = new HashMap<>();

        l1Hits = 0;
        l2Hits = 0;
        l3Hits = 0;
        totalRequests = 0;

        loadSampleVideos();
    }

    private void loadSampleVideos() {
        l3Database.put("video_123", new VideoData("video_123", "Netflix Movie A", 1));
        l3Database.put("video_999", new VideoData("video_999", "Documentary B", 1));
        l3Database.put("video_777", new VideoData("video_777", "Series C", 1));
        l3Database.put("video_555", new VideoData("video_555", "Comedy D", 1));

        l2Cache.put("video_123", l3Database.get("video_123"));
        l2Cache.put("video_777", l3Database.get("video_777"));
    }

    public String getVideo(String videoId) {
        totalRequests++;

        if (l1Cache.containsKey(videoId)) {
            l1Hits++;
            incrementAccess(videoId);
            return "L1 Cache HIT (0.5ms)\nTotal: 0.5ms\nData: " + l1Cache.get(videoId);
        }

        if (l2Cache.containsKey(videoId)) {
            l2Hits++;
            incrementAccess(videoId);

            VideoData data = l2Cache.get(videoId);
            if (accessCount.get(videoId) >= 2) {
                l1Cache.put(videoId, data);
                return "L1 Cache MISS (0.5ms)\nL2 Cache HIT (5ms)\nPromoted to L1\nTotal: 5.5ms\nData: " + data;
            }

            return "L1 Cache MISS (0.5ms)\nL2 Cache HIT (5ms)\nTotal: 5.5ms\nData: " + data;
        }

        if (l3Database.containsKey(videoId)) {
            l3Hits++;
            incrementAccess(videoId);

            VideoData data = l3Database.get(videoId);
            l2Cache.put(videoId, data);

            return "L1 Cache MISS\nL2 Cache MISS\nL3 Database HIT (150ms)\nAdded to L2 (access count: "
                    + accessCount.get(videoId) + ")\nTotal: 150ms\nData: " + data;
        }

        return "Video not found";
    }

    private void incrementAccess(String videoId) {
        accessCount.put(videoId, accessCount.getOrDefault(videoId, 0) + 1);
    }

    public void invalidateVideo(String videoId, String newContent) {
        if (l3Database.containsKey(videoId)) {
            VideoData oldData = l3Database.get(videoId);
            VideoData newData = new VideoData(videoId, newContent, oldData.version + 1);

            l3Database.put(videoId, newData);
            l1Cache.remove(videoId);
            l2Cache.remove(videoId);

            System.out.println("Cache invalidated for " + videoId + ". Updated to version " + newData.version);
        } else {
            System.out.println("Video not found in database");
        }
    }

    public void getStatistics() {
        double l1Rate = totalRequests == 0 ? 0 : (l1Hits * 100.0) / totalRequests;
        double l2Rate = totalRequests == 0 ? 0 : (l2Hits * 100.0) / totalRequests;
        double l3Rate = totalRequests == 0 ? 0 : (l3Hits * 100.0) / totalRequests;
        double overallRate = totalRequests == 0 ? 0 : ((l1Hits + l2Hits + l3Hits) * 100.0) / totalRequests;

        double overallAvgTime = 0.0;
        if (totalRequests > 0) {
            overallAvgTime = ((l1Hits * 0.5) + (l2Hits * 5.5) + (l3Hits * 150.0)) / totalRequests;
        }

        System.out.println("\nCache Statistics:");
        System.out.println("L1: Hit Rate " + String.format("%.2f", l1Rate) + "%, Avg Time: 0.5ms");
        System.out.println("L2: Hit Rate " + String.format("%.2f", l2Rate) + "%, Avg Time: 5ms");
        System.out.println("L3: Hit Rate " + String.format("%.2f", l3Rate) + "%, Avg Time: 150ms");
        System.out.println("Overall: Hit Rate " + String.format("%.2f", overallRate) +
                "%, Avg Time: " + String.format("%.2f", overallAvgTime) + "ms");
    }

    public static void main(String[] args) {
        hashtable obj = new hashtable();

        System.out.println("getVideo(\"video_123\")");
        System.out.println(obj.getVideo("video_123"));
        System.out.println();

        System.out.println("getVideo(\"video_123\") [second request]");
        System.out.println(obj.getVideo("video_123"));
        System.out.println();

        System.out.println("getVideo(\"video_123\") [third request]");
        System.out.println(obj.getVideo("video_123"));
        System.out.println();

        System.out.println("getVideo(\"video_999\")");
        System.out.println(obj.getVideo("video_999"));
        System.out.println();

        System.out.println("getVideo(\"video_999\") [second request]");
        System.out.println(obj.getVideo("video_999"));
        System.out.println();

        obj.invalidateVideo("video_123", "Netflix Movie A - Updated");
        System.out.println();

        System.out.println("getVideo(\"video_123\") after invalidation");
        System.out.println(obj.getVideo("video_123"));

        obj.getStatistics();
    }
}