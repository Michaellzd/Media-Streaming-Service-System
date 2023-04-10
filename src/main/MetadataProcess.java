package main;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class MetadataProcess {
    public static void metadataAndRecordsMenu(MediaStreamingService mediaStreamingService, Scanner scanner) {
        int choice;
        do {
            System.out.println("********************************");
            System.out.println("Metadata and Records Menu");
            System.out.println("1. Enter play count for songs/artists");
            System.out.println("2. Update play count for songs");
            System.out.println("3. Enter play count for podcast episodes");
            System.out.println("4. Update play count for podcast episodes");
            System.out.println("5. Update the count of monthly listeners for artists");
            System.out.println("6. Enter the total count of ratings for podcasts");
            System.out.println("7. Enter the total count of subscribers for podcasts");
            System.out.println("8. Update ratings for podcasts");
            System.out.println("9. Update the total count of subscribers for podcasts");
            System.out.println("10. Find songs given artist");
            System.out.println("11. Find songs given album");
            System.out.println("12. Find podcast episodes given podcast");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();

            MetadataService metadataService = new MetadataService(mediaStreamingService);

            System.out.println("********************************");

            switch (choice) {
                case 1:
                    enterSongPlayCount(metadataService, scanner);
                    break;
                case 2:
                    updateSongPlayCount(metadataService, scanner);
                    break;
                case 3:
                    enterPodcastEpisodeListeningCount(metadataService, scanner);
                    break;
                case 4:
                    updatePodcastEpisodeListeningCount(metadataService, scanner);
                    break;
                case 5:
                    updateArtistMonthlyListeners(metadataService, scanner);
                    break;
                case 6:
                    enterPodcastRatings(metadataService, scanner);
                    break;
                case 7:
                    enterPodcastSubscribers(metadataService, scanner);
                    break;
                case 8:
                    updateRating(metadataService, scanner);
                    break;
                case 9:
                    updatePodcastSubscribers(metadataService, scanner);
                    break;
                case 10:
                    findSongsGivenArtist(metadataService, scanner);
                    break;
                case 11:
                    findSongsGivenAlbum(metadataService, scanner);
                    break;
                case 12:
                    findPodcastEpisodesGivenPodcast(metadataService, scanner);
                    break;
                case 0:
                    // go back to main menu
                    return;
            }
        } while (choice != 0);

    }

    private static void enterPodcastSubscribers(MetadataService metadataService, Scanner scanner) {
    }

    private static void updateRating(MetadataService metadataService, Scanner scanner){
        System.out.print("Enter podcast id to update rating: ");
        int podcastId = scanner.nextInt();
        metadataService.updateRating(podcastId);

    }

    private static void updateArtistMonthlyListeners(MetadataService metadataService, Scanner scanner) {
        System.out.println("Enter the artist ID:");
        int artistId = scanner.nextInt();
        System.out.println("Enter the month (yyyy-MM):");
        String month = scanner.next();
        metadataService.updateMonthlyListenerForArtists(artistId, month);
    }
    private static void enterPodcastRatings(MetadataService metadataService, Scanner scanner){
        System.out.print("Enter podcast ID: ");
        int podcastId = scanner.nextInt();

        System.out.print("Enter user ID: ");
        int userId = scanner.nextInt();

        System.out.print("Enter podcast rating (out of 5): ");
        double rating = scanner.nextDouble();

        metadataService.addPodcastRatings(podcastId, userId, rating);

    }
    private static void updatePodcastSubscribers(MetadataService metadataService, Scanner scanner){
        System.out.print("Enter the podcast id: ");
        int podcastId = scanner.nextInt();
        metadataService.updateTotalCountOfSubscribers(podcastId);

    }
    private static void enterPodcastEpisodeListeningCount(MetadataService metadataService, Scanner scanner){

        System.out.println("Enter listener ID:");
        int listenerId = scanner.nextInt();
        System.out.println("Enter podcast episode ID:");
        int podcastEpisodeId = scanner.nextInt();
        System.out.println("Enter year and month (YYYY-MM format):");
        String yearMonth = scanner.next();
        System.out.println("Enter listen count:");
        int listenCount = scanner.nextInt();
        metadataService.addUserListenedPodcast(listenerId, podcastEpisodeId, yearMonth, listenCount);

    }
    private static void updatePodcastEpisodeListeningCount(MetadataService metadataService, Scanner scanner){
        System.out.println("Enter the podcast episode ID:");
        int episodeId = scanner.nextInt();
        System.out.println("Enter the date (yyyy-MM):");
        String date = scanner.next();
        metadataService.updatePodcastEpisodeListeningCount(episodeId, date);
    }

    private static void findPodcastEpisodesGivenPodcast(MediaStreamingService mediaStreamingService, Scanner scanner) {
        System.out.print("Enter the podcast name: ");
        scanner.nextLine();
        String podcastName = scanner.nextLine();

        List<String> episodeTitles = mediaStreamingService.findEpisodesByPodcast(podcastName);

        System.out.println("Episodes in the podcast " + podcastName + ":");
        for (String episodeTitle : episodeTitles) {
            System.out.println(episodeTitle);
        }
    }

    private static void findSongsGivenAlbum(MediaStreamingService mediaStreamingService, Scanner scanner) {
        System.out.print("Enter the album name: ");
        scanner.nextLine();
        String albumName = scanner.nextLine();

        List<String> songTitles = mediaStreamingService.findSongsByAlbum(albumName);

        System.out.println("Songs in the album " + albumName + ":");
        for (String songTitle : songTitles) {
            System.out.println(songTitle);
        }
    }

    private static void findSongsGivenArtist(MediaStreamingService mediaStreamingService, Scanner scanner) {
        System.out.print("Enter the artist name: ");
        scanner.nextLine();
        String artistName = scanner.nextLine();

        List<String> songTitles = mediaStreamingService.findSongsByArtist(artistName);

        System.out.println("Songs by " + artistName + ":");
        for (String songTitle : songTitles) {
            System.out.println(songTitle);
        }
    }

    private static void enterSongPlayCount(MetadataService metadataService,Scanner scanner){
        System.out.println("Enter the user ID:");
        int userId = scanner.nextInt();
        System.out.println("Enter the song ID:");
        int songId = scanner.nextInt();
        System.out.println("Enter month (YYYY-MM format):");
        String month = scanner.next();

        System.out.println("Enter the listened times:");
        int count = scanner.nextInt();
        metadataService.addUserListenedSong(userId, songId, month, count);
    }
    private static void updateSongPlayCount(MetadataService metadataService, Scanner scanner){
        System.out.println("Enter the song ID:");
        int songId = scanner.nextInt();
        System.out.println("Enter the date (yyyy-MM):");
        String date = scanner.next();
        metadataService.updateSongPlayCount(songId,date);
    }

}
