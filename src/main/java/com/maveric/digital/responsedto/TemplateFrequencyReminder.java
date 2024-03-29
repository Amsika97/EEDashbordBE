package com.maveric.digital.responsedto;

import lombok.Getter;

@Getter
public enum TemplateFrequencyReminder {

    WEEKLY(7),
    MONTHLY(30),
    QUARTERLY(75),
    NA(0);
    private final int days;

    TemplateFrequencyReminder(int days) {
        this.days=days;
    }
}
