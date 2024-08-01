package com.elepy.schemas;

import com.elepy.annotations.FileReference;
import com.elepy.annotations.InnerObject;
import com.elepy.annotations.Model;

import java.util.List;

@Model(name = "Nav Menu", path = "/nav")
public class NavigationMenu {

    private String id;
    @FileReference(allowedMimeType = "image/*")
    private String logo;

    private String subtitle;


    private List<@InnerObject(maxRecursionDepth = 20)NavigationItem> menuItems;
} 
