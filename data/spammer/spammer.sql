-- Скрипт архитектуры БД для задачи "0.2. PreparedStatement [#379307]".

CREATE SCHEMA spammer;

DROP TABLE IF EXISTS spammer.users;

CREATE TABLE spammer.users(
	id INTEGER GENERATED ALWAYS AS IDENTITY,
	"name" TEXT,
	email TEXT,
	CONSTRAINT users_pk PRIMARY KEY(id)
);

CREATE UNIQUE INDEX users_name_uidx ON spammer.users("name");