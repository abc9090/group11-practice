package com.example.group11practice.enums;

public enum PageEnum {
    DEFAULT_PAGE_NO(0, "默认序数"),
    DEFAULT_PAGE_SIZE(20, "默认长度");

    private final Integer number;
    private final String name;

    PageEnum(Integer number, String name) {
        this.number = number;
        this.name = name;
    }

    public Integer getKey() {
        return number;
    }

    public String getValue() {
        return name;
    }

    }
