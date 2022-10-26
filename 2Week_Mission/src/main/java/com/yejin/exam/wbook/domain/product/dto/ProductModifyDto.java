package com.yejin.exam.wbook.domain.product.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ProductModifyDto {
    @NotBlank
    private String subject;
    @NotNull
    private int price;
    @NotBlank
    private String productTagContents;
}