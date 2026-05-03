import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

// Bus class
class Bus {
    int busNo;
    boolean ac;
    int capacity;

    Bus(int no, boolean ac, int cap) {
        this.busNo = no;
        this.ac = ac;
        this.capacity = cap;
    }

    void displayBusInfo(int availableSeats) {
        System.out.println("Bus No: " + busNo + " | AC: " + ac + " | Total Capacity: " + capacity
                + " | Available Seats: " + availableSeats);
    }
}

// Booking class
class Booking {
    String passengerName;
    int busNo;
    Date date;

    Booking(String name, int busNo, Date date) {
        this.passengerName = name;
        this.busNo = busNo;
        this.date = date;
    }
}

// Main class
public class BusDemo {

    static ArrayList<Bus> buses = new ArrayList<>();
    static ArrayList<Booking> bookings = new ArrayList<>();
    static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");

    public static void main(String[] args) {

        // Adding some buses manually
        buses.add(new Bus(1, true, 2));
        buses.add(new Bus(2, false, 3));
        buses.add(new Bus(3, true, 1));

        BusReservationGUI.showWindow();
    }

    // Function to check seat availability
    static boolean isAvailable(int busNo, Date date) {
        return getAvailableSeats(busNo, date) > 0;
    }

    static int getAvailableSeats(int busNo, Date date) {

        int capacity = 0;

        // Find bus capacity
        for (Bus b : buses) {
            if (b.busNo == busNo) {
                capacity = b.capacity;
            }
        }

        int booked = 0;

        // Count how many bookings already exist
        for (Booking b : bookings) {
            if (b.busNo == busNo && b.date.equals(date)) {
                booked++;
            }
        }

        // Checking if seats are full
        return capacity - booked;
    }

    static boolean busExists(int busNo) {
        for (Bus b : buses) {
            if (b.busNo == busNo) {
                return true;
            }
        }
        return false;
    }

    static Date parseDate(String dateInput) {
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        format.setLenient(false);

        try {
            return format.parse(dateInput);
        } catch (ParseException e) {
            return null;
        }
    }

    static String readRequiredLine(Scanner sc, String prompt) {
        System.out.print(prompt);

        if (!sc.hasNextLine()) {
            return null;
        }

        return sc.nextLine().trim();
    }

    static Integer readInt(Scanner sc, String prompt) {
        String input = readRequiredLine(sc, prompt);
        if (input == null) {
            return null;
        }

        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
            return -1;
        }
    }
}
