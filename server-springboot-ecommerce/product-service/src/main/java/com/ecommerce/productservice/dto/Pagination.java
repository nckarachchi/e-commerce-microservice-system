package com.ecommerce.productservice.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Pagination<T> {
    private List<T> data;
    private long totalSize;
}
