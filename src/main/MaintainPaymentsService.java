package main;

import java.sql.*;

public class MaintainPaymentsService extends MediaStreamingService {
    public MaintainPaymentsService(Connection connection) {
        super(connection);
    }

    public ResultSet monthlyPaymentForGivenSong(int songId) {
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

    public void updateManagementAccount(int streamingAccountId, Double amount, Boolean isMinus) {
        String sql = "UPDATE theMediaSreamingManagement SET IF(?, balance = balance - ?, balance = balance + ?) WHERE streaming_account_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            if (amount != null) {
                statement.setDouble(2, amount);
                statement.setDouble(3, amount);
            } else {
                statement.setNull(2, Types.INTEGER);
                statement.setNull(3, Types.INTEGER);
            }

            if (isMinus != null) {
                statement.setBoolean(1, isMinus);
            } else {
                statement.setNull(1, Types.BOOLEAN);
            }

            statement.setInt(4, streamingAccountId);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Management account balance updated successfully.");
            } else {
                System.out.println("Error: Unable to update management account balance.");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void payLabelForGivenSongGivenMonth(Integer songId, String month, Integer streamingAccountId) {
//        Need Tranction Here !!!!!!!!!!!!!

        String sql1 = "SELECT s.song_id, COUNT(*)*s.royalty_rate payment, DATE_FORMAT(`date`,'%Y-%m'), s.song_title " +
                "  FROM listenedSong l LEFT JOIN Songs s ON l.song_id =s.song_id " +
                "  GROUP BY s.song_id,DATE_FORMAT(`date`,'%Y-%m')" +
                "  HAVING s.song_id = ? AND DATE_FORMAT(`date`,'%Y-%m') = ?";

        String sql2 = "INSERT INTO paidLabel (paid_streaming_account_id, paid_record_label_id, amount, date) " +
                "SELECT ? as paid_streaming_account_id, " +
                "Artists.record_label as paid_record_label_id, " +
                "(COUNT(*) * Songs.royalty_rate * 0.3) as amount, " +
                "NOW() as date " +
                "FROM listenedSong INNER JOIN Songs ON listenedSong.song_id = Songs.song_id " +
                "INNER JOIN performed ON performed.song_id = Songs.song_id " +
                "INNER JOIN Artists ON Artists.artist_id = performed.artist_id " +
                "WHERE listenedSong.song_id = ? " +
                "AND DATE_FORMAT(listenedSong.date, '%Y-%m') = ? " +
                "AND performed.is_collaborator = 0";

        PreparedStatement sm1 = null;
        PreparedStatement sm2 = null;

        try {
            sm1 = connection.prepareStatement(sql1);
            sm2 = connection.prepareStatement(sql2);
            if (streamingAccountId != null) {
                sm2.setInt(1, streamingAccountId);
            } else {
                sm2.setNull(1, Types.INTEGER);
            }

            if (songId != null) {
                sm2.setInt(2, songId);
                sm1.setInt(1, songId);
            } else {
                sm2.setNull(2, Types.INTEGER);
                sm1.setNull(1, Types.INTEGER);
            }

            if (month != null) {
                sm2.setString(3, month);
                sm1.setString(2, month);
            } else {
                sm2.setNull(3, Types.INTEGER);
                sm1.setNull(2, Types.INTEGER);
            }

            int rowsAffected = sm1.executeUpdate() + sm2.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Paid successfully.");
            } else {
                ResultSet resultSet = sm1.executeQuery();
                sm2.executeUpdate();
                if (resultSet.next()) {
                    updateManagementAccount(streamingAccountId, resultSet.getFloat("payment") * 0.3, true);
                }
                System.out.println("Error: Unable to pay.");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void payArtistForGivenSongGivenMonth(Integer songId, String month, Integer streamingAccountId) {
//        Need Tranction Here !!!!!!!!!!!!!

        String sql1 = "SELECT s.song_id, COUNT(*)*s.royalty_rate payment, DATE_FORMAT(`date`,'%Y-%m'), s.song_title " +
                "  FROM listenedSong l LEFT JOIN Songs s ON l.song_id =s.song_id " +
                "  GROUP BY s.song_id,DATE_FORMAT(`date`,'%Y-%m')" +
                "  HAVING s.song_id = ? AND DATE_FORMAT(`date`,'%Y-%m') = ?";

        String sql2 = "INSERT INTO paidArtist (paid_streaming_account_id, paid_artist_id, amount, date) " +
                "SELECT ? as paid_streaming_account_id, " +
                "performed.artist_id as paid_artist_id, " +
                "(COUNT(*) * Songs.royalty_rate * 0.7 / " +
                "(SELECT COUNT(DISTINCT artist_id) FROM performed WHERE song_id = listenedSong.song_id)) as amount, " +
                "NOW() as date " +
                "FROM listenedSong " +
                "INNER JOIN Songs ON listenedSong.song_id = Songs.song_id " +
                "INNER JOIN performed ON performed.song_id = Songs.song_id " +
                "WHERE listenedSong.song_id = ? AND DATE_FORMAT(listenedSong.date, '%Y-%m') = ? " +
                "GROUP BY performed.artist_id;";

        PreparedStatement sm1 = null;
        PreparedStatement sm2 = null;

        try {
            connection.setAutoCommit(false);

            sm1 = connection.prepareStatement(sql1);
            sm2 = connection.prepareStatement(sql2);
            if (streamingAccountId != null) {
                sm2.setInt(1, streamingAccountId);
            } else {
                sm2.setNull(1, Types.INTEGER);
            }

            if (songId != null) {
                sm2.setInt(2, songId);
                sm1.setInt(1, songId);
            } else {
                sm2.setNull(2, Types.INTEGER);
                sm1.setNull(1, Types.INTEGER);
            }

            if (month != null) {
                sm2.setString(3, month);
                sm1.setString(2, month);
            } else {
                sm2.setNull(3, Types.INTEGER);
                sm1.setNull(2, Types.INTEGER);
            }

            int rowsAffected = sm1.executeUpdate() + sm2.executeUpdate();
            connection.commit();
            if (rowsAffected > 0) {
                System.out.println("Paid successfully.");
            } else {
                ResultSet resultSet = sm1.executeQuery();
                sm2.executeUpdate();
                if (resultSet.next()) {
                    updateManagementAccount(streamingAccountId, resultSet.getFloat("payment") * 0.7, true);
                }
                System.out.println("Error: Unable to pay.");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            try {
                connection.rollback();
                System.out.println("Rollback successful");
            } catch (Exception e2) {
                e.printStackTrace();
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
