package com.elepy.hibernate;

import com.elepy.annotations.RestModel;
import com.elepy.annotations.Searchable;
import com.elepy.annotations.Text;
import com.elepy.annotations.Unique;
import com.elepy.models.TextType;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "resources")
@RestModel(name = "Test Resource", slug = "/resources")
public class Resource {

    @Id
    private int id;

    @Text(TextType.TEXTFIELD)
    private String textField;

    @Searchable
    private String searchableField;

    @Unique
    private String uniqueField;

    public String getTextField() {
        return textField;
    }

    public void setTextField(String textField) {
        this.textField = textField;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSearchableField() {
        return searchableField;
    }

    public void setSearchableField(String searchableField) {
        this.searchableField = searchableField;
    }

    public String getUniqueField() {
        return uniqueField;
    }

    public void setUniqueField(String uniqueField) {
        this.uniqueField = uniqueField;
    }
}
