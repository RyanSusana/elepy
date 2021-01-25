package com.elepy.tests.devfrontend;

import com.elepy.annotations.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Model(name = "Dogs", path = "dogs")
public class Dog {
    private String id;

    @NotBlank
    @Size(min = 0, max = 15)
    @Description("The Description")
    private String name;


    @TrueFalse(trueValue = "Yes", falseValue = "No")
    private boolean showDescription;

    @NotBlank
    @Label("Description")
    @Description("The Description")
    @Size(min = 0, max = 10)
    @ShowIf("root.showDescription == true")
    private String description;

    @Valid
    private BreedDetail breedDetail;

    @Label("Nicknames")
    @Size(min = 0, max = 5)
    @Description("The Description")
    @NotNull
    private List<@Size(max = 5) String> nicknames;

    @Label("Details")
    @Description("The Description")
    private List<@Valid BreedDetail> details;

    public boolean isShowDescription() {
        return showDescription;
    }

    public void setShowDescription(boolean showDescription) {
        this.showDescription = showDescription;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getNicknames() {
        return nicknames;
    }

    public void setNicknames(List<String> nicknames) {
        this.nicknames = nicknames;
    }

    public BreedDetail getBreedDetail() {
        return breedDetail;
    }

    public void setBreedDetail(BreedDetail breedDetail) {
        this.breedDetail = breedDetail;
    }

    public List<BreedDetail> getDetails() {
        return details;
    }

    public void setDetails(List<BreedDetail> details) {
        this.details = details;
    }
}
