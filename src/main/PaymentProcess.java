package main;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class PaymentProcess {

    /**
     * Generate payment processing menu
     * @param maintainPaymentsService
     * @param scanner
     * @throws SQLException
     */
    public static void paymentProcessingMenu(MaintainPaymentsService maintainPaymentsService, Scanner scanner) throws SQLException {
        int choice;
        do {
            System.out.println("********************************");
            System.out.println("Payment Processing Menu");
            System.out.println("1. Make Monthly Payment to Record Label for Given Song");
            System.out.println("2. Make Monthly Payment to Artist for Given Song");
            System.out.println("3. Make Payment to Podcast Hosts");
            System.out.println("4. Make (All) Monthly Payment to Artists and Record Labels");
            System.out.println("5. Receive Monthly Payment from Subscribers");
            System.out.println("6. Calculate Payment Amount and Pay to Podcast Hosts");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            System.out.println(choice);
            System.out.println("********************************");
            switch (choice) {
                case 1:
                    payLabelForGivenSongGivenMonthMenu(maintainPaymentsService, scanner);
                    break;
                case 2:
                    payArtistsForGivenSongGivenMenu(maintainPaymentsService, scanner);
                    break;
                case 3:
                    payHostsGivenMonthMenu(maintainPaymentsService, scanner);
                    break;
                case 4:
                    makeMonthlyPaymentToArtistAndLabel(maintainPaymentsService,scanner);
                    break;
                case 5:
                    receiveSubFeeMenu(maintainPaymentsService, scanner);
                    break;
                case 6:
                    payHostsCalMenu(maintainPaymentsService, scanner);
                    break;

            }
        } while (choice != 0);
    }

    private static void makeMonthlyPaymentToArtistAndLabel(MaintainPaymentsService maintainPaymentsService,Scanner scanner){
        scanner.nextLine();
        List<String> monthWithDueToArtist = new ArrayList<>();
        List<String> monthWithDueToLabel = new ArrayList<>();
        System.out.println("Enter song ID:");
        int songId = scanner.nextInt();
        System.out.println("Enter streaming account ID");
        int accountId = scanner.nextInt();
        List<Map<String,Object>> monthHasDueToArtist = getDueMonthToArtist(maintainPaymentsService,songId);
        List<Map<String,Object>> monthHasDueToLabel = getDueMonthToLabel(maintainPaymentsService,songId);
        try {
            maintainPaymentsService.connection.setAutoCommit(false);
            for (Map<String, Object> map : monthHasDueToArtist) {
                try {
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
                    Date date = dateFormat.parse((String) map.get("yearmonth"));
                    monthWithDueToArtist.add(dateFormat.format(date));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            for (Map<String, Object> map : monthHasDueToLabel) {
                try {
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
                    Date date = dateFormat.parse((String) map.get("yearmonth"));
                    monthWithDueToLabel.add(dateFormat.format(date));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            System.out.println(monthWithDueToArtist);
            for (String yearmonth : monthWithDueToArtist) {
                maintainPaymentsService.payArtistForGivenSongGivenMonth(songId,yearmonth,accountId);
            }
            for(String yearmonth : monthWithDueToLabel){
                maintainPaymentsService.payLabelForGivenSongGivenMonth(songId,yearmonth,accountId);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                maintainPaymentsService.connection.commit();
                maintainPaymentsService.connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    private static List<Map<String,Object>> getDueMonthToArtist(MaintainPaymentsService maintainPaymentsService, int songId){
        List list = new ArrayList();
        try {
            ResultSet resultSet = maintainPaymentsService.getMonthHasDueToArtist(songId);
            if(resultSet==null){
                return list;
            }
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            while(true){
                Map<String,Object> rowData = new HashMap();
                for (int i = 1; i <= columnCount; i++) {
                    rowData.put(metaData.getColumnName(i),resultSet.getObject(i));
                }
                list.add(rowData);
                if(!resultSet.next()){
                    break;
                };
            }
            System.out.println(list);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return list;
    }

    private static List<Map<String,Object>> getDueMonthToLabel(MaintainPaymentsService maintainPaymentsService,int songId){
        List list = new ArrayList();
        try {
            ResultSet resultSet = maintainPaymentsService.getMonthHasDueToLabel(songId);
            if(resultSet==null){
                return list;
            }
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            while(true){
                Map<String,Object> rowData = new HashMap();
                for (int i = 1; i <= columnCount; i++) {
                    rowData.put(metaData.getColumnName(i),resultSet.getObject(i));
                }
                list.add(rowData);
                if(!resultSet.next()){
                    break;
                };
            }
            System.out.println(list);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Generate monthly payment to record labels for given song.
     * @param maintainPaymentsService
     * @param scanner
     */
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

    }

    /**
     * Generate monthly payment to artists for given song.
     * @param maintainPaymentsService
     * @param scanner
     */
    private static void payArtistsForGivenSongGivenMenu(MaintainPaymentsService maintainPaymentsService, Scanner scanner) {

        scanner.nextLine();

        System.out.print("Enter streaming account ID: ");
        String streamingAccountIdStr = scanner.nextLine();

        System.out.print("Enter Song ID: ");
        String songIdStr = scanner.nextLine();

        System.out.println("Enter Year Month YYYY-mm");
        String month = scanner.nextLine();

        Integer streamingAccountId = Integer.parseInt(streamingAccountIdStr);
        Integer songId = Integer.parseInt(songIdStr);

        maintainPaymentsService.payArtistForGivenSongGivenMonth(songId, month, streamingAccountId);
    }

    /**
     * Generate payment to podcast hosts.
     * @param maintainPaymentsService
     * @param scanner
     */
    private static void payHostsGivenMonthMenu(MaintainPaymentsService maintainPaymentsService, Scanner scanner) {
        scanner.nextLine();

        System.out.print("Enter host ID: ");
        String hostIdStr = scanner.nextLine();
        Integer hostId = Integer.parseInt(hostIdStr);

        System.out.print("Enter payment amount: ");
        String paymentStr = scanner.nextLine();
        BigDecimal payment = new BigDecimal(paymentStr);

        System.out.print("Enter month (YYYY-MM): ");
        String month = scanner.nextLine();

        System.out.print("Enter streaming account ID: ");
        String streamingAccountIdStr = scanner.nextLine();
        Integer streamingAccountId = Integer.parseInt(streamingAccountIdStr);

        maintainPaymentsService.payHosts(hostId, payment, month, streamingAccountId);
    }

    /**
     * Receive monthly payment from subscribers
     * @param maintainPaymentsService
     * @param scanner
     */
    private static void receiveSubFeeMenu(MaintainPaymentsService maintainPaymentsService, Scanner scanner) {
        scanner.nextLine();
        System.out.println("Enter Month");
        String month = scanner.nextLine();
        maintainPaymentsService.receiveSubFee(month);
    }


    /**
     * Calculate payment amount based on the episodes and pay to podcast hosts.
     * @param maintainPaymentsService
     * @param scanner
     */
     private static void payHostsCalMenu(MaintainPaymentsService maintainPaymentsService, Scanner scanner) {
        scanner.nextLine();

        System.out.print("Enter streaming account ID: ");
        String streamingAccountIdStr = scanner.nextLine();


        Integer streamingAccountId = Integer.parseInt(streamingAccountIdStr);

        maintainPaymentsService.payHostsCal(streamingAccountId);
    }

}
