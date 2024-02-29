package moviesapp.controller.command_line;

import moviesapp.model.api.Genres;
import moviesapp.model.api.TheMovieDbAPI;
import moviesapp.model.exceptions.*;
import moviesapp.model.movies.Movies;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static moviesapp.model.api.Genres.GENRE_NAME_ID_MAP;
import static moviesapp.model.api.Genres.genresToGenreIds;
import static moviesapp.model.api.UrlRequestBuilder.*;
import static moviesapp.model.api.UrlRequestBuilder.maxAcceptableYearValue;
import static moviesapp.model.exceptions.IndexException.isValidIndex;
import static moviesapp.model.json.JsonReader.SEARCH_READER;
import static moviesapp.model.json.JsonReader.updateSearchReader;

public class CLSearch extends CLController {

    private final TheMovieDbAPI apiObject = new TheMovieDbAPI();

    /**
     * Display only the title, the year of release and the average note of every film in the catalog (popular films generated by API)
     */
    public void catalog(){
        apiObject.popularMovies("1");
        do{
            updateSearchReader();
            System.out.println("The most popular movies at the moment are listed below: \n" + SEARCH_READER.findAllMovies());
        } while(askPreviousOrNextPage(messageOfAskPreviousOrNextPage()));
    }

    /**
     * Ask title, a year span, vote average and genres information to the user to select a specific group of movies
     */
    void search(){
        String title = "";
        String singleYearOrMinYear = "";
        String maxYear ="";
        String minVoteAverage = "";
        List<String> genres = new ArrayList<>();

        boolean searchModeSuccess = false;

        while(!searchModeSuccess){
            try {
                searchMode = selectMode("Search mode: [0] Exit Search, ["+ searchModeSearch +"] With Title, ["+ searchModeDiscover +"] Without Title",
                        Arrays.asList("0", searchModeSearch,searchModeDiscover));
                searchModeSuccess = true;
            }
            catch (IndexException e ){
                System.out.println(e.getMessage());
            }
        }

        switch(searchMode){
            case "0" -> {
                return;
            }
            case "1" -> {
                while(title.isEmpty()){
                    title = askValue("Title of the movie (required): ");
                }
                singleYearOrMinYear = getYear();
                if(informationSent(title, singleYearOrMinYear, maxYear, minVoteAverage, genres)){
                    maxYear = SINGLE_MODE_KEYWORD;
                }
                else{
                    return;
                }
            }
            case "2" -> {
                String[] years = getYears();
                if (years != null && (!years[0].isEmpty() || !years[1].isEmpty())){
                    singleYearOrMinYear = years[0];
                    maxYear = years[1];
                }
                minVoteAverage = getMinVoteAverage(0,10);
                genres = genresToGenreIds(specifiedGenresByUser());
            }
            default -> {
                System.out.println(new SelectModeException().getMessage());
                return;
            }
        }
        try {
            if(informationSent(title, singleYearOrMinYear, maxYear, minVoteAverage, genres)){
                apiObject.searchMovies(title, singleYearOrMinYear, maxYear, genres, minVoteAverage, "1");
                do{
                    updateSearchReader();
                    Movies moviesFromSearch = SEARCH_READER.findAllMovies();
                    Movies.searchableMovie(moviesFromSearch);
                    System.out.println("\nYour list of movies found in your search: \n" + moviesFromSearch);

                } while(askPreviousOrNextPage(title, singleYearOrMinYear, maxYear, genres, minVoteAverage, messageOfAskPreviousOrNextPage()));
            }
        }
        catch (NoMovieFoundException e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * Return the user's specified genres
     * @return the user's specified genres
     */
    private List<String> specifiedGenresByUser(){
        List<String> genres = new ArrayList<>();

        if(askToConfirm("Do you want to specify one or more genres?")){
            System.out.print("\nList of genres: \n" + Genres.instance.getGenres());

            do{
                System.out.println("\nGenres already selected: " + genres);
                List<String> genreNames = new ArrayList<>(GENRE_NAME_ID_MAP.keySet());
                String genreSelected = selectGenreByIndex(genreNames);

                if (GENRE_NAME_ID_MAP.containsKey(genreSelected) && !genres.contains(genreSelected)) {
                    genres.add(genreSelected);
                }
                else {
                    System.out.println("\n| No genre added. Please enter a valid genre that is not already selected.");
                }
            } while(askToConfirm("Do you want to add more genres?"));
        }
        return genres;
    }

    /**
     * Retrieves the release year based on user input.
     * @return An array containing the selected release year.
     */
    private String getYear() {

        String yearOfReleaseOption = selectModeTry("Select release year option: [0] Skip, [1] Specify", Arrays.asList("0","1"));

        switch (yearOfReleaseOption){
            case "0" -> {
                return "";
            }
            case "1" -> {
                return getYearTry("Release year [" + minAcceptableYearValue + "-" + maxAcceptableYearValue + "]: ");
            }
            default -> {
                System.out.println(new SelectModeException().getMessage());
                return "";
            }
        }
    }

    /**
     * Prompts the user to input a release year within a specified range.
     *
     * @return the release year entered by the user
     */
    private String getYearTry(String message){
        String releaseYear = "";
        boolean getYearSuccess = false;

        while (!getYearSuccess){
            try {
                releaseYear = askValue(message);

                if(!releaseYear.isEmpty()) {
                    validateValueInterval(convertAsInt(releaseYear), minAcceptableYearValue, maxAcceptableYearValue);
                    getYearSuccess = true;
                }
                getYearSuccess = true;

            } catch (IntervalException | NotAnIntegerException e){
                System.out.println(e.getMessage());
            }
        }

        return releaseYear;
    }

    /**
     * Checks if no search information is provided.
     *
     * @param title             The title of the movie.
     * @param singleYearOrMinYear   The single year or minimum year in the range.
     * @param maxYear           The maximum year in the range.
     * @param minVoteAverage    The minimum vote average.
     * @param genres            The list of genres.
     * @return True if no information is sent for the search, false otherwise.
     */
    private boolean informationSent(String title, String singleYearOrMinYear, String maxYear, String minVoteAverage, List<String> genres){
        if(title.isEmpty() && singleYearOrMinYear.isEmpty() && maxYear.isEmpty() && minVoteAverage.isEmpty() && genres.isEmpty()){
            System.out.println("\n| No information sent. \n| Please give me more details for your next search.");
            return false;
        }
        return true;
    }

    /**
     * Retrieves the release years based on user input.
     * @return An array containing the selected release years. Null if skipped.
     */
    private String[] getYears(){

        String yearOfReleaseOption = selectModeTry("Select release year option: [0] Skip, [1] Single, [2] Range (min-max)", Arrays.asList("0","1","2"));

        String[] yearsFromTheUser = new String[2];

        switch (yearOfReleaseOption){
            case "0" -> {
                return null;
            }
            case "1" -> {
                yearsFromTheUser[0] = getYearTry("Release year [" + minAcceptableYearValue + "-" + maxAcceptableYearValue + "]: ");
                yearsFromTheUser[1] = SINGLE_MODE_KEYWORD;
            }
            case "2" -> {
                yearsFromTheUser[0] = getYearTry("Min release year [ ≧ " + minAcceptableYearValue + "]: ");
                yearsFromTheUser[1] = getYearTry("Max release year [ ≦ " + maxAcceptableYearValue + "]: ");
            }
            default -> {
                System.out.println(new SelectModeException().getMessage());
                return getYears();
            }
        }

        return yearsFromTheUser;
    }

    /**
     * Validates if a given value falls within the acceptable interval defined by minimum and maximum values.
     *
     * @param value               The value to be validated.
     * @param minAcceptableValue  The minimum acceptable value of the interval.
     * @param maxAcceptableValue  The maximum acceptable value of the interval.
     * @throws IntervalException  If the value is not within the acceptable interval.
     */
    private void validateValueInterval(double value, double minAcceptableValue, double maxAcceptableValue) throws IntervalException {

        boolean validNumber = value >= minAcceptableValue && value <= maxAcceptableValue;

        if (!validNumber){
            throw new IntervalException();
        }
    }

    /**
     * Retrieves the minimum vote average for a movie.
     * @return The minimum vote average provided by the user.
     */
    private String getMinVoteAverage(int minAcceptableValue, int maxAcceptableValue){

        String minVoteAverage = "";
        int minVoteAverageValue = 0;
        boolean getMinVoteAverageSuccess = false;

        while (!getMinVoteAverageSuccess) {

            try {
                minVoteAverage = askValue("Movie's minimum rate [" + minAcceptableValue + "-" + maxAcceptableValue + "]: ");

                if(!minVoteAverage.isEmpty()) {
                    minVoteAverageValue = Integer.parseInt(minVoteAverage);
                }

                getMinVoteAverageSuccess = true;
            }
            catch (NumberFormatException e) {
                System.out.println("\n| Pleaser enter a value that corresponding to an integer.");
            }
        }

        if(!minVoteAverage.isEmpty()){

            try{
                validateValueInterval(minVoteAverageValue, minAcceptableValue, maxAcceptableValue);
            }
            catch (IntervalException e) {
                System.out.println(e.getMessage());
                return getMinVoteAverage(minAcceptableValue,maxAcceptableValue);
            }
        }
        return minVoteAverage;
    }



    /**
     * Selects a genre from the list based on the provided index.
     * @param genres The list of genres to select from.
     * @return The selected movie.
     */
    private String selectGenreByIndex(List<String> genres) {
        try {
            int index = retrieveAsInt("Enter genre index: ") - 1;
            isValidIndex(index, genres.size());
            return genres.get(index);
        }
        catch (IndexException e){
            System.out.println(new IndexException().getMessage());
            return selectGenreByIndex(genres);
        }
    }

    /**
     * Ask the user to select an interaction with page management system (previous, next, stop)
     * @param title from the precedent search
     * @param minYear from the precedent search
     * @param maxYear from the precedent search
     * @param genres from the precedent search
     * @param minVoteAverage from the precedent search
     * @param message the message to print to the user to interact with page management system
     * @return the user's answer
     */
    private boolean askPreviousOrNextPage(String title, String minYear, String maxYear, List<String> genres, String minVoteAverage , String message){
        String response = askValue(message);

        switch (response) {
            case "3" -> {
                int pageNumber = Integer.parseInt(askValue("Enter page number: "));
                if (pageNumber >= 1 && pageNumber <= SEARCH_READER.numberOfPagesOfMoviesInJson()){
                    apiObject.searchMovies(title, minYear, maxYear, genres, minVoteAverage, String.valueOf(pageNumber));
                    return true;
                }
                System.out.println("\n| Page number unavailable.");
            }
            case "2" -> {
                if(SEARCH_READER.getPageInJson() < SEARCH_READER.numberOfPagesOfMoviesInJson()){
                    apiObject.searchMovies(title, minYear, maxYear, genres, minVoteAverage, String.valueOf(SEARCH_READER.getPageInJson() + 1));
                    return true;
                }
                System.out.println("\n| There is no next page.");
            }
            case "1" -> {
                if(SEARCH_READER.getPageInJson() > 1){
                    apiObject.searchMovies(title, minYear, maxYear, genres, minVoteAverage, String.valueOf(SEARCH_READER.getPageInJson() - 1));
                    return true;
                }
                System.out.println("\n| There is no precedent page.");
            }
            case "0" -> {
                return false;
            }
            default -> System.out.println("\n| Please enter a valid option.");
        }

        return askPreviousOrNextPage(title, minYear, maxYear, genres, minVoteAverage, message);
    }

    /**
     * Return the message corresponding to the page management user interactive
     * @return the message corresponding to the page management user interactive
     */
    private String messageOfAskPreviousOrNextPage(){
        return "Choose your action: [0] Continue/Leave command, [1] Previous Page, [2] Next Page, [3] Specify Page | page ("
                + SEARCH_READER.getPageInJson()
                + "/"
                + SEARCH_READER.numberOfPagesOfMoviesInJson() +")";
    }



    /**
     * Ask the user to select an interaction with page management system (previous, next, stop)
     * @param message the message to print to the user to interact with page management system
     * @return {@code true} if the user select something else than stopping the page management system
     */
    private boolean askPreviousOrNextPage(String message){
        String response = askValue(message);

        switch (response) {
            case "3" -> {
                int pageNumber = Integer.parseInt(askValue("Enter page number: "));
                if (pageNumber >= 1 && pageNumber <= SEARCH_READER.numberOfPagesOfMoviesInJson()){
                    apiObject.popularMovies(String.valueOf(pageNumber));
                    System.out.println();
                    return true;
                }
                System.out.println("\n| Page number unavailable.");
            }
            case "2" -> {
                if(SEARCH_READER.getPageInJson() < SEARCH_READER.numberOfPagesOfMoviesInJson()){
                    apiObject.popularMovies( String.valueOf(SEARCH_READER.getPageInJson() + 1));
                    System.out.println();
                    return true;
                }
                System.out.println("\n| There is no next page.");
            }
            case "1" -> {
                if(SEARCH_READER.getPageInJson() > 1){
                    apiObject.popularMovies(String.valueOf(SEARCH_READER.getPageInJson() - 1));
                    System.out.println();
                    return true;
                }
                System.out.println("\n| There is no precedent page.");
            }
            case "0" -> {
                return false;
            }
            default -> System.out.println("\n| Please enter a valid option.");
        }

        return askPreviousOrNextPage(message);
    }

    /**
     * Converts the given string value to an integer.
     *
     * @param valueToConvert the string value to convert to an integer
     * @return the integer value of the converted string
     * @throws NotAnIntegerException if the string cannot be parsed as an integer
     */
    private int convertAsInt(String valueToConvert) throws NotAnIntegerException {
        try{
            return Integer.parseInt(valueToConvert);
        }
        catch (NumberFormatException e){
            throw new NotAnIntegerException();
        }
    }
}
