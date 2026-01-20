create table if not exists football_team
(
    id SERIAL NOT NULL,
    name TEXT UNIQUE NOT NULL,

    CONSTRAINT pk_football_team_id PRIMARY KEY (id)
);