package main;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class PaymentProcess {
    public  void informationProcessingMenu(MaintainPaymentsService maintainPaymentsService, Scanner scanner) throws SQLException {
        int choice;
        do {
            System.out.println("Payment Processing Menu");
            System.out.println("1. Generate Monthly Payment");
            System.out.println("2. Pay to Record Label");
            System.out.println("3. Pay to Artists");

            System.out.println("0. Back to Main Menu");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                    break;

            }
        } while (choice != 0);
    }
}
