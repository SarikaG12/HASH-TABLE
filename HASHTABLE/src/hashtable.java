import java.util.*;

public class hashtable {

    class DNSEntry {
        String domain;
        String ipAddress;
        long expiryTime;

        DNSEntry(String domain, String ipAddress, long ttlSeconds) {
            this.domain = domain;
            this.ipAddress = ipAddress;
            this.expiryTime = System.currentTimeMillis() + (ttlSeconds * 1000);
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }

        long getRemainingTTL() {
            long remaining = (expiryTime - System.currentTimeMillis()) / 1000;
            return Math.max(0, remaining);
        }
    }

    private LinkedHashMap<String, DNSEntry> cache;
    private int maxSize;
    private int hits;
    private int misses;
    private long totalLookupTime;

    public hashtable(int maxSize) {
        this.maxSize = maxSize;
        this.hits = 0;
        this.misses = 0;
        this.totalLookupTime = 0;

        cache = new LinkedHashMap<String, DNSEntry>(16, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, DNSEntry> eldest) {
                return size() > hashtable.this.maxSize;
            }
        };
    }

    public String resolve(String domain, long ttlSeconds) {
        long start = System.nanoTime();

        if (cache.containsKey(domain)) {
            DNSEntry entry = cache.get(domain);

            if (!entry.isExpired()) {
                hits++;
                long end = System.nanoTime();
                totalLookupTime += (end - start);

                return "Cache HIT -> " + entry.ipAddress + " (TTL remaining: " + entry.getRemainingTTL() + "s)";
            } else {
                cache.remove(domain);
                System.out.println("Cache EXPIRED for " + domain);
            }
        }

        misses++;
        String ip = queryUpstreamDNS(domain);
        cache.put(domain, new DNSEntry(domain, ip, ttlSeconds));

        long end = System.nanoTime();
        totalLookupTime += (end - start);

        return "Cache MISS -> Query upstream -> " + ip + " (TTL: " + ttlSeconds + "s)";
    }

    private String queryUpstreamDNS(String domain) {
        if (domain.equals("google.com")) {
            return "172.217.14.206";
        } else if (domain.equals("youtube.com")) {
            return "142.250.183.238";
        } else if (domain.equals("github.com")) {
            return "140.82.121.3";
        } else {
            return "192.168.1.1";
        }
    }

    public void removeExpiredEntries() {
        Iterator<Map.Entry<String, DNSEntry>> it = cache.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<String, DNSEntry> mapEntry = it.next();
            if (mapEntry.getValue().isExpired()) {
                it.remove();
            }
        }
    }

    public String getCacheStats() {
        int totalRequests = hits + misses;
        double hitRate = 0.0;
        double avgLookupTimeMs = 0.0;

        if (totalRequests > 0) {
            hitRate = (hits * 100.0) / totalRequests;
            avgLookupTimeMs = (totalLookupTime / 1000000.0) / totalRequests;
        }

        return "Hit Rate: " + String.format("%.2f", hitRate) + "%, Avg Lookup Time: "
                + String.format("%.4f", avgLookupTimeMs) + " ms";
    }

    public void displayCache() {
        System.out.println("\nCurrent Cache:");
        if (cache.isEmpty()) {
            System.out.println("Cache is empty");
            return;
        }

        for (Map.Entry<String, DNSEntry> entry : cache.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue().ipAddress
                    + " , TTL remaining: " + entry.getValue().getRemainingTTL() + "s");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        hashtable obj = new hashtable(3);

        System.out.println(obj.resolve("google.com", 5));
        System.out.println(obj.resolve("youtube.com", 8));
        System.out.println(obj.resolve("google.com", 5));

        obj.displayCache();

        Thread.sleep(6000);

        System.out.println("\nAfter 6 seconds:");
        System.out.println(obj.resolve("google.com", 5));

        obj.removeExpiredEntries();
        obj.displayCache();

        System.out.println(obj.resolve("github.com", 10));
        System.out.println(obj.resolve("hotmail.com", 10));

        obj.displayCache();

        System.out.println("\n" + obj.getCacheStats());
    }
}