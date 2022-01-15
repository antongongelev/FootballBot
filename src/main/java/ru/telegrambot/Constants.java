package ru.telegrambot;

import java.time.DayOfWeek;
import java.util.regex.Pattern;

public interface Constants {

    String CHAT_NAME = "CHAT_NAME";
    String BOT_NAME = "BOT_NAME";
    String BOT_TOKEN = "BOT_TOKEN";
    String TEAM = "СОСТАВ";
    String HELP = "/HELP";
    String CLEAR = "СБРОС";
    String ADD_ME = "+";
    String REMOVE_ME = "-";
    String DO_NOT_KNOW = "?";
    DayOfWeek DAY_OF_WEEK = DayOfWeek.WEDNESDAY;
    Pattern MINUS_PATTERN = Pattern.compile("[-][1-9]");
    Pattern PLUS_PATTERN = Pattern.compile("[+][1-9]");
    String DELIMITER = "======================";
    int CODE_LENGTH = 4;
}
