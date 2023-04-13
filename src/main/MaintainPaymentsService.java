package main;

import java.math.BigDecimal;
import java.sql.*;

public class MaintainPaymentsService extends MediaStreamingService {
    /**
     * Constructor heritage from MediaStreamingService.
     * @param connection
     */
    public MaintainPaymentsService(Connection connection) {
        super(connection);
    }

    /**
     * After paying the Artist, set the mark: isPaid As 'true' to avoid duplicated paid.
     * @param songId
     * @param yearmonth
     */
    public void setListenedSongToArtistIsPaid(int songId, String yearmonth){
        String sql = "UPDATE listenedSong SET isPaid = 'true' WHERE song_id = ? AND DATE_FORMAT(`date`, '%Y-%m') = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, songId);
            statement.setString(2,yearmonth);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * After paying the Label, set the mark: isPaid As 'true' to avoid duplicated paid.
     * @param songId
     * @param yearmonth
     */
    public void setListenedSongToLabelIsPaid(int songId, String yearmonth){
        String sql = "UPDATE listenedSong SET isPaidLabel = 'true' WHERE song_id = ? AND DATE_FORMAT(`date`, '%Y-%m') = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, songId);
            statement.setString(2,yearmonth);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Get the due payment to Artist group by Month. (isPaid listening record would not be counted.)
     * @param songId
     * @return
     */
    public ResultSet monthlyPaymentForGivenSongForArtist(int songId) {
        String sql = "SELECT COUNT(*)*s.royalty_rate payment, DATE_FORMAT(`date`,'%Y-%m') AS yearmonth, s.song_title \n" +
                "  FROM listenedSong l LEFT JOIN Songs s ON l.song_id =s.song_id \n" +
                "  WHERE s.song_id = ? \n AND l.isPaid= 'false'" +
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

    /**
     * Get the due payment to Record Label group by Month. (isPaidLabel listening record would not be counted.)
     * @param songId
     * @return
     */
    public ResultSet monthlyPaymentForGivenSongForLabel(int songId) {
        String sql = "SELECT COUNT(*)*s.royalty_rate payment, DATE_FORMAT(`date`,'%Y-%m') AS yearmonth, s.song_title \n" +
                "  FROM listenedSong l LEFT JOIN Songs s ON l.song_id =s.song_id \n" +
                "  WHERE s.song_id = ? \n AND l.isPaidLabel= 'false'" +
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

    /**
     * To confirm whether the streaming account has enough deposit to make the payment.
     * And make update to the manage account.
     * @param streamingAccountId
     * @param amount
     * @param isMinus
     * @throws SQLException
     */
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
    }
    /**
     * Description: Generate monthly payment to record labels.
     */

    /**
     * Payment process includes 2 steps:
     * 1. insert payment information into paidLabel table;
     * 2. update management account balance.
     * The two steps should be either both successfully executed or both not,
     * so we use transaction here.
     *
     * Transaction Logic:
     * 1. Set auto_commit to false;
     * 2. Try step 1 first, if exception occurs, rollback, otherwise try step 2;
     * 3. If step 2 throws out exception, rollback;
     *    ( Notice that we encapsulated step 2 into function updateManagementAccount,
     *      which would throw out exceptions if step 2 failed. )
     * 4. Otherwise, step 1 and step 2 were both executed successfully, commit;
     * 5. Set auto_commit back to true.
     */
    public void payLabelForGivenSongGivenMonth(Integer songId, String yearmonth, Integer streamingAccountId) {

        String sql = "INSERT INTO paidLabel (paid_streaming_account_id, paid_record_label_id, amount, date) " +
                "SELECT ? as paid_streaming_account_id, " +
                "Artists.record_label as paid_record_label_id, " +
                "? * 0.3 as amount, " +
                "? as date " +
                "FROM listenedSong INNER JOIN Songs ON listenedSong.song_id = Songs.song_id " +
                "INNER JOIN performed ON performed.song_id = Songs.song_id " +
                "INNER JOIN Artists ON Artists.artist_id = performed.artist_id " +
                "WHERE listenedSong.song_id = ? AND performed.is_collaborator = 1 " + // 0 or 1 ? tbd
                "AND DATE_FORMAT(listenedSong.date, '%Y-%m') = ? " +
                "LIMIT 1 ";

        PreparedStatement sm = null;

        try {
            ResultSet rs = monthlyPaymentForGivenSongForLabel(songId);
            Float totalPayment = null;
            rs.beforeFirst();

            while (rs.next()) {
                if (rs.getString(2).equals(yearmonth)) {
                    totalPayment = rs.getFloat("payment");
                }
            }
            if (totalPayment == null) {
                System.out.println("No available amount to pay");
            } else {
                connection.setAutoCommit(false);
                sm = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                sm.setFloat(2, totalPayment);
                if (streamingAccountId != null) {
                    sm.setInt(1, streamingAccountId);
                } else {
                    sm.setNull(1, Types.INTEGER);
                }

                if (songId != null) {
                    sm.setInt(4, songId);
                } else {
                    sm.setNull(4, Types.INTEGER);
                }

                if (yearmonth != null) {
                    sm.setString(3, yearmonth+"-28");
                    sm.setString(5, yearmonth);
                } else {
                    sm.setNull(3, Types.DATE);
                    sm.setNull(5, Types.DATE);
                }
                int rowsAffected = sm.executeUpdate();
                if (rowsAffected == 0) {
                    System.out.println("Unable to pay");
                    // In this situation, no single row was infected in the whole database,
                    // So there is no need to rollback or commit.
                } else {
                    System.out.println("Paid Successfully");
                    updateManagementAccount(streamingAccountId, totalPayment * 0.3, true);
                    setListenedSongToLabelIsPaid(songId,yearmonth);
                    connection.commit();
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

    /**
     * To generate monthly payment to artists for given song.
     * Including 1) insert into paidArtist table; 2) update management account
     * @param songId
     * @param yearmonth
     * @param streamingAccountId
     */
    public void payArtistForGivenSongGivenMonth(Integer songId, String yearmonth, Integer streamingAccountId) {

        String sql = "INSERT INTO paidArtist (paid_streaming_account_id, paid_artist_id, amount, date) " +
                "SELECT" +
                " ? as paid_streaming_account_id, " +
                "performed.artist_id as paid_artist_id, " +
                "(? * 0.7 / " +
                "(SELECT COUNT(DISTINCT artist_id) FROM performed WHERE song_id = listenedSong.song_id)) as amount, " +
                "? as date " +
                "FROM listenedSong " +
                "INNER JOIN Songs ON listenedSong.song_id = Songs.song_id " +
                "INNER JOIN performed ON performed.song_id = Songs.song_id " +
                "WHERE listenedSong.song_id = ? AND DATE_FORMAT(listenedSong.date, '%Y-%m') = ?" +
                "GROUP BY performed.artist_id";

        PreparedStatement sm = null;

        try {
            ResultSet rs = monthlyPaymentForGivenSongForArtist(songId);
            Float totalPayment = null;
            rs.beforeFirst();

            while (rs.next()) {
                if (rs.getString(2).equals(yearmonth)) {
                    totalPayment = rs.getFloat("payment");
                }
            }
            if (totalPayment == null) {
                System.out.println("No available amount to pay");
            } else {
                connection.setAutoCommit(false);
                sm = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                sm.setFloat(2, totalPayment);
                if (streamingAccountId != null) {
                    sm.setInt(1, streamingAccountId);
                } else {
                    sm.setNull(1, Types.INTEGER);
                }

                if (songId != null) {
                    sm.setInt(4, songId);
                } else {
                    sm.setNull(4, Types.INTEGER);
                }

                if (yearmonth != null) {
                    sm.setString(3, yearmonth + "-28");
                    sm.setString(5, yearmonth);
                } else {
                    sm.setNull(3, Types.DATE);
                    sm.setNull(5, Types.DATE);
                }
                int rowsAffected = sm.executeUpdate();
                if (rowsAffected == 0) {
                    System.out.println("Unable to pay");
                    // In this situation, no single row was infected in the whole database,
                    // So there is no need to rollback or commit.
                } else {
                    System.out.println("Paid Successfully");
                    updateManagementAccount(streamingAccountId, totalPayment * 0.3, true);
                    setListenedSongToArtistIsPaid(songId,yearmonth);
                    connection.commit();
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

    /**
     * to calculate payment amount to hosts
     * @return result set
     */
    public ResultSet PaymentToHosts() {
        String sql = "SELECT SUM((10 + PodcastEpisodes.advertisement_count * 100)) as payment " +
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

    /**
     * make payment to host. Need the user specify the payment amount.
     * @param hostId
     * @param payment
     * @param month
     * @param streamingAccountId
     */
    public void payHosts(Integer hostId, BigDecimal payment, String month, Integer streamingAccountId) {
        String sql = "INSERT INTO paidHost (paid_host_id, amount, date, paid_streaming_account_id) " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement sm = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            sm.setInt(1, hostId);
            sm.setBigDecimal(2, payment);
            sm.setString(3, month+ "-28");
            sm.setInt(4, streamingAccountId);

            connection.setAutoCommit(false);
            int rowsAffected = sm.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("Unable to pay");
            } else {
                System.out.println("Paid successfully");
                double payment_new = payment.doubleValue();
                updateManagementAccount(streamingAccountId, payment_new, true);
                connection.commit();
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            try {
                connection.rollback();
                System.out.println("Rollback successful");
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Description: Receive monthly payment from subscribers
     * Assumption: The subscribers' payment is received by account 1
     */

    /**
     * Payment process includes 2 steps: 1. insert payment information into paidService table;
     *                                   2. update management account balance.
     * The two steps should be either both successfully executed or both not, so we use transaction here.
     * Transaction Logic: 1. Set auto_commit to false;
     *                    2. Try step 1 first, if exception occurs, rollback, otherwise try step 2;
     *                    3. If step 2 throws out exception, rollback;
     *                       ( Notice that we encapsulated step 2 into function updateManagementAccount,
     *                       which would throw out exceptions if step 2 failed. )
     *                    4. Otherwise, step 1 and step 2 were both executed successfully, commit;
     *                   `5. Set auto_commit back to true.
     */
    public void receiveSubFee(String month) {

        String sql1 = "SELECT COUNT(*) FROM User WHERE status_of_subscription = 1";
        String sql2 =  "INSERT INTO paidService (monthly_subscription_fee, date, paid_user_id, paid_streaming_account_id) " +
                "SELECT DISTINCT 10.0 as monthly_subscription_fee, " +
                "? as date, " +
                "User.listener_id as paid_user_id, " +
                "1 as paid_streaming_account_id " +
                "FROM User, theMediaStreamingManagement " +
                "WHERE User.status_of_subscription = 1";

        PreparedStatement sm1 = null;
        PreparedStatement sm2 = null;

        try {
            connection.setAutoCommit(false);
            sm1 = connection.prepareStatement(sql1);
            sm2 = connection.prepareStatement(sql2, Statement.RETURN_GENERATED_KEYS);
            if (month != null) {
                sm2.setString(1, month + "-01");
            } else {
                sm2.setNull(1, Types.DATE);
            }
            int rowsAffected = sm2.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet resultSet = sm1.executeQuery();
                if (resultSet.next()) {
                    updateManagementAccount(1, 10.0 * resultSet.getDouble(1), false);
                    System.out.println("paidService added successfully.");
                    connection.commit();
                }
                else{
                    System.out.println("No active user.");
                    // In this situation, no payment is generated from any user,
                    // so no need to rollback or commit
                }
            } else {
                System.out.println("Failed to add paidService.");
                // In this situation, no single row was infected in the whole database,
                // So there is no need to rollback or commit.
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

    public ResultSet getDuePaymentOfGivenSong(int songId){
        String sql = "SELECT a.song_id, a.yearmonth, playcount, s.royalty_rate ,playcount*s.royalty_rate AS due_payment FROM (\n" +
                "\tSELECT song_id, COUNT(*) playcount, DATE_FORMAT(`date`,'%Y-%m') yearmonth \n" +
                "\tFROM listenedSong ls \n" +
                "\tWHERE song_id = ? GROUP BY yearmonth\n" +
                ") AS a\n" +
                "LEFT JOIN Songs s ON s.song_id = a.song_id";
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

    public ResultSet getMonthHasDueToArtist(int songId){
        String sql = "SELECT DISTINCT(DATE_FORMAT(`date`,'%Y-%m')) yearmonth FROM listenedSong ls WHERE song_id = ? AND isPaid='false'; \n";
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

    public ResultSet getMonthHasDueToLabel(int songId){
        String sql = "SELECT DISTINCT(DATE_FORMAT(`date`,'%Y-%m')) yearmonth FROM listenedSong ls WHERE song_id = ? AND isPaidLabel='false'; \n";
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

    /**
     * To calculate the amount based on adds and episodes and then make payment to podcast hosts.
     * @param streamingAccountId
     */
     public void payHostsCal(Integer streamingAccountId) {
        String sql = "INSERT INTO paidHost (paid_host_id, amount, date, paid_streaming_account_id) " +
                "SELECT DISTINCT PodcastHosts.host_id as paid_host_id, " +
                "(10 + PodcastEpisodes.advertisement_count * 100) as amount, " +
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
                if (rowsAffected == 0) {
                    System.out.println("Unable to Pay.");
                } else {
                    updateManagementAccount(streamingAccountId, totalPayment * 1.0, true);
                    System.out.println("Paid Hosts Successfully");
                    connection.commit();
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



}
