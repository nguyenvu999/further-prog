# Claim Management System

This is a simple Claim Management System application designed to manage customer claims and insurance card information. The application provides basic operations such as viewing one or all entries for customers and insurance cards, and it offers full functionality for managing claims, including filing, updating, deleting, viewing, and saving to file.

## Table of Contents
- [Installation](#installation)
- [Usage](#usage)
- [Menu Structure](#menu-structure)
- [Future Plans](#future-plans)
- [User Guides](#insurance-claim-management-system-user-guide)

## Installation

To use this application, follow these steps:

1. Clone the GitHub repository:
   ```
   git clone https://github.com/nguyenvu999/further-prog.git

2. Make sure you are on the `main` branch:
   ```
   git checkout main
   ```

3. Compile and run the `Main` class or run the application using your preferred IDE.

## Usage

The application allows users to perform various operations related to managing customers, insurance cards, and claims. Below are the available operations:

### Customer and Insurance Card Operations

- **ViewOne**: View details of a single customer or insurance card.
- **ViewAll**: View details of all customers or insurance cards.

### Claim Operations

- **File a Claim**: Add a new claim to the system.
- **Update a Claim**: Update an existing claim.
- **Delete a Claim**: Remove a claim from the system.
- **ViewOne**: View details of a single claim.
- **ViewAll**: View details of all claims.
- **Save to File**: Save all claims to a file.

## Menu Structure

The application's menu structure is as follows:

1. The user selects the type of operation: customer, insurance card, or claim.
2. Each sub-menu offers the same set of operations, with full functionality available only for claims.

## Future Plans

Future enhancements for the application may include:

- Implementing additional operations for customers and insurance cards.
- Adding validation and error handling for claim operations.
- Improving the user interface for better user experience.
- Integrating with databases for data storage and retrieval.

Feel free to contribute to the project and suggest further improvements!

---

# Insurance Claim Management System User Guide

Welcome to the Insurance Claim Management System, a console-based application designed to streamline the management of insurance claims, customer data, and insurance card information. This guide provides detailed instructions on how to navigate and utilize the application effectively.

## Starting the Application

Upon launching the application, you will be greeted by the main menu, which prompts you to choose the type of data you wish to manage:

1. **Claims**
2. **Customers**
3. **Insurance Cards**

Use the number keys to select your desired option.

## Managing Claims

After selecting **Claims**, you're presented with a secondary menu offering various operations:

- **Add a Claim**: Follow the prompts to input the claim's details, including dates, amounts, and associated customer and insurance card information.
- **Update a Claim**: Input the ID of the claim you wish to update. You'll then have the option to modify any of the claim's details.
- **Delete a Claim**: Enter the ID of the claim you want to remove. You will be asked for confirmation before the deletion is finalized.
- **View a Claim**: Provide the ID of the claim you wish to view, and its details will be displayed.
- **View All Claims**: Displays a list of all claims stored in the system.
- **Save & Exit**: Saves all changes made during the session and exits the application.

## Viewing Customers

Selecting **Customers** from the main menu allows you to:

- **View a Customer**: Enter the customer ID to display their details.
- **View All Customers**: Lists all customers currently stored in the system.

These options are designed for quick access and review of customer information.

## Viewing Insurance Cards

Upon choosing **Insurance Cards**, the menu will offer:

- **View an Insurance Card**: Input the insurance card number to see the associated details.
- **View All Insurance Cards**: Shows a comprehensive list of all insurance cards in the database.

This section provides an efficient way to manage and review insurance card information.

## General Navigation Tips

- At any point, you can return to the previous menu by selecting the **Back** option.
- To ensure no data is lost, use the **Save & Exit** option to save all changes before exiting the application.

## Data Saving and Loading

- **Automatic Data Loading**: The application automatically loads existing data from CSV files at startup, ensuring you have access to the most up-to-date information.
- **Saving Data**: Changes are saved locally during your session. Utilize the **Save & Exit** function to permanently save changes to the CSV files.

## Conclusion

The Insurance Claim Management System offers a comprehensive suite of tools for managing insurance-related data. By following the instructions and utilizing the functionalities as outlined in this guide, you can efficiently handle claims, customer information, and insurance card details, streamlining administrative processes within your organization.

For additional support or inquiries, refer to the application documentation or contact the development team.

---

