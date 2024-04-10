// ATM Interface
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;


public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("ATM System");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 200);
            frame.setLocationRelativeTo(null); // Center the frame on the screen


            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());


            JLabel enterAccNumLabel = new JLabel("Enter account number:");
            JTextField accNumField = new JTextField(15);


            JPanel accNumPanel = new JPanel();
            accNumPanel.add(enterAccNumLabel);
            accNumPanel.add(accNumField);


            JLabel enterPINLabel = new JLabel("Enter PIN:");
            JPasswordField pinField = new JPasswordField(15);


            JPanel pinPanel = new JPanel();
            pinPanel.add(enterPINLabel);
            pinPanel.add(pinField);


            JPanel buttonPanel = new JPanel();
            JButton enterButton = new JButton("Enter");
            buttonPanel.add(enterButton);


            panel.add(accNumPanel, BorderLayout.NORTH);
            panel.add(pinPanel, BorderLayout.CENTER);
            panel.add(buttonPanel, BorderLayout.SOUTH);


            frame.add(panel);
            frame.setVisible(true);


            enterButton.addActionListener(e -> {
                String accountNumber = accNumField.getText();
                String pin = new String(pinField.getPassword());
                if (ATMManager.signIn(accountNumber, pin)) {
                    openTransactionMenu();
                    frame.dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid account number or PIN. Please try again.");
                }
            });
        });
    }


    private static void openTransactionMenu() {
        JFrame transactionFrame = new JFrame("Transaction Menu");


        JPanel contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int w = getWidth();
                int h = getHeight();
                Color color1 = new Color(135, 206, 250);
                Color color2 = new Color(30, 144, 255);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };


        transactionFrame.setContentPane(contentPane);


        JButton withdrawButton = new JButton("Withdraw");
        contentPane.add(withdrawButton);


        JButton depositButton = new JButton("Deposit");
        contentPane.add(depositButton);


        JButton balanceButton = new JButton("Check Balance");
        contentPane.add(balanceButton);


        JButton transferButton = new JButton("Transfer Funds");
        contentPane.add(transferButton);


        JButton exitButton = new JButton("Exit");
        contentPane.add(exitButton);


        transactionFrame.setSize(400, 250);
        transactionFrame.setLayout(new FlowLayout());
        transactionFrame.setVisible(true);


        withdrawButton.addActionListener(event -> {
            String amountStr = JOptionPane.showInputDialog("Enter amount to withdraw:");
            if (amountStr != null && !amountStr.isEmpty()) {
                double amount = Double.parseDouble(amountStr);
                if (ATMManager.withdraw(amount)) {
                    JOptionPane.showMessageDialog(null, "Withdrawal of $" + amount + " completed.");
                } else {
                    JOptionPane.showMessageDialog(null, "Insufficient funds or invalid amount.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Invalid amount. Please try again.");
            }
        });


        depositButton.addActionListener(event -> {
            String amountStr = JOptionPane.showInputDialog("Enter amount to deposit:");
            if (amountStr != null && !amountStr.isEmpty()) {
                double amount = Double.parseDouble(amountStr);
                ATMManager.deposit(amount);
                JOptionPane.showMessageDialog(null, "Deposit of $" + amount + " completed.");
            } else {
                JOptionPane.showMessageDialog(null, "Invalid amount. Please try again.");
            }
        });


        balanceButton.addActionListener(event -> {
            double balance = ATMManager.getBalance();
            JOptionPane.showMessageDialog(null, "Current balance: $" + balance);
        });


        transferButton.addActionListener(event -> {
            String transferTo = JOptionPane.showInputDialog("Enter recipient's account number:");
            if (transferTo != null && !transferTo.isEmpty()) {
                String amountStr = JOptionPane.showInputDialog("Enter amount to transfer:");
                if (amountStr != null && !amountStr.isEmpty()) {
                    double amount = Double.parseDouble(amountStr);
                    if (ATMManager.transfer(transferTo, amount)) {
                        JOptionPane.showMessageDialog(null, "Transfer of $" + amount + " to account " + transferTo + " completed.");
                    } else {
                        JOptionPane.showMessageDialog(null, "Insufficient funds or invalid recipient account.");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid amount. Please try again.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Invalid recipient account number. Please try again.");
            }
        });


        exitButton.addActionListener(event -> {
            JOptionPane.showMessageDialog(null, "Thank you for visiting us! Have a great day.");
            System.exit(0);
        });
    }


    static class ATMManager {

        private static Map<String, Account> accounts = new HashMap<>();



        static {
            accounts.put("123456", new Account("123456", "1234", "John Doe", 500.00));
            accounts.put("987654", new Account("987654", "4321", "Jane Smith", 2000.00));
        }


        private static String currentAccountNumber; // Current logged-in account


        public static boolean signIn(String accountNumber, String pin) {
            Account account = accounts.get(accountNumber);
            if (account != null && account.getPin().equals(pin)) {
                currentAccountNumber = accountNumber;
                return true;
            }
            return false;
        }


        public static boolean withdraw(double amount) {
            Account account = accounts.get(currentAccountNumber);
            if (account != null && account.getBalance() >= amount) {
                account.withdraw(amount);
                return true;
            }
            return false;
        }


        public static void deposit(double amount) {
            Account account = accounts.get(currentAccountNumber);
            if (account != null) {
                account.deposit(amount);
            }
        }


        public static double getBalance() {
            Account account = accounts.get(currentAccountNumber);
            return account != null ? account.getBalance() : 0.0;
        }


        public static boolean transfer(String recipientAccountNumber, double amount) {
            Account senderAccount = accounts.get(currentAccountNumber);
            Account recipientAccount = accounts.get(recipientAccountNumber);
            if (senderAccount != null && recipientAccount != null && senderAccount.getBalance() >= amount) {
                senderAccount.withdraw(amount);
                recipientAccount.deposit(amount);
                return true;
            }
            return false;
        }
    }


    static class Account {

        private String accountNumber;
        private String pin;
        private String accountHolderName;
        private double balance;



        public Account(String accountNumber, String pin, String accountHolderName, double balance) {
            this.accountNumber = accountNumber;
            this.pin = pin;
            this.accountHolderName = accountHolderName;
            this.balance = balance;
        }



        public String getAccountNumber() {
            return accountNumber;
        }


        public String getPin() {
            return pin;
        }


        public String getAccountHolderName() {
            return accountHolderName;
        }


        public double getBalance() {
            return balance;
        }



        public void deposit(double amount) {
            balance += amount;
        }



        public void withdraw(double amount) {
            balance -= amount;
        }
    }
}
