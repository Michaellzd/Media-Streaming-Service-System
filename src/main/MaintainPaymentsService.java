package main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MaintainPaymentsService extends MediaStreamingService {
    public MaintainPaymentsService(Connection connection) {
        super(connection);
    }
    public ResultSet monthlyPaymentForGivenSong(int songId){
        String sql = "SELECT COUNT(*)*s.royalty_rate payment, DATE_FORMAT(`date`,'%Y-%m'), s.song_title \n" +
                "  FROM listenedSong l LEFT JOIN Songs s ON l.song_id =s.song_id \n" +
                "  WHERE s.song_id = ?\n" +
                "  GROUP BY s.song_id,DATE_FORMAT(`date`,'%Y-%m');\n";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, songId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet;
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }

}
