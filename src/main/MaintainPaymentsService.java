package main;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;

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

    public void updateManagementAccount(int streamingAccountId, Double amount, Boolean isMinus) throws SQLException {
        String sql = "UPDATE theMediaStreamingManagement SET balance = IF(?, balance - ?, balance + ?) WHERE streaming_account_id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
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
            throw new SQLException();
        }
//        } catch (SQLException e) {
//            System.out.println("Error: " + e.getMessage());
//        }
    }

    public void payLabelForGivenSongGivenMonth(Integer songId, String month, Integer streamingAccountId) {
//        Need Transaction Here !!!!!!!!!!!!!

        String sql2 = "INSERT INTO paidLabel (paid_streaming_account_id, paid_record_label_id, amount, date) " +
                "SELECT ? as paid_streaming_account_id, " +
                "Artists.record_label as paid_record_label_id, " +
                "? * 0.3 as amount, " +
                "NOW() as date " +
                "FROM listenedSong INNER JOIN Songs ON listenedSong.song_id = Songs.song_id " +
                "INNER JOIN performed ON performed.song_id = Songs.song_id " +
                "INNER JOIN Artists ON Artists.artist_id = performed.artist_id " +
                "WHERE listenedSong.song_id = ? " +
                "AND DATE_FORMAT(listenedSong.date, '%Y-%m') = ? " +
                "AND performed.is_collaborator = 0 " +
                "LIMIT 1";

//        PreparedStatement sm1 = null;
        PreparedStatement sm2 = null;

        try {
            ResultSet rs = monthlyPaymentForGivenSong(songId);
//            ResultSetMetaData rsmd = rs.getMetaData(); // debug
//            int columnsNumber = rsmd.getColumnCount(); // debug
            Float totalPayment = null;
            rs.beforeFirst();

            while (rs.next()) {

                if (rs.getString(2).equals(month)) {
                    totalPayment = rs.getFloat("payment");
                }
            }
//            System.out.println(totalPayment); //debug
            if (totalPayment == null) {
                System.out.println("No available amount to pay");
            } else {
                connection.setAutoCommit(false);
                sm2 = connection.prepareStatement(sql2, Statement.RETURN_GENERATED_KEYS);
                sm2.setFloat(2, totalPayment);
                if (streamingAccountId != null) {
                    sm2.setInt(1, streamingAccountId);
                } else {
                    sm2.setNull(1, Types.INTEGER);
                }

                if (songId != null) {
                    sm2.setInt(3, songId);
//                    sm1.setInt(1, songId);
                } else {
                    sm2.setNull(3, Types.INTEGER);
//                    sm1.setNull(1, Types.INTEGER);
                }

                if (month != null) {
                    sm2.setString(4, month);
//                    sm1.setString(2, month);
                } else {
                    sm2.setNull(4, Types.INTEGER);
//                    sm1.setNull(2, Types.INTEGER);
                }

                int rowsAffected = sm2.executeUpdate();
                if (rowsAffected == 0) {
                    System.out.println("Unable to pay");
                } else {
//                    ResultSet resultSet = sm1.executeQuery();
//                    sm2.executeUpdate();
//                    System.out.println(totalPayment); // debug
                    System.out.println("Paid Successfully");
                    updateManagementAccount(streamingAccountId, totalPayment * 0.3, true);
                }
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

    public void payArtistForGivenSongGivenMonth(Integer songId, String month, Integer streamingAccountId) {
//        Need Transaction Here !!!!!!!!!!!!!

        String sql2 = "INSERT INTO paidArtist (paid_streaming_account_id, paid_artist_id, amount, date) " +
                "SELECT" +
                " ? as paid_streaming_account_id, " +
                "performed.artist_id as paid_artist_id, " +
                "(? * 0.7 / " +
                "(SELECT COUNT(DISTINCT artist_id) FROM performed WHERE song_id = listenedSong.song_id)) as amount, " +
                "NOW() as date " +
                "FROM listenedSong " +
                "INNER JOIN Songs ON listenedSong.song_id = Songs.song_id " +
                "INNER JOIN performed ON performed.song_id = Songs.song_id " +
                "WHERE listenedSong.song_id = ? AND DATE_FORMAT(listenedSong.date, '%Y-%m') = ? " +
                "GROUP BY performed.artist_id";

//        PreparedStatement sm1 = null;
        PreparedStatement sm2 = null;

        try {
            ResultSet rs = monthlyPaymentForGivenSong(songId);
            Float totalPayment = null;
            rs.beforeFirst();
            while (rs.next()) {
                if (rs.getString(2).equals(month)) {
                    totalPayment = rs.getFloat("payment");
                }
            }
            if (totalPayment == null) {
                System.out.println("No available amount to pay");
            } else {
                connection.setAutoCommit(false);

//            sm1 = connection.prepareStatement(sql1);
                sm2 = connection.prepareStatement(sql2, Statement.RETURN_GENERATED_KEYS);
                sm2.setFloat(2, totalPayment);

                if (streamingAccountId != null) {
                    sm2.setInt(1, streamingAccountId);
                } else {
                    sm2.setNull(1, Types.INTEGER);
                }

                if (songId != null) {
                    sm2.setInt(3, songId);
//                sm1.setInt(1, songId);
                } else {
                    sm2.setNull(3, Types.INTEGER);
//                sm1.setNull(1, Types.INTEGER);
                }

                if (month != null) {
                    sm2.setString(4, month);
//                sm1.setString(2, month);
                } else {
                    sm2.setNull(4, Types.INTEGER);
//                sm1.setNull(2, Types.INTEGER);
                }

                int rowsAffected = sm2.executeUpdate();
                connection.commit();
                if (rowsAffected == 0) {
                    System.out.println("Unable to Pay.");
                } else {
//                ResultSet resultSet = sm1.executeQuery();
                    updateManagementAccount(streamingAccountId, totalPayment * 0.7, true);
                    System.out.println("Paid Successfully");
                }
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

    public ResultSet PaymentToHosts() {
        String sql = "SELECT SUM((500 + PodcastEpisodes.advertisement_count * 100)) as payment " +
                "FROM PodcastEpisodes " +
                "INNER JOIN hosted ON PodcastEpisodes.podcast_episode_id = hosted.podcast_episode_id " +
                "INNER JOIN PodcastHosts ON hosted.host_id = PodcastHosts.host_id ";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet;
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }

    public void payHosts(Integer streamingAccountId) {
        /*
            Make monthly payment to all hosts. No need to specify host_id.
         */

        String sql = "INSERT INTO paidHost (paid_host_id, amount, date, paid_streaming_account_id) " +
                "SELECT DISTINCT PodcastHosts.host_id as paid_host_id, " +
                "(500 + PodcastEpisodes.advertisement_count * 100) as amount, " +
                "NOW() as date, " +
                "? as paid_streaming_account_id " +
                "FROM PodcastEpisodes " +
                "INNER JOIN hosted ON PodcastEpisodes.podcast_episode_id = hosted.podcast_episode_id " +
                "INNER JOIN PodcastHosts ON hosted.host_id = PodcastHosts.host_id";

        PreparedStatement sm = null;

        try {
            ResultSet rs = PaymentToHosts();
            Float totalPayment = null;
            rs.beforeFirst();
            if (rs.next()) {
                totalPayment = rs.getFloat("payment");
            }

            if (totalPayment == null) {
                System.out.println("No available amount to pay");
            } else {
                connection.setAutoCommit(false);
                sm = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                if (streamingAccountId != null) {
                    sm.setInt(1, streamingAccountId);
                } else {
                    sm.setNull(1, Types.INTEGER);
                }
                int rowsAffected = sm.executeUpdate();
                connection.commit();
                if (rowsAffected == 0) {
                    System.out.println("Unable to Pay.");
                } else {
                    updateManagementAccount(streamingAccountId, totalPayment * 1.0, true);
                    System.out.println("Paid Hosts Successfully");
                }
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

    public void receiveSubFee() {
        // Implement the logic to delete a song from the database
//        All subscription fee is paid to streaming account 2
        String sql1 = "SELECT COUNT(*) FROM User WHERE status_of_subscription = 1";
        String sql2 =  "INSERT INTO paidService (monthly_subscription_fee, date, paid_user_id, paid_streaming_account_id) " +
                "SELECT DISTINCT 10.0 as monthly_subscription_fee, " +
                "NOW() as date, " +
                "User.listener_id as paid_user_id, " +
                "2 as paid_streaming_account_id " +
                "FROM User, theMediaStreamingManagement " +
                "WHERE User.status_of_subscription = 1";

        PreparedStatement sm1 = null;
        PreparedStatement sm2 = null;

        try {
            connection.setAutoCommit(false);
            sm1 = connection.prepareStatement(sql1);
            sm2 = connection.prepareStatement(sql2, Statement.RETURN_GENERATED_KEYS);

            int rowsAffected = sm2.executeUpdate();

            if (rowsAffected > 0) {
//                sm2.executeUpdate();
                ResultSet resultSet = sm1.executeQuery();
                if (resultSet.next()) {
                    updateManagementAccount(2, 10.0 * resultSet.getDouble(1), false);
                    System.out.println("paidService added successfully.");
                }
                else{
                    System.out.println("No active user.");
                }
            } else {
                System.out.println("Failed to add paidService.");
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
