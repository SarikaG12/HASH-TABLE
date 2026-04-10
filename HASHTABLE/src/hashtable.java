import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class hashtable {

    class TokenBucket {
        private int tokens;
        private final int maxTokens;
        private final double refillRatePerSecond;
        private long lastRefillTime;

        public TokenBucket(int maxTokens, double refillRatePerSecond) {
            this.maxTokens = maxTokens;
            this.tokens = maxTokens;
            this.refillRatePerSecond = refillRatePerSecond;
            this.lastRefillTime = System.currentTimeMillis();
        }

        private void refill() {
            long currentTime = System.currentTimeMillis();
            long elapsedMillis = currentTime - lastRefillTime;

            if (elapsedMillis > 0) {
                double tokensToAdd = (elapsedMillis / 1000.0) * refillRatePerSecond;
                if (tokensToAdd >= 1) {
                    tokens = Math.min(maxTokens, tokens + (int) tokensToAdd);
                    lastRefillTime = currentTime;
                }
            }
        }

        public synchronized String allowRequest() {
            refill();

            if (tokens > 0) {
                tokens--;
                return "Allowed (" + tokens + " requests remaining)";
            } else {
                long currentTime = System.currentTimeMillis();
                double secondsForOneToken = 1.0 / refillRatePerSecond;
                long retryAfter = (long) Math.ceil(secondsForOneToken);
                return "Denied (0 requests remaining, retry after " + retryAfter + "s)";
            }
        }

        public synchronized String getStatus() {
            refill();
            int used = maxTokens - tokens;
            long resetTime = System.currentTimeMillis() / 1000 + (long) Math.ceil(tokens / refillRatePerSecond);

            return "{used: " + used + ", limit: " + maxTokens + ", remaining: " + tokens +
                    ", reset: " + resetTime + "}";
        }
    }

    private ConcurrentHashMap<String, TokenBucket> clientBuckets;
    private final int LIMIT = 1000;
    private final double REFILL_RATE = 1000.0 / 3600.0; // 1000 tokens per hour

    public hashtable() {
        clientBuckets = new ConcurrentHashMap<>();
    }

    public String checkRateLimit(String clientId) {
        clientBuckets.putIfAbsent(clientId, new TokenBucket(LIMIT, REFILL_RATE));
        return clientBuckets.get(clientId).allowRequest();
    }

    public String getRateLimitStatus(String clientId) {
        clientBuckets.putIfAbsent(clientId, new TokenBucket(LIMIT, REFILL_RATE));
        return clientBuckets.get(clientId).getStatus();
    }

    public static void main(String[] args) {
        hashtable obj = new hashtable();

        System.out.println("checkRateLimit(clientId=\"abc123\") -> " + obj.checkRateLimit("abc123"));
        System.out.println("checkRateLimit(clientId=\"abc123\") -> " + obj.checkRateLimit("abc123"));
        System.out.println("checkRateLimit(clientId=\"abc123\") -> " + obj.checkRateLimit("abc123"));

        System.out.println("getRateLimitStatus(\"abc123\") -> " + obj.getRateLimitStatus("abc123"));

        for (int i = 0; i < 998; i++) {
            obj.checkRateLimit("abc123");
        }

        System.out.println("checkRateLimit(clientId=\"abc123\") -> " + obj.checkRateLimit("abc123"));
        System.out.println("getRateLimitStatus(\"abc123\") -> " + obj.getRateLimitStatus("abc123"));
    }
}