package com.elepy.models;

import com.elepy.annotations.*;
import com.elepy.annotations.Number;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Model(name = "Simple Object", path = "/simple-object")
public class SimpleObject {
    @Identifier
    private String inputNotAnnotated;

    @Input(type = "email")
    private String inputAnnotated;

    private long number;
    private Integer numberWrapped;
    @Number
    private long numberAnnotated;

    private boolean bool;
    private Boolean boolWrapped;
    @TrueFalse
    private Boolean boolAnnotated;
    @TrueFalse
    private boolean boolWrappedAnnotated;


    @FileReference
    private String fileReference;

    @HTML
    private String html;

    @Reference(to = SimpleObject.class)
    private String reference;

    @Markdown
    private String markdown;

    private EnumType enumType;


    @Array
    private List<String> listOfStringsAnnotated;
    private List<Integer> listOfIntegers;
    private List<Boolean> listOfBooleans;
    private List<EnumType> listOfEnums;

    private Set<@HTML String> setOfHTML;

    private ArrayList<@Markdown String> arrayListOfMarkdown;



    private static enum EnumType {
        ENUM1, ENUM2
    }
}
