package ru.telegrambot.domain;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.CollectionUtils;

import java.util.*;

public class PlayerTagService {

    private static final int USED_JOKES_NUMBER = 10;

    private static final Random RANDOM = new Random();

    private static final Map<Long, PlayerTag> PLAYER_TAG_MAP = new HashMap<Long, PlayerTag>() {{
        put(377287783L, PlayerTag.AGGRESSIVE);
    }};

    private static final Map<PlayerTag, ArrayDeque<String>> USED_JOKES = new HashMap<PlayerTag, ArrayDeque<String>>() {{
        put(PlayerTag.AGGRESSIVE, new ArrayDeque<>());
    }};

    private static final Map<PlayerTag, List<String>> TAG_JOKES_MAP = new HashMap<PlayerTag, List<String>>() {{
        put(PlayerTag.AGGRESSIVE, new ArrayList<String>() {{
            add(", так что подумайте дважды, стоит ли вам это делать");
            add(" без доли сомнений");
            add(". Кто-то против?");
            add(". Самое время подумать о наличии ДМС");
            add(" с двух ног");
            add(" с ноги");
            add(", но обещал в этот раз быть помягче");
            add(", заранее принося свои извинения");
            add(". Поаккуратнее у стен, парни");
            add(". Остается надеяться, что он будет в хорошем настроении");
            add(". А ты?");
            add(", ведь спорт - это здоровье");
            add(" и, возможно, он будет играть против тебя");
            add(". Еще не поздно найти другие планы на среду");
            add(". Ожидается большое кол-во штрафных ударов");
            add(" и мы уже знаем, о чем ты подумал");
            add(". Играем full-контакт");
            add(", размышляя о правилах игры");
            add(", обещая быть в ударе");
            add(". Соперникам рекомендуется сделать растяжку");
            add(", обещая показать, что такое настоящий прессинг");
            add(", переопределяя понятие 'игра корпусом'");
            add(". Помните: кости срастаются, а поражение - навсегда");
            add(". Помните: синяки - это просто трофеи, которые можно носить с собой");
            add(", зная, что правила могут трактоваться по-разному");
            add(". На этот раз все серьезно");
            add(", вспоминая, что в прошлый раз было принято судейское решение не в его пользу");
            add(", зная, что ты сам отдашь ему мяч");
        }});
    }};

    public static synchronized Optional<Pair<String, Boolean>> getJokeByID(Long id) {
        PlayerTag playerTag = PLAYER_TAG_MAP.get(id);
        if (Objects.isNull(playerTag)) {
            return Optional.empty();
        }

        List<String> jokes = TAG_JOKES_MAP.get(playerTag);
        if (CollectionUtils.isEmpty(jokes)) {
            return Optional.empty();
        }

        ArrayDeque<String> usedJokes = USED_JOKES.get(playerTag);
        if (Objects.isNull(usedJokes)) {
            return Optional.empty();
        }

        String joke = jokes.get(RANDOM.nextInt(jokes.size()));
        if (usedJokes.contains(joke)) {
            return getJokeByID(id);
        }

        if (usedJokes.size() >= USED_JOKES_NUMBER) {
            usedJokes.pollLast();
        }

        usedJokes.push(joke);
        boolean isDotNeeded = !(joke.endsWith("?") || joke.endsWith("!"));

        return Optional.of(Pair.of(joke, isDotNeeded));
    }

}
