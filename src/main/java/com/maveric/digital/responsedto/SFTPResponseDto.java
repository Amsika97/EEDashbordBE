package com.maveric.digital.responsedto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SFTPResponseDto implements Serializable {

    @Serial
    private static final long serialVersionUID = -1851239658185521792L;

    private String fileName;
    private String message;
    private int statusCode;

}
