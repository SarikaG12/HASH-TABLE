import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

class hashtable {

    // username -> userId
    private ConcurrentHashMap<String, Integer> usernameMap;

    // username -> attempt count
    private ConcurrentHashMap<String, AtomicInteger> attemptMap;

    // ✅ constructor (same name as class)
    public hashtable() {
        usernameMap = new ConcurrentHashMap<>();
        attemptMap = new ConcurrentHashMap<>();
    }

    // Register username
    public boolean register(String username, int userId) {
        if (usernameMap.containsKey(username)) {
            return false; // already taken
        }
        usernameMap.put(username, userId);
        return true;
    }

    // Check availability (O(1))
    public boolean checkAvailability(String username) {

        // update attempt count
        attemptMap.putIfAbsent(username, new AtomicInteger(0));
        attemptMap.get(username).incrementAndGet();

        return !usernameMap.containsKey(username);
    }

    // Suggest alternatives
    public List<String> suggestAlternatives(String username) {
        List<String> list = new ArrayList<>();

        // add numbers
        for (int i = 1; i <= 5; i++) {
            String temp = username + i;
            if (!usernameMap.containsKey(temp)) {
                list.add(temp);
            }
        }

        // replace underscore
        if (username.contains("_")) {
            String temp = username.replace("_", ".");
            if (!usernameMap.containsKey(temp)) {
                list.add(temp);
            }
        }

        // add suffix
        String temp2 = username + "2026";
        if (!usernameMap.containsKey(temp2)) {
            list.add(temp2);
        }

        return list;
    }

    // Get most attempted username
    public String getMostAttempted() {
        String maxUser = "";
        int max = 0;

        for (Map.Entry<String, AtomicInteger> entry : attemptMap.entrySet()) {
            int count = entry.getValue().get();

            if (count > max) {
                max = count;
                maxUser = entry.getKey();
            }
        }

        return maxUser + " (" + max + " attempts)";
    }

    // Main method
    public static void main(String[] args) {

        hashtable obj = new hashtable();

        // Preload users
        obj.register("john_doe", 1);
        obj.register("admin", 2);

        // Check availability
        System.out.println("john_doe -> " + obj.checkAvailability("john_doe"));
        System.out.println("jane_smith -> " + obj.checkAvailability("jane_smith"));

        // Suggestions
        System.out.println("Suggestions: " + obj.suggestAlternatives("john_doe"));

        // simulate attempts
        obj.checkAvailability("admin");
        obj.checkAvailability("admin");
        obj.checkAvailability("admin");

        // Most attempted
        System.out.println("Most attempted: " + obj.getMostAttempted());
    }
}