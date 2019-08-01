package com.elepy;


import com.elepy.http.HttpService;

public interface ElepyExtension {

    /**
     * This method is used by Elepy to let a module execute it's functionality after Elepy is completely setup.
     *
     * @param http  The SparkJava Service
     * @param elepy a link to Elepy after it has been constructed and all the dependencies have been set and Elepy specific routes have been generated
     */
    void setup(HttpService http, ElepyPostConfiguration elepy);


}
