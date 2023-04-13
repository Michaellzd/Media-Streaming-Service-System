package main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PaymentService {

    protected final Connection connection;

    public PaymentService(Connection connection) {
        this.connection = connection;
    }

    public ResultSet getPeriodPaymentToRecordLabel(String startDate, String endDate) {
        ResultSet resultSet = null;
        String sql = "SELECT RecordLabel.record_label_name, SUM(paidLabel.amount) as total_payment " +
                "FROM paidLabel " +
                "INNER JOIN RecordLabel ON paidLabel.paid_record_label_id = RecordLabel.record_label_id " +
                "WHERE paidLabel.date BETWEEN ? AND ? " +
                "GROUP BY RecordLabel.record_label_id";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, startDate);
            statement.setString(2, endDate);
            resultSet = statement.executeQuery();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return resultSet;
    }

    public ResultSet getPeriodPaymentToArtist(String startDate, String endDate) {
        ResultSet resultSet = null;
        String sql = "SELECT Artists.artist_name, SUM(paidArtist.amount) as total_payment " +
                "FROM paidArtist " +
                "INNER JOIN Artists ON paidArtist.paid_artist_id = Artists.artist_id " +
                "WHERE paidArtist.date BETWEEN ? AND ? " +
                "GROUP BY Artists.artist_id";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, startDate);
            statement.setString(2, endDate);
            resultSet = statement.executeQuery();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return resultSet;
    }

    public ResultSet getPeriodPaymentToHost(String startDate, String endDate) {
        ResultSet resultSet = null;
        String sql = "SELECT PodcastHosts.first_name, PodcastHosts.last_name, SUM(paidHost.amount) total_payment " +
                "FROM paidHost " +
                "INNER JOIN PodcastHosts ON paidHost.paid_host_id = PodcastHosts.host_id " +
                "WHERE paidHost.date BETWEEN ? AND ? " +
                "GROUP BY PodcastHosts.host_id";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, startDate);
            statement.setString(2, endDate);
            resultSet = statement.executeQuery();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return resultSet;
    }

    public ResultSet getStreamingServiceMonthlyRevenue() {
        String sql = "SELECT yearmonths.yearmonth as yearmonth, \n" +
                "    COALESCE(SUM(paidLabel.total_payment), '0.00') as label_payment, \n" +
                "    COALESCE(SUM(paidArtist.total_payment), '0.00') as artist_payment, \n" +
                "    COALESCE(SUM(paidHost.total_payment), '0.00') as host_payment, \n" +
                "    COALESCE(SUM(paidService.total_payment), '0.00') as user_subscription_income, \n" +
                "    COALESCE(SUM(advertisementPay.advertisements_money), '0.00') as advertisement_income,\n" +
                "    COALESCE(SUM(advertisementPay.advertisements_money), '0.00')+ COALESCE(SUM(paidService.total_payment), '0.00')as revenue,\n"+
                "    ROUND(COALESCE(SUM(paidService.total_payment), '0.00') +COALESCE(SUM(advertisementPay.advertisements_money), '0.00')" +
                "        - COALESCE(SUM(paidHost.total_payment), '0.00') \n" +
                "        - COALESCE(SUM(paidArtist.total_payment), '0.00') \n" +
                "        - COALESCE(SUM(paidLabel.total_payment), '0.00') , 2)\n" +
                "         AS profit\n" +
                "FROM (\n" +
                "    SELECT DISTINCT DATE_FORMAT(date, '%Y-%m') AS yearmonth \n" +
                "    FROM (\n" +
                "        SELECT date FROM paidLabel \n" +
                "        UNION \n" +
                "        SELECT date FROM paidArtist \n" +
                "        UNION \n" +
                "        SELECT date FROM paidHost \n" +
                "        UNION \n" +
                "        SELECT date FROM paidService \n" +
                "        UNION \n" +
                "        SELECT date FROM advertisementPay\n" +
                "    ) AS all_dates\n" +
                ") AS yearmonths \n" +
                "LEFT JOIN (\n" +
                "    SELECT DATE_FORMAT(date, '%Y-%m') AS yearmonth, SUM(amount) as total_payment \n" +
                "    FROM paidLabel \n" +
                "    GROUP BY DATE_FORMAT(date, '%Y-%m') \n" +
                ") AS paidLabel ON yearmonths.yearmonth = paidLabel.yearmonth \n" +
                "LEFT JOIN (\n" +
                "    SELECT DATE_FORMAT(date, '%Y-%m') AS yearmonth, SUM(amount) as total_payment \n" +
                "    FROM paidArtist \n" +
                "    GROUP BY DATE_FORMAT(date, '%Y-%m') \n" +
                ") AS paidArtist ON yearmonths.yearmonth = paidArtist.yearmonth \n" +
                "LEFT JOIN (\n" +
                "    SELECT DATE_FORMAT(date, '%Y-%m') AS yearmonth, SUM(amount) as total_payment \n" +
                "    FROM paidHost \n" +
                "    GROUP BY DATE_FORMAT(date, '%Y-%m') \n" +
                ") AS paidHost ON yearmonths.yearmonth = paidHost.yearmonth \n" +
                "LEFT JOIN (\n" +
                "    SELECT DATE_FORMAT(date, '%Y-%m') AS yearmonth, SUM(monthly_subscription_fee) as total_payment \n" +
                "    FROM paidService \n" +
                "    GROUP BY DATE_FORMAT(date, '%Y-%m') \n" +
                ") AS paidService ON yearmonths.yearmonth = paidService.yearmonth \n" +
                "LEFT JOIN (\n" +
                "    SELECT DATE_FORMAT(date, '%Y-%m') AS yearmonth, SUM(advertisements_money) as advertisements_money \n" +
                "    FROM advertisementPay \n" +
                "    GROUP BY DATE_FORMAT(date, '%Y-%m') \n" +
                ") AS advertisementPay ON yearmonths.yearmonth = advertisementPay.yearmonth \n" +
                "GROUP BY yearmonths.yearmonth;\n";
        
        ResultSet resultSet = null;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            resultSet = statement.executeQuery();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return resultSet;
    }

    public ResultSet getStreamingServiceYearlyRevenue() {
        String sql = "SELECT \n" +
                "  IFNULL(yearmonth1, yearmonth) AS `year`, \n" +
                "  IFNULL(host_payment, '0.00') host_payment, \n" +
                "  IFNULL(artist_payment, '0.00') artist_payment, \n" +
                "  IFNULL(label_payment, '0.00') label_payment, \n" +
                "  IFNULL(advertisement_income, '0.00') advertisement_income,\n" +
                "  user_subscription_income, \n" +

                "  ROUND((user_subscription_income - IFNULL(host_payment, '0.00') - IFNULL(artist_payment, '0.00') - IFNULL(label_payment, '0.00') + IFNULL(advertisement_income, '0.00')), 2) AS revenue \n" +
                "FROM \n" +
                "(\n" +
                "  SELECT * FROM \n" +
                "  (\n" +
                "    SELECT SUM(ph.amount) host_payment, A.artist_payment, E.label_payment, F.advertisement_income, DATE_FORMAT(ph.date, '%Y') yearmonth1 \n" +
                "    FROM paidHost ph \n" +
                "    LEFT OUTER JOIN \n" +
                "    (\n" +
                "      SELECT SUM(pa.amount) artist_payment, DATE_FORMAT(pa.date, '%Y') AS yearmonth2 \n" +
                "      FROM paidArtist pa \n" +
                "      GROUP BY yearmonth2 \n" +
                "    ) AS A ON DATE_FORMAT(ph.date, '%Y') = A.yearmonth2 \n" +
                "    LEFT OUTER JOIN \n" +
                "    (\n" +
                "      SELECT SUM(pl.amount) label_payment, DATE_FORMAT(pl.date, '%Y') as yearmonth3 \n" +
                "      FROM paidLabel pl \n" +
                "      GROUP BY yearmonth3 \n" +
                "    ) AS E ON DATE_FORMAT(ph.date, '%Y') = E.yearmonth3 \n" +
                "    LEFT OUTER JOIN \n" +
                "    (\n" +
                "      SELECT SUM(ap.advertisements_money) AS advertisement_income, DATE_FORMAT(ap.date, '%Y') AS yearmonth4 \n" +
                "      FROM advertisementPay ap \n" +
                "      GROUP BY yearmonth4 \n" +
                "    ) AS F ON DATE_FORMAT(ph.date, '%Y') = F.yearmonth4 \n" +
                "    GROUP BY yearmonth1 \n" +
                "  ) AS B \n" +
                "  RIGHT OUTER JOIN \n" +
                "  (\n" +
                "    SELECT SUM(ps.monthly_subscription_fee) user_subscription_income, DATE_FORMAT(ps.date, '%Y') as yearmonth \n" +
                "    FROM paidService ps \n" +
                "    GROUP BY yearmonth \n" +
                "  ) AS C ON B.yearmonth1 = C.yearmonth \n" +
                ") AS D\n";

        ResultSet resultSet = null;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            resultSet = statement.executeQuery();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return resultSet;
    }

}
