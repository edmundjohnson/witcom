package uk.jumpingmouse.wittertainment.data;

/**
 * The critic object.
 * @author Edmund Johnson
 */
public class Critic {

    private String id;
    private String name;
    private String description;

    /** Private constructor to prevent instantiation. */
    private Critic() {
    }

    /**
     * Create and return a new instance of a critic.
     * @param id the critic id, e.g. "MK"
     * @param name the critic name, e.g. "Mark Kermode".
     * @param description a description of the critic
     * @return the new critic.
     */
    public static Critic newInstance(String id, String name, String description) {
        Critic critic = new Critic();
        critic.id = id;
        critic.name = name;
        critic.description = description;
        return critic;
    }

    //---------------------------------------------------------------------
    // Validation

    /**
     * Returns whether a set of String values are valid critic fields.
     * @param id the critic id, e.g. "MK"
     * @param name the critic name, e.g. "Mark Kermode".
     * @param description a description of the critic
     * @return true if the parameters are valid critic fields, false otherwise
     */
    public static boolean isFieldsValid(String id, String name, String description) {
        return isIdValid(id)
                && isNameValid(name)
                && isDescriptionValid(description);
    }

    /**
     * Returns whether an critic id is valid.
     * @param id the critic id
     * @return true if id is a valid critic id, false otherwise
     */
    private static boolean isIdValid(String id) {
        return id != null
                && !id.trim().isEmpty();
    }

    /**
     * Returns whether a critic name is valid.
     * @param name the critic name
     * @return true if name is a valid critic name, false otherwise
     */
    private static boolean isNameValid(String name) {
        return name != null
                && !name.trim().isEmpty();
    }

    /**
     * Returns whether a critic name is valid.
     * @param description a description of the critic
     * @return true if name is a valid critic name, false otherwise
     */
    private static boolean isDescriptionValid(String description) {
        return description != null
                && !description.trim().isEmpty();
    }

    //---------------------------------------------------------------------
    // Getters and setters

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

}
