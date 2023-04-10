package main;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class PaymentProcess {
    public static void paymentProcessingMenu(MaintainPaymentsService maintainPaymentsService, Scanner scanner) throws SQLException {
        int choice;
        do {
            System.out.println("********************************");

            System.out.println("Payment Processing Menu");
            System.out.println("1. Make Monthly Payment to Record Label for Given Song");
            System.out.println("2. Make Monthly Payment to Artist for Given Song");
            System.out.println("3. Make Monthly Payment to Podcast Hosts");
            System.out.println("4. Receive Monthly Payment from Subscribers");

            System.out.println("0. Back to Main Menu");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            System.out.println("********************************");


            switch (choice) {
                case 1:
                    payLabelForGivenSongGivenMonthMenu(maintainPaymentsService, scanner);
                    break;
                case 2:
                    payArtistsForGivenSongGivenMonthMenu(maintainPaymentsService, scanner);
                    break;
                case 3:
                    payHostsGivenMonthMenu(maintainPaymentsService, scanner);
                    break;
                case 4:
                    receiveSubFeeMenu(maintainPaymentsService);
                    break;

            }
        } while (choice != 0);
    }

    private static void payLabelForGivenSongGivenMonthMenu(MaintainPaymentsService maintainPaymentsService, Scanner scanner) {
        scanner.nextLine();

        System.out.print("Enter streaming account ID: ");
        String streamingAccountIdStr = scanner.nextLine();

        System.out.print("Enter Song ID: ");
        String songIdStr = scanner.nextLine();

        System.out.println("Enter Month");
        String month = scanner.nextLine();

        Integer streamingAccountId = Integer.parseInt(streamingAccountIdStr);
        Integer songId = Integer.parseInt(songIdStr);

        maintainPaymentsService.payLabelForGivenSongGivenMonth(songId, month, streamingAccountId);
//        maintainPaymentsService.payArtistForGivenSongGivenMonth(songId, month, streamingAccountId);

//        if (podcastId != -1 && hostId != -1) {
//            mediaStreamingService.assignHostToPodcast(hostId, podcastId);
//        } else {
//            System.out.println("Podcast or host not found. Please try again.");
//        }
    }

    private static void payArtistsForGivenSongGivenMonthMenu(MaintainPaymentsService maintainPaymentsService, Scanner scanner) {
        scanner.nextLine();

        System.out.print("Enter streaming account ID: ");
        String streamingAccountIdStr = scanner.nextLine();

        System.out.print("Enter Song ID: ");
        String songIdStr = scanner.nextLine();

        System.out.println("Enter Month");
        String month = scanner.nextLine();

        Integer streamingAccountId = Integer.parseInt(streamingAccountIdStr);
        Integer songId = Integer.parseInt(songIdStr);

        maintainPaymentsService.payArtistForGivenSongGivenMonth(songId, month, streamingAccountId);
    }

    private static void payHostsGivenMonthMenu(MaintainPaymentsService maintainPaymentsService, Scanner scanner) {
        scanner.nextLine();

        System.out.print("Enter streaming account ID: ");
        String streamingAccountIdStr = scanner.nextLine();

//        System.out.println("Enter Month");
//        String month = scanner.nextLine();

        Integer streamingAccountId = Integer.parseInt(streamingAccountIdStr);

        maintainPaymentsService.payHosts(streamingAccountId);
    }

    private static void receiveSubFeeMenu(MaintainPaymentsService maintainPaymentsService) {
        maintainPaymentsService.receiveSubFee();
    }

}
