package com.maveric.digital.responsedto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ProjectInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 3851650666630899303L;

    private Long id;
    private String projectName;

    public ProjectInfo(Long id, String projectName) {
        this.id = id;
        this.projectName = projectName;
    }


}
