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
}
