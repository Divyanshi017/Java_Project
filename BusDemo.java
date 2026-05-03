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

    public static void main(String[] args) {

        // Adding some buses manually
        buses.add(new Bus(1, true, 2));
        buses.add(new Bus(2, false, 3));
        buses.add(new Bus(3, true, 1));

        Scanner sc = new Scanner(System.in);

        int choice = 1;

        // Main loop for menu
        while (choice != 2) {

            System.out.println("\n--- Bus Reservation System ---");

            String viewDateInput = readRequiredLine(sc, "Enter travel date to view buses (dd-mm-yyyy): ");
            if (viewDateInput == null) {
                System.out.println("\nInput closed. Exiting...");
                break;
            }

            Date viewDate = parseDate(viewDateInput);
            if (viewDate == null) {
                System.out.println("Invalid date format!");
                continue;
            }

            // Display buses
            for (Bus b : buses) {
                b.displayBusInfo(getAvailableSeats(b.busNo, viewDate));
            }

            System.out.println("1. Book Ticket");
            System.out.println("2. Exit");

            Integer menuChoice = readInt(sc, "Enter choice: ");
            if (menuChoice == null) {
                System.out.println("\nInput closed. Exiting...");
                break;
            }

            choice = menuChoice;

            if (choice != 1 && choice != 2) {
                System.out.println("Please enter 1 or 2.");
                continue;
            }

            if (choice == 1) {
                String name = readRequiredLine(sc, "Enter passenger name: ");
                if (name == null) {
                    System.out.println("\nInput closed. Exiting...");
                    break;
                }

                if (name.isEmpty()) {
                    System.out.println("Passenger name cannot be empty.");
                    continue;
                }

                Integer busNoInput = readInt(sc, "Enter bus number: ");
                if (busNoInput == null) {
                    System.out.println("\nInput closed. Exiting...");
                    break;
                }

                int busNo = busNoInput;

                Date date = viewDate;

                if (!busExists(busNo)) {
                    System.out.println("Invalid bus number!");
                    continue;
                }

                int availableSeats = getAvailableSeats(busNo, date);

                System.out.println("Available seats for bus " + busNo + " on " + viewDateInput + ": " + availableSeats);

                // Checking if seats are available
                if (isAvailable(busNo, date)) {
                    bookings.add(new Booking(name, busNo, date));
                    System.out.println("Booking Confirmed!");
                    System.out.println("Remaining seats: " + getAvailableSeats(busNo, date));
                } else {
                    System.out.println("Sorry! Bus is full for this date.");
                }
            }
        }

        System.out.println("Thank you!");
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

