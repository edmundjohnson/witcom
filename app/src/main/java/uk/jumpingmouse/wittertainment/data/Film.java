package uk.jumpingmouse.wittertainment.data;

/**
 * The film object.
 * @author Edmund Johnson
 */
public class Film {

    private String imdbId;
    private String title;

    /** Private constructor to prevent instantiation. */
    private Film() {
    }

    /**
     * Create and return a new instance of a film.
     * @param imdbId the IMDB identifier for the film
     * @param title the English title of the film.
     *              Not for display, just for making the database more readable
     * @return the new film.
     */
    public static Film newInstance(String imdbId, String title) {
        Film film = new Film();
        film.imdbId = imdbId;
        film.title = title;
        return film;
    }

    //---------------------------------------------------------------------
    // Validation

    /**
     * Returns whether a set of String values are valid film fields.
     * @param imdbId the IMDB identifier for the film
     * @param title the title of the film.
     * @return true if the parameters are valid film fields, false otherwise
     */
    public static boolean isFieldsValid(String imdbId, String title) {
        return isImdbIdValid(imdbId)
                && isTitleValid(title);
    }

    /**
     * Returns whether an IMDB id is valid.
     * @param imdbId the IMDB id
     * @return true if imdbId is a valid IMDB id, false otherwise
     */
    private static boolean isImdbIdValid(String imdbId) {
        return imdbId != null
                && !imdbId.trim().isEmpty();
    }

    /**
     * Returns whether a title is valid.
     * @param title the title
     * @return true if title is a valid film title, false otherwise
     */
    private static boolean isTitleValid(String title) {
        return title != null
                && !title.trim().isEmpty();
    }

    //---------------------------------------------------------------------
    // Getters and setters

    public String getImdbId() {
        return imdbId;
    }

    public String getTitle() {
        return title;
    }

}
