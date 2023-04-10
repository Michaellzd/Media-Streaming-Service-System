package main;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class InformProcess {


    //Submenu for information Process
    public  void informationProcessingMenu(MediaStreamingService mediaStreamingService, Scanner scanner) throws SQLException {
        int choice;
        do {
            System.out.println("********************************");
            System.out.println("Information Processing Menu");
            System.out.println("1. Manage Songs");
            System.out.println("2. Manage Artists");
            System.out.println("3. Manage Podcast Hosts");
            System.out.println("4. Manage Podcast Episodes");
            System.out.println("5. Assign Artists to Albums");
            System.out.println("6. Assign Artists to Record Labels");

            /*System.out.println("4. Manage Podcast Episodes");
           ;*/
            System.out.println("7. Assign  Hosts to Podcasts");
            System.out.println("8. add User");

            System.out.println("9. List All Songs");
            System.out.println("10. List All Artists");
            System.out.println("11. List All Podcasts");
            System.out.println("12. List All Podcast Host");
            System.out.println("13. List All Podcast Episodes");


            System.out.println("14. add Record Label");
            System.out.println("15. add Podcast");
            System.out.println("16. add Album");


            System.out.println("0. Back to Main Menu");
            System.out.print("Enter your choice: ");

            choice = scanner.nextInt();
            System.out.println("********************************");

            switch (choice) {
                case 1:
                    manageSongsMenu(mediaStreamingService, scanner);
                    break;
                case 2:
                    manageArtistsMenu(mediaStreamingService, scanner);
                    break;
                case 3:
                    managePodcastHostsMenu(mediaStreamingService, scanner);
                    break;

                case 4:
                    managePodcastEpisodesMenu(mediaStreamingService, scanner);
                    break;
                case 5:
                    assignArtistsToAlbumsMenu(mediaStreamingService, scanner);
                    break;
                    
                    
                case 6:
                    assignArtistsToRecordLabels(mediaStreamingService, scanner);
                    break;

                case 7:
                    assignHostsToPodcastsMenu(mediaStreamingService, scanner);
                    break;


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
                case 14:
                    addRecordLabel(mediaStreamingService,scanner);
                    break;
                case 15:
                    addPodcast(mediaStreamingService,scanner);
                    break;
                case 16:
                    addAlbum(mediaStreamingService,scanner);
                    break;

            }
        } while (choice != 0);
    }

    //Assign host to podcast
    private void assignHostsToPodcastsMenu(MediaStreamingService mediaStreamingService, Scanner scanner) {
        scanner.nextLine();

        System.out.print("Enter podcast name: ");
        String podcastName = scanner.nextLine();

        System.out.print("Enter host name: ");
        String hostName = scanner.nextLine();

        int podcastId = mediaStreamingService.getPodcastIdByName(podcastName);
        int hostId = mediaStreamingService.getHostIdByName(hostName);

        if (podcastId != -1 && hostId != -1) {
            mediaStreamingService.assignHostToPodcast(hostId, podcastId);
        } else {
            System.out.println("Podcast or host not found. Please try again.");
        }
    }

    //Assign artist to album
    private void assignArtistsToAlbumsMenu(MediaStreamingService mediaStreamingService, Scanner scanner) {
        scanner.nextLine();
        System.out.println("Enter artist name: ");
        String artistName = scanner.nextLine();
        System.out.println("Enter album name: ");
        String albumName = scanner.nextLine();

        int artistId = mediaStreamingService.getArtistIdByName(artistName);
        int albumId = mediaStreamingService.getAlbumIdByName(albumName);

        if (artistId != -1 && albumId != -1) {
            mediaStreamingService.associateArtistWithAlbum(artistId, albumId);
        } else {
            System.out.println("Artist or album not found. Please try again.");
        }
    }

    //enter album
    private void addAlbum(MediaStreamingService mediaStreamingService, Scanner scanner) {
        System.out.print("Enter Album Id: ");
        int albumId = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        System.out.print("Enter Album Name: ");
        String albumName = scanner.nextLine();

        System.out.print("Enter Track Number: ");
        int trackNumber = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter Release Year: ");
        String releaseYear = scanner.nextLine();

        System.out.print("Enter Edition (collector's edition/limited/special): ");
        String edition = scanner.nextLine();

        mediaStreamingService.addAlbum(albumId, albumName, trackNumber, releaseYear, edition);
    }

    //submenu to manage podcats episodes
    private void managePodcastEpisodesMenu(MediaStreamingService mediaStreamingService, Scanner scanner) {
        int choice;
        do {
            System.out.println("********************************");

            System.out.println("Manage Podcast Episodes Menu");
            System.out.println("1. Add Podcast Episode");
            System.out.println("2. Update Podcast Episode");
            System.out.println("3. Delete Podcast Episode");
            System.out.println("0. Back to Information Processing Menu");
            System.out.print("Enter your choice: ");

            choice = scanner.nextInt();
            System.out.println("********************************");

            scanner.nextLine(); // Consume the newline character

            switch (choice) {
                case 1:
                    System.out.print("Enter podcast_episode_id: ");
                    int podcast_episode_id = scanner.nextInt();

                    System.out.println("Enter duration: ");
                    String duration = scanner.next();

                    scanner.nextLine(); // Consume the newline character

                    System.out.println("Enter episode_title: ");
                    String episode_title = scanner.nextLine();


                    System.out.println("Enter release_date: ");
                    String release_date = scanner.next();
                    System.out.println("Enter listening_count: ");
                    int listening_count = scanner.nextInt();
                    System.out.println("Enter advertisement_count (Input 0 if there is no advertisement count ): ");
                    int advertisement_count = 0;
                    if (scanner.hasNextInt()) {
                        advertisement_count = scanner.nextInt();
                    } else {
                        scanner.nextLine(); // Consume the newline character
                    }

                    scanner.nextLine(); // Consume the newline character
                    System.out.println("Enter this episode to a podcast, and the name of the postcast is : ");
                    String podcast_title = scanner.nextLine();


                    int podcast_id = mediaStreamingService.getPodcastIdByName(podcast_title);

                    mediaStreamingService.addPodcastEpisode(podcast_episode_id, episode_title, duration, release_date, listening_count, advertisement_count, podcast_id);
                    break;
                case 2:
                    System.out.print("Enter podcast_episode_id to update: ");
                    int updatePodcastEpisodeId = scanner.nextInt();
                    scanner.nextLine();

                    System.out.print("Enter new episode_title (leave blank to keep current title): ");
                    String newEpisodeTitle = scanner.nextLine();
                    if (newEpisodeTitle.isEmpty()) {
                        newEpisodeTitle = null;
                    }

                    System.out.print("Enter new duration (leave blank to keep current duration): ");
                    String newDuration = scanner.nextLine();
                    if (newDuration.isEmpty()) {
                        newDuration = null;
                    }

                    System.out.print("Enter new release_date (leave blank to keep current release_date, format: yyyy-MM-dd): ");
                    String newReleaseDate = scanner.nextLine();
                    if (newReleaseDate.isEmpty()) {
                        newReleaseDate = null;
                    }

                    System.out.print("Enter new listening_count (leave blank to keep current value): ");
                    String newListeningCountInput = scanner.nextLine();
                    Integer newListeningCount = null;
                    if (!newListeningCountInput.isEmpty()) {
                        newListeningCount = Integer.parseInt(newListeningCountInput);
                    }

                    System.out.print("Enter new advertisement_count (leave blank to keep current value): ");
                    String newAdvertisementCountInput = scanner.nextLine();
                    Integer newAdvertisementCount = null;
                    if (!newAdvertisementCountInput.isEmpty()) {
                        newAdvertisementCount = Integer.parseInt(newAdvertisementCountInput);
                    }

                    System.out.print("Enter new podcast (leave blank to keep current podcast): ");
                    String newPodcastInput = scanner.nextLine();
                    Integer newPodcast = null;
                    if (!newPodcastInput.isEmpty()) {
                        newPodcast = Integer.parseInt(newPodcastInput);
                    }

                    mediaStreamingService.updatePodcastEpisode(updatePodcastEpisodeId, newEpisodeTitle, newDuration, newReleaseDate, newListeningCount, newAdvertisementCount, newPodcast);
                    break;

                case 3:
                    System.out.print("Enter podcast_episode_id to delete: ");
                    int deleteEpisodeId = scanner.nextInt();
                    scanner.nextLine();
                    mediaStreamingService.deletePodcastEpisode(deleteEpisodeId);
                    break;
            }
        } while (choice != 0);
    }


    //add podcast
    private void addPodcast(MediaStreamingService mediaStreamingService, Scanner scanner) {
        System.out.print("Enter Podcast Id: ");
        int podcastId = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        System.out.print("Enter Podcast Name: ");
        String podcastName = scanner.nextLine();

        System.out.print("Enter Total Subscribers: ");
        int totalSubscribers = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter Country: ");
        String country = scanner.nextLine();

        System.out.print("Enter Episode Count: ");
        int episodeCount = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter Genres: ");
        String genres = scanner.nextLine();

        System.out.print("Enter Language: ");
        String language = scanner.nextLine();

        System.out.print("Enter Sponsors (optional, press Enter to skip): ");
        String sponsors = scanner.nextLine();

        System.out.print("Enter Rating: ");
        double rating = scanner.nextDouble();
        scanner.nextLine();

        mediaStreamingService.addPodcast(podcastId, podcastName, totalSubscribers, country, episodeCount, genres, language, sponsors, rating);
    }

    //assign artist to the record label
    private void assignArtistsToRecordLabels(MediaStreamingService mediaStreamingService, Scanner scanner) {
        System.out.println("Enter artist name: ");
        String artistName = scanner.next();
        scanner.nextLine();
        System.out.println("Enter record label name: ");
        String recordLabelName = scanner.nextLine();


        int recordLabelId = mediaStreamingService.getRecordLabelIdByName(recordLabelName);
        if (recordLabelId != -1) {
            mediaStreamingService.updateArtistRecordLabel(artistName, recordLabelId);
        } else {
            System.out.println("Record label not found. Please try again.");
        }

    }
    //submenu for managment of artists
    private void manageArtistsMenu(MediaStreamingService mediaStreamingService, Scanner scanner) {

        int choice;
        do {
            System.out.println("********************************");
            System.out.println("Manage Artists Menu");
            System.out.println("1. Add Artist");
            System.out.println("2. Update Artist");
            System.out.println("3. Delete Artist");
            System.out.println("0. Back to Information Processing Menu");
            System.out.print("Enter your choice: ");

            choice = scanner.nextInt();

            scanner.nextLine(); // Consume the newline character
            System.out.println("********************************");

            switch (choice) {
                case 1:
                    System.out.print("Enter artist_id: ");
                    int artist_id = scanner.nextInt();
                    System.out.println("Enter artist_name: ");
                    String artist_name = scanner.next();
                    System.out.println("Enter status (active/retired): ");
                    String status = scanner.next();
                    System.out.println("Enter monthly_listener: ");
                    int monthly_listener = scanner.nextInt();
                    System.out.println("Enter type (composer/musician/Band): ");
                    String type = scanner.next();
                    System.out.println("Enter country: ");
                    String country = scanner.next();
                    System.out.println("Enter primary_genre: ");
                    String primary_genre = scanner.next();


                    mediaStreamingService.addArtist(artist_id, artist_name, status, monthly_listener, type, country, primary_genre);



                    break;

                case 2:
                    System.out.print("Enter artist_id to update: ");
                    int updateArtistId = scanner.nextInt();
                    scanner.nextLine();

                    System.out.print("Enter new artist_name (leave blank to keep current name): ");
                    String newArtistName = scanner.nextLine();
                    if (newArtistName.isEmpty()) {
                        newArtistName = null;
                    }

                    System.out.print("Enter new status (active/retired, leave blank to keep current status): ");
                    String newStatus = scanner.nextLine();
                    if (newStatus.isEmpty()) {
                        newStatus = null;
                    }

                    System.out.print("Enter new monthly_listener (leave blank to keep current value): ");
                    String newMonthlyListenerInput = scanner.nextLine();
                    Integer newMonthlyListener = null;
                    if (!newMonthlyListenerInput.isEmpty()) {
                        newMonthlyListener = Integer.parseInt(newMonthlyListenerInput);
                    }

                    System.out.print("Enter new type (composer/musician/Band, leave blank to keep current type): ");
                    String newType = scanner.nextLine();
                    if (newType.isEmpty()) {
                        newType = null;
                    }

                    System.out.print("Enter new country (leave blank to keep current country): ");
                    String newCountry = scanner.nextLine();
                    if (newCountry.isEmpty()) {
                        newCountry = null;
                    }

                    System.out.print("Enter new primary_genre (leave blank to keep current primary_genre): ");
                    String newPrimaryGenre = scanner.nextLine();
                    if (newPrimaryGenre.isEmpty()) {
                        newPrimaryGenre = null;
                    }

                    mediaStreamingService.updateArtist(updateArtistId, newArtistName, newStatus, newMonthlyListener, newType, newCountry, newPrimaryGenre);
                    break;
                case 3:
                    System.out.print("Enter artist_id to delete: ");
                    int deleteArtistId = scanner.nextInt();
                    scanner.nextLine();
                    mediaStreamingService.deleteArtist(deleteArtistId);
                    break;
            }
        } while (choice != 0);
    }
    //add record label
    private void addRecordLabel(MediaStreamingService mediaStreamingService,Scanner scanner) {
        System.out.print("Enter Record Label Id: ");
        int recordLabelId = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        System.out.print("Enter Record Label Name: ");
        String recordLabelName = scanner.nextLine();

        mediaStreamingService.addRecordLabel(recordLabelId,recordLabelName);
    }

    //Display all podcast episodes
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

    //display all podcast hosts
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

    //display all artist
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


    //display all songs
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


    //sub menu for mangement of songs
    private void manageSongsMenu(MediaStreamingService mediaStreamingService, Scanner scanner) {
        int choice;
        do {
            System.out.println("********************************");

            System.out.println("Manage Songs Menu");
            System.out.println("1. Add Song");
            System.out.println("2. Update Song");
            System.out.println("3. Delete Song");
            System.out.println("0. Back to Information Processing Menu");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();

            System.out.println("********************************");

            switch (choice) {
                case 1:
                    System.out.print("Enter Song Id: ");
                    int songId = scanner.nextInt();
                    scanner.nextLine();

                    System.out.print("Enter Song Title: ");
                    String songTitle = scanner.nextLine();

                    System.out.print("Enter Duration: ");
                    String duration = scanner.nextLine();

                    System.out.print("Enter Genres: ");
                    String genres = scanner.nextLine();

                    System.out.print("Enter Play Count: ");
                    int playCount = scanner.nextInt();
                    scanner.nextLine();

                    System.out.print("Enter Language: ");
                    String language = scanner.nextLine();

                    System.out.print("Enter Royalty Rate: ");
                    double royaltyRate = scanner.nextDouble();
                    scanner.nextLine();

                    System.out.print("Enter Release Date: ");
                    String releaseDate = scanner.nextLine();

                    System.out.print("Enter Release Country: ");
                    String releaseCountry = scanner.nextLine();

                    System.out.print("Enter Album Name (optional, press Enter to skip): ");
                    String albumNameInput = scanner.nextLine();
                    Integer albumId = null;
                    if (!albumNameInput.isEmpty()) {
                        albumId = mediaStreamingService.getAlbumIdByName(albumNameInput);
                        if (albumId == -1) {
                            System.out.println("Album not found. Please try again.");
                            continue;
                        }
                    }

                    mediaStreamingService.addSong(songId, songTitle, duration, genres, playCount, language, royaltyRate, releaseDate, releaseCountry, albumId);

                    System.out.print("Enter number of artists performing the song: ");
                    int numberOfArtists = scanner.nextInt();
                    scanner.nextLine();

                    for (int i = 0; i < numberOfArtists; i++) {
                        System.out.print("Enter Artist Name (Artist " + (i + 1) + "): ");
                        String artistName = scanner.nextLine();
                        int artistId = mediaStreamingService.getArtistIdByName(artistName);

                        if (artistId == -1) {
                            System.out.println("Artist not found. Please try again.");
                            i--; // Repeat the loop for the same artist
                            continue;
                        }

                        System.out.print("Is the artist a collaborator? (1 for main singer, 0 for collaborator): ");
                        String isCollaborator = scanner.nextLine();

                        mediaStreamingService.assignArtistToSong(isCollaborator, songId, artistId);
                    }

                    break;

                case 2:
                    System.out.print("Enter song_id to update: ");
                    int updateSongId = scanner.nextInt();
                    scanner.nextLine();

                    System.out.print("Enter new song_title (or press Enter to keep current title): ");
                    String newSongTitle = scanner.nextLine();
                    if (newSongTitle.isEmpty()) {
                        newSongTitle = null;
                    }

                    System.out.print("Enter new duration (or press Enter to keep current duration): ");
                    String newDuration = scanner.nextLine();
                    if (newDuration.isEmpty()) {
                        newDuration = null;
                    }

                    System.out.print("Enter new genres (or press Enter to keep current genres): ");
                    String newGenres = scanner.nextLine();
                    if (newGenres.isEmpty()) {
                        newGenres = null;
                    }

                    System.out.print("Enter new play_count (or press Enter to keep current play_count): ");
                    String playCountInput = scanner.nextLine();
                    Integer newPlayCount = null;
                    if (!playCountInput.isEmpty()) {
                        newPlayCount = Integer.parseInt(playCountInput);
                    }

                    System.out.print("Enter new language (or press Enter to keep current language): ");
                    String newLanguage = scanner.nextLine();
                    if (newLanguage.isEmpty()) {
                        newLanguage = null;
                    }

                    System.out.print("Enter new royalty_rate (or press Enter to keep current royalty_rate): ");
                    String royaltyRateInput = scanner.nextLine();
                    BigDecimal newRoyaltyRate = null;
                    if (!royaltyRateInput.isEmpty()) {
                        newRoyaltyRate = new BigDecimal(royaltyRateInput);
                    }

                    System.out.print("Enter new release_date (or press Enter to keep current release_date): ");
                    String newReleaseDate = scanner.nextLine();
                    if (newReleaseDate.isEmpty()) {
                        newReleaseDate = null;
                    }

                    System.out.print("Enter new release_country (or press Enter to keep current release_country): ");
                    String newReleaseCountry = scanner.nextLine();
                    if (newReleaseCountry.isEmpty()) {
                        newReleaseCountry = null;
                    }

                    mediaStreamingService.updateSong(updateSongId, newSongTitle, newDuration, newGenres, newPlayCount, newLanguage, newRoyaltyRate, newReleaseDate, newReleaseCountry);
                    break;
                case 3:
                    System.out.print("Enter Song Id to delete: ");
                    int deleteSongId = scanner.nextInt();
                    scanner.nextLine();

                    mediaStreamingService.deleteSong(deleteSongId);
                    break;
            }
        } while (choice != 0);
    }


    //submenu for the management of podcats hosts
    public void managePodcastHostsMenu(MediaStreamingService mediaStreamingService, Scanner scanner){
        int choice;
        do {
            System.out.println("********************************");

            System.out.println("Manage PodcastHosts Menu");
            System.out.println("1. Add PodcastHosts");
            System.out.println("2. Update PodcastHosts");
            System.out.println("3. Delete PodcastHosts");
            System.out.println("0. Back to Information Processing Menu");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            System.out.println("********************************");

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
                    System.out.print("Enter host_id to update: ");
                    int updateHostId = scanner.nextInt();
                    scanner.nextLine();

                    System.out.print("Enter new first_name (leave blank to keep current name): ");
                    String newFirstName = scanner.nextLine();
                    if (newFirstName.isEmpty()) {
                        newFirstName = null;
                    }

                    System.out.print("Enter new last_name (leave blank to keep current name): ");
                    String newLastName = scanner.nextLine();
                    if (newLastName.isEmpty()) {
                        newLastName = null;
                    }

                    System.out.print("Enter new phone (leave blank to keep current phone): ");
                    String newPhone = scanner.nextLine();
                    if (newPhone.isEmpty()) {
                        newPhone = null;
                    }

                    System.out.print("Enter new email (leave blank to keep current email): ");
                    String newEmail = scanner.nextLine();
                    if (newEmail.isEmpty()) {
                        newEmail = null;
                    }

                    System.out.print("Enter new city (leave blank to keep current city): ");
                    String newCity = scanner.nextLine();
                    if (newCity.isEmpty()) {
                        newCity = null;
                    }

                    mediaStreamingService.updatePodcastHost(updateHostId, newFirstName, newLastName, newPhone, newEmail, newCity);
                    break;

                case 3:
                    System.out.print("Enter host_id to delete: ");
                    int deleteHostId = scanner.nextInt();
                    scanner.nextLine();
                    mediaStreamingService.deletePodcastHost(deleteHostId);
                    System.out.println("Podcast host deleted successfully.");
                    break;
            }
        } while (choice != 0);


    }

    // add user
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
