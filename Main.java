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
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String id = parts[0];
                Date claimDate = DATE_FORMAT.parse(parts[1]);
                String insuredPerson = parts[2];
                String cardNumber = parts[3];
                Date examDate = DATE_FORMAT.parse(parts[4]);
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
                writer.write(customer.id + "," + customer.fullName + "," + customer.insuranceCard.cardNumber);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveClaims(List<Claim> claims, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Claim claim : claims) {
                String documents = String.join(";", claim.documents);
                writer.write(claim.id + "," + DATE_FORMAT.format(claim.claimDate) + "," + claim.insuredPerson +
                        "," + claim.cardNumber + "," + DATE_FORMAT.format(claim.examDate) + "," + documents +
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

    private static InsuranceCard findInsuranceCard(String cardNumber) {
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
            System.out.println("\nInsurance Claims Management System");
            System.out.println("1. Add Claim");
            System.out.println("2. Update Claim");
            System.out.println("3. Delete Claim");
            System.out.println("4. View All Claims");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    addClaim();
                    break;
                case "2":
                    updateClaim();
                    break;
                case "3":
                    deleteClaim();
                    break;
                case "4":
                    viewAllClaims();
                    break;
                case "5":
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

    private static void addClaim() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter claim ID:");
        String id = scanner.nextLine();

        System.out.println("Enter claim date (YYYY-MM-DD):");
        Date claimDate = parseDate(scanner.nextLine());

        System.out.println("Enter insured person:");
        String insuredPerson = scanner.nextLine();

        System.out.println("Enter card number:");
        String cardNumber = scanner.nextLine();

        System.out.println("Enter exam date (YYYY-MM-DD):");
        Date examDate = parseDate(scanner.nextLine());

        System.out.println("Enter claim amount:");
        double claimAmount = scanner.nextDouble();
        scanner.nextLine();

        System.out.println("Enter claim status (New, Processing, Done):");
        String status = scanner.nextLine();

        System.out.println("Enter receiver banking info:");
        String receiverBankingInfo = scanner.nextLine();

        Claim newClaim = new Claim(id, claimDate, insuredPerson, cardNumber, examDate,
                null, claimAmount, status, receiverBankingInfo);
        claimManager.add(newClaim);

        System.out.println("Claim added successfully.");
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


    private static Date parseDate(String dateString) {

        return null;
    }
}
