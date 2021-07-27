-- Create database and assign power user.
CREATE USER superuser WITH PASSWORD 'superuser';
CREATE DATABASE superchat_database OWNER superuser ENCODING 'UTF8';
GRANT ALL ON DATABASE superchat_database TO superuser;
ALTER USER superuser CREATEDB;
\c superchat_database

-- Add some extensions
create extension if not exists "uuid-ossp";
create extension if not exists citext;

--** Tables are created and initialize some data like below. For the purpose of future scaling, table must
--** not have reference out side of its own service.
------------------------------------------------------------
-- Core service tables
CREATE TABLE source (
    id            varchar(8)        PRIMARY KEY,
    description   text              NOT NULL DEFAULT ''
);
ALTER TABLE source OWNER TO superuser;
INSERT INTO "source" (id, description) VALUES ('SC', 'Superchat');  -- Internal, default
INSERT INTO "source" (id, description) VALUES ('FB', 'Facebook');   -- Not supported
INSERT INTO "source" (id, description) VALUES ('TG', 'Telegram');   -- Not supported
INSERT INTO "source" (id, description) VALUES ('GM', 'Gmail');      -- Not supported
INSERT INTO "source" (id, description) VALUES ('AN', 'Anonymous');  -- Default external

CREATE TABLE chat_type (
    id            varchar(8)        PRIMARY KEY,
    description   text              NOT NULL DEFAULT ''
);
ALTER TABLE chat_type OWNER TO superuser;
INSERT INTO chat_type (id, description) VALUES ('DM', 'DirectMessage'); -- Default
INSERT INTO chat_type (id, description) VALUES ('GR', 'Group');         -- Not supported
INSERT INTO chat_type (id, description) VALUES ('CH', 'Channel');       -- Not supported

-- This is to shard message table - not really happens yet.
CREATE TABLE room (
    id                     serial      PRIMARY KEY,
    participant1_id        uuid,       -- Direct message
    participant2_id        uuid,       -- Direct message
    group_id               uuid,       -- Group(Not supported)
    channel_id             uuid,       -- Chanel(Not supported)
    UNIQUE (participant1_id, participant2_id),
    UNIQUE (group_id),
    UNIQUE (channel_id)
);
ALTER TABLE room OWNER TO superuser;
ALTER TABLE room add CONSTRAINT room_chk CHECK ((participant1_id IS NOT NULL AND participant2_id IS NOT NULL)
OR group_id IS NOT NULL
OR channel_id IS NOT NULL);

-- Auth and user service tables
CREATE TABLE role (
    id            varchar(8)        PRIMARY KEY,
    description   text              NOT NULL DEFAULT ''
);
ALTER TABLE role OWNER TO superuser;
INSERT INTO role (id, description) VALUES ('ADMIN', 'Superchat admin');
INSERT INTO role (id, description) VALUES ('USER', 'Superchat internal and external user');

CREATE TABLE "user" (
    id           uuid         PRIMARY KEY DEFAULT uuid_generate_v4(),
    username     varchar(64)  NOT NULL UNIQUE,
    email        citext       UNIQUE,
    salt         varchar(32), -- Always 16 bytes in HEX form
    password     text,
    source       varchar(8)   NOT NULL DEFAULT 'SC', -- External source not supported yet
    active       boolean      NOT NULL DEFAULT true
);
ALTER TABLE "user" OWNER TO superuser;
INSERT INTO "user" ("username", "email", "salt", "password")
VALUES ('superchatadmin', 'admin@superchat.de', 'e33339d9c7bed9be4019d4012d48c66b', '$2a$05$2xK30ac80Z3.EbO/JShEYuEq9reotaaRJ/2oejr.KPug.SCjoLaGW');
INSERT INTO "user" ("username", "email", "salt", "password") -- raw password superchatadmin
VALUES ('andodevel', 'ando.devel@gmail.com', 'c3083ab1477a600a0801d0f17314286d', '$2a$05$uue4qSb4W.mG.bBvavOmZOCNM2jq3dcuUzlwXM/FoQ80J3c1McoBS');
INSERT INTO "user" ("username", "source")  -- raw password andodevel
VALUES ('dummyfacebookuser', 'FB');
INSERT INTO "user" ("username", "source")
VALUES ('dummygmailuser', 'GM');

CREATE TABLE user_role (
    user_id       uuid              REFERENCES "user"(id) ON DELETE CASCADE,
    role_id       varchar(8)        REFERENCES role(id) ON UPDATE CASCADE,
    CONSTRAINT user_role_pkey PRIMARY KEY (user_id, role_id)
);
ALTER TABLE user_role OWNER TO superuser;
INSERT INTO user_role (user_id, role_id) VALUES ((SELECT "id" from "user" where "username" = 'superchatadmin'), 'ADMIN');
INSERT INTO user_role (user_id, role_id) VALUES ((SELECT "id" from "user" where "username" = 'superchatadmin'), 'USER');
INSERT INTO user_role (user_id, role_id) VALUES ((SELECT "id" from "user" where "username" = 'andodevel'), 'USER');
INSERT INTO user_role (user_id, role_id) VALUES ((SELECT "id" from "user" where "username" = 'dummyfacebookuser'), 'USER');
INSERT INTO user_role (user_id, role_id) VALUES ((SELECT "id" from "user" where "username" = 'dummygmailuser'), 'USER');

CREATE TABLE user_info (
    user_id       uuid               PRIMARY KEY REFERENCES "user"(id) ON DELETE CASCADE,
    firstname     varchar(64)        DEFAULT '',
    lastname      varchar(64)        DEFAULT '',
    created       timestamp WITHOUT time zone DEFAULT now()
);
ALTER TABLE user_info OWNER TO superuser;
INSERT INTO "user_info" ("user_id", "firstname")
VALUES ((SELECT "id" from "user" where "username" = 'superchatadmin'), 'Admin');
INSERT INTO "user_info" ("user_id", "firstname", "lastname")
VALUES ((SELECT "id" from "user" where "username" = 'andodevel'), 'An', 'Do');
INSERT INTO "user_info" ("user_id", "firstname", "lastname")
VALUES ((SELECT "id" from "user" where "username" = 'dummyfacebookuser'), 'Fynn', 'Kliemann');
INSERT INTO "user_info" ("user_id", "firstname", "lastname")
VALUES ((SELECT "id" from "user" where "username" = 'dummygmailuser'), 'Uberto', 'Bauer');

------------------------------------------------------------
-- Message service tables
CREATE TABLE message (
    id            uuid              PRIMARY KEY DEFAULT uuid_generate_v4(),
    sender_id     uuid              NOT NULL,
    receiver_id   uuid              NOT NULL,
    type          varchar(8)        NOT NULL DEFAULT 'DM',
    source        varchar(8)        NOT NULL DEFAULT 'SC',
    room_id       uuid, -- TODO: Room is better way to manage the message and scale the system. Unfortunately, not support yet!
    content       text              NOT NULL DEFAULT '',
    created       timestamp WITHOUT time zone DEFAULT now()
);
ALTER TABLE message OWNER TO superuser;
CREATE INDEX message_sender_id ON message (sender_id);
CREATE INDEX message_receiver_id ON message (receiver_id);
CREATE INDEX message_created ON message (created); -- No need for composite index, let optimizer do it.

------------------------------------------------------------
-- Webhook service tables
CREATE TABLE webhook (
    id            uuid             PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id       uuid             NOT NULL,
    source_id     varchar(8)       NOT NULL,
    created       timestamp WITHOUT time zone DEFAULT now()
);
ALTER TABLE webhook OWNER TO superuser;
CREATE INDEX webhook_user_id ON webhook (user_id);
