package main;


import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Scanner;


public class MediaStreamingService {

    private final Connection connection;

    public MediaStreamingService(Connection connection) {
        this.connection = connection;
    }



    public void addSong(String title, int duration) {
        // Implement the logic to add a song to the database
    }

    public void updateSong(int songId, String newTitle, int newDuration) {
        // Implement the logic to update a song in the database
    }

    public void deleteSong(int songId) {
        // Implement the logic to delete a song from the database
    }


    public void addUser(int listener_id, String first_name,String last_name,String phone, String email, int status_of_subscription){
        String sql = "INSERT INTO User (listener_id,first_name, last_name,phone,email,status_of_subscription) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, listener_id);
            statement.setString(2, first_name);
            statement.setString(3, last_name);

            statement.setString(4, phone);
            statement.setString(5, email);
            statement.setInt(6, status_of_subscription);


            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("User added successfully.");
            } else {
                System.out.println("Failed to add user.");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void paidService(int paid_user_id) {
        // Implement the logic to delete a song from the database
        String sql = "INSERT INTO paidService (monthly_subscription_fee,`date`,paid_user_id,paid_streaming_account_id) VALUES ( ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBigDecimal(1, BigDecimal.valueOf(10));
            Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());

            statement.setTimestamp(2, timestamp);

            statement.setInt(3, paid_user_id);
            statement.setInt(4, 2);


            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("paidService added successfully.");
            } else {
                System.out.println("Failed to add paidService.");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }

    }



    public void addPodcastHost(int host_id, String first_name,String last_name,String phone,String email,String city){

        // Implement the logic to delete a song from the database
        String sql = "INSERT INTO PodcastHosts (host_id,first_name,last_name,phone,email,city) VALUES ( ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, host_id);
            statement.setString(2, first_name);
            statement.setString(3, last_name);

            statement.setString(4, phone);
            statement.setString(5, email);
            statement.setString(6, city);



            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("addPodcastHost added successfully.");
            } else {
                System.out.println("Failed to add addPodcastHost.");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }

    }


    public ResultSet listAllSongs() throws SQLException {

        String sql = "SELECT * FROM Songs";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        return pstmt.executeQuery();


    }

    public ResultSet listAllArtists() throws SQLException {
        String sql = "SELECT * FROM Artists";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        return pstmt.executeQuery();
    }

    public ResultSet listAllPodcast() throws SQLException {
        String sql = "SELECT * FROM Podcast";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        return pstmt.executeQuery();
    }

    public ResultSet listAllPodcastHosts() throws SQLException {
        String sql = "SELECT * FROM PodcastHosts";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        return pstmt.executeQuery();
    }

    public ResultSet listAllPodcastEpisodes() throws SQLException {
        String sql = "SELECT * FROM PodcastEpisodes";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        return pstmt.executeQuery();
    }

    public ResultSet reportPerArtists() throws SQLException {
        String sql = "SELECT Artists.artist_name, DATE_FORMAT(listenedSong.date, '%Y-%m') as yearmonth, COUNT(*) AS monthly_play_count FROM listenedSong INNER JOIN Songs ON listenedSong.song_id = Songs.song_id INNER JOIN performed ON performed.song_id = Songs.song_id INNER JOIN Artists ON Artists.artist_id = performed.artist_id GROUP BY yearmonth, Artists.artist_name";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        return pstmt.executeQuery();

    }

    public ResultSet reportPerSongs() throws SQLException {

        String sql = "SELECT DATE_FORMAT(listenedSong.date, '%Y-%m') as yearmonth, Songs.song_title, COUNT(*) as play_count FROM listenedSong INNER JOIN Songs ON listenedSong.song_id = Songs.song_id GROUP BY Songs.song_id";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        return pstmt.executeQuery();
    }

    public ResultSet reportPerAlbums() throws SQLException {

        String sql = "SELECT Album.album_name, COUNT(*) as monthly_play_count, DATE_FORMAT(listenedSong.date, '%Y-%m') as yearmonth FROM Songs INNER JOIN Album ON Songs.album = Album.album_id INNER JOIN listenedSong ON Songs.song_id = listenedSong.song_id GROUP BY yearmonth, Album.album_id;";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        return pstmt.executeQuery();
    }

    public ResultSet reportGivenArtists(String artist_name) throws SQLException {

        String sql = "SELECT s.song_title " +
                "FROM Songs s " +
                "JOIN performed p ON s.song_id = p.song_id " +
                "JOIN Artists a ON p.artist_id = a.artist_id " +
                "WHERE a.artist_name = ?";
        
      PreparedStatement statement = connection.prepareStatement(sql);
      statement.setString(1, artist_name);
            
        return statement.executeQuery();
    }

    public ResultSet reportGivenAlbum(String album_name) throws SQLException {

        String sql = "SELECT s.song_title" +
                " FROM Songs s" +
                "  JOIN Album a ON s.album = a.album_id" +
                " WHERE a.album_name = ?";

        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, album_name);

        return statement.executeQuery();
    }

    public ResultSet reportGivenPodcast(String episode_title) throws SQLException {

        String sql = "SELECT episode_title FROM PodcastEpisodes WHERE podcast = ( SELECT podcast_id FROM Podcast WHERE podcast_name = ? )";

        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, episode_title);

        return statement.executeQuery();
    }
}
