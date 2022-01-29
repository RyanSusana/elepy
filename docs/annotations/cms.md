# CMS-specific Annotations
This chapter focuses on annotations that make changes to the CMS.

# @View
This annotation completely swaps out the way models are presented in the CMS.
By default this is the `DefaultView`(located in the admin module).
An option is to use the `SingleView` instead. 
SingleView changes the default table of many items to a SingleView of only 1 item.
This allows to create things like a Settings panel.

Look at [this guide](guides/custom-views.md) on how I implemented the old Elepy docs site with @View

_Example_

```java
@View(SingleView.class)
@Model(...)
class SEOSettings{
    private String author;
    private String siteTitle;
    
    @TextArea
    private String description;
    private String keywords
}
```