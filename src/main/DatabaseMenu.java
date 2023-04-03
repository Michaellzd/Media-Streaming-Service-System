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



    public static void main(String[] args) {

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            Scanner scanner = new Scanner(System.in);

            int choice;

            do {
                System.out.println("Simple Database Menu");
                System.out.println("1. Information Processing");
                System.out.println("2. Maintaining Metadata and Records");
                System.out.println("3. Maintaining Payments");
                System.out.println("4. Reports");
                System.out.println("0. Exit");
                System.out.print("Enter your choice: ");
                choice = scanner.nextInt();

                MediaStreamingService mediaStreamingService = new MediaStreamingService(connection);
                InformProcess informProcess=new InformProcess();
                ReportProcess reportProcess=new ReportProcess();

                switch (choice) {
                    case 1:
                        informProcess.informationProcessingMenu(mediaStreamingService, scanner);
                        break;
                    case 2:
                        metadataAndRecordsMenu(mediaStreamingService, scanner);
                        break;
                    case 3:
                        paymentsMenu(mediaStreamingService, scanner);
                        break;
                    case 4:
                        reportProcess.reportsMenu(mediaStreamingService, scanner);
                        break;
                }
            } while (choice != 0);

        }
        catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }



    }




    private static void metadataAndRecordsMenu(MediaStreamingService mediaStreamingService, Scanner scanner) {


    }


    private static void paymentsMenu(MediaStreamingService mediaStreamingService, Scanner scanner) {
    }


    private static void reportsMenu(MediaStreamingService mediaStreamingService, Scanner scanner) {
    }








}
