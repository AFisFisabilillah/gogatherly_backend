package com.gogatherly.gogatherly.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WebResponseList<T> {
    private String status;

    private String message;

    private T data;

    private MetaData meta;
}
