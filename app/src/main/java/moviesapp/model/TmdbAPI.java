package moviesapp.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class TmdbAPI {

    private final String fileName = System.getProperty("user.dir") + "/src/main/java/moviesapp/model/api-results.json";
    private final OkHttpClient client = new OkHttpClient();
    private final static String baseUrl = "https://api.themoviedb.org/3";
    private final static String apiKey = "&api_key=5e40bf6f22600832c99dbb5d52115269";
    private final static String language = "language=en-US";
    public static final TreeMap<String, String> GENRE_ID_MAP;

    static {
        GENRE_ID_MAP = new TreeMap<>();
        GENRE_ID_MAP.put("action", "28");
        GENRE_ID_MAP.put("adventure", "12");
        GENRE_ID_MAP.put("animation", "16");
        GENRE_ID_MAP.put("comedy", "35");
        GENRE_ID_MAP.put("crime", "80");
        GENRE_ID_MAP.put("documentary", "99");
        GENRE_ID_MAP.put("drama", "18");
        GENRE_ID_MAP.put("family", "10751");
        GENRE_ID_MAP.put("fantasy", "14");
        GENRE_ID_MAP.put("history", "36");
        GENRE_ID_MAP.put("horror", "27");
        GENRE_ID_MAP.put("music", "10402");
        GENRE_ID_MAP.put("romance", "9648");
        GENRE_ID_MAP.put("mystery", "10749");
        GENRE_ID_MAP.put("science fiction", "878");
        GENRE_ID_MAP.put("tv movie", "10770");
        GENRE_ID_MAP.put("thriller", "53");
        GENRE_ID_MAP.put("war", "10752");
        GENRE_ID_MAP.put("western", "37");
    }

    /**
     * Return a list of every registered genres
     * @return list of every genre
     */
    public StringBuilder genreList(){
        StringBuilder list = new StringBuilder();
        for (String genre : GENRE_ID_MAP.keySet()) {
            list.append("  -").append(genre).append("\n");
        }
        return list;
    }

    /**
     * Call every necessary methods to create the appropriate url from the given parameters
     * @param title part of or complete title of a movie
     * @param releaseYear year of release of movie
     * @param genres a list of genres
     * @param voteAverage the min vote average
     */
    public void searchMovie(String title, String releaseYear, List<String> genres, String voteAverage){
        Request request = buildRequest(title, releaseYear, genres, voteAverage);

        try {
            Response response = client.newCall(request).execute();

            if(response.isSuccessful() && response.body() != null){
                String searchResult = response.body().string();
                requestToFile(searchResult);
            }
            else{
                System.out.println("Error: " + response.code());
            }
        }catch(IOException e){
            System.err.println("IOException e from 'Response response = client.newCall(request).execute();' or 'String searchResult = response.body().string();' ");
        }
    }

    /**
     * According to title value (null or not), choose to build API request from url with API search command or API discover command
     * @param title part of or complete title of a movie
     * @param releaseYear year of release of movie
     * @param genres a list of genres
     * @param voteAverage the min vote average
     * @return the request built
     */
    private Request buildRequest(String title, String releaseYear, List<String> genres, String voteAverage){
        String urlString;

        if(title.isEmpty()){
            urlString = urlBuilderDiscover(releaseYear, genresToGenreIds(genres), voteAverage);
        }
        else{
            urlString = urlBuilderMovie(title, releaseYear);
        }
        return new Request.Builder().url(urlString).build();
    }

    /**
     * Return a list of genre ids from a list of genre
     * @param genres a list of genres
     * @return a list of ids
     */
    private List<String> genresToGenreIds(List<String> genres){
        List<String> genreIds = new ArrayList<>();

        for(String genre : genres){
            genreIds.add(GENRE_ID_MAP.get(genre.toLowerCase(Locale.ROOT).trim()));
        }
        return genreIds;
    }

    /**
     * Return an url using API discover command from the given parameters
     * @param releaseYear release year of a film
     * @param genreIds list of genres of a film
     * @param voteAverage minimum vote average ofa film
     * @return the desired url based on given parameters
     */
    private String urlBuilderDiscover(String releaseYear, List<String> genreIds, String voteAverage){
        boolean isGenreEmpty = genreIds.isEmpty();
        boolean isVoteAverageEmpty = voteAverage.isEmpty();

        StringBuilder urlBuilder = new StringBuilder(baseUrl + "/discover/movie?" + language);

        buildUrlWithReleaseYear(urlBuilder, releaseYear, releaseYear.isEmpty());

        if(!isGenreEmpty){
            urlBuilder.append("&with_genres=");
            for (int i = 0 ; i < genreIds.size(); i++) {
                urlBuilder.append(genreIds.get(i));
                if (i < genreIds.size() - 1) {
                    urlBuilder.append(",");
                }
            }
        }
        if(!isVoteAverageEmpty){
            urlBuilder.append("&vote_average.gte=").append(voteAverage);
        }
        return urlBuilder + apiKey;
    }

    /**
     * Append to urlBuilder string corresponding to releaseYear argument if it is not empty
     * @param urlBuilder StringBuilder to modify
     * @param releaseYear of the movies to search with API with discover command
     * @param isReleaseYearEmpty flag to append to urlBuilder
     */
    private void buildUrlWithReleaseYear(StringBuilder urlBuilder, String releaseYear, boolean isReleaseYearEmpty){
        if(!isReleaseYearEmpty){
            urlBuilder.append("&primary_release_year=").append(releaseYear);
        }
    }

    /**
     * return an url from given parameters
     * @param title title or part of a title of a movie
     * @param year year of release of a movie
     * @return the desired url from given url
     */
    private String urlBuilderMovie(String title, String year){
        boolean noYear = year.isEmpty();
        if(!noYear){
            return baseUrl + "/search/movie?query=" + title + "&primary_release_year=" + year + language + apiKey;
        }
        return baseUrl + "/search/movie?" + language + "&query=" + title + apiKey;
    }

    /**
     * turns the response of an api request into a json file
     * @param result response of the api request
     */
    private void requestToFile(String result){
        try {
            ObjectMapper mapper = JsonMapper.builder().build();
            ObjectNode node = mapper.readValue(result, ObjectNode.class);
            try (FileWriter fileWriter = new FileWriter(fileName, StandardCharsets.UTF_8)) {
                mapper.enable(SerializationFeature.INDENT_OUTPUT);
                mapper.writeValue(fileWriter, node);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
