package com.elepy.admin;

import com.elepy.annotations.*;
import com.elepy.http.ActionType;

import java.math.BigDecimal;
import java.util.Date;

@RestModel(name = "Products", slug = "/products")
@Create(requiredPermissions = {})
@Delete(requiredPermissions = {})
@Update(requiredPermissions = {})
@Find(requiredPermissions = {})
@Action(name = "Find On Google", handler = ProductFindOnGoogle.class, actionType = ActionType.SINGLE)
public class Product {

    @PrettyName("Product ID")
    @Identifier(generated = false)
    private Integer id;

    @PrettyName("Price")
    private BigDecimal price;

    @DateTime()
    @PrettyName("Expiration Date")
    private Date date;

    @TrueFalse(trueValue = "This product is awesome", falseValue = "This product is meh")
    private boolean awesome;


    public boolean isAwesome() {
        return awesome;
    }

    public void setAwesome(boolean awesome) {
        this.awesome = awesome;
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


}
