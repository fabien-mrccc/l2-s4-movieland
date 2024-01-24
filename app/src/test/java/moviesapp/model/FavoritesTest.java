package moviesapp.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class FavoritesTest {
    private static Favorites favorites;
    private final Movie movie1 = new Movie();
    private final Movie movie2 = new Movie();
    private final Movie movie3 = new Movie();

    @BeforeAll
    static void setupBeforeAll(){
        favorites = Favorites.instance;
    }

    @BeforeEach
    void setupBeforeEach(){
        if(!favorites.isEmpty()){
            favorites.clear();
        }
    }

    @Test
    void testAdd() {
        assertThat(favorites.toString().equals("Your list of favorites is empty.")).isTrue();
        favorites.add(movie1);
        assertThat(favorites.toString().equals(movie1 + "\n")).isTrue();
        favorites.add(movie2);
        assertThat(favorites.toString().equals(
                movie1 + "\n" + movie2 + "\n")).isTrue();
        favorites.add(movie3);
        assertThat(favorites.toString().equals(
                movie1 + "\n" + movie2 + "\n" + movie3 + "\n")).isTrue();
        favorites.add(null);
        assertThat(favorites.toString().equals(
                movie1 + "\n" + movie2 + "\n" + movie3 + "\n")).isTrue();
        favorites.add(movie3);
        assertThat(favorites.toString().equals(
                movie1 + "\n" + movie2 + "\n" + movie3 + "\n")).isTrue();
    }

    @Test
    void testToString(){
        assertThat(favorites.toString().equals("Your list of favorites is empty.")).isTrue();
        favorites.add(movie1);
        assertThat(favorites.toString().equals(movie1 + "\n")).isTrue();
        favorites.add(movie2);
        favorites.add(movie3);
        assertThat(favorites.toString().equals(
                movie1 + "\n" + movie2 + "\n" + movie3 + "\n")).isTrue();
        favorites.remove(movie2);
        assertThat(favorites.toString().equals(
                movie1 + "\n" + movie3 + "\n")).isTrue();
        favorites.remove(movie1);
        favorites.remove(movie3);
        assertThat(favorites.toString().equals("Your list of favorites is empty.")).isTrue();
    }
    @Test
    void testRemove(){
        Movie movie1 = new Movie();
        Movie movie2 = new Movie();
        favorites.add(movie1);
        favorites.add(movie2);
        favorites.remove(movie1);
        assertThat(favorites.toString()).isEqualTo(movie2.toString());
        favorites.remove(movie2);
        assertThat(favorites.toString()).isEqualTo("Your list of favorites is empty.");
        assertThatThrownBy(() -> favorites.remove(movie2))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("This movie does not belong to your list of favorites");
    }
}