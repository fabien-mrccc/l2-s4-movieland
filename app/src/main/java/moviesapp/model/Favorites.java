package moviesapp.model;
import java.util.ArrayList;
import java.util.List;
public class Favorites {

    public static final Favorites instance = new Favorites();
    private final List<Movie> favorites;

    private Favorites(){
        favorites = new ArrayList<>();
    }

    /** return true if the list of favorites is empty, if not return false
     @return boolean
     **/
    public boolean isEmpty(){
        return favorites.isEmpty();
    }

    /** Add a film to the favorites of the user (only if it is not already in the list)
     @param movie the movie to add to the list
     **/
    public void add(Movie movie){
        if(!contains(movie)){
            favorites.add(movie);
        }
    }

    /**
     * Check if the favorites list contains a specific movie, returns false if the list is empty
     * @param movie the movie that we check if it is in our list
     * @return boolean
     */
    private boolean contains(Movie movie){
        if(isEmpty()){
            return false;
        }
        if(movie == null){
            return true;
        }
        return favorites.contains(movie);
    }

    /** Remove a film from the list of favorites
     @param movie the movie to remove from the list
     **/
    public void remove(Movie movie){
        try {favorites.remove(movie);}
        catch (UnsupportedOperationException e){
            System.out.println("This movie does not belong to your list of favorites.");
        }
    }
    @Override
    public String toString(){
        StringBuilder favoritesString = new StringBuilder();
        if(favorites.isEmpty()){
            favoritesString = new StringBuilder("Your list of favorites is empty.");
            return favoritesString.toString();
        }
        for(Movie movie: favorites){
            favoritesString.append(movie).append("\n");
        }
        return favoritesString.toString();
    }

    /**
     * Remove all the movies registered in user favorite list.
     */
    public void clear(){
        try{
            favorites.clear();
        }
        catch(UnsupportedOperationException e){
            System.out.println("The operation was unsuccessful.");
        }
    }

    /**
     * Add a group of movies to the user's favorite list by selecting only those
     * which are not already in it
     * @param movies: the movies that we want to add to the user favorite list
     */
    public void addAll(List<Movie> movies){
        if(movies == null){
            return;
        }
        favorites.addAll(moviesToAddToFavorites(movies));
    }
    /**
     * Filter the list of movies given on parameters by removing from the list
     * the movies already in the favorites
     * @return the list of movies to add to favorites (those which are not already in the favorites)
     * @param movies: the list of movies to add to favorites (those which are already
     *               in the favorites and those which are not)
     */
    private List<Movie> moviesToAddToFavorites(List<Movie> movies){
        List<Movie> moviesNotInFavoriteList = new ArrayList<>();
        for(Movie movie : movies){
            if(!favorites.contains(movie)){
                moviesNotInFavoriteList.add(movie);
            }
        }
        return moviesNotInFavoriteList;
    }
    /**
     * Remove a group of movies from the user's favorite list by selecting only those
     * which are already in it
     * @param movies: the movies that we want to remove from the favorites
     */
    public void removeAll(List<Movie> movies){
        if(movies == null){
            return;
        }
        favorites.removeAll(moviesToRemoveFromFavorites(movies));
    }
    /**
     * Filter the list of movies given on parameters by removing from the list
     * the movies not in the favorites
     * @return the list of movies to remove from favorites (only those which are in the favorites)
     * @param movies: the list of movies to remove from favorites (those which are in the favorites
     *             and those which are not)
     */
    private List<Movie> moviesToRemoveFromFavorites(List<Movie> movies){
        List<Movie> moviesInFavoriteList = new ArrayList<>();
        for(Movie movie : movies){
            if(favorites.contains(movie)){
                moviesInFavoriteList.add(movie);
            }
        }
        return moviesInFavoriteList;
    }
}