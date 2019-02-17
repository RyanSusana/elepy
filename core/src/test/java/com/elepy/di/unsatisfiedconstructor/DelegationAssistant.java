package com.elepy.di.unsatisfiedconstructor;

import com.elepy.annotations.ElepyConstructor;

public class DelegationAssistant {

    @ElepyConstructor
    public DelegationAssistant(Siri siri, AmazonAlexa amazonAlexa, GoogleAssistant googleAssistant) {
    }
}
