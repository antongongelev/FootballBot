package ru.telegrambot.domain;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.CollectionUtils;

import java.util.*;

public class PlayerTagService {

    private static final Random RANDOM = new Random();

    private static final Map<Long, PlayerTag> PLAYER_TAG_MAP = new HashMap<Long, PlayerTag>() {{
        put(377287783L, PlayerTag.AGGRESSIVE);
    }};

    private static final Map<PlayerTag, List<Pair<String, Boolean>>> TAG_JOKES_MAP = new HashMap<PlayerTag, List<Pair<String, Boolean>>>() {{
        put(PlayerTag.AGGRESSIVE, new ArrayList<Pair<String, Boolean>>() {{
            add(Pair.of(", так что подумайте дважды, стоит ли вам это делать", true));
            add(Pair.of(" без доли сомнений", true));
            add(Pair.of(". Кто-то против?", false));
            add(Pair.of(". Самое время подумать о наличии ДМС", true));
            add(Pair.of(" с двух ног", true));
            add(Pair.of(" с ноги", true));
            add(Pair.of(", но обещал в этот раз быть помягче", true));
            add(Pair.of(", заранее принося свои извинения", true));
            add(Pair.of(". Поаккуратнее у стен, парни", true));
            add(Pair.of(". Остается надеяться, что он будет в хорошем настроении", true));
            add(Pair.of(". А ты?", false));
            add(Pair.of(", ведь спорт - это здоровье", true));
            add(Pair.of(" и, возможно, он будет играть против тебя", true));
            add(Pair.of(". Еще не поздно найти другие планы на среду", true));
            add(Pair.of(". Ожидается большое кол-во штрафных ударов", true));
            add(Pair.of(" и мы уже знаем, о чем ты подумал", true));
            add(Pair.of(". Играем full-контакт", true));
            add(Pair.of(", размышляя о правилах игры", true));
            add(Pair.of(", обещая быть в ударе", true));
            add(Pair.of(". Соперникам рекомендуется сделать растяжку", true));
            add(Pair.of(", обещая показать, что такое настоящий прессинг", true));
            add(Pair.of(", переопределяя понятие 'игра корпусом'", true));
            add(Pair.of(". Помните: кости срастаются, а поражение - навсегда", true));
            add(Pair.of(". Помните: синяки - это просто трофеи, которые можно носить с собой", true));
            add(Pair.of(", зная, что правила могут трактоваться по-разному", true));
            add(Pair.of(". На этот раз все серьезно", true));
            add(Pair.of(", вспоминая, что в прошлый раз было принято судейское решение не в его пользу", true));
            add(Pair.of(", зная, что ты сам отдашь ему мяч", true));
        }});
    }};

    public static Optional<Pair<String, Boolean>> getJokeByID(Long id) {
        PlayerTag playerTag = PLAYER_TAG_MAP.get(id);
        if (Objects.isNull(playerTag)) {
            return Optional.empty();
        }

        List<Pair<String, Boolean>> jokes = TAG_JOKES_MAP.get(playerTag);
        if (CollectionUtils.isEmpty(jokes)) {
            return Optional.empty();
        }

        Pair<String, Boolean> randomJoke = jokes.get(RANDOM.nextInt(jokes.size()));
        return Optional.ofNullable(randomJoke);
    }

}
