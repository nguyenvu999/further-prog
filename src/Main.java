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
    String role; // Add a field for the role

    public Customer(String id, String fullName) {
        this.id = id;
        this.fullName = fullName;
        this.claims = new ArrayList<>();
        this.dependents = new ArrayList<>();
    }


    // Add a setter method for InsuranceCard
    public void setInsuranceCard(InsuranceCard insuranceCard) {
        this.insuranceCard = insuranceCard;
    }

    public InsuranceCard getInsuranceCard() {
        return insuranceCard;
    }

    public String getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    // Getter and setter for the role field
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void addClaim(Claim claim) {
        claims.add(claim);
    }

    public void removeClaim(Claim claim) {
        claims.remove(claim);
    }


    public void setDependents(List<Customer> dependents) {
        this.dependents = dependents;
    }


    public static String getInputNotBlank(Scanner scanner, String fieldName) {
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

    public void addDependent(Customer dependent) {
        if (this.dependents != null) {
            this.dependents.add(dependent);
        }
    }

    public List<Customer> getDependents() {
        return this.dependents;
    }

    public void updateInsuranceCardNumber(String newCardNumber) {
        if (insuranceCard != null) {
            insuranceCard.cardNumber = newCardNumber;
            System.out.println("Insurance Card Number updated successfully.");
        } else {
            System.out.println("No insurance card information available.");
        }
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


    public InsuranceCard(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardNumber() {
        return cardNumber;
    }
    public static String getInputNotBlank(Scanner scanner, String fieldName) {
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
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String id = parts[0];
                String fullName = parts[1];
                String role = parts[2]; // Assuming role is the third field in the file
                String cardNumber = parts[3]; // Assuming card number is the fourth field in the file

                // Create a new Customer object with insurance card information
                Customer customer = new Customer(id, fullName);
                customer.setRole(role);

                // Create an InsuranceCard object with card number and set it to the customer
                InsuranceCard insuranceCard = new InsuranceCard(cardNumber, "", "", null);
                customer.setInsuranceCard(insuranceCard);

                customers.add(customer);
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

                // Validate card number to contain only numbers
                if (!cardNumber.matches("\\d+")) {
                    System.out.println("Error: Invalid card number detected in file. Only numbers are allowed.");
                    continue;
                }

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
                // Format: id,fullName,role,cardNumber
                String customerData = String.format("%s,%s,%s,%s",
                        customer.getId(), customer.getFullName(), customer.getRole(), customer.getInsuranceCard().getCardNumber());
                writer.write(customerData);
                writer.newLine();
            }
            System.out.println("Customers saved to file successfully.");
        } catch (IOException e) {
            System.out.println("Error saving customers to file: " + e.getMessage());
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


}

public class Main {
    private static ClaimProcessManager claimManager;
    private static List<Customer> customers;
    private static List<InsuranceCard> insuranceCards; // Add this variable

    public static void main(String[] args) {
        insuranceCards = FileManager.loadInsuranceCards("D:\\untitled\\src\\insurance_cards.txt"); // Load insurance cards first

        customers = FileManager.loadCustomers("D:\\untitled\\src\\customers.txt"); // Pass insurance cards
        List<Claim> claims = FileManager.loadClaims("D:\\untitled\\src\\claims.txt");

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
                    FileManager.saveCustomers(customers, "D:\\untitled\\src\\customers.txt");
                    FileManager.saveClaims(claimManager.getAll(), "D:\\untitled\\src\\claims.txt");
                    FileManager.saveInsuranceCards(insuranceCards, "D:\\untitled\\src\\insurance_cards.txt");
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

        System.out.print("Enter insured person: ");
        String insuredPerson = scanner.nextLine();

        String cardNumber;
        do {
            System.out.print("Enter card number (10 digits only): ");
            cardNumber = scanner.nextLine().trim();
            if (!isValidCardNumber(cardNumber)) {
                System.out.println("Error: Card number can only contain digits and must be 10 digits long.");
            }
        } while (!isValidCardNumber(cardNumber));


        // Find the related customer


        Claim newClaim = new Claim(id, new Date(), insuredPerson, cardNumber, new Date(), null, 0, "New", "");

        claimManager.add(newClaim);

        System.out.println("Claim added successfully to customer: " );
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
            System.out.println("2. Update Customer");
            System.out.println("3. Delete Customer");
            System.out.println("4. View All Customers");
            System.out.println("5. Back to Main Menu");
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    addCustomer(customers);
                    break;
                case "2":
                    updateCustomer(customers);
                    break;
                case "3":
                    deleteCustomer(customers);
                    break;
                case "4":
                    viewAllCustomers(customers);
                    break;
                case "5":
                    return;

                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
            }
        }
    }


    private static void addCustomer(List<Customer> customers) {
        Scanner scanner = new Scanner(System.in);

        String id;
        do {
            System.out.print("Enter customer ID (Format: c-numbers;7 numbers): ");
            id = Customer.getInputNotBlank(scanner, "customer ID");
            if (!isValidCustomerIdFormat(id)) {
                System.out.println("Error: Invalid customer ID format. Please enter in the format c-numbers;7 numbers.");
                id = null; // Reset id to trigger re-entry of customer ID
            } else if (isDuplicateCustomerId(id, customers)) {
                System.out.println("Error: Customer with the same ID already exists.");
                id = null; // Reset id to trigger re-entry of customer ID
            }
        } while (id == null || !isValidCustomerIdFormat(id)); // Ensure id is not null before matching format

        System.out.print("Enter customer full name: ");
        String fullName = Customer.getInputNotBlank(scanner, "customer full name");

        System.out.print("Choose role (Enter '1' for policy holder, '2' for dependent): ");
        String roleChoice = scanner.nextLine();
        String role;
        List<Customer> chosenDependents = new ArrayList<>(); // Initialize chosenDependents list

        if (roleChoice.equals("1")) {
            role = "policy holder";
            // Show list of dependents for policy holder to choose from
            System.out.println("List of Dependents:");
            int index = 1;
            for (Customer customer : customers) {
                if (customer.getRole().equalsIgnoreCase("dependent")) {
                    System.out.println(index + ". ID: " + customer.getId() + ", Full Name: " + customer.getFullName());
                    index++;
                }
            }
            System.out.println("Enter the numbers of dependents you want to choose (comma if you want to choose more than 1): ");
            String chosenDependentsInput = scanner.nextLine();
            String[] chosenDependentsArray = chosenDependentsInput.split(",");
            for (String dependentIndex : chosenDependentsArray) {
                int depIndex = Integer.parseInt(dependentIndex.trim()) - 1;
                if (depIndex >= 0 && depIndex < customers.size()) {
                    Customer dependent = customers.get(depIndex);
                    if (dependent.getRole().equalsIgnoreCase("dependent")) {
                        chosenDependents.add(dependent);
                    }
                }
            }
        } else if (roleChoice.equals("2")) {
            role = "dependent";
        } else {
            System.out.println("Invalid role choice. Defaulting to dependent.");
            role = "dependent";
        }

        String cardNumber;
        do {
            System.out.print("Enter insurance card number (10 digits only): ");
            cardNumber = scanner.nextLine().trim();
            if (!isValidCardNumber(cardNumber)) {
                System.out.println("Error: Card number can only contain digits and must be 10 digits long.");
            }
        } while (!isValidCardNumber(cardNumber));

        // Create InsuranceCard object with card number
        InsuranceCard insuranceCard = new InsuranceCard(cardNumber);

        Customer newCustomer = new Customer(id, fullName);
        newCustomer.setRole(role);
        newCustomer.setInsuranceCard(insuranceCard);
        newCustomer.setDependents(chosenDependents);
        customers.add(newCustomer);

        System.out.println("Customer added successfully.");
    }







    private static boolean isValidCustomerIdFormat(String customerId) {
        // Check if the ID matches the required format c-numbers;7 numbers
        return customerId.matches("c-\\d{7}");
    }




    private static boolean isDuplicateCustomerId(String customerId, List<Customer> customers) {
        for (Customer customer : customers) {
            if (customer.id.equals(customerId)) {
                return true;
            }
        }
        return false;
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
            System.out.println("Role: " + customer.getRole());

            // Print insurance card information
            if (customer.insuranceCard != null) {
                System.out.println("Insurance Card Number: " + customer.insuranceCard.cardNumber);
            }

            // Print dependents for policyholders
            if (customer.getRole().equalsIgnoreCase("policy holder")) {
                List<Customer> dependents = customer.getDependents();
                if (!dependents.isEmpty()) {
                    System.out.println("Dependents:");
                    for (Customer dependent : dependents) {
                        System.out.println("  - ID: " + dependent.id + ", Full Name: " + dependent.fullName);
                    }
                } else {
                    System.out.println("No dependents chosen.");
                }
            }
            System.out.println();
        }
    }

    private static void updateCustomer(List<Customer> customers) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter customer ID to update: ");
        String idToUpdate = scanner.nextLine();

        Customer customerToUpdate = null;
        for (Customer customer : customers) {
            if (customer.id.equals(idToUpdate)) {
                customerToUpdate = customer;
                break;
            }
        }

        if (customerToUpdate == null) {
            System.out.println("Customer not found.");
            return;
        }

        System.out.println("Updating Customer ID: " + customerToUpdate.id);
        System.out.print("Enter updated full name: ");
        String updatedFullName = scanner.nextLine();
        customerToUpdate.fullName = updatedFullName;



        System.out.print("Enter updated insurance card number: ");
        String newCardNumber = scanner.nextLine();
        customerToUpdate.updateInsuranceCardNumber(newCardNumber);

        System.out.println("Customer information updated successfully.");
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

        String cardNumber;
        do {
            System.out.print("Enter card number: ");
            cardNumber = scanner.nextLine().trim();
            if (!isValidCardNumber(cardNumber)) {
                System.out.println("Error: Card number can only contain digits. Please enter a valid card number.");
            }
        } while (!isValidCardNumber(cardNumber));

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

    private static boolean isValidCardNumber(String cardNumber) {
        // Check if cardNumber contains only digits and has length of 10
        return cardNumber.matches("\\d{10}");
    }


    private static void addDependent(Customer policyHolder, List<Customer> customers) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Available Dependents:");
        for (Customer customer : customers) {
            if (!customer.id.equals(policyHolder.id)) {
                System.out.println("ID: " + customer.id + ", Full Name: " + customer.fullName);
            }
        }

        System.out.print("Enter dependent ID: ");
        String dependentId = scanner.nextLine();

        Customer dependent = findCustomerById(dependentId, customers);
        if (dependent != null) {
            policyHolder.addDependent(dependent);
            System.out.println(dependent.fullName + " added as a dependent to " + policyHolder.fullName);
        } else {
            System.out.println("Dependent not found.");
        }
    }

    private static Customer findCustomerById(String id, List<Customer> customers) {
        for (Customer customer : customers) {
            if (customer.id.equals(id)) {
                return customer;
            }
        }
        return null;
    }

}
