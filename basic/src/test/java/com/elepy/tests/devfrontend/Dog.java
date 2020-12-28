package com.elepy.tests.devfrontend;

import com.elepy.annotations.Description;
import com.elepy.annotations.Label;
import com.elepy.annotations.Model;

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

    @NotBlank
    @Label("Description")
    @Description("The Description")
    @Size(min = 0, max = 10)
    private String description;


    @Label("Nicknames")
    @Size(min = 0, max = 5)
    @Description("The Description")
    @NotNull
    private List<@Size(max = 5) String> nicknames;

    @Label("Details")
    @Description("The Description")
    private List<@Valid DogDetail> details;

    public List<DogDetail> getDetails() {
        return details;
    }

    public void setDetails(List<DogDetail> details) {
        this.details = details;
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
}
