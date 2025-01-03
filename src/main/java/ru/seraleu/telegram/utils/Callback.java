package ru.seraleu.telegram.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Callback {

    START("/callback/start"),
    DISHES("/callback/dishes"),
    OPTION1("/callback/option1"),
    OPTION2("/callback/option1");

    private final String path;
}
