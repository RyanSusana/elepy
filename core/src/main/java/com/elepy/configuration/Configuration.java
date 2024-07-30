package com.elepy.configuration;

public interface Configuration {

    /**
     * This method is used by Elepy to let a module add extra configuration.
     *
     * @param elepy A link to the Elepy object where extra configuration can happen
     */
    void preConfig(ElepyPreConfiguration elepy);

    /**
     * This method is called after the {@link #preConfig(ElepyPreConfiguration)} of all the Configurations is called useful if you want a list of
     * everything associated with the Elepy instance
     *
     * @param elepy A link to the Elepy object where extra configuration can happen
     */
    default void afterPreConfig(ElepyPreConfiguration elepy) {
    }

    /**
     * This method is used by Elepy to let a module execute it's functionality after Elepy is completely setup.
     *
     * @param elepy a link to Elepy after it has been constructed and all the dependencies have been set and Elepy specific routes have been generated
     */
    void postConfig(ElepyPostConfiguration elepy);

} 
