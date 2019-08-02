# What This Guide is About

This an explanation guide of how I implemented the old https://docs.elepy.com (now replaced by docsify) in Kotlin. It will focus on the `@View` annotation.

## How I implemented it
It uses this custom RestModel:
```kotlin
@RestModel(name = "Pages", slug = "/api/pages", defaultSortField = "title")
@View(MarkdownPageView::class)
data class MarkdownPage @JsonCreator constructor(
        @JsonProperty("id") val id: String?,
        @JsonProperty("title") @Searchable @Unique val title: String,
        @JsonProperty("slug") @Searchable @Unique val slug: String,
        @JsonProperty("live") @TrueFalse(trueValue = "Live", falseValue = "Draft") val live: Boolean?,
        @JsonProperty("content") val content: String?
)
```

Notice the `@View` annotation? It points to the class that defines the way models are viewed in the CMS. Here is the MarkdownPageView class that handles this:

```kotlin
class MarkdownPageView : RestModelView {

    @Inject
    private lateinit var templateCompiler: TemplateCompiler

    override fun renderView(descriptor: ModelDescription<*>): String {
        return templateCompiler.compile("custom-models/markdown-page/markdown-edit.peb")
    }
}
```
The MarkdownPageView __must__ implement the RestModelView interface from Elepy. It has one method: a String that you must return. This String is basically the overridden HTML content of your RestModel in your CMS.

The TemplateCompiler is an uninteresting dependency. What it is, is that it looks for a template file in the ClassPath resources and returns it as a String. The more interesting thing is the `"custom-models/markdown-page/markdown-edit.html"`.

It's basically an HTML file that you can find [here](https://github.com/RyanSusana/elepy-docs/blob/master/src/main/resources/custom-models/markdown-page/markdown-edit.peb).

This is your portrait. You can throw any HTML/JavaScript in here and have it display in your CMS, instead of the default table/search bar/pagination.

There are 2 custom tags you should know about, though. `<style>` and `<stylesheet>`

The `<style></style>` tag is what you should expect it to be, a place where you can put your CSS. This will get placed as the last tag in the `<head></head>` of your page. You can have multiple `<style/>` tags.

The `<stylesheet src = "" />` tag is where you can point to additional stylesheets. They also appear in the `<head></head>`.

## The RestModel in Action
Here is a video of the MarkdownEditor in action.

<iframe src="https://player.vimeo.com/video/325447625" width="640" height="480" frameborder="0" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>
