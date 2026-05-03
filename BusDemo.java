import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

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

    static JTextField dateField;
    static JTextField nameField;
    static JComboBox<Integer> busBox;
    static JTable busTable;
    static DefaultTableModel busTableModel;
    static DefaultListModel<String> bookingListModel;
    static JLabel messageLabel;

    public static void main(String[] args) {

        // Adding some buses manually
        buses.add(new Bus(1, true, 2));
        buses.add(new Bus(2, false, 3));
        buses.add(new Bus(3, true, 1));

        SwingUtilities.invokeLater(BusDemo::createWindow);
    }

    static void createWindow() {
        JFrame frame = new JFrame("Bus Reservation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(680, 430);
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(12, 12));
        mainPanel.setBorder(new EmptyBorder(14, 14, 14, 14));

        JLabel title = new JLabel("Bus Reservation System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        mainPanel.add(title, BorderLayout.NORTH);

        busTableModel = new DefaultTableModel(new String[]{"Bus No", "Type", "Capacity", "Available"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        busTable = new JTable(busTableModel);
        busTable.setRowHeight(24);
        busTable.getTableHeader().setReorderingAllowed(false);
        mainPanel.add(new JScrollPane(busTable), BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Book a Ticket"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        dateField = new JTextField(DATE_FORMAT.format(new Date()), 10);
        nameField = new JTextField(14);
        busBox = new JComboBox<>();
        for (Bus bus : buses) {
            busBox.addItem(bus.busNo);
        }

        JButton refreshButton = new JButton("Show Buses");
        JButton bookButton = new JButton("Book Ticket");

        addFormRow(formPanel, gbc, 0, "Travel Date", dateField);
        addFormRow(formPanel, gbc, 1, "Passenger Name", nameField);
        addFormRow(formPanel, gbc, 2, "Bus Number", busBox);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        buttonPanel.add(refreshButton);
        buttonPanel.add(bookButton);
        formPanel.add(buttonPanel, gbc);

        messageLabel = new JLabel("Enter a date and choose Show Buses.");
        messageLabel.setForeground(new Color(40, 90, 40));
        gbc.gridy = 4;
        formPanel.add(messageLabel, gbc);

        bookingListModel = new DefaultListModel<>();
        JList<String> bookingList = new JList<>(bookingListModel);
        JScrollPane bookingScroll = new JScrollPane(bookingList);
        bookingScroll.setBorder(BorderFactory.createTitledBorder("Bookings"));
        bookingScroll.setPreferredSize(new Dimension(240, 120));

        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.add(formPanel, BorderLayout.CENTER);
        bottomPanel.add(bookingScroll, BorderLayout.EAST);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        refreshButton.addActionListener(e -> refreshBusTable());
        bookButton.addActionListener(e -> bookTicket());

        frame.setContentPane(mainPanel);
        refreshBusTable();
        frame.setVisible(true);
    }

    static void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(field, gbc);
    }

    static void refreshBusTable() {
        Date date = parseDate(dateField.getText().trim());
        if (date == null) {
            showMessage("Date should be in dd-mm-yyyy format.", true);
            return;
        }

        busTableModel.setRowCount(0);
        for (Bus bus : buses) {
            busTableModel.addRow(new Object[]{
                    bus.busNo,
                    bus.ac ? "AC" : "Non-AC",
                    bus.capacity,
                    getAvailableSeats(bus.busNo, date)
            });
        }

        showMessage("Showing seats for " + DATE_FORMAT.format(date) + ".", false);
    }

    static void bookTicket() {
        String name = nameField.getText().trim();
        Date date = parseDate(dateField.getText().trim());
        Integer busNo = (Integer) busBox.getSelectedItem();

        if (date == null) {
            showMessage("Date should be in dd-mm-yyyy format.", true);
            return;
        }

        if (name.isEmpty()) {
            showMessage("Please enter passenger name.", true);
            return;
        }

        if (busNo == null || !busExists(busNo)) {
            showMessage("Please choose a valid bus.", true);
            return;
        }

        if (!isAvailable(busNo, date)) {
            showMessage("Bus " + busNo + " is full for this date.", true);
            return;
        }

        bookings.add(new Booking(name, busNo, date));
        bookingListModel.addElement(name + " - Bus " + busNo + " - " + DATE_FORMAT.format(date));
        nameField.setText("");
        refreshBusTable();
        showMessage("Booking confirmed for " + name + ".", false);
    }

    static void showMessage(String message, boolean error) {
        messageLabel.setText(message);
        messageLabel.setForeground(error ? new Color(150, 40, 40) : new Color(40, 90, 40));
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
