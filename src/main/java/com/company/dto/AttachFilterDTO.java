package com.company.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AttachFilterDTO {

    private Integer id;
    private String originName;
    private Long fromSize;
    private Long toSize;
    private LocalDate fromDate;
    private LocalDate toDate;

    private Boolean visible;

}
