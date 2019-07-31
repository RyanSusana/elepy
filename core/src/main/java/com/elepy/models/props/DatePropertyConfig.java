package com.elepy.models.props;

import com.elepy.annotations.DateTime;
import com.elepy.models.FieldType;
import com.elepy.models.Property;
import com.elepy.utils.DateUtils;

import java.lang.reflect.AccessibleObject;
import java.util.Date;

public class DatePropertyConfig implements PropertyConfig {
    private final Date minimumDate;
    private final Date maximumDate;
    private final boolean includeTime;

    public DatePropertyConfig(Date minimumDate, Date maximumDate, boolean includeTime) {
        this.minimumDate = minimumDate;
        this.maximumDate = maximumDate;
        this.includeTime = includeTime;
    }

    public static DatePropertyConfig of(AccessibleObject field) {
        final DateTime annotation = field.getAnnotation(DateTime.class);
        if (annotation != null) {
            return new DatePropertyConfig(DateUtils.guessDate(annotation.minimumDate()), DateUtils.guessDate(annotation.maximumDate()), annotation.includeTime());
        } else {
            return new DatePropertyConfig(DateUtils.guessDate("1970-01-01"), DateUtils.guessDate("2099-12-22"), true);
        }
    }

    public static DatePropertyConfig of(Property property) {
        return new DatePropertyConfig(
                property.getExtra("minimumDate"),
                property.getExtra("maximumDate"),
                property.getExtra("includeTime")
        );
    }

    @Override
    public void config(Property property) {

        property.setType(FieldType.DATE);

        property.setExtra("includeTime", includeTime);
        property.setExtra("minimumDate", minimumDate);
        property.setExtra("maximumDate", maximumDate);
    }

    private Object formatForCMS(Date date) {
        return date.toInstant().toEpochMilli();
    }

    public Date getMinimumDate() {
        return minimumDate;
    }

    public Date getMaximumDate() {
        return maximumDate;
    }

    public boolean isIncludeTime() {
        return includeTime;
    }
}