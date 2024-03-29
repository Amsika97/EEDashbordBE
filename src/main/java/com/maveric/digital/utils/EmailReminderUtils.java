package com.maveric.digital.utils;

import com.maveric.digital.responsedto.TemplateFrequencyReminder;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EmailReminderUtils {
    public static final String PROJECT_CODE = "projectcode";
    public static final String USER = "user";
    public static final String MESSAGE = "message";
    public static final String PROJECT_NAME = "projectname";
    public static final String ACCOUNT_NAME = "accountname";
    public static final String PROJECT_TYPE = "projecttype";
    public static final String TEMPLAT_ENAME = "templatename";
    public static final String DUE_DATE = "duedate";
    public static final String WEEKLY = "weekly";
    public static final String MONTHLY = "monthly";
    public static final String QUARTERLY = "quarterly";
    public static final String NA = "NA";


    public static long setEmailRemainderDate(TemplateFrequencyReminder templateFrequency,int sendReminderAtBefore) {
        return switch (templateFrequency){
            case WEEKLY -> DateUtils.addDays(new Date(),TemplateFrequencyReminder.WEEKLY.getDays()-sendReminderAtBefore).getTime();
            case MONTHLY ->DateUtils.addDays(new Date(),TemplateFrequencyReminder.MONTHLY.getDays()-sendReminderAtBefore).getTime();
            case QUARTERLY ->DateUtils.addDays(new Date(),TemplateFrequencyReminder.QUARTERLY.getDays()-sendReminderAtBefore).getTime();
            default -> 0;
        };
    }



    public static List<Long> setEmailOverDueRemainderDates(TemplateFrequencyReminder templateFrequency, int overDueReminders,int thresholdForOverDueReminders) {
        List<Long> list = new ArrayList<>();
        int baseDays;
        if (TemplateFrequencyReminder.WEEKLY.equals(templateFrequency)) {
            baseDays = TemplateFrequencyReminder.WEEKLY.getDays();
        } else if (TemplateFrequencyReminder.MONTHLY.equals(templateFrequency)) {
            baseDays = TemplateFrequencyReminder.MONTHLY.getDays();
        } else  if (TemplateFrequencyReminder.QUARTERLY.equals(templateFrequency)) {
            baseDays = TemplateFrequencyReminder.QUARTERLY.getDays();
        } else {
            return List.of();
        }

        int threshold = thresholdForOverDueReminders;
        for (int i = 0; i < overDueReminders; i++) {
            list.add(DateUtils.addDays(new Date(), baseDays + threshold).getTime());
            threshold += threshold;
        }
        return list;
    }



}
