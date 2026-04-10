import java.util.*;

public class hashtable {

    static class Transaction {
        int id;
        int amount;
        String merchant;
        String account;
        int timeMinutes; // store time in minutes

        Transaction(int id, int amount, String merchant, String account, String time) {
            this.id = id;
            this.amount = amount;
            this.merchant = merchant;
            this.account = account;
            this.timeMinutes = convertToMinutes(time);
        }

        static int convertToMinutes(String time) {
            String[] parts = time.split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);
            return hour * 60 + minute;
        }

        public String toString() {
            return "{id:" + id + ", amount:" + amount + ", merchant:\"" + merchant +
                    "\", account:\"" + account + "\"}";
        }
    }

    private ArrayList<Transaction> transactions;

    public hashtable() {
        transactions = new ArrayList<>();
    }

    public void addTransaction(int id, int amount, String merchant, String account, String time) {
        transactions.add(new Transaction(id, amount, merchant, account, time));
    }

    // 1. Classic Two Sum
    public void findTwoSum(int target) {
        HashMap<Integer, Transaction> map = new HashMap<>();
        boolean found = false;

        System.out.println("findTwoSum(target=" + target + ") ->");

        for (Transaction t : transactions) {
            int complement = target - t.amount;

            if (map.containsKey(complement)) {
                System.out.println("(" + map.get(complement) + ", " + t + ")");
                found = true;
            }

            map.put(t.amount, t);
        }

        if (!found) {
            System.out.println("No pair found");
        }
    }

    // 2. Two Sum with 1 hour window
    public void findTwoSumWithTimeWindow(int target) {
        HashMap<Integer, ArrayList<Transaction>> map = new HashMap<>();
        boolean found = false;

        System.out.println("findTwoSumWithTimeWindow(target=" + target + ") ->");

        for (Transaction t : transactions) {
            int complement = target - t.amount;

            if (map.containsKey(complement)) {
                ArrayList<Transaction> list = map.get(complement);

                for (Transaction old : list) {
                    if (Math.abs(t.timeMinutes - old.timeMinutes) <= 60) {
                        System.out.println("(" + old + ", " + t + ")");
                        found = true;
                    }
                }
            }

            map.putIfAbsent(t.amount, new ArrayList<Transaction>());
            map.get(t.amount).add(t);
        }

        if (!found) {
            System.out.println("No pair found within 1 hour");
        }
    }

    // 3. K Sum
    public void findKSum(int k, int target) {
        System.out.println("findKSum(k=" + k + ", target=" + target + ") ->");
        ArrayList<Transaction> current = new ArrayList<>();
        boolean found = kSumHelper(0, k, target, current);

        if (!found) {
            System.out.println("No combination found");
        }
    }

    private boolean kSumHelper(int start, int k, int target, ArrayList<Transaction> current) {
        if (k == 0) {
            if (target == 0) {
                System.out.println(current);
                return true;
            }
            return false;
        }

        boolean found = false;

        for (int i = start; i < transactions.size(); i++) {
            current.add(transactions.get(i));
            found = kSumHelper(i + 1, k - 1, target - transactions.get(i).amount, current) || found;
            current.remove(current.size() - 1);
        }

        return found;
    }

    // 4. Duplicate detection
    public void detectDuplicates() {
        HashMap<String, ArrayList<Transaction>> map = new HashMap<>();
        boolean found = false;

        System.out.println("detectDuplicates() ->");

        for (Transaction t : transactions) {
            String key = t.amount + "|" + t.merchant;

            map.putIfAbsent(key, new ArrayList<Transaction>());
            map.get(key).add(t);
        }

        for (Map.Entry<String, ArrayList<Transaction>> entry : map.entrySet()) {
            ArrayList<Transaction> list = entry.getValue();

            HashSet<String> accounts = new HashSet<>();
            for (Transaction t : list) {
                accounts.add(t.account);
            }

            if (accounts.size() > 1 && list.size() > 1) {
                System.out.println("Possible duplicate: amount=" + list.get(0).amount +
                        ", merchant=\"" + list.get(0).merchant + "\", accounts=" + accounts);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No duplicates found");
        }
    }

    public static void main(String[] args) {
        hashtable obj = new hashtable();

        obj.addTransaction(1, 500, "Store A", "acc1", "10:00");
        obj.addTransaction(2, 300, "Store B", "acc2", "10:15");
        obj.addTransaction(3, 200, "Store C", "acc3", "10:30");
        obj.addTransaction(4, 500, "Store A", "acc4", "10:40");
        obj.addTransaction(5, 700, "Store D", "acc5", "12:30");
        obj.addTransaction(6, 300, "Store B", "acc6", "12:50");

        obj.findTwoSum(500);
        System.out.println();

        obj.findTwoSumWithTimeWindow(500);
        System.out.println();

        obj.findKSum(3, 1000);
        System.out.println();

        obj.detectDuplicates();
    }
}