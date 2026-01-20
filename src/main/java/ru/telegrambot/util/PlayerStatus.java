package ru.telegrambot.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PlayerStatus {

    READY("Готов"),
    DOES_NOT_KNOW("Под вопросом"),
    CALLED_FRIENDS("Позвал друзей"),
    NOT_READY("Не готов");

    @Getter
    private final String value;

}
