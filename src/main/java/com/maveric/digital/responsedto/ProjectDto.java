package com.maveric.digital.responsedto;

import com.maveric.digital.utils.ServiceConstants;
import com.opencsv.bean.CsvDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 4726073229281101032L;

    private Long id;
    @NotBlank(message = "Should not be Empty")

    private String projectName;
    @NotNull(message = "Should not be Empty")

    private String managerName;
    private String businessUnit;
    @CsvDate(ServiceConstants.DATE_PATTERN)

    private LocalDate startDate;
    @CsvDate(ServiceConstants.DATE_PATTERN)

    private LocalDate endDate;
    @NotNull(message = "Should not be Empty")

    private String updatedBy;
    @NotNull(message = "Should not be Empty")
    private boolean status;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    @NotNull(message = "Account Id cannot be empty")
    private Long accountId;
    @NotNull(message = "projectCode cannot be empty")
    private String projectCode;


}
