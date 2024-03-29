package com.maveric.digital.responsedto;

import java.io.Serializable;
import java.time.LocalDate;

import com.maveric.digital.service.LocalDateConverter;
import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvCustomBindByPosition;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CsvMetaDataDto implements Serializable {
    private static final long serialVersionUID = 6781284982167055591L;
    @CsvBindByPosition(position = 0)
    private String accountCode;
    @CsvBindByPosition(position = 1)
    private String accountName;
    @CsvBindByPosition(position = 2)
    private String projectCode;
    @CsvBindByPosition(position = 3)
    private String project;
    @CsvBindByPosition(position = 4)
    private String portfolio;

    @CsvCustomBindByPosition(position = 5, converter = LocalDateConverter.class)
    private LocalDate startDate;

    @CsvCustomBindByPosition(position = 6, converter = LocalDateConverter.class)
    private LocalDate endDate;

    @CsvBindByPosition(position = 7)
    private String deliveryManager;

    @CsvBindByPosition(position = 8)
    private String deliveryManagerEmail;

    @CsvBindByPosition(position = 9)
    private String deliveryPartner;
    @CsvBindByPosition(position = 10)
    private String deliveryPartnerEmail;

    @CsvBindByPosition(position = 11)
    private String accountManager;

    @CsvBindByPosition(position = 12)
    private String accountManagerEmail;

    @CsvBindByPosition(position = 13)
    private String growthPartner;

    @CsvBindByPosition(position = 14)
    private String growthPartnerEmail;

    @CsvBindByPosition(position = 15)
    private String projectBillingType;

    @CsvBindByPosition(position = 16)
    private String engagementType;

    private Long id;
    private Long accountId;

}
