import java.util.*;

public class hashtable {

    static class ParkingSpot {
        String licensePlate;
        long entryTime;
        String status; // EMPTY, OCCUPIED, DELETED

        ParkingSpot() {
            this.licensePlate = null;
            this.entryTime = 0;
            this.status = "EMPTY";
        }
    }

    private ParkingSpot[] table;
    private int capacity;
    private int occupiedCount;
    private int totalProbes;
    private int totalParks;
    private HashMap<Integer, Integer> entryHourCount;

    public hashtable(int capacity) {
        this.capacity = capacity;
        this.table = new ParkingSpot[capacity];
        for (int i = 0; i < capacity; i++) {
            table[i] = new ParkingSpot();
        }
        this.occupiedCount = 0;
        this.totalProbes = 0;
        this.totalParks = 0;
        this.entryHourCount = new HashMap<>();
    }

    private int hash(String licensePlate) {
        int hashValue = 0;
        for (int i = 0; i < licensePlate.length(); i++) {
            hashValue = (hashValue * 31 + licensePlate.charAt(i)) % capacity;
        }
        return Math.abs(hashValue);
    }

    public String parkVehicle(String licensePlate) {
        if (occupiedCount == capacity) {
            return "Parking lot full";
        }

        int preferredSpot = hash(licensePlate);
        int probes = 0;

        for (int i = 0; i < capacity; i++) {
            int index = (preferredSpot + i) % capacity;

            if (table[index].status.equals("OCCUPIED") && table[index].licensePlate.equals(licensePlate)) {
                return "Vehicle already parked at spot #" + index;
            }

            if (table[index].status.equals("EMPTY") || table[index].status.equals("DELETED")) {
                table[index].licensePlate = licensePlate;
                table[index].entryTime = System.currentTimeMillis();
                table[index].status = "OCCUPIED";

                occupiedCount++;
                totalProbes += probes;
                totalParks++;

                Calendar cal = Calendar.getInstance();
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                entryHourCount.put(hour, entryHourCount.getOrDefault(hour, 0) + 1);

                return "Assigned spot #" + index + " (" + probes + " probes)";
            }

            probes++;
        }

        return "No available spot found";
    }

    public String exitVehicle(String licensePlate) {
        int preferredSpot = hash(licensePlate);

        for (int i = 0; i < capacity; i++) {
            int index = (preferredSpot + i) % capacity;

            if (table[index].status.equals("EMPTY")) {
                return "Vehicle not found";
            }

            if (table[index].status.equals("OCCUPIED") && table[index].licensePlate.equals(licensePlate)) {
                long exitTime = System.currentTimeMillis();
                long durationMillis = exitTime - table[index].entryTime;

                double hours = durationMillis / (1000.0 * 60 * 60);
                double fee = Math.ceil(hours * 5.0);

                long totalMinutes = durationMillis / (1000 * 60);
                long displayHours = totalMinutes / 60;
                long displayMinutes = totalMinutes % 60;

                table[index].licensePlate = null;
                table[index].entryTime = 0;
                table[index].status = "DELETED";
                occupiedCount--;

                return "Spot #" + index + " freed, Duration: " + displayHours + "h " +
                        displayMinutes + "m, Fee: $" + String.format("%.2f", fee);
            }
        }

        return "Vehicle not found";
    }

    public String findNearestAvailableSpot() {
        for (int i = 0; i < capacity; i++) {
            if (table[i].status.equals("EMPTY") || table[i].status.equals("DELETED")) {
                return "Nearest available spot to entrance: #" + i;
            }
        }
        return "No available spot";
    }

    public String getStatistics() {
        double occupancy = (occupiedCount * 100.0) / capacity;
        double avgProbes = totalParks == 0 ? 0.0 : (totalProbes * 1.0) / totalParks;

        int peakHour = -1;
        int maxEntries = 0;

        for (Map.Entry<Integer, Integer> entry : entryHourCount.entrySet()) {
            if (entry.getValue() > maxEntries) {
                maxEntries = entry.getValue();
                peakHour = entry.getKey();
            }
        }

        String peakHourText = (peakHour == -1) ? "No data" : peakHour + ":00-" + (peakHour + 1) + ":00";

        return "Occupancy: " + String.format("%.2f", occupancy) + "%, Avg Probes: " +
                String.format("%.2f", avgProbes) + ", Peak Hour: " + peakHourText;
    }

    public static void main(String[] args) throws InterruptedException {
        hashtable obj = new hashtable(10);

        System.out.println(obj.parkVehicle("ABC-1234"));
        System.out.println(obj.parkVehicle("ABC-1235"));
        System.out.println(obj.parkVehicle("XYZ-9999"));

        System.out.println(obj.findNearestAvailableSpot());

        Thread.sleep(3000);

        System.out.println(obj.exitVehicle("ABC-1234"));
        System.out.println(obj.getStatistics());
    }
}