package com.ryansusana.elepy.modules;

import com.ryansusana.elepy.concepts.FieldDescriber;
import com.ryansusana.elepy.models.FieldType;
import j2html.TagCreator;
import j2html.tags.DomContent;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static j2html.TagCreator.*;


public class EleHTML {


    public static DomContent eleToHtml(Object object) throws IllegalAccessException {

        List<DomContent> fields = new ArrayList<>();
        for (Field field : object.getClass().getDeclaredFields()) {
            fields.add(getFieldValue(field, object));
        }


        return div(fields.toArray(new DomContent[0]));
    }

    public static DomContent getFieldValue(Field field, Object object) throws IllegalAccessException {

        field.setAccessible(true);
        if (object == null || field.get(object) == null) {
            return TagCreator.div().withStyle("display: none;");
        }
        FieldDescriber fieldDescriber = new FieldDescriber(field);

        if (fieldDescriber.getType().equals(FieldType.OBJECT)) {
            return div(h4(fieldDescriber.getPrettyName()), eleToHtml(field.get(object)));
        } else if (fieldDescriber.getType().equals(FieldType.DATE)) {

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

            final Object date = field.get(object);
            if (date instanceof Date) {

                return rawHtml(fieldDescriber.getPrettyName() + ": " + simpleDateFormat.format((Date) date) + "<br>");
            } else if (date instanceof Instant) {
                final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy").withZone(ZoneId.systemDefault());
                return rawHtml(fieldDescriber.getPrettyName() + ": " + dateTimeFormatter.format((Instant) date) + "<br>");

            }
        }

        return rawHtml(fieldDescriber.getPrettyName() + ": " + field.get(object).toString() + "<br>");


    }
}
