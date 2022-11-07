package com.yejin.exam.wbook.domain.withdraw.dto;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class WithdrawApplyDto {

    @NotBlank(message = "은행명을 입력해주세요")
    private String bankName;

    @NotBlank(message = "은행 계좌번호를 입력해주세요")
    @Pattern(regexp = "^[0-9]+$", message = "숫자만 사용할 수 있습니다.")
    private String backAccountNo;

    @NotBlank(message = "출금하고자 하는 금액를 입력해주세요.")
    private String price;


}