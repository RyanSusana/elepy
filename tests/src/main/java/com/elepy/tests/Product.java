package com.elepy.tests;

import com.elepy.annotations.*;
import com.elepy.models.TextType;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Model(name = "Products", path = "/products")
@Create(requiredPermissions = {})
@Delete(requiredPermissions = {})
@Update(requiredPermissions = {})
@Find(requiredPermissions = {})
@Entity(name = "Products")
@Table(name = "products")
@Action(name = "Remove", handler = DeleteAllAction.class)
@Action(name = "Remove2", handler = DeleteAllAction.class)
public class Product {

    @PrettyName("Product ID")
    @Identifier(generated = false)
    @Id
    private Integer id;

    @PrettyName("Price")
    private BigDecimal price;

    @DateTime()
    @PrettyName("Expiration Date")
    private Date date;

    @Text(TextType.TEXTAREA)
    @Searchable
    private String shortDescription;

    @Text(TextType.HTML)
    private String htmlDescription;

    @Text(TextType.MARKDOWN)
    private String markdown;

    @TrueFalse(trueValue = "This product is awesome", falseValue = "This product is meh")
    private boolean productIsAwesome;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> tags;

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public boolean isProductIsAwesome() {
        return productIsAwesome;
    }

    public void setProductIsAwesome(boolean productIsAwesome) {
        this.productIsAwesome = productIsAwesome;
    }


    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getHtmlDescription() {
        return htmlDescription;
    }

    public void setHtmlDescription(String htmlDescription) {
        this.htmlDescription = htmlDescription;
    }

    public String getMarkdown() {
        return markdown;
    }

    public void setMarkdown(String markdown) {
        this.markdown = markdown;
    }

    public static Product withDescription(String s) {
        final var product = new Product();
        product.setShortDescription(s);
        return product;
    }
}
