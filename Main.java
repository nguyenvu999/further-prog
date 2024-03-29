    import java.io.*;
    import java.text.ParseException;
    import java.text.SimpleDateFormat;
    import java.util.*;

    class Customer {
        String id;
        String fullName;
        InsuranceCard insuranceCard;
        List<Claim> claims;
        List<Customer> dependents;

        public Customer(String id, String fullName, InsuranceCard insuranceCard) {
            this.id = id;
            this.fullName = fullName;
            this.insuranceCard = insuranceCard;
            this.claims = new ArrayList<>();
            this.dependents = new ArrayList<>();
        }

        public void addClaim(Claim claim) {
            claims.add(claim);
        }

        public void removeClaim(Claim claim) {
            claims.remove(claim);
        }
    }

    class InsuranceCard {
        String cardNumber;
        String cardHolder;
        String policyOwner;
        Date expirationDate;

        public InsuranceCard(String cardNumber, String cardHolder, String policyOwner, Date expirationDate) {
            this.cardNumber = cardNumber;
            this.cardHolder = cardHolder;
            this.policyOwner = policyOwner;
            this.expirationDate = expirationDate;
        }
    }

    class Claim {
        String id;
        Date claimDate;
        String insuredPerson;
        String cardNumber;
        Date examDate;
        List<String> documents;
        double claimAmount;
        String status;
        String receiverBankingInfo;

        public Claim(String id, Date claimDate, String insuredPerson, String cardNumber, Date examDate,
                     List<String> documents, double claimAmount, String status, String receiverBankingInfo) {
            this.id = id;
            this.claimDate = claimDate;
            this.insuredPerson = insuredPerson;
            this.cardNumber = cardNumber;
            this.examDate = examDate;
            this.documents = documents;
            this.claimAmount = claimAmount;
            this.status = status;
            this.receiverBankingInfo = receiverBankingInfo;
        }
    }

    interface ClaimProcessManager {
        void add(Claim claim);
        void update(Claim claim);
        void delete(String claimId);
        Claim getOne(String claimId);
        List<Claim> getAll();
        List<String> getAllClaimIds(); // New method to retrieve all claim IDs
    }

    class SimpleClaimProcessManager implements ClaimProcessManager {
        private Map<String, Claim> claims;

        public SimpleClaimProcessManager() {
            claims = new HashMap<>();
        }

        @Override
        public void add(Claim claim) {
            claims.put(claim.id, claim);
        }
        @Override
        public void update(Claim claim) {
            if (claims.containsKey(claim.id)) {
                claims.put(claim.id, claim);
            }
        }
        public List<String> getAllClaimIds() {
            return new ArrayList<>(claims.keySet());
        }

        @Override
        public void delete(String claimId) {
            claims.remove(claimId);
        }

        @Override
        public Claim getOne(String claimId) {
            return claims.get(claimId);
        }

        @Override
        public List<Claim> getAll() {
            return new ArrayList<>(claims.values());
        }
    }

    class FileManager {
        private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

        public static List<Customer> loadCustomers(String filePath) {
            List<Customer> customers = new ArrayList<>();
            Set<String> existingIds = new HashSet<>(); // To track existing IDs
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    String id = parts[0];

                    // Check if the ID already exists
                    if (existingIds.contains(id)) {
                        System.out.println("Error: Duplicate customer ID found in the file. Skipping.");
                        continue; // Skip adding this customer
                    }
                    existingIds.add(id); // Add ID to set

                    String fullName = parts[1];
                    String cardNumber = parts[2];
                    InsuranceCard insuranceCard = findInsuranceCard(cardNumber);
                    customers.add(new Customer(id, fullName, insuranceCard));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return customers;
        }


        public static List<Claim> loadClaims(String filePath) {
            List<Claim> claims = new ArrayList<>();
            Set<String> existingIds = new HashSet<>(); // To track existing claim IDs
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    String id = parts[0];

                    // Check if the ID already exists
                    if (existingIds.contains(id)) {
                        System.out.println("Error: Duplicate claim ID found in the file. Skipping.");
                        continue; // Skip adding this claim
                    }
                    existingIds.add(id); // Add ID to set

                    Date claimDate = parts[1].isEmpty() ? null : DATE_FORMAT.parse(parts[1]);
                    String insuredPerson = parts[2];
                    String cardNumber = parts[3];
                    Date examDate = parts[4].isEmpty() ? null : DATE_FORMAT.parse(parts[4]);
                    // Parse documents
                    List<String> documents = Arrays.asList(parts[5].split(";"));
                    double claimAmount = Double.parseDouble(parts[6]);
                    String status = parts[7];
                    String receiverBankingInfo = parts[8];
                    claims.add(new Claim(id, claimDate, insuredPerson, cardNumber, examDate,
                            documents, claimAmount, status, receiverBankingInfo));
                }
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
            return claims;
        }


        public static List<InsuranceCard> loadInsuranceCards(String filePath) {
            List<InsuranceCard> cards = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    String cardNumber = parts[0];
                    String cardHolder = parts[1];
                    String policyOwner = parts[2];
                    Date expirationDate = DATE_FORMAT.parse(parts[3]);
                    cards.add(new InsuranceCard(cardNumber, cardHolder, policyOwner, expirationDate));
                }
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
            return cards;
        }

        public static void saveCustomers(List<Customer> customers, String filePath) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                for (Customer customer : customers) {
                    String cardNumber = customer.insuranceCard != null ? customer.insuranceCard.cardNumber : "N/A";
                    writer.write(customer.id + "," + customer.fullName + "," + cardNumber);
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public static void saveClaims(List<Claim> claims, String filePath) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                for (Claim claim : claims) {
                    String claimDateStr = claim.claimDate != null ? DATE_FORMAT.format(claim.claimDate) : "";
                    String examDateStr = claim.examDate != null ? DATE_FORMAT.format(claim.examDate) : "";
                    String documents = claim.documents != null ? String.join(";", claim.documents) : "";
                    writer.write(claim.id + "," + claimDateStr + "," + claim.insuredPerson +
                            "," + claim.cardNumber + "," + examDateStr + "," + documents +
                            "," + claim.claimAmount + "," + claim.status + "," + claim.receiverBankingInfo);
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public static void saveInsuranceCards(List<InsuranceCard> cards, String filePath) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                for (InsuranceCard card : cards) {
                    writer.write(card.cardNumber + "," + card.cardHolder + "," + card.policyOwner + "," +
                            DATE_FORMAT.format(card.expirationDate));
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private static InsuranceCard findInsuranceCard(String cardNumber
        ) {
            // Implement logic to find insurance card by card number
            return null;
        }
    }

    public class Main {
        private static ClaimProcessManager claimManager;

        public static void main(String[] args) {

            List<Customer> customers = FileManager.loadCustomers("customers.txt");
            List<Claim> claims = FileManager.loadClaims("claims.txt");
            List<InsuranceCard> insuranceCards = FileManager.loadInsuranceCards("insurance_cards.txt");

            claimManager = new SimpleClaimProcessManager();
            for (Claim claim : claims) {
                claimManager.add(claim);
            }

            // Simple text-based UI
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("\nInsurance Management System");
                System.out.println("1. Manage Claims");
                System.out.println("2. Manage Customers");
                System.out.println("3. Manage Insurance Cards");
                System.out.println("4. Exit");
                System.out.print("Enter your choice: ");

                String choice = scanner.nextLine();

                switch (choice) {
                    case "1":
                        manageClaims();
                        break;
                    case "2":
                        manageCustomers(customers);
                        break;
                    case "3":
                        manageInsuranceCards(insuranceCards);
                        break;
                    case "4":
                        // Save data to files and exit
                        FileManager.saveCustomers(customers, "customers.txt");
                        FileManager.saveClaims(claimManager.getAll(), "claims.txt");
                        FileManager.saveInsuranceCards(insuranceCards, "insurance_cards.txt");
                        System.out.println("Data saved. Exiting...");
                        System.exit(0);
                    default:
                        System.out.println("Invalid choice. Please enter a valid option.");
                }
            }
        }

        private static void manageClaims() {
            // Add, delete, view claims functionality
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("\nManage Claims");
                System.out.println("1. Add Claim");
                System.out.println("2. Delete Claim");
                System.out.println("3. View All Claims");
                System.out.println("4. Back to Main Menu");
                System.out.print("Enter your choice: ");
                String choice = scanner.nextLine();

                switch (choice) {
                    case "1":
                        addClaim();
                        break;
                    case "2":
                        deleteClaim();
                        break;
                    case "3":
                        viewAllClaims();
                        break;
                    case "4":
                        return;
                    default:
                        System.out.println("Invalid choice. Please enter a valid option.");
                }
            }
        }

        private static void addClaim() {
            Scanner scanner = new Scanner(System.in);

            String id;
            do {
                System.out.print("Enter claim ID (Format: f-numbers;10 numbers): ");
                id = scanner.nextLine().trim();
                if (!id.matches("f-\\d{10}")) {
                    System.out.println("Error: Invalid claim ID format. Please enter in the format f-numbers;10 numbers.");
                } else if (isDuplicateClaimId(id, claimManager.getAll())) {
                    System.out.println("Error: Claim with the same ID already exists.");
                    id = null; // Reset id to trigger re-entry of claim ID
                }
            } while (id == null || !id.matches("f-\\d{10}")); // Ensure id is not null before matching


            System.out.print("Enter claim date (YYYY-MM-DD): ");
            Date claimDate = parseDate(getInputNotBlank(scanner, "Claim Date"));

            String insuredPerson;
            do {
                System.out.print("Insured Person: ");
                insuredPerson = getInputNotBlank(scanner, "Insured Person");
                if (containsDigits(insuredPerson)) {
                    System.out.println("Error: Name cannot contain numbers. Please enter a valid name.");
                    insuredPerson = null;
                }
            } while (insuredPerson == null);

            System.out.print("Enter card number: ");
            String cardNumber = getInputNotBlank(scanner, "Card Number");

            System.out.print("Enter exam date (YYYY-MM-DD): ");
            Date examDate = parseDate(getInputNotBlank(scanner, "Exam Date"));

            double claimAmount;
            while (true) {
                try {
                    System.out.print("Enter claim amount: ");
                    String input = getInputNotBlank(scanner, "Claim Amount");
                    claimAmount = Double.parseDouble(input);
                    break; // Exit loop if input is successfully read
                } catch (NumberFormatException e) {
                    System.out.println("Error: Invalid input for claim amount. Please enter a valid number.");
                }
            }

            String status;
            do {
                status = getInputNotBlank(scanner, "Claim Status (New, Processing, Done)");
                if (!isValidStatus(status)) {
                    System.out.println("Error: Invalid status. Please enter one of the following: New, Processing, Done.");
                }
            } while (!isValidStatus(status));

            System.out.print("Enter receiver banking info: ");
            String receiverBankingInfo = getInputNotBlank(scanner, "Receiver Banking Info");

            Claim newClaim = new Claim(id, claimDate, insuredPerson, cardNumber, examDate,
                    null, claimAmount, status, receiverBankingInfo);
            claimManager.add(newClaim);

            System.out.println("Claim added successfully.");
        }

        private static String getInputNotBlank(Scanner scanner, String fieldName) {
            String input;
            do {

                input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    System.out.println("Error: " + fieldName + " cannot be blank. Please enter a value.");
                    System.out.print("Enter " + fieldName + ": ");
                }
            } while (input.isEmpty());
            return input;
        }
        private static boolean isDuplicateClaimId(String claimId, List<Claim> claims) {
            for (Claim claim : claims) {
                if (claim.id.equals(claimId)) {
                    return true;
                }
            }
            return false;
        }





        private static boolean isValidStatus(String status) {
            return status.equalsIgnoreCase("New") || status.equalsIgnoreCase("Processing") || status.equalsIgnoreCase("Done");
        }
        private static boolean containsDigits(String str) {
            for (char c : str.toCharArray()) {
                if (Character.isDigit(c)) {
                    return true;
                }
            }
            return false;
        }

        private static void deleteClaim() {
            Scanner scanner = new Scanner(System.in);

            System.out.println("Enter claim ID to delete:");
            String id = scanner.nextLine();

            Claim existingClaim = claimManager.getOne(id);
            if (existingClaim == null) {
                System.out.println("Claim not found.");
                return;
            }

            claimManager.delete(id);

            System.out.println("Claim deleted successfully.");
        }

        private static void viewAllClaims() {
            List<Claim> allClaims = claimManager.getAll();
            if (allClaims.isEmpty()) {
                System.out.println("No claims found.");
                return;
            }

            System.out.println("All Claims:");
            for (Claim claim : allClaims) {
                System.out.println("ID: " + claim.id);
                System.out.println("Claim Date: " + claim.claimDate);
                System.out.println("Insured Person: " + claim.insuredPerson);
                System.out.println("Card Number: " + claim.cardNumber);
                System.out.println("Exam Date: " + claim.examDate);
                System.out.println("Claim Amount: " + claim.claimAmount);
                System.out.println("Status: " + claim.status);
                System.out.println("Receiver Banking Info: " + claim.receiverBankingInfo);
                System.out.println();
            }
        }

        private static void updateClaim() {
            Scanner scanner = new Scanner(System.in);

            System.out.println("Enter claim ID to update:");
            String id = scanner.nextLine();

            Claim existingClaim = claimManager.getOne(id);
            if (existingClaim == null) {
                System.out.println("Claim not found.");
                return;
            }


            System.out.println("Enter updated claim date (YYYY-MM-DD):");
            existingClaim.claimDate = parseDate(scanner.nextLine());

            System.out.println("Enter updated exam date (YYYY-MM-DD):");
            existingClaim.examDate = parseDate(scanner.nextLine());

            System.out.println("Enter updated claim amount:");
            existingClaim.claimAmount = scanner.nextDouble();
            scanner.nextLine();

            System.out.println("Enter updated claim status (New, Processing, Done):");
            existingClaim.status = scanner.nextLine();

            System.out.println("Enter updated receiver banking info:");
            existingClaim.receiverBankingInfo = scanner.nextLine();

            claimManager.update(existingClaim);

            System.out.println("Claim updated successfully.");
        }


        private static void manageCustomers(List<Customer> customers) {
            // Add, delete, view customers functionality
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("\nManage Customers");
                System.out.println("1. Add Customer");
                System.out.println("2. Delete Customer");
                System.out.println("3. View All Customers");
                System.out.println("4. Back to Main Menu");
                System.out.print("Enter your choice: ");
                String choice = scanner.nextLine();

                switch (choice) {
                    case "1":
                        addCustomer(customers);
                        break;
                    case "2":
                        deleteCustomer(customers);
                        break;
                    case "3":
                        viewAllCustomers(customers);
                        break;
                    case "4":
                        return;
                    default:
                        System.out.println("Invalid choice. Please enter a valid option.");
                }
            }
        }

        private static void addCustomer(List<Customer> customers) {
            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter customer ID: ");
            String id = scanner.nextLine();

            // Check if the ID already exists
            for (Customer customer : customers) {
                if (customer.id.equals(id)) {
                    System.out.println("Error: Customer with the same ID already exists.");
                    return; // Exit the method if ID already exists
                }
            }

            System.out.print("Enter customer full name: ");
            String fullName = scanner.nextLine();

            // Assuming InsuranceCard details are already managed elsewhere
            InsuranceCard insuranceCard = null;

            Customer newCustomer = new Customer(id, fullName, insuranceCard);
            customers.add(newCustomer);

            System.out.println("Customer added successfully.");
        }

        private static void deleteCustomer(List<Customer> customers) {
            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter customer ID to delete: ");
            String id = scanner.nextLine();

            Iterator<Customer> iterator = customers.iterator();
            while (iterator.hasNext()) {
                Customer customer = iterator.next();
                if (customer.id.equals(id)) {
                    iterator.remove();
                    System.out.println("Customer deleted successfully.");
                    return;
                }
            }

            System.out.println("Customer not found.");
        }

        private static void viewAllCustomers(List<Customer> customers) {
            System.out.println("All Customers:");
            for (Customer customer : customers) {
                System.out.println("ID: " + customer.id);
                System.out.println("Full Name: " + customer.fullName);
                System.out.println("Insurance Card: " + customer.insuranceCard); // Update as needed
                System.out.println();
            }
        }
        private static void manageInsuranceCards(List<InsuranceCard> insuranceCards) {
            // Add, delete, view insurance cards functionality
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("\nManage Insurance Cards");
                System.out.println("1. Add Insurance Card");
                System.out.println("2. Delete Insurance Card");
                System.out.println("3. View All Insurance Cards");
                System.out.println("4. Back to Main Menu");
                System.out.print("Enter your choice: ");
                String choice = scanner.nextLine();

                switch (choice) {
                    case "1":
                        addInsuranceCard(insuranceCards);
                        break;
                    case "2":
                        deleteInsuranceCard(insuranceCards);
                        break;
                    case "3":
                        viewAllInsuranceCards(insuranceCards);
                        break;
                    case "4":
                        return;
                    default:
                        System.out.println("Invalid choice. Please enter a valid option.");
                }
            }
        }

        private static void addInsuranceCard(List<InsuranceCard> insuranceCards) {
            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter card number: ");
            String cardNumber = scanner.nextLine();

            System.out.print("Enter card holder: ");
            String cardHolder = scanner.nextLine();

            System.out.print("Enter policy owner: ");
            String policyOwner = scanner.nextLine();

            System.out.print("Enter expiration date (YYYY-MM-DD): ");
            Date expirationDate = parseDate(scanner.nextLine());

            InsuranceCard newCard = new InsuranceCard(cardNumber, cardHolder, policyOwner, expirationDate);
            insuranceCards.add(newCard);

            System.out.println("Insurance card added successfully.");
        }

        private static void deleteInsuranceCard(List<InsuranceCard> insuranceCards) {
            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter card number to delete: ");
            String cardNumber = scanner.nextLine();

            Iterator<InsuranceCard> iterator = insuranceCards.iterator();
            while (iterator.hasNext()) {
                InsuranceCard card = iterator.next();
                if (card.cardNumber.equals(cardNumber)) {
                    iterator.remove();
                    System.out.println("Insurance card deleted successfully.");
                    return;
                }
            }

            System.out.println("Insurance card not found.");
        }

        private static void viewAllInsuranceCards(List<InsuranceCard> insuranceCards) {
            System.out.println("All Insurance Cards:");
            for (InsuranceCard card : insuranceCards) {
                System.out.println("Card Number: " + card.cardNumber);
                System.out.println("Card Holder: " + card.cardHolder);
                System.out.println("Policy Owner: " + card.policyOwner);
                System.out.println("Expiration Date: " + card.expirationDate);
                System.out.println();
            }
        }

        private static Date parseDate(String dateString) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateFormat.setLenient(false); // Disable lenient parsing

            while (true) {
                try {
                    return dateFormat.parse(dateString);
                } catch (ParseException e) {
                    System.out.println("Invalid date format! Please enter a date in the format YYYY-MM-DD");
                    System.out.print("Enter date (YYYY-MM-DD): ");
                    Scanner scanner = new Scanner(System.in);
                    dateString = scanner.nextLine();
                }
            }
        }




    }
