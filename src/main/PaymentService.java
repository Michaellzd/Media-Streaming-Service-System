package main;

import java.sql.*;

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

    // TODO: wrong search result
    public ResultSet getStreamingServiceMonthlyRevenue() {
        String sql = "SELECT "
                + "   IFNULL(host_payment, '0.00' ) host_payment,"
                + "   IFNULL(artist_payment, '0.00' ) artist_payment, "
                + "   IFNULL(label_payment, '0.00' ) label_payment,"
                + "   income, "
                + "   IFNULL(yearmonth1, yearmonth ) as `year-month`, "
                + "   ROUND((income-IFNULL(host_payment, '0.00' ) -IFNULL(artist_payment, '0.00' )-IFNULL(label_payment,'0.00')),2) AS revenue "
                + "FROM "
                + "("
                + "    SELECT * FROM ("
                + "        SELECT SUM(ph.amount) host_payment, A.artist_payment,E.label_payment, DATE_FORMAT(ph.date, '%Y-%m') yearmonth1 FROM paidHost ph "
                + "        LEFT OUTER JOIN "
                + "            ("
                + "                SELECT SUM(pa.amount) artist_payment,DATE_FORMAT(pa.date, '%Y-%m') AS yearmonth2 "
                + "                FROM paidArtist pa  "
                + "                GROUP BY yearmonth2"
                + "            ) AS A "
                + "        ON DATE_FORMAT(ph.date, '%Y-%m') = A.yearmonth2"
                + "        LEFT OUTER JOIN "
                + "            ("
                + "                SELECT SUM(pl.amount) label_payment,DATE_FORMAT(pl.date, '%Y-%m') as yearmonth3 "
                + "                FROM paidLabel pl "
                + "                GROUP BY yearmonth3"
                + "            ) AS E"
                + "        ON DATE_FORMAT(ph.date, '%Y-%m') = E.yearmonth3"
                + "        GROUP BY yearmonth1"
                + "    ) AS B RIGHT OUTER JOIN "
                + "    (SELECT SUM(ps.monthly_subscription_fee) income, DATE_FORMAT(ps.date, '%Y-%m') as yearmonth FROM paidService ps GROUP BY yearmonth"
                + "    ) AS C ON B.yearmonth1 = C.yearmonth"
                + ") AS D";

        ResultSet resultSet = null;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            resultSet = statement.executeQuery();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return resultSet;
    }

    // TODO: to be optimized
    public ResultSet getStreamingServiceYearlyRevenue() {
        String sql = "SELECT "
                + "  IFNULL(host_payment, '0.00') host_payment, "
                + "  IFNULL(artist_payment, '0.00') artist_payment, "
                + "  IFNULL(label_payment, '0.00') label_payment, "
                + "  income, "
                + "  IFNULL(yearmonth1, yearmonth) AS `year`, "
                + "  ROUND((income - IFNULL(host_payment, '0.00') - IFNULL(artist_payment, '0.00') - IFNULL(label_payment, '0.00')), 2) AS revenue "
                + "FROM "
                + "( "
                + "  SELECT * FROM ( "
                + "    SELECT SUM(ph.amount) host_payment, A.artist_payment, E.label_payment, DATE_FORMAT(ph.date, '%Y') yearmonth1 FROM paidHost ph "
                + "    LEFT OUTER JOIN "
                + "      ( "
                + "        SELECT SUM(pa.amount) artist_payment, DATE_FORMAT(pa.date, '%Y') AS yearmonth2 "
                + "        FROM paidArtist pa "
                + "        GROUP BY yearmonth2 "
                + "      ) AS A "
                + "    ON DATE_FORMAT(ph.date, '%Y') = A.yearmonth2 "
                + "    LEFT OUTER JOIN "
                + "      ( "
                + "        SELECT SUM(pl.amount) label_payment, DATE_FORMAT(pl.date, '%Y') as yearmonth3 FROM paidLabel pl "
                + "        GROUP BY yearmonth3 "
                + "      ) AS E "
                + "    ON DATE_FORMAT(ph.date, '%Y') = E.yearmonth3 "
                + "    GROUP BY yearmonth1 "
                + "  ) AS B RIGHT OUTER JOIN "
                + "  ( "
                + "    SELECT SUM(ps.monthly_subscription_fee) income, DATE_FORMAT(ps.date, '%Y') as yearmonth FROM paidService ps GROUP BY yearmonth "
                + "  ) AS C ON B.yearmonth1 = C.yearmonth "
                + ") AS D";

        ResultSet resultSet = null;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            resultSet = statement.executeQuery();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return resultSet;
    }

}
