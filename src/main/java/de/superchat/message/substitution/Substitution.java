package de.superchat.message.substitution;

public interface Substitution {

    /**
     * Substitute placeholder by vallue
     *
     * @param placeholder
     * @return
     */
    String substitute(String placeholder);

    /**
     * Configure regex pattern of the placeholder
     *
     * @return
     */
    String getPlaceholderPattern();

}
