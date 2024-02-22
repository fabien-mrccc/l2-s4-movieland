package moviesapp.model.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import moviesapp.controller.command_line.CLController;
import moviesapp.model.json.JsonReader;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class TheMovieDbAPI {

    private static final OkHttpClient client = new OkHttpClient();
    private final static String baseUrl = "https://api.themoviedb.org/3";
    private final static String apiKey = "&api_key=5e40bf6f22600832c99dbb5d52115269";
    private final static String language = "language=en-US";
    public static final TreeMap<String, String> GENRE_NAME_ID_MAP = new TreeMap<>();

    /**
     * Return a list of every registered genres
     * @return list of every genre
     */
    public StringBuilder genreList(){
        StringBuilder list = new StringBuilder();
        int i = 1;
        for (String genre : GENRE_NAME_ID_MAP.keySet()) {
            list.append("  • [").append(i).append("] ").append(genre).append("\n");
            i++;
        }
        return list;
    }

    /**
     * Call every necessary methods to create the appropriate url from the given parameters
     * @param title part of or complete title of a movie
     * @param minYear min year of release of movie
     * @param maxYear max year of release of movie
     * @param genres a list of genres
     * @param voteAverage the min vote average
     */
    public void searchMovies(String title, String minYear, String maxYear, List<String> genres, String voteAverage , String page){
        Request request = buildRequest(title, minYear, maxYear, genres, voteAverage , page );

        try {
            Response response = client.newCall(request).execute();
            reactToRequestResponse(response);

        } catch(IOException e){
            System.err.println("IOException e from 'Response response = client.newCall(request).execute();' ");
        }
    }

    /**
     * According to title value (null or not), choose to build API request from url with API search command or API discover command
     * @param title part of or complete title of a movie
     * @param minYear min year of release of movie
     * @param maxYear max year of release of movie
     * @param genres a list of genres
     * @param voteAverage the min vote average
     * @return the request built
     */
    private Request buildRequest(String title, String minYear, String maxYear, List<String> genres, String voteAverage, String page){
        String urlString;

        if(title.isEmpty()){
            urlString = urlBuilderDiscover(minYear, maxYear, genresToGenreIds(genres), voteAverage , page);
        }
        else{
            urlString = urlBuilderSearch(title, minYear, page);
        }
        return new Request.Builder().url(urlString).build();
    }

    /**
     * Return a list of genre ids from a list of genre
     * @param genres a list of genres
     * @return a list of ids
     */
    public static List<String> genresToGenreIds(List<String> genres){
        if(genres == null){
            return null;
        }

        List<String> genreIds = new ArrayList<>();

        for(String genre : genres){
            genreIds.add(GENRE_NAME_ID_MAP.get(genre.toLowerCase(Locale.ROOT).trim()));
        }
        return genreIds;
    }

    /**
     * Return a list of genre from a list of genre ids
     * @param genresIds a list of genre ids
     * @return genres
     */
    public static List<String> genreIdsToGenres(List<String> genresIds){
        if(genresIds == null){
            return null;
        }

        List<String> genres = new ArrayList<>();

        for (Map.Entry<String, String> genreData : GENRE_NAME_ID_MAP.entrySet()) {
            if(genresIds.contains(genreData.getValue())){
                genres.add(genreData.getKey());
            }
        }
        return genres;
    }

    /**
     * Return an url using API discover command from the given parameters
     * @param minYear min release year of a film
     * @param maxYear max release year of a film
     * @param genreIds list of genres of a film
     * @param voteAverage minimum vote average of a film
     * @return the desired url based on given parameters
     */
    private String urlBuilderDiscover(String minYear, String maxYear, List<String> genreIds, String voteAverage, String page){
        StringBuilder urlBuilder = new StringBuilder(baseUrl + "/discover/movie?" + language);

        buildUrlWithYearSpan(urlBuilder, minYear, maxYear, minYear == null || minYear.isEmpty(), maxYear == null || maxYear.isEmpty());
        buildUrlWithGenres(urlBuilder, genreIds, genreIds == null || genreIds.isEmpty());
        buildUrlWithVoteAverage(urlBuilder, voteAverage, voteAverage == null || voteAverage.isEmpty());
        buildUrlWithPage(urlBuilder, page);
        return urlBuilder + apiKey;
    }

    /**
     * Append to urlBuilder string corresponding to the given year span argument if it is not empty
     * @param urlBuilder StringBuilder to modify
     * @param minYear of the movies to search with API with discover command
     * @param maxYear of the movies to search with API with discover command
     * @param isMaxYearEmpty flag to append to urlBuilder
     * @param isMinYearEmpty flag to append to urlBuilder
     */
    private void buildUrlWithYearSpan(StringBuilder urlBuilder, String minYear, String maxYear, boolean isMinYearEmpty, boolean isMaxYearEmpty){
        if(!isMinYearEmpty){
            urlBuilder.append("&primary_release_date.gte=").append(minYear).append("-01-01");
        }
        if(!isMaxYearEmpty){
            urlBuilder.append("&primary_release_date.lte=").append(maxYear).append("-12-31");
        }
    }

    private void buildUrlWithReleaseYear(StringBuilder urlBuilder, String releaseYear, boolean isReleaseYearEmpty){
        if(!isReleaseYearEmpty){
            urlBuilder.append("&primary_release_year=").append(releaseYear);
        }
    }

    /**
     * Append to UrlBuilder string corresponding to page
     * @param urlBuilder StringBuilder to modify
     * @param page the number of the page
     */
    private void buildUrlWithPage(StringBuilder urlBuilder, String page ) {
            urlBuilder.append("&page=").append(page);
        }

    /**
     * Append to urlBuilder string corresponding to genreIds argument if it is not empty
     * @param urlBuilder StringBuilder to modify
     * @param genreIds of the movies to search with API with discover command
     * @param isGenreEmpty flag to append to urlBuilder
     */
    private void buildUrlWithGenres(StringBuilder urlBuilder, List<String> genreIds, boolean isGenreEmpty){
        if(!isGenreEmpty){
            urlBuilder.append("&with_genres=");
            for(String genre : genreIds){
                urlBuilder.append(genre).append(",");
            }
            urlBuilder.deleteCharAt(urlBuilder.length() - 1);
        }
    }

    /**
     * Append to urlBuilder string corresponding to voteAverage argument if it is not empty
     * @param urlBuilder StringBuilder to modify
     * @param voteAverage of the movies to search with API with discover command
     * @param isVoteAverageEmpty flag to append to urlBuilder
     */
    private void buildUrlWithVoteAverage(StringBuilder urlBuilder, String voteAverage, boolean isVoteAverageEmpty){
        if(!isVoteAverageEmpty){
            urlBuilder.append("&vote_average.gte=").append(voteAverage);
        }
    }

    /**
     * Return an url using API search command from the given parameters
     * @param title title or part of a title of a movie
     * @param releaseYear release year of a movie
     * @return the desired url based on given parameters
     */
    private String urlBuilderSearch(String title, String releaseYear , String page){
        StringBuilder urlBuilder = new StringBuilder(baseUrl + "/search/movie?" + language + "&query=" + title);
        buildUrlWithReleaseYear(urlBuilder, releaseYear, releaseYear == null || releaseYear.isEmpty());
        buildUrlWithPage(urlBuilder, page);
        return urlBuilder + apiKey;
    }

    /**
     * Convert the request response to a JSON file if it is successful or print an error.
     * @param response from the API after a specific request
     */
    private void reactToRequestResponse(Response response){
        try{
            if(response.isSuccessful() && response.body() != null){
                String searchResult = response.body().string();
                searchResultFromRequestToFile(searchResult);
            }
            else{
                System.err.println("Error API request: " + response.code());
            }
        } catch (IOException e){
            System.err.println("IOException e from 'String searchResult = response.body().string();'");
        }
    }

    /**
     * Turns the response to an API request into a JSON file
     * @param searchResult response of the api request
     */
    public void searchResultFromRequestToFile(String searchResult){
        ObjectMapper mapper = JsonMapper.builder().build();

        try {
            ObjectNode node = mapper.readValue(searchResult, ObjectNode.class);

            try (FileWriter fileWriter = new FileWriter(CLController.apiFilePath, StandardCharsets.UTF_8)) {
                mapper.enable(SerializationFeature.INDENT_OUTPUT);
                mapper.writeValue(fileWriter, node);
            } catch (IOException e) {
                System.err.println("IOException from 'new FileWriter(...)' or 'mapper.writeValue(fileWriter, node)'");
            }

        } catch (JsonProcessingException e){
            System.err.println("JsonProcessingException from 'ObjectNode node = mapper.readValue(searchResult, ObjectNode.class);'");
        }
    }

    public void displayCatalog(int page){
        String url = urlBuilderCatalog(page);
        Request request = new Request.Builder().url(url).build();
        try {
            Response response = client.newCall(request).execute();
            reactToRequestResponse(response);

        } catch(IOException e){
            System.err.println("IOException e from 'Response response = client.newCall(request).execute();' ");
        }
    }

    private String urlBuilderCatalog(int page){
        return baseUrl + "/movie/popular?" + language + "&page=" + page + apiKey;
    }



    /** Update the genres.json then fill the static GENRE_ID_MAP with the genres located in genres.json
     * Update the genres.json then fill the static GENRE_ID_MAP with the genres located in genres.json
     */
    public static void fillGENRE_NAME_ID_MAP(){
        updateGenresFile();
        JsonReader jsonGenresReader = new JsonReader(CLController.genresFilePath);
        for(JsonNode genre : jsonGenresReader.getJsonGenres()){
            GENRE_NAME_ID_MAP.put(genre.get("name").asText(),genre.get("id").asText());
        }
    }

    /**
     * Update the genres.json with all genres from Tmdb
     */
    private static void updateGenresFile(){
        String url = baseUrl + "/genre/movie/list?language=en" + apiKey;

        Request request = new Request.Builder().url(url).build();

        try {
            Response response = client.newCall(request).execute();

            if(response.isSuccessful()){
                assert response.body() != null;
                String genresResult = response.body().string();
                requestToGenresFile(genresResult);
            }
            else{
                System.out.println("error :" + response.code());
            }
        }catch(IOException e){
            System.err.println("IOException e from 'Response response = client.newCall(request).execute();' ");
        }
    }

    /**
     * Turns the response of an api request into a json file filled with genres
     * @param result response of the api request
     */
    private static void requestToGenresFile(String result){
        try {
            ObjectMapper mapper = JsonMapper.builder().build();
            ObjectNode node = mapper.readValue(result, ObjectNode.class);
            try (FileWriter fileWriter = new FileWriter(CLController.genresFilePath, StandardCharsets.UTF_8)) {
                mapper.enable(SerializationFeature.INDENT_OUTPUT);
                mapper.writeValue(fileWriter, node);
            }
        }catch (IOException e){
            System.err.println("IOException from 'new FileWriter(...)' or 'mapper.writeValue(fileWriter, node)'");
        }
    }
}