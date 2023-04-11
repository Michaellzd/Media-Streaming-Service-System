package main;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;

public class ReportProcess {

    public static boolean isValidDateString(String dateString) {
        String dateFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        sdf.setLenient(false);

        try {
            sdf.parse(dateString);
            return true;
        } catch (ParseException e) {
            System.out.println("Invalid date format. Format the date as YYYY-MM-DD, e.g. 2023-03-08.");
            return false;
        }
    }

    public void reportsMenu(MediaStreamingService mediaStreamingService, PaymentService paymentService, Scanner scanner) throws SQLException {
        int choice;
        do {
            System.out.println("Report Menu");
            System.out.println("1. Report Monthly play count per artist");
            System.out.println("2. Report Monthly play count per given song.");
            System.out.println("3. Report Monthly play count per album");
            System.out.println("4. Report all songs given an artist");
            System.out.println("5. Report all songs  given an album");
            System.out.println("6. Report podcast episodes given an podcast.");
            System.out.println("7. Report total payments made out to record labels per given time period.");
            System.out.println("8. Report total payments made out to artists per given time period.");
            System.out.println("9. Report total payments made out to hosts per given time period.");
            System.out.println("10. Total revenue of the streaming service per month.");
            System.out.println("11. Total revenue of the streaming service per year.");
            System.out.println("0. Back to Main Menu");

            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    reportPerArtists(mediaStreamingService, scanner);
                    break;
                case 2:
                    reportPerSongs(mediaStreamingService, scanner);
                    break;
                case 3:
                    reportPerAlbum(mediaStreamingService, scanner);
                    break;
                case 4:
                    reportGivenArtists(mediaStreamingService, scanner);
                    break;
                case 5:
                    reportGivenAlbum(mediaStreamingService, scanner);
                    break;
                case 6:
                    reportGivenPodcast(mediaStreamingService, scanner);
                    break;
                case 7:
                    getPeriodPaymentToRecordLabel(paymentService, scanner);
                    break;
                case 8:
                    getPeriodPaymentToArtist(paymentService, scanner);
                    break;
                case 9:
                    getPeriodPaymentToHost(paymentService, scanner);
                    break;
                case 10:
                    getStreamingServiceMonthlyRevenue(paymentService, scanner);
                    break;
                case 11:
                    getStreamingServiceYearlyRevenue(paymentService, scanner);
                    break;
            }
        } while (choice != 0);
    }

    private void reportGivenPodcast(MediaStreamingService mediaStreamingService, Scanner scanner) throws SQLException {
        System.out.println("Input Podcast name:");
        String name = scanner.nextLine();
        ResultSet resultSet = mediaStreamingService.reportGivenPodcast(name);

        try {
            while (resultSet.next()) {
                String episode_title = resultSet.getString("episode_title");
                System.out.printf("episode_title: %s%n",
                        episode_title);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void reportGivenAlbum(MediaStreamingService mediaStreamingService, Scanner scanner) throws SQLException {
        System.out.println("Input Album name:");
        String name = scanner.nextLine();
        ResultSet resultSet = mediaStreamingService.reportGivenAlbum(name);

        try {
            ResultSetPrinter.printResultSet(resultSet);
//            while (resultSet.next()) {
//                String song_title = resultSet.getString("song_title");
//
//                System.out.printf("Song title: %s%n",
//                        song_title);
//            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void reportGivenArtists(MediaStreamingService mediaStreamingService, Scanner scanner) throws SQLException {
        System.out.println("Input Artists name:");
        String name = scanner.nextLine();
        ResultSet resultSet = mediaStreamingService.reportGivenArtists(name);
        // System.out.println("artist name:" + name);
        try {
            ResultSetPrinter.printResultSet(resultSet);
//            while (resultSet.next()) {
//                String song_title = resultSet.getString("song_title");
//
//                System.out.printf("Song title %s%n",
//                        song_title);
//            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void reportPerAlbum(MediaStreamingService mediaStreamingService, Scanner scanner) throws SQLException {
        System.out.println("Report Monthly play count per Album:");
        ResultSet resultSet = mediaStreamingService.reportPerAlbums();

        try {
            ResultSetPrinter.printResultSet(resultSet);
//            while (resultSet.next()) {
//                String yearmonth = resultSet.getString("yearmonth");
//                String album_name = resultSet.getString("album_name");
//                int monthly_play_count = resultSet.getInt("monthly_play_count");
//
//                System.out.printf("Year-Month: %s%n Album Name: %s%n Monthly Play Count: %d%n",
//                        yearmonth, album_name, monthly_play_count);
//            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void reportPerSongs(MediaStreamingService mediaStreamingService, Scanner scanner) throws SQLException {

        System.out.println("Report Monthly play count per Song:");
        ResultSet resultSet = mediaStreamingService.reportPerSongs();

        try {
            ResultSetPrinter.printResultSet(resultSet);
//            while (resultSet.next()) {
//                String yearmonth = resultSet.getString("yearmonth");
//                String song_title = resultSet.getString("song_title");
//                int play_count = resultSet.getInt("play_count");
//
//                System.out.printf("Year-Month: %s%n Song Title: %s%n Play Count: %d%n",
//                        yearmonth, song_title, play_count);
//            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void reportPerArtists(MediaStreamingService mediaStreamingService, Scanner scanner) throws SQLException {

        System.out.println("Report Monthly play count per artist:");
        ResultSet resultSet = mediaStreamingService.reportPerArtists();

        try {
            ResultSetPrinter.printResultSet(resultSet);
//            while (resultSet.next()) {
//                String artist_name = resultSet.getString("artist_name");
//                String yearmonth = resultSet.getString("yearmonth");
//                int monthly_play_count = resultSet.getInt("monthly_play_count");
//
//                System.out.printf("Artist Name: %s%n Year-Month: %s%n Monthly Play Count: %d%n",
//                        artist_name, yearmonth, monthly_play_count);
//            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void getPeriodPaymentToRecordLabel(PaymentService paymentService, Scanner scanner) throws SQLException {

        System.out.println("Report total payments made out to record labels per given time period:");
        System.out.println("Please format the date as YYYY-MM-DD, e.g. 2023-03-08.");

        System.out.println("Input start date:");
        String start = scanner.next();
        if (!isValidDateString(start)) {
            return;
        }

        System.out.println("Input end date:");
        String end = scanner.next();
        if (!isValidDateString(end)) {
            return;
        }

        ResultSet resultSet = paymentService.getPeriodPaymentToRecordLabel(start, end);
        try {
            while (resultSet.next()) {
                String recordLabelName = resultSet.getString("record_label_name");
                double totalPayment = resultSet.getDouble("total_payment");

                System.out.printf("Record Label: %s%n Total Payment: %s%n%n",
                        recordLabelName, totalPayment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void getPeriodPaymentToArtist(PaymentService paymentService, Scanner scanner) throws SQLException {

        System.out.println("Report total payments made out to artists per given time period:");
        System.out.println("Please format the date as YYYY-MM-DD, e.g. 2023-03-08.");

        System.out.println("Input start date:");
        String start = scanner.next();
        if (!isValidDateString(start)){
            return;
        }

        System.out.println("Input end date:");
        String end = scanner.next();
        if (!isValidDateString(end)){
            return;
        }

        ResultSet resultSet = paymentService.getPeriodPaymentToArtist(start, end);

        try {
             ResultSetPrinter.printResultSet(resultSet);
//            while (resultSet.next()) {
//                String artistName = resultSet.getString("artist_name");
//                double totalPayment = resultSet.getDouble("total_payment");
//
//                System.out.printf("Artist Name: %s%n Total Payment: %s%n%n",
//                        artistName, totalPayment);
//            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void getPeriodPaymentToHost(PaymentService paymentService, Scanner scanner) throws SQLException {

        System.out.println("Report total payments made out to hosts per given time period:");
        System.out.println("Please format the date as YYYY-MM-DD, e.g. 2023-03-08.");

        System.out.println("Input start date:");
        String start = scanner.next();
        if (!isValidDateString(start)) {
            return;
        }

        System.out.println("Input end date:");
        String end = scanner.next();
        if (!isValidDateString(end)) {
            return;
        }

        ResultSet resultSet = paymentService.getPeriodPaymentToHost(start, end);

        try {
            ResultSetPrinter.printResultSet(resultSet);
//            while (resultSet.next()) {
//                String first_name = resultSet.getString("first_name");
//                String last_name = resultSet.getString("last_name");
//                double totalPayment = resultSet.getDouble("total_payment");
//
//                System.out.printf("Host Name: %s %s%n Total Payment: %s%n%n",
//                        first_name, last_name, totalPayment);
//            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void getStreamingServiceMonthlyRevenue(PaymentService paymentService, Scanner scanner) throws SQLException {

        System.out.println("Report total revenue of the streaming service per month:");

        ResultSet resultSet = paymentService.getStreamingServiceMonthlyRevenue();

        try {
            ResultSetPrinter.printResultSet(resultSet);
            // while (resultSet.next()) {
                // String hostPayment = resultSet.getString("host_payment");
                // String artistPayment = resultSet.getString("artist_payment");
                // String labelPayment = resultSet.getString("label_payment");
                // double income = resultSet.getDouble("income");
                // String yearMonth = resultSet.getString("yearmonth");
                // double revenue = resultSet.getDouble("revenue");

                // System.out.printf("Year-Month: %s%n Host Payment: %s%n Artist Payment: %s%n Label Payment: %s%n Income: %s%n Revenue: %s%n%n",
                // yearMonth, hostPayment, artistPayment, labelPayment, income, revenue);
            // }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void getStreamingServiceYearlyRevenue(PaymentService paymentService, Scanner scanner) throws SQLException {

        System.out.println("Report total revenue of the streaming service per year:");

        ResultSet resultSet = paymentService.getStreamingServiceYearlyRevenue();

        try {
            ResultSetPrinter.printResultSet(resultSet);

            // while (resultSet.next()) {
//                String hostPayment = resultSet.getString("host_payment");
//                String artistPayment = resultSet.getString("artist_payment");
//                String labelPayment = resultSet.getString("label_payment");
//                double income = resultSet.getDouble("income");
//                String year = resultSet.getString("year");
//                double revenue = resultSet.getDouble("revenue");
//
//                System.out.printf("Year: %s%n Host Payment: %s%n Artist Payment: %s%n Label Payment: %s%n Income: %s%n Revenue: %s%n%n",
//                        year, hostPayment, artistPayment, labelPayment, income, revenue);
            // }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
