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

    public void updateMonthlyListenerForSongs() {
        String sql = "UPDATE Songs\n" +
                "SET play_count = IFNULL((\n" +
                "  SELECT COUNT(*)\n" +
                "  FROM listenedSong\n" +
                "  WHERE listenedSong.song_id = Songs.song_id\n" +
                "   AND DATE_FORMAT(`date`,'%Y-%m') = DATE_FORMAT(NOW(),'%Y-%m') \n" +
                "  GROUP BY song_id\n" +
                "),0) ";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Song Monthly Listener updated successfully.");
            } else {
                System.out.println("Error: Unable to update.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateMonthlyListenerForArtists() {
        String sql = "UPDATE Artists a\n" +
                "SET monthly_listener = IFNULL((\n" +
                "  SELECT COUNT(*)\n" +
                "  FROM listenedSong l\n" +
                "  \tLEFT JOIN performed  p ON p.song_id  = l.song_id \n" +
                "  WHERE p.artist_id  = a.artist_id \n" +
                "  \tAND DATE_FORMAT(`date`,'%Y-%m') = DATE_FORMAT(NOW(),'%Y-%m') \n" +
                "  GROUP BY p.artist_id\n" +
                "),0)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
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

    public void updateTheAvgRatingOfPodcast() {
        String sql = "UPDATE Podcast p\n" +
                " SET rating = IFNULL((\n" +
                " \tSELECT AVG(rating)\n" +
                " \tFROM ratedPodcast r\n" +
                " \tWHERE r.podcast_id = p.podcast_id \n" +
                " \tGROUP BY r.podcast_id \n" +
                "),0); ";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Rating updated successfully.");
            } else {
                System.out.println("Error: Unable to update.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
