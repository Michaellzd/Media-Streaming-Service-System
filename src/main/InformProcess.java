package main;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class InformProcess {

    public  void informationProcessingMenu(MediaStreamingService mediaStreamingService, Scanner scanner) throws SQLException {
        int choice;
        do {
            System.out.println("Information Processing Menu");
            System.out.println("1. Manage Songs");
            System.out.println("3. Manage Podcast Hosts");

            /*System.out.println("2. Manage Artists");
            System.out.println("4. Manage Podcast Episodes");
            System.out.println("5. Assign Songs and Artists to Albums");
            System.out.println("6. Assign Artists to Record Labels");
            System.out.println("7. Assign Podcast Episodes and Hosts to Podcasts");*/
            System.out.println("8. add User");

            System.out.println("9. List All Songs");
            System.out.println("10. List All Artists");
            System.out.println("11. List All Podcasts");
            System.out.println("12. List All Podcast Host");
            System.out.println("13. List All Podcast Episodes");






            System.out.println("0. Back to Main Menu");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    manageSongsMenu(mediaStreamingService, scanner);
                    break;
                case 3:
                    managePodcastHostsMenu(mediaStreamingService, scanner);
                    break;
                /*case 2:
                    manageArtistsMenu(mediaStreamingService, scanner);
                    break;
                case 3:
                    managePodcastHostsMenu(mediaStreamingService, scanner);
                    break;
                case 4:
                    managePodcastEpisodesMenu(mediaStreamingService, scanner);
                    break;
                case 5:
                    assignSongsAndArtistsToAlbumsMenu(mediaStreamingService, scanner);
                    break;
                case 6:
                    assignArtistsToRecordLabelsMenu(mediaStreamingService, scanner);
                    break;
                case 7:
                    assignPodcastEpisodesAndHostsToPodcastsMenu(mediaStreamingService, scanner);
                    break;*/
                case 8:
                    addUser(mediaStreamingService, scanner);
                    break;
                case 9:
                    DisplaySongs(mediaStreamingService);
                    break;
                case 10:
                    DisplayArtist(mediaStreamingService);
                    break;
                case 11:
                    DisplayPodcastMenu(mediaStreamingService);
                    break;
                case 12:
                    DisplayPodcastHostMenu(mediaStreamingService);
                    break;
                case 13:
                    DisplayPodcastEpisode(mediaStreamingService);
                    break;


            }
        } while (choice != 0);
    }

    private void DisplayPodcastEpisode(MediaStreamingService mediaStreamingService) throws SQLException {
        System.out.println("All Podcast Episodes:");
        ResultSet resultSet = mediaStreamingService.listAllPodcastEpisodes();

        try {
            while (resultSet.next()) {
                int podcast_episode_id = resultSet.getInt("podcast_episode_id");
                String episode_title = resultSet.getString("episode_title");
                String duration = resultSet.getString("duration");
                String release_date = resultSet.getString("release_date");
                int listening_count = resultSet.getInt("listening_count");
                int advertisement_count = resultSet.getInt("advertisement_count");
                int podcast = resultSet.getInt("podcast");

                System.out.printf("Episode ID: %d%n Episode Title: %s%n Duration: %s%n Release Date: %s%n" +
                                "Listening Count: %d%n Advertisement Count: %d%n Podcast: %d%n",
                        podcast_episode_id, episode_title, duration, release_date, listening_count, advertisement_count, podcast);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void DisplayPodcastHostMenu(MediaStreamingService mediaStreamingService) throws SQLException {
        System.out.println("All Podcast Hosts:");
        ResultSet resultSet = mediaStreamingService.listAllPodcastHosts();

        try {
            while (resultSet.next()) {
                int host_id = resultSet.getInt("host_id");
                String first_name = resultSet.getString("first_name");
                String last_name = resultSet.getString("last_name");
                String phone = resultSet.getString("phone");
                String email = resultSet.getString("email");
                String city = resultSet.getString("city");

                System.out.printf("Host ID: %d%n First Name: %s%n Last Name: %s%n Phone: %s%n" +
                                "Email: %s%n City: %s%n",
                        host_id, first_name, last_name, phone, email, city);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void DisplayPodcastMenu(MediaStreamingService mediaStreamingService) throws SQLException {
        System.out.println("All Podcasts:");
        ResultSet resultSet = mediaStreamingService.listAllPodcast();

        try {
            while (resultSet.next()) {
                int podcast_id = resultSet.getInt("podcast_id");
                int total_subscribers = resultSet.getInt("total_subscribers");
                String podcast_name = resultSet.getString("podcast_name");
                String country = resultSet.getString("country");
                int episode_count = resultSet.getInt("episode_count");
                String genres = resultSet.getString("genres");
                String language = resultSet.getString("language");
                String sponsors = resultSet.getString("sponsors");
                double rating = resultSet.getDouble("rating");

                System.out.printf("Podcast podcast_id: %d%n Podcast Name: %s%n Podcast total_subscribers: %d%n" +
                                "Podcast country: %s%n Podcast episode_count: %d%n Podcast genres: %s%n" +
                                "Podcast language: %s%n Podcast sponsors: %s%n Podcast rating: %.2f%n ",
                        podcast_id, podcast_name, total_subscribers, country, episode_count, genres, language, sponsors, rating);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void DisplayArtist(MediaStreamingService mediaStreamingService) throws SQLException {
        System.out.println("All Artists:");
        ResultSet resultSet = mediaStreamingService.listAllArtists();

        try {
            while (resultSet.next()) {
                int artist_id = resultSet.getInt("artist_id");
                String artist_name = resultSet.getString("artist_name");
                String status = resultSet.getString("status");
                int monthly_listener = resultSet.getInt("monthly_listener");
                String type = resultSet.getString("type");
                String country = resultSet.getString("country");
                String primary_genre = resultSet.getString("primary_genre");
                String record_label = resultSet.getString("record_label");

                System.out.printf("Artists artist_id: %d%n Artists Name: %s%n Artists status: %s%n" +
                                "Artists monthly_listener: %s%n Artists type: %s%n Artists country: %s%n Songs primary_genre: %s%n" +
                                "Artists record_label: %s%n ",
                        artist_id, artist_name, status, monthly_listener, type, country, primary_genre, record_label);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void DisplaySongs(MediaStreamingService mediaStreamingService) throws SQLException {
        System.out.println("All Songs:");
        ResultSet resultSet = mediaStreamingService.listAllSongs();

        try {
            while (resultSet.next()) {
                int song_id = resultSet.getInt("song_id");
                String song_title = resultSet.getString("song_title");
                String duration = resultSet.getString("duration");
                String genres = resultSet.getString("genres");
                int play_count = resultSet.getInt("play_count");
                String language = resultSet.getString("language");
                BigDecimal royalty_rate = resultSet.getBigDecimal("royalty_rate");
                String release_date = resultSet.getString("release_date");
                String release_country = resultSet.getString("release_country");
                int album = resultSet.getInt("album");


                System.out.printf("Songs ID: %d%n Songs Name: %s%n Songs duration: %s%n" +
                                "Songs genres: %s%n Songs play_count: %d%n Songs language: %s%n Songs royalty_rate: %.2f%n" +
                                "Songs release_date: %s%n Songs release_country: %s%n Songs album: %d%n",
                        song_id, song_title,duration,genres, play_count,language,royalty_rate,release_date,release_country, album);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public void manageSongsMenu(MediaStreamingService mediaStreamingService, Scanner scanner){

    }

    public void managePodcastHostsMenu(MediaStreamingService mediaStreamingService, Scanner scanner){
        int choice;
        do {
            System.out.println("Manage PodcastHosts Menu");
            System.out.println("1. Add PodcastHosts");
            System.out.println("2. Update PodcastHosts");
            System.out.println("3. Delete PodcastHosts");
            System.out.println("0. Back to Information Processing Menu");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();

            scanner.nextLine(); // Consume the newline character

            switch (choice) {
                case 1:
                    System.out.print("Enter host_id: ");
                    int host_id = scanner.nextInt();
                    System.out.println("Enter firstname: ");
                    String firstname = scanner.next();
                    System.out.println("Enter lastname: ");
                    String lastname = scanner.next();
                    System.out.println("Enter phone: ");
                    String phone = scanner.next();

                    System.out.println("Enter email: ");
                    String email = scanner.next();

                    System.out.println("Enter city: ");
                    String city = scanner.next();

                    scanner.nextLine();

                    mediaStreamingService.addPodcastHost(host_id,firstname,lastname,phone,email,city);
                    break;
                case 2:
                    System.out.print("Enter song ID to update: ");
                    int songId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Enter new song title: ");
                    String newTitle = scanner.nextLine();
                    System.out.print("Enter new song duration (in seconds): ");
                    int newDuration = scanner.nextInt();
                    scanner.nextLine();
                    mediaStreamingService.updateSong(songId, newTitle, newDuration);
                    break;
                case 3:
                    System.out.print("Enter song ID to delete: ");
                    int deleteSongId = scanner.nextInt();
                    scanner.nextLine();
                    mediaStreamingService.deleteSong(deleteSongId);
                    break;
            }
        } while (choice != 0);


    }

    public void addUser(MediaStreamingService mediaStreamingService, Scanner scanner){
        System.out.print("Enter User Id: ");
        int userId = scanner.nextInt();
        System.out.println("Enter firstname: ");
        String firstname = scanner.next();
        System.out.println("Enter lastname: ");
        String lastname = scanner.next();

        System.out.println("Enter phone: ");
        String phone = scanner.next();

        System.out.println("Enter email: ");
        String email = scanner.next();

        System.out.print("Enter status(0:inactive/1:active): ");
        int status = scanner.nextInt();
        scanner.nextLine();
        mediaStreamingService.addUser(userId,firstname,lastname,phone,email,status);
        if(status==1){
            mediaStreamingService.paidService(userId);
        }
    }
}
