package main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class MetadataService extends MediaStreamingService {
    public MetadataService(MediaStreamingService mediaStreamingService){
        super(mediaStreamingService.connection);
    }
    public MetadataService(Connection connection) {
        super(connection);
    }

    public void addUserListenedPodcast(int listenerId, int podcastEpisodeId, String yearMonth, int listenCount) {
        String date = yearMonth + "-01"; // 将月份转换为日期格式
        String sql = "INSERT INTO listenedPodcast (listener_id, podcast_episode_id, date) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            for (int i = 0; i < listenCount; i++) {
                statement.setInt(1, listenerId);
                statement.setInt(2, podcastEpisodeId);
                statement.setString(3, localDate.withDayOfMonth(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                statement.executeUpdate();
            }
            System.out.println(listenCount + " records inserted.");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    public void addPodcastRatings(int podcastId, int userId, double rating) {
        String sql = "INSERT INTO ratedPodcast (listener_id, podcast_id, rating) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.setInt(2, podcastId);
            statement.setDouble(3, rating);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Successfully added rating for podcast " + podcastId + " by user " + userId);
            } else {
                System.out.println("Failed to add rating for podcast " + podcastId + " by user " + userId);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }




    public void addUserListenedSong(int listenerId, int songId, String month, int listenCount) {
        String date = month + "-01"; // 将月份转换为日期格式
        String sql = "INSERT INTO listenedSong (listener_id, song_id, date) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            for (int i = 0; i < listenCount; i++) {
                statement.setInt(1, listenerId);
                statement.setInt(2, songId);
                statement.setString(3, localDate.withDayOfMonth(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                statement.executeUpdate();
            }
            System.out.println(listenCount + " records inserted.");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    public void updatePlayCountForSongs(int songId, int playCount) {
        updateSong(songId, null, null, null, playCount, null, null, null, null);
    }

    public void updatePlayCountForArtist(int artistId, int playCount) {
        updateArtist(artistId, null, null, playCount, null, null, null);
    }
    // count through listened records table, only sum up current month record.

    public void updateSongPlayCount(int songId,String date) {
        String sql = "UPDATE Songs\n" +
                "SET play_count = IFNULL((\n" +
                "  SELECT COUNT(*)\n" +
                "  FROM listenedSong\n" +
                "  WHERE listenedSong.song_id = Songs.song_id\n" +
                "   AND listenedSong.song_id = ?\n" +
                "   AND DATE_FORMAT(listenedSong.date, '%Y-%m') = ?\n" +
                "),0) ";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, songId);
            statement.setString(2, date);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Song play count updated successfully.");
            } else {
                System.out.println("Error: Unable to update.");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    public void updateMonthlyListenerForArtists(int artistId, String month) {
        String sql = "UPDATE Artists a\n" +
                "SET monthly_listener = IFNULL((\n" +
                "  SELECT COUNT(*)\n" +
                "  FROM listenedSong l\n" +
                "  \tLEFT JOIN performed  p ON p.song_id  = l.song_id \n" +
                "  WHERE p.artist_id  = a.artist_id \n" +
                "  \tAND DATE_FORMAT(l.date,'%Y-%m') = ?\n" +
                "  GROUP BY p.artist_id\n" +
                "),0) WHERE a.artist_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, month);
            statement.setInt(2, artistId);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Artist Monthly Listener updated successfully.");
            } else {
                System.out.println("Error: Unable to update.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void updatePodcastEpisodeListeningCount(int episodeId, String date) {
        String sql = "UPDATE PodcastEpisodes\n" +
                "SET listening_count = IFNULL((\n" +
                "  SELECT COUNT(*)\n" +
                "  FROM listenedPodcast\n" +
                "  WHERE listenedPodcast.podcast_episode_id = PodcastEpisodes.podcast_episode_id\n" +
                "    AND listenedPodcast.podcast_episode_id = ?\n" +
                "    AND DATE_FORMAT(listenedPodcast.date, '%Y-%m') = ?\n" +
                "), 0)\n" +
                "WHERE podcast_episode_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, episodeId);
            statement.setString(2, date);
            statement.setInt(3, episodeId);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Podcast episode listening count updated successfully.");
            } else {
                System.out.println("Error: Unable to update.");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }



    public void updateTotalCountOfSubscribers() {
        String sql = "UPDATE Podcast p \n" +
                " SET total_subscribers = IFNULL((\n" +
                "\tSELECT COUNT(*)\n" +
                "\tFROM subscribedPodcast s\n" +
                "\tWHERE p.podcast_id = s.podcast_id \n" +
                "\tGROUP BY p.podcast_id \n" +
                "),0); ";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Subscribers updated successfully.");
            } else {
                System.out.println("Error: Unable to update.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateRating(int podcastId) {
        String sql = "UPDATE Podcast\n" +
                "SET rating = IFNULL((\n" +
                "  SELECT AVG(rating)\n" +
                "  FROM ratedPodcast\n" +
                "  WHERE podcast_id = ?\n" +
                "), 0)\n" +
                "WHERE podcast_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, podcastId);
            statement.setInt(2, podcastId);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Podcast rating updated successfully.");
            } else {
                System.out.println("Error: Unable to update.");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


}
