package com.gogatherly.gogatherly.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MetaData {
    private Integer page;
    private Integer size;
    private Integer totalPages;
    private Long totalElements;
    private boolean hasNext;
    private boolean hasPrevious;
}
