import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class hashtable {

    private ConcurrentHashMap<String, Integer> stockMap;
    private ConcurrentHashMap<String, LinkedList<Integer>> waitingListMap;

    public hashtable() {
        stockMap = new ConcurrentHashMap<>();
        waitingListMap = new ConcurrentHashMap<>();
    }

    public void addProduct(String productId, int stock) {
        stockMap.put(productId, stock);
        waitingListMap.put(productId, new LinkedList<Integer>());
    }

    public String checkStock(String productId) {
        if (!stockMap.containsKey(productId)) {
            return "Product not found";
        }
        return stockMap.get(productId) + " units available";
    }

    public synchronized String purchaseItem(String productId, int userId) {
        if (!stockMap.containsKey(productId)) {
            return "Product not found";
        }

        int stock = stockMap.get(productId);

        if (stock > 0) {
            stockMap.put(productId, stock - 1);
            return "Success, " + (stock - 1) + " units remaining";
        } else {
            LinkedList<Integer> queue = waitingListMap.get(productId);
            queue.add(userId);
            return "Added to waiting list, position #" + queue.size();
        }
    }

    public void showWaitingList(String productId) {
        if (!waitingListMap.containsKey(productId)) {
            System.out.println("Product not found");
            return;
        }
        System.out.println("Waiting List: " + waitingListMap.get(productId));
    }

    public static void main(String[] args) {
        hashtable obj = new hashtable();

        obj.addProduct("IPHONE15_256GB", 3);

        System.out.println("checkStock(\"IPHONE15_256GB\") -> " + obj.checkStock("IPHONE15_256GB"));

        System.out.println("purchaseItem(\"IPHONE15_256GB\", 12345) -> " + obj.purchaseItem("IPHONE15_256GB", 12345));
        System.out.println("purchaseItem(\"IPHONE15_256GB\", 67890) -> " + obj.purchaseItem("IPHONE15_256GB", 67890));
        System.out.println("purchaseItem(\"IPHONE15_256GB\", 11111) -> " + obj.purchaseItem("IPHONE15_256GB", 11111));
        System.out.println("purchaseItem(\"IPHONE15_256GB\", 99999) -> " + obj.purchaseItem("IPHONE15_256GB", 99999));
        System.out.println("purchaseItem(\"IPHONE15_256GB\", 88888) -> " + obj.purchaseItem("IPHONE15_256GB", 88888));

        obj.showWaitingList("IPHONE15_256GB");
    }
}