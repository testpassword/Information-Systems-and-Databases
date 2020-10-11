--стержневая
CREATE TABLE planetary_system
(
	ssId  SERIAL PRIMARY KEY,
	name  TEXT NOT NULL
);

--стержневая
CREATE TABLE planet
(
	planetId 	  	  SERIAL PRIMARY KEY,
	name	 	  	  TEXT NOT NULL,
	planetarySystemId INTEGER REFERENCES planetary_system ON DELETE CASCADE,
	isSun		  	  BOOLEAN,
	thereIsLife	  	  BOOLEAN
);

--характеристическая
CREATE TYPE creature_class AS ENUM ('человек', 'робот', 'инопланетянин');

--стержневая
CREATE TABLE creature
(
	crId			SERIAL PRIMARY KEY,
	name 			TEXT NOT NULL,
	class 			CREATURE_CLASS NOT NULL,
	planetOfOrigin  INTEGER REFERENCES planet ON DELETE RESTRICT
);

--стержневая
CREATE TABLE antenna
(
	antennaId SERIAL PRIMARY KEY,
	name 	  TEXT,
	planetId  INTEGER NOT NULL REFERENCES planet
);

--стержневая
CREATE TABLE message
(
	messageId SERIAL PRIMARY KEY,
	date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	data TEXT,
	senderId INTEGER NOT NULL REFERENCES antenna
);

--ассоциативная
CREATE TABLE transmission
(
	messageId INTEGER NOT NULL REFERENCES message,
	reciverId INTEGER NOT NULL REFERENCES antenna
);

INSERT INTO planetary_system (name) VALUES 
	('Солнечная система'), 
	('TRAPPIST-1'),
	('UX Tau A');

INSERT INTO planet (name, planetarySystemId, isSun, thereIsLife) VALUES
	('Земля', 1, false, true),
    ('ФлексПланет', 2, true),
	('Солнце', 1, true, false);

INSERT INTO planet (name, planetarySystemId, isSun) VALUES ('TRAPPIST-1b', 2, false);

INSERT INTO creature (name, class, planetOfOrigin) VALUES
	('Боумен', 'человек', 1),
	('Пул', 'человек', 1),
    ('Кекер', 'инопланетянин', 2),
    ('Лолер', 'инопланетянин', 4),
	('HAL9000', 'робот', 1);

INSERT INTO creature (name, class) VALUES ('Чёрный монолит', 'инопланетянин');

INSERT INTO antenna (name, planetId) VALUES 
	('Discovery', 1), 
	('Beyond', 3);

INSERT INTO message (data, senderId) VALUES
	('За бесконечность', 1),
	('Поехали!', 1),
	('Земля в иллюминатарееее', 1),
	('Небрежность в космосе - простейший способ самоубийства', 2),
	('Чем совершеннее техника передачи информации, тем более заурядным, пошлым, серым становится её содержание', 2),
	('Миссия выполнена', 2);

INSERT INTO transmission VALUES
	(1, 1),
	(1, 2),
	(2, 1),
	(3, 1),
	(3, 2);