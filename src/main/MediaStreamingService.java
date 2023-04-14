package main;


import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class MediaStreamingService {
    //connection
    protected final Connection connection;

    public MediaStreamingService(Connection connection) {
        this.connection = connection;
    }
    //add song(JDBC)
    public void addSong(int songId, String songTitle, String duration, String genres, int playCount, String language, double royaltyRate, String releaseDate, String releaseCountry, Integer albumId) {
        String sql = "INSERT INTO Songs (song_id, song_title, duration, genres, play_count, language, royalty_rate, release_date, release_country, album) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, songId);
            statement.setString(2, songTitle);
            statement.setString(3, duration);
            statement.setString(4, genres);
            statement.setInt(5, playCount);
            statement.setString(6, language);
            statement.setDouble(7, royaltyRate);
            statement.setString(8, releaseDate);
            statement.setString(9, releaseCountry);
            if (albumId == null) {
                statement.setNull(10, Types.INTEGER);
            } else {
                statement.setInt(10, albumId);
            }
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    //ASSIGN ARTIST TO SONG(!this is for the performed table)

    public void assignArtistToSong(String isCollaborator, int songId, int artistId) {
        String sql = "INSERT INTO performed (is_collaborator, song_id, artist_id) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, isCollaborator);
            statement.setInt(2, songId);
            statement.setInt(3, artistId);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    //update the song
    public void updateSong(int songId, String newSongTitle, String newDuration, String newGenres, Integer newPlayCount, String newLanguage, BigDecimal newRoyaltyRate, String newReleaseDate, String newReleaseCountry) {
        String sql = "UPDATE Songs SET song_title = COALESCE(?, song_title), duration = COALESCE(?, duration), genres = COALESCE(?, genres), play_count = COALESCE(?, play_count), language = COALESCE(?, language), royalty_rate = COALESCE(?, royalty_rate), release_date = COALESCE(?, release_date), release_country = COALESCE(?, release_country) WHERE song_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            if (newSongTitle != null) {
                statement.setString(1, newSongTitle);
            } else {
                statement.setNull(1, Types.VARCHAR);
            }

            if (newDuration != null) {
                statement.setString(2, newDuration);
            } else {
                statement.setNull(2, Types.VARCHAR);
            }

            if (newGenres != null) {
                statement.setString(3, newGenres);
            } else {
                statement.setNull(3, Types.VARCHAR);
            }

            if (newPlayCount != null) {
                statement.setInt(4, newPlayCount);
            } else {
                statement.setNull(4, Types.INTEGER);
            }

            if (newLanguage != null) {
                statement.setString(5, newLanguage);
            } else {
                statement.setNull(5, Types.VARCHAR);
            }

            if (newRoyaltyRate != null) {
                statement.setBigDecimal(6, newRoyaltyRate);
            } else {
                statement.setNull(6, Types.DECIMAL);
            }

            if (newReleaseDate != null) {
                statement.setString(7, newReleaseDate);
            } else {
                statement.setNull(7, Types.VARCHAR);
            }

            if (newReleaseCountry != null) {
                statement.setString(8, newReleaseCountry);
            } else {
                statement.setNull(8, Types.VARCHAR);
            }

            statement.setInt(9, songId);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Song updated successfully.");
            } else {
                System.out.println("Error: Unable to update song.");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    //update the podcast host(jdbc)
    public void updatePodcastHost(int hostId, String newFirstName, String newLastName, String newPhone, String newEmail, String newCity) {
        String sql = "UPDATE PodcastHosts SET first_name = COALESCE(?, first_name), last_name = COALESCE(?, last_name), phone = COALESCE(?, phone), email = COALESCE(?, email), city = COALESCE(?, city) WHERE host_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            if (newFirstName != null) {
                statement.setString(1, newFirstName);
            } else {
                statement.setNull(1, Types.VARCHAR);
            }

            if (newLastName != null) {
                statement.setString(2, newLastName);
            } else {
                statement.setNull(2, Types.VARCHAR);
            }

            if (newPhone != null) {
                statement.setString(3, newPhone);
            } else {
                statement.setNull(3, Types.VARCHAR);
            }

            if (newEmail != null) {
                statement.setString(4, newEmail);
            } else {
                statement.setNull(4, Types.VARCHAR);
            }

            if (newCity != null) {
                statement.setString(5, newCity);
            } else {
                statement.setNull(5, Types.VARCHAR);
            }

            statement.setInt(6, hostId);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Podcast host updated successfully.");
            } else {
                System.out.println("Error: Unable to update podcast host.");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    //update podcast episodes
    public void updatePodcastEpisode(int podcastEpisodeId, String newEpisodeTitle, String newDuration, String newReleaseDate, Integer newListeningCount, Integer newAdvertisementCount, Integer newPodcast) {
        String sql = "UPDATE PodcastEpisodes SET episode_title = COALESCE(?, episode_title), duration = COALESCE(?, duration), release_date = COALESCE(?, release_date), listening_count = COALESCE(?, listening_count), advertisement_count = COALESCE(?, advertisement_count), podcast = COALESCE(?, podcast) WHERE podcast_episode_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            if (newEpisodeTitle != null) {
                statement.setString(1, newEpisodeTitle);
            } else {
                statement.setNull(1, Types.VARCHAR);
            }

            if (newDuration != null) {
                statement.setString(2, newDuration);
            } else {
                statement.setNull(2, Types.VARCHAR);
            }

            if (newReleaseDate != null) {
                statement.setString(3, newReleaseDate);
            } else {
                statement.setNull(3, Types.VARCHAR);
            }

            if (newListeningCount != null) {
                statement.setInt(4, newListeningCount);
            } else {
                statement.setNull(4, Types.INTEGER);
            }

            if (newAdvertisementCount != null) {
                statement.setInt(5, newAdvertisementCount);
            } else {
                statement.setNull(5, Types.INTEGER);
            }

            if (newPodcast != null) {
                statement.setInt(6, newPodcast);
            } else {
                statement.setNull(6, Types.INTEGER);
            }

            statement.setInt(7, podcastEpisodeId);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Podcast episode updated successfully.");
            } else {
                System.out.println("Error: Unable to update podcast episode.");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    //update artsist
    public void updateArtist(int artistId, String newArtistName, String newStatus, Integer newMonthlyListener, String newType, String newCountry, String newPrimaryGenre) {
        String sql = "UPDATE Artists SET artist_name = COALESCE(?, artist_name), status = COALESCE(?, status), monthly_listener = COALESCE(?, monthly_listener), type = COALESCE(?, type), country = COALESCE(?, country), primary_genre = COALESCE(?, primary_genre) WHERE artist_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            if (newArtistName != null) {
                statement.setString(1, newArtistName);
            } else {
                statement.setNull(1, Types.VARCHAR);
            }

            if (newStatus != null) {
                statement.setString(2, newStatus);
            } else {
                statement.setNull(2, Types.VARCHAR);
            }

            if (newMonthlyListener != null) {
                statement.setInt(3, newMonthlyListener);
            } else {
                statement.setNull(3, Types.INTEGER);
            }

            if (newType != null) {
                statement.setString(4, newType);
            } else {
                statement.setNull(4, Types.VARCHAR);
            }

            if (newCountry != null) {
                statement.setString(5, newCountry);
            } else {
                statement.setNull(5, Types.VARCHAR);
            }

            if (newPrimaryGenre != null) {
                statement.setString(6, newPrimaryGenre);
            } else {
                statement.setNull(6, Types.VARCHAR);
            }

            statement.setInt(7, artistId);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Artist updated successfully.");
            } else {
                System.out.println("Error: Unable to update artist.");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    //delete song
    public void deleteSong(int songId) {
        // Remove artist associations in the 'performed' table
        String sqlRemoveArtists = "DELETE FROM performed WHERE song_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sqlRemoveArtists)) {
            statement.setInt(1, songId);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error while deleting artist associations: " + e.getMessage());
        }

        // Remove the song from the 'Songs' table
        String sqlRemoveSong = "DELETE FROM Songs WHERE song_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sqlRemoveSong)) {
            statement.setInt(1, songId);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error while deleting the song: " + e.getMessage());
        }
    }
    
    public void deleteArtist(int artistId) {
        // Remove song associations in the 'performed' table
        String sqlRemoveSongs = "DELETE FROM performed WHERE artist_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sqlRemoveSongs)) {
            statement.setInt(1, artistId);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error while deleting song associations: " + e.getMessage());
        }

        // Remove the artist from the 'Artists' table
        String sqlRemoveArtist = "DELETE FROM Artists WHERE artist_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sqlRemoveArtist)) {
            statement.setInt(1, artistId);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error while deleting the artist: " + e.getMessage());
        }
    }

    public void deletePodcastHost(int hostId) {
        // Remove podcast associations in the 'hosted' table
        String sqlRemovePodcasts = "DELETE FROM hosted WHERE host_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sqlRemovePodcasts)) {
            statement.setInt(1, hostId);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error while deleting podcast associations: " + e.getMessage());
        }

        // Remove the podcast host from the 'PodcastHosts' table
        String sqlRemoveHost = "DELETE FROM PodcastHosts WHERE host_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sqlRemoveHost)) {
            statement.setInt(1, hostId);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error while deleting the podcast host: " + e.getMessage());
        }
    }

    public void deletePodcastEpisode(int podcastEpisodeId) {
        String sql = "DELETE FROM PodcastEpisodes WHERE podcast_episode_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, podcastEpisodeId);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error while deleting the podcast episode: " + e.getMessage());
        }
    }
    //add user(jdbc)
    public void addUser(int listener_id, String first_name, String last_name, String phone, String email, int status_of_subscription) {
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

//    public void paidService(int paid_user_id) {
//        // Implement the logic to delete a song from the database
//        String sql = "INSERT INTO paidService (monthly_subscription_fee,`date`,paid_user_id,paid_streaming_account_id) VALUES ( ?, ?, ?, ?)";
//
//        try (PreparedStatement statement = connection.prepareStatement(sql)) {
//            statement.setBigDecimal(1, BigDecimal.valueOf(10));
//            Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
//
//            statement.setTimestamp(2, timestamp);
//
//            statement.setInt(3, paid_user_id);
//            statement.setInt(4, 2);
//
//
//            int rowsAffected = statement.executeUpdate();
//
//            if (rowsAffected > 0) {
//                System.out.println("paidService added successfully.");
//            } else {
//                System.out.println("Failed to add paidService.");
//            }
//        } catch (SQLException e) {
//            System.out.println("Error: " + e.getMessage());
//        }
//
//    }
    //add podcastHost(jdbc)
    public void addPodcastHost(int host_id, String first_name, String last_name, String phone, String email, String city) {

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
    //Below are display(just select * from the specific table)
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

    public ResultSet listAllAlbum() throws SQLException {
        String sql = "SELECT * FROM Album";
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

        String sql = "SELECT DATE_FORMAT(listenedSong.date, '%Y-%m') as yearmonth, Songs.song_title, COUNT(*) as play_count FROM listenedSong INNER JOIN Songs ON listenedSong.song_id = Songs.song_id GROUP BY yearmonth, Songs.song_id";
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

    public void addRecordLabel(int record_label_id, String record_label_name) {
        // Implement the logic to delete a song from the database
        String sql = "INSERT INTO RecordLabel (record_label_id,record_label_name) VALUES ( ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, record_label_id);
            statement.setString(2, record_label_name);


            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("addRecordLabel added successfully.");
            } else {
                System.out.println("Failed to add addRecordLabel.");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }

    }

    public void addArtist(int artist_id, String artist_name, String status, int monthly_listener, String type, String country, String primary_genre) {


        // Implement the logic to add an artist to the database
        String sql = "INSERT INTO Artists (artist_id, artist_name, status, monthly_listener, `type`, country, primary_genre) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, artist_id);
            statement.setString(2, artist_name);
            statement.setString(3, status);
            statement.setInt(4, monthly_listener);
            statement.setString(5, type);
            statement.setString(6, country);
            statement.setString(7, primary_genre);

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Artist added successfully.");
            } else {
                System.out.println("Failed to add artist.");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }

    }

    //get Record label的id 用于assign
    public int getRecordLabelIdByName(String record_label_name) {
        String sql = "SELECT record_label_id FROM RecordLabel WHERE record_label_name = ?";
        int recordLabelId = -1;

        System.out.println(record_label_name);

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, record_label_name);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                recordLabelId = resultSet.getInt("record_label_id");
            } else {
                System.out.println("Record label not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }

        return recordLabelId;
    }

    public void updateArtistRecordLabel(String artistName, int recordLabelId) {
        String sql = "UPDATE Artists SET record_label = ? WHERE artist_name = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, recordLabelId);
            statement.setString(2, artistName);

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Artist's record label updated successfully.");
            } else {
                System.out.println("Failed to update artist's record label.");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void addPodcast(int podcastId, String podcastName, int totalSubscribers, String country, int episodeCount, String genres, String language, String sponsors, double rating) {
        String sql = "INSERT INTO Podcast (podcast_id, podcast_name, total_subscribers, country, episode_count, genres, language, sponsors, rating) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, podcastId);
            statement.setString(2, podcastName);
            statement.setInt(3, totalSubscribers);
            statement.setString(4, country);
            statement.setInt(5, episodeCount);
            statement.setString(6, genres);
            statement.setString(7, language);

            if (sponsors == null || sponsors.trim().isEmpty()) {
                statement.setNull(8, Types.VARCHAR);
            } else {
                statement.setString(8, sponsors);
            }

            statement.setDouble(9, rating);

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Podcast added successfully.");
            } else {
                System.out.println("Failed to add podcast.");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    //上面都是Record label

    //assign EP to Podcast
    public int getPodcastIdByName(String epname) {
        String sql = "SELECT podcast_id FROM Podcast WHERE podcast_name = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, epname);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
//                return resultSet.getInt("podcast_episode_id");
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return -1;
    }

    public int getPodcastEpisodeIdByTitle(String episode_title) {
        String sql = "SELECT podcast_episode_id FROM PodcastEpisodes WHERE episode_title = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, episode_title);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("podcast_episode_id");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return -1;
    }

    public void updatePodcastEpisodePodcast(int podcastEpisodeId, int podcastId) {
        String sql = "UPDATE PodcastEpisodes SET podcast = ? WHERE podcast_episode_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, podcastId);
            statement.setInt(2, podcastEpisodeId);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Podcast episode assigned to the podcast successfully.");
            } else {
                System.out.println("Failed to assign podcast episode to the podcast.");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    //上面都是EP TO PD的方法

    //assign art to album
    public void associateArtistWithAlbum(int artistId, int albumId) {
        String sql = "INSERT INTO released (artist_id, album_id) VALUES (?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, artistId);
            statement.setInt(2, albumId);

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Artist and album associated successfully.");
            } else {
                System.out.println("Failed to associate artist and album.");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }


    }

    public int getArtistIdByName(String artistName) {
        String sql = "SELECT artist_id FROM Artists WHERE artist_name = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, artistName);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("artist_id");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }

        return -1;
    }

    public int getAlbumIdByName(String albumName) {
        String sql = "SELECT album_id FROM Album WHERE album_name = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, albumName);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("album_id");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }

        return -1;
    }

    //art to album above

    public void addPodcastEpisode(int podcast_episode_id, String episode_title, String duration, String release_date, int listening_count, int advertisement_count, int podcast_id) {
        String sql = "INSERT INTO PodcastEpisodes (podcast_episode_id, episode_title, duration, release_date, listening_count, advertisement_count, podcast) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, podcast_episode_id);
            statement.setString(2, episode_title);
            statement.setString(3, duration);
            statement.setString(4, release_date);
            statement.setInt(5, listening_count);
            statement.setInt(6, advertisement_count);
            statement.setInt(7, podcast_id);

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Podcast episode added successfully.");
            } else {
                System.out.println("Failed to add podcast episode.");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void addAlbum(int album_id, String album_name, int track_number, String release_year, String edition) {
        String sql = "INSERT INTO Album (album_id, album_name, track_number, release_year, edition) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, album_id);
            statement.setString(2, album_name);
            statement.setInt(3, track_number);
            statement.setString(4, release_year);
            statement.setString(5, edition);

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Album added successfully.");
            } else {
                System.out.println("Failed to add album.");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    //assign host to podcast
    public int getHostIdByName(String hostName) {
        String sql = "SELECT host_id FROM PodcastHosts WHERE CONCAT(first_name, ' ', last_name) = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, hostName);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("host_id");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }

        return -1;
    }

    public void assignHostToPodcast(int hostId, int epid) {
        String sql = "INSERT INTO hosted (host_id, podcast_episode_id) VALUES (?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, hostId);
            statement.setInt(2, epid);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    //host to podcast above
    //give a artist name and get songs(jdbc)
    public List<String> findSongsByArtist(String artistName) {
        List<String> songTitles = new ArrayList<>();

        String sql = "SELECT s.song_title " +
                "FROM Songs s, performed p, Artists ar " +
                "WHERE s.song_id = p.song_id " +
                "AND ar.artist_id = p.artist_id " +
                "AND ar.artist_name = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, artistName);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String songTitle = resultSet.getString("song_title");
                songTitles.add(songTitle);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }

        return songTitles;
    }
    //give a name of album-->songs(jdbc)

    public List<String> findSongsByAlbum(String albumName) {
        List<String> songTitles = new ArrayList<>();

        String sql = "SELECT s.song_title " +
                "FROM Songs s, Album a " +
                "WHERE a.album_id = s.album " +
                "AND a.album_name = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, albumName);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String songTitle = resultSet.getString("song_title");
                songTitles.add(songTitle);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }

        return songTitles;
    }
    //give a name of podcast-->episode(jdbc)
    public List<String> findEpisodesByPodcast(String podcastName) {
        List<String> episodeTitles = new ArrayList<>();

        String sql = "SELECT pe.episode_title " +
                "FROM PodcastEpisodes pe, Podcast p " +
                "WHERE pe.podcast = p.podcast_id " +
                "AND p.podcast_name = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, podcastName);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String episodeTitle = resultSet.getString("episode_title");
                episodeTitles.add(episodeTitle);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }

        return episodeTitles;
    }
}
