package com.maveric.digital.model;

import com.opencsv.bean.CsvBindByPosition;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(value = "account")
public class Account extends IdentifiedEntity{
    private String accountName;
    private Long createdAt;
    private Long updatedAt;
    private String accountCode;
    private String AccountManager;


}
