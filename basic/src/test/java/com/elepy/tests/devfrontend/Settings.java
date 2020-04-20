package com.elepy.tests.devfrontend;

import com.elepy.annotations.Model;
import com.elepy.annotations.TextArea;
import com.elepy.annotations.View;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.List;

@Model(name = "Settings2", path = "/settings2")
@Entity(name = "settings2")
@Table(name = "settings2")
@View(View.Defaults.SINGLE)
public class Settings {
    private String id;
    private String title;

    @TextArea
    private String description;

    private List<@TextArea String> descriptions;

    private List<String> tags;

    public List<String> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(List<String> descriptions) {
        this.descriptions = descriptions;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
