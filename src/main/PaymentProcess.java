package main;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class PaymentProcess {

    public static void paymentProcessingMenu(MaintainPaymentsService maintainPaymentsService, Scanner scanner) throws SQLException {
        int choice;
        do {
            System.out.println("********************************");
            System.out.println("Payment Processing Menu");
            System.out.println("1. Make Monthly Payment to Record Label for Given Song");
            System.out.println("2. Make Monthly Payment to Artist for Given Song");
            System.out.println("3. Make Payment to Podcast Hosts");
            System.out.println("4. Make Monthly Payment to All Artists");
            System.out.println("5. Make Monthly Payment to All Label Records");
            System.out.println("6. Receive Monthly Payment from Subscribers");
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
                    makeMonthlyPaymentToArtist(maintainPaymentsService,scanner);
                    break;
                case 5:
                    break;
                case 6:
                    receiveSubFeeMenu(maintainPaymentsService, scanner);
                    break;

            }
        } while (choice != 0);
    }
    private static void makeMonthlyPaymentToArtist(MaintainPaymentsService maintainPaymentsService,Scanner scanner){
        List<Map<String,Object>>  paymentDueForSong = getPaymentForSongs(maintainPaymentsService,scanner);
        try {
            maintainPaymentsService.connection.setAutoCommit(false);
            for (Map<String, Object> map : paymentDueForSong) {
                try {
                    int song_id = (Integer) map.get("song_id");
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
                    Date date = dateFormat.parse((String) map.get("yearmonth"));
                    Double due_payment = (Double) map.get("due_payment");

                }catch (Exception e){
                    e.printStackTrace();
                }

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

    private static List<Map<String,Object>> getPaymentForSongs(MaintainPaymentsService maintainPaymentsService, Scanner scanner){
        scanner.nextLine();
        System.out.println("Enter song ID:");
        List list = new ArrayList();
        int songId = scanner.nextInt();
        try {
            ResultSet resultSet = maintainPaymentsService.getDuePaymentOfGivenSong(songId);
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


    private static void receiveSubFeeMenu(MaintainPaymentsService maintainPaymentsService, Scanner scanner) {
        scanner.nextLine();
        System.out.println("Enter Month");
        String month = scanner.nextLine();
        maintainPaymentsService.receiveSubFee(month);
    }


    /* PAST version of pay host
     private static void payHostsGivenMonthMenu(MaintainPaymentsService maintainPaymentsService, Scanner scanner) {
        scanner.nextLine();

        System.out.print("Enter streaming account ID: ");
        String streamingAccountIdStr = scanner.nextLine();


        Integer streamingAccountId = Integer.parseInt(streamingAccountIdStr);

        maintainPaymentsService.payHosts(streamingAccountId);
    }
    * */

}
