package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;


public class DatabaseMenu {
    private static final String DB_URL = "jdbc:mariadb://classdb2.csc.ncsu.edu:3306/zlu28";
    private static final String DB_USER = "zlu28";
    private static final String DB_PASSWORD = "200476848";


    //setting your own pwd


    // This is the main menu.
    public static void main(String[] args) {

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            Scanner scanner = new Scanner(System.in);

            int choice;

            do {
                //The tasks to do
                System.out.println("********************************");

                System.out.println("Simple Database Menu");
                System.out.println("1. Information Processing");
                System.out.println("2. Maintaining Metadata and Records");
                System.out.println("3. Maintaining Payments");
                System.out.println("4. Reports");
                System.out.println("0. Exit");
                System.out.print("Enter your choice: ");

                choice = scanner.nextInt();
                System.out.println("********************************");

                MediaStreamingService mediaStreamingService = new MediaStreamingService(connection);
                PaymentService paymentService = new PaymentService(connection);
                InformProcess informProcess=new InformProcess();
                ReportProcess reportProcess=new ReportProcess();
                PaymentProcess PaymentProcess=new PaymentProcess();
                MaintainPaymentsService maintainPaymentsService=new MaintainPaymentsService(connection);

                switch (choice) {
                    case 1:
                        informProcess.informationProcessingMenu(mediaStreamingService, scanner);
                        break;
                    case 2:
                        MetadataProcess.metadataAndRecordsMenu(mediaStreamingService, scanner);
                        break;
                    case 3:
                        PaymentProcess.paymentProcessingMenu(maintainPaymentsService, scanner);
                        break;
                    case 4:
                        reportProcess.reportsMenu(mediaStreamingService, paymentService, scanner);
                        break;
                }
            } while (choice != 0);

        }
        catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }


    }











}
