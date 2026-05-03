import java.awt.*;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

class BusReservationGUI {

    static JTextField dateField;
    static JTextField nameField;
    static JComboBox<Integer> busBox;
    static JTable busTable;
    static DefaultTableModel busTableModel;
    static DefaultListModel<String> bookingListModel;
    static JLabel messageLabel;

    static void showWindow() {
        SwingUtilities.invokeLater(BusReservationGUI::createWindow);
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

        busTableModel = new DefaultTableModel(new String[]{"Bus No", "Type", "Capacity", "Available"}, 0);
        busTable = new JTable(busTableModel);
        busTable.setRowHeight(24);
        busTable.getTableHeader().setReorderingAllowed(false);
        mainPanel.add(new JScrollPane(busTable), BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Book a Ticket"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        dateField = new JTextField(BusDemo.DATE_FORMAT.format(new Date()), 10);
        nameField = new JTextField(14);
        busBox = new JComboBox<>();
        for (Bus bus : BusDemo.buses) {
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
        Date date = BusDemo.parseDate(dateField.getText().trim());
        if (date == null) {
            showMessage("Date should be in dd-mm-yyyy format.", true);
            return;
        }

        busTableModel.setRowCount(0);
        for (Bus bus : BusDemo.buses) {
            busTableModel.addRow(new Object[]{
                    bus.busNo,
                    bus.ac ? "AC" : "Non-AC",
                    bus.capacity,
                    BusDemo.getAvailableSeats(bus.busNo, date)
            });
        }

        showMessage("Showing seats for " + BusDemo.DATE_FORMAT.format(date) + ".", false);
    }

    static void bookTicket() {
        String name = nameField.getText().trim();
        Date date = BusDemo.parseDate(dateField.getText().trim());
        Integer busNo = (Integer) busBox.getSelectedItem();

        if (date == null) {
            showMessage("Date should be in dd-mm-yyyy format.", true);
            return;
        }

        if (name.isEmpty()) {
            showMessage("Please enter passenger name.", true);
            return;
        }

        if (busNo == null || !BusDemo.busExists(busNo)) {
            showMessage("Please choose a valid bus.", true);
            return;
        }

        if (!BusDemo.isAvailable(busNo, date)) {
            showMessage("Bus " + busNo + " is full for this date.", true);
            return;
        }

        BusDemo.bookings.add(new Booking(name, busNo, date));
        bookingListModel.addElement(name + " - Bus " + busNo + " - " + BusDemo.DATE_FORMAT.format(date));
        nameField.setText("");
        refreshBusTable();
        showMessage("Booking confirmed for " + name + ".", false);
    }

    static void showMessage(String message, boolean error) {
        messageLabel.setText(message);
        messageLabel.setForeground(error ? new Color(150, 40, 40) : new Color(40, 90, 40));
    }
}
