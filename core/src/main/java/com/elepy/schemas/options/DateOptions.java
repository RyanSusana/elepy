package com.elepy.schemas.options;

import com.elepy.annotations.DateTime;
import com.elepy.utils.DateUtils;

import java.lang.reflect.AnnotatedElement;
import java.util.Date;

public class DateOptions implements Options {

    private Date minimumDate;
    private Date maximumDate;
    private boolean includeTime;

    private DateOptions(Date minimumDate, Date maximumDate, boolean includeTime) {
        this.minimumDate = minimumDate;
        this.maximumDate = maximumDate;
        this.includeTime = includeTime;
    }

    public static DateOptions of(AnnotatedElement field) {
        final DateTime annotation = com.elepy.utils.Annotations.get(field,DateTime.class);
        if (annotation != null) {
            return new DateOptions(DateUtils.guessDate(annotation.minimumDate(), annotation.format()), DateUtils.guessDate(annotation.maximumDate(), annotation.format()), annotation.includeTime());
        } else {
            return new DateOptions(DateUtils.guessDate("1970-01-01", ""), DateUtils.guessDate("2099-12-22", ""), true);
        }
    }

    public Date getMinimumDate() {
        return minimumDate;
    }

    public void setMinimumDate(Date minimumDate) {
        this.minimumDate = minimumDate;
    }

    public Date getMaximumDate() {
        return maximumDate;
    }

    public void setMaximumDate(Date maximumDate) {
        this.maximumDate = maximumDate;
    }

    public boolean isIncludeTime() {
        return includeTime;
    }

    public void setIncludeTime(boolean includeTime) {
        this.includeTime = includeTime;
    }
}
