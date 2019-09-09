package com.elepy.describers;

import com.elepy.annotations.FileReference;
import com.elepy.annotations.InnerObject;
import com.elepy.annotations.RestModel;

import java.util.List;

@RestModel(name = "Nav Menu", slug = "/nav")
public class NavigationMenu {

    private String id;
    @FileReference(allowedMimeType = "image/*")
    private String logo;

    private String subtitle;

    @InnerObject(maxRecursionDepth = 20)
    private List<NavigationItem> menuItems;
} 
