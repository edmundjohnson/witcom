package uk.jumpingmouse.wittertainment.data;

/**
 * An award given to a film, e.g. DVD of the week for 23/12/16.
 * @author Edmund Johnson
 */
public class Award {

    private static final String CATEGORY_FILM = "F";
    private static final String CATEGORY_TV = "T";
    private static final String CATEGORY_DVD = "D";

//    private String awardId;
    private String awardDate;
    private String categoryId;
    private String filmId;
    private String criticId;
    private String criticQuote;

    private Award() {
    }

    /**
     * Create and return a new instance of an award.
     * @param awardDate the date the award was made
     * @param categoryId the award category (e.g. film of the week, DVD of the week)
     * @param filmId the id of the film being given the award
     * @param criticId the critic making the award
     * @param criticQuote a description of the film made by the critic
     * @return the new award.
     */
    public static Award newInstance(String awardDate, String categoryId, String filmId,
                 String criticId, String criticQuote) {
        Award award = new Award();

//        award.awardId = awardId;
        award.awardDate = awardDate;
        award.categoryId = categoryId;
        award.filmId = filmId;
        award.criticId = criticId;
        award.criticQuote = criticQuote;

        return award;
    }

    //---------------------------------------------------------------------
    // Validation

    /**
     * Returns whether a set of String values are valid award fields.
     * @param awardDate the award date (YYMMDD)
     * @param categoryId the potential category id
     * @param filmId the film id
     * @param criticId the critic id
     * @param criticQuote the critic quote
     * @return true if the parameters are valid award fields, false otherwise
     */
    public static boolean isFieldsValid(String awardDate, String categoryId, String filmId,
                                        String criticId, String criticQuote) {
        return isAwardDateValid(awardDate)
                && isCategoryIdValid(categoryId)
                && isFilmIdValid(filmId)
                && isCriticIdValid(criticId)
                && isCriticQuoteValid(criticQuote);
    }

    private static boolean isAwardDateValid(String awardDate) {
        return awardDate != null
                && !awardDate.trim().isEmpty();
    }

    /**
     * Returns whether a String is a valid award category id.
     * @param categoryId the potential category id
     * @return true if categoryId is a valid award category id, false otherwise
     */
    private static boolean isCategoryIdValid(String categoryId) {
        return categoryId != null
                && (categoryId.equals(CATEGORY_FILM)
                || categoryId.equals(CATEGORY_TV)
                || categoryId.equals(CATEGORY_DVD));
    }

    private static boolean isFilmIdValid(String filmId) {
        return filmId != null
                && !filmId.trim().isEmpty();
    }

    private static boolean isCriticIdValid(String criticId) {
        return criticId != null
                && !criticId.trim().isEmpty();
    }

    private static boolean isCriticQuoteValid(String criticQuote) {
        return criticQuote != null
                && !criticQuote.trim().isEmpty();
    }


    //---------------------------------------------------------------------
    // Getters and setters

//    public String getAwardId() {
//        return awardId;
//    }

    public String getAwardDate() {
        return awardDate;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public String getFilmId() {
        return filmId;
    }

    public String getCriticId() {
        return criticId;
    }

    public String getCriticQuote() {
        return criticQuote;
    }

}
