create table if not exists football_player
(
    id SERIAL NOT NULL,
    telegram_id BIGINT NOT NULL,
    username TEXT NOT NULL,
    timestamp TIMESTAMPTZ NOT NULL,
    status TEXT NOT NULL,
    called_friends INTEGER NOT NULL,
    football_team_name TEXT NOT NULL,

    CONSTRAINT pk_football_player_id PRIMARY KEY (id),
    CONSTRAINT fk_football_player_football_team_name FOREIGN KEY (football_team_name) REFERENCES football_team (name) ON DELETE CASCADE
);