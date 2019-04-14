package com.elepy;

public interface Configuration {

    /**
     * This method is used by Elepy to let a module add extra configuration.
     *
     * @param elepy A link to the Elepy object where extra configuration can happen
     */
    void before(ElepyPreConfiguration elepy);

    /**
     * This method is used by Elepy to let a module execute it's functionality after Elepy is completely setup.
     *
     * @param elepy a link to Elepy after it has been constructed and all the dependencies have been set and Elepy specific routes have been generated
     */
    void after(ElepyPostConfiguration elepy);

} 
