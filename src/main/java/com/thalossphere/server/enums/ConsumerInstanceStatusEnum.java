package com.thalossphere.server.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ConsumerInstanceStatusEnum {

    NORMAL(1, "正常"),;

    private int status;

    private String desc;

}
