package moviesapp.model;
import com.fasterxml.jackson.databind.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;

public class JsonReader extends MovieFinder {
    private final File jsonFile;
    private final ObjectMapper objectMapper;
    private final JsonNode jsonMovies ;
    private final JsonNode jsonGenres;

    public JsonReader(String path){
        jsonFile = new File(path);
        objectMapper = new ObjectMapper();
        jsonMovies = getJsonMoviesNode() ;
        jsonGenres = getJsonGenresNode();
    }

    /**
     * Convert a jsonNode to a Movie
     * @param jsonNode: the jsonNode to convert to a movie
     * @return the jsonNode converted to a movie
     */
    private Movie jsonNodeToMovie(JsonNode jsonNode) {

        return new Movie(
                jsonNode.get("adult").asBoolean(),
                jsonNode.get("backdrop_path").asText(),
                getGenresFromJson(jsonNode),
                jsonNode.get("id").asText(),
                jsonNode.get("original_language").asText(),
                jsonNode.get("original_title").asText(),
                jsonNode.get("overview").asText(),
                jsonNode.get("popularity").asDouble(),
                jsonNode.get("poster_path").asText(),
                jsonNode.get("release_date").asText(),
                jsonNode.get("title").asText(),
                jsonNode.get("video").asBoolean(),
                jsonNode.get("vote_average").asDouble(),
                jsonNode.get("vote_count").asInt()
        );
    }

    /**
     * Browse genre_ids jsonNode to collect values to store in a list
     * @param jsonNode to browse
     * @return the list of genre identifiers
     */
    private List<String> getGenresFromJson(JsonNode jsonNode){
        JsonNode jsonGenreIds = jsonNode.get("genre_ids");
        List<String> genreIds = new ArrayList<>();

        for(JsonNode jsonGenreId: jsonGenreIds){
            genreIds.add(jsonGenreId.asText());
        }
        return genreIds;
    }

    @Override
    public void findMoviesByCriteria(Movies movies, String title, String releaseYear, List<String> genres, String voteAverage) {
        for (JsonNode movie : jsonMovies) {
            boolean titleCondition = title == null ||
                    title.isEmpty() ||
                    movie.get("original_title").asText().toLowerCase().contains(title.toLowerCase());
            boolean yearCondition = releaseYear == null ||
                    releaseYear.isEmpty() ||
                    movie.get("release_date").asText().startsWith(releaseYear);
            boolean genreCondition =
                    genres == null ||
                    genres.isEmpty() ||
                    movieContainsAnyGenre(movie, genres);
            boolean voteCondition =
                    voteAverage == null ||
                    voteAverage.isEmpty() ||
                    Double.parseDouble(voteAverage) <= movie.get("vote_average").asDouble();

            if (titleCondition && yearCondition && genreCondition && voteCondition) {
                movies.add(jsonNodeToMovie(movie));
            }
        }
    }

    /**
     * Checks if the given movie matches any of the provided genre IDs.
     * @param movie The JSON node representing the movie.
     * @param genres The list of genre to match against.
     * @return {@code true} if the movie matches any of the provided genre IDs, {@code false} otherwise.
     */
    private boolean movieContainsAnyGenre(JsonNode movie, List<String> genres) {
        List<String> genreIds = TmdbAPI.genresToGenreIds(genres);

        for (JsonNode genreIdNode : movie.get("genre_ids")) {
            String genreId = genreIdNode.asText();
            if (genreIds.contains(genreId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return a list of Movie containing there information from the JSON file
     * @return the list of Movie contained in the JSON File
     */
    public Movies findAllMovies(){
        Movies movieList = new Movies();
        if(jsonMovies != null){
            for(JsonNode jsonMovie : jsonMovies ){
                movieList.add(jsonNodeToMovie(jsonMovie));
            }
            return movieList;
        }
        return null;
    }

    /**
     * Return the origin jsonNode from our default jsonFile with exception management
     * @return the origin jsonNode from our default jsonFile
     */
    private JsonNode getJsonMoviesNode(){
        return getSpecificJsonNode("results");
    }

    /**
     * Return the number of total pages of movies available in the json file of the class
     * @return the number of total pages of movies available in the json file of the class
     */
    public int numberOfPagesOfMoviesInJson(){
        JsonNode totalPagesNode = getSpecificJsonNode("total_pages");

        if(totalPagesNode != null){
            return totalPagesNode.asInt();
        }

        return 0;
    }

    /**
     * return the actual page générate in api-resut
     * @return the actual page
     */
    public int getPageInJson(){
        JsonNode pageNode = getSpecificJsonNode("page");

        if(pageNode!= null){
            return pageNode.asInt();
        }

        return -1;
    }

    /**
     * Return a specific jsonNode from our json file of the class
     * @return a specific jsonNode from our json file of the class
     */
    private JsonNode getSpecificJsonNode(String jsonNodeName){
        try{
            return objectMapper.readTree(jsonFile).get(jsonNodeName);
        }
        catch (IOException e) {
            System.err.println("IOException: objectMapper.readTree(jsonFile) exception");
        }
        catch (NullPointerException e){
            System.err.println("NullPointerException: objectMapper.readTree(jsonFile).get(\"results\") exception");
        }
        return null;
    }

    /**
     * Return the origin jsonNode from our default jsonFile with exception management
     * @return the origin jsonNode from our default jsonFile
     */
    private JsonNode getJsonGenresNode() {
        try {
            return objectMapper.readTree(jsonFile).get("genres");
        } catch (IOException e) {
            System.err.println("IOException: objectMapper.readTree(jsonFile) exception");
        } catch (NullPointerException e) {
            System.err.println("NullPointerException: objectMapper.readTree(jsonFile).get(\"genres\") exception");
        }
        return null;
    }

    public JsonNode getJsonGenres(){
        return jsonGenres;
    }

    /**
     * Checks if the specified file is empty.
     * @param filePath The path to the file to check.
     * @return {@code true} if the file is empty or does not exist, {@code false} otherwise.
     */
    public static boolean isFileEmpty(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            return reader.readLine() == null;
        } catch (IOException e) {
            System.err.println("IOException from public static boolean isFileEmpty(String filePath) in JsonReader.java");
            return true;
        }
    }

}