package com.elepy.tests.devfrontend;

import javax.validation.constraints.Max;
import javax.validation.constraints.Size;

public class DogDetail {

    @Size(max = 10)
    private String  detailName;

    @Size(min = 10)
    private String detailDescription;

    public String getDetailDescription() {
        return detailDescription;
    }

    public void setDetailDescription(String detailDescription) {
        this.detailDescription = detailDescription;
    }

    public String getDetailName() {
        return detailName;
    }

    public void setDetailName(String detailName) {
        this.detailName = detailName;
    }
}
