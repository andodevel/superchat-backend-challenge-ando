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
    name          varchar(64)       NOT NULL UNIQUE
);
ALTER TABLE source OWNER TO superuser;
INSERT INTO "source" (id, name) VALUES ('SC', 'Superchat');  -- Internal, default
INSERT INTO "source" (id, name) VALUES ('FB', 'Facebook');   -- Not supported
INSERT INTO "source" (id, name) VALUES ('TG', 'Telegram');   -- Not supported
INSERT INTO "source" (id, name) VALUES ('GM', 'Gmail');      -- Not supported
INSERT INTO "source" (id, name) VALUES ('AN', 'Anonymous');  -- Default external

CREATE TABLE chat_type (
    id            varchar(8)        PRIMARY KEY,
    name          varchar(64)       NOT NULL UNIQUE
);
ALTER TABLE chat_type OWNER TO superuser;
INSERT INTO chat_type (id, name) VALUES ('DM', 'DirectMessage'); -- Default
INSERT INTO chat_type (id, name) VALUES ('GR', 'Group');         -- Not supported
INSERT INTO chat_type (id, name) VALUES ('CH', 'Channel');       -- Not supported

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
ALTER TABLE room add CONSTRAINT chk_room CHECK ((participant1_id IS NOT NULL AND participant2_id IS NOT NULL)
OR group_id IS NOT NULL
OR channel_id IS NOT NULL);

-- User service tables
CREATE TABLE "user" (
    id           uuid         PRIMARY KEY DEFAULT uuid_generate_v4(),
    username     text         NOT NULL UNIQUE,
    email        citext       UNIQUE,
    salt         text,
    password     text,
    active       boolean      NOT NULL DEFAULT true,
    source       varchar(8)   NOT NULL DEFAULT 'SC' -- External source not supported yet
);
ALTER TABLE "user" OWNER TO superuser;
INSERT INTO "user" ("username", "email", "salt", "password")
VALUES ('superchatadmin', 'admin@superchat.de', 'tothemoon', '$2a$10$jx5agtJfsTXHdo5Acy0ZouS3pIQbpALAq7ViPpsLfhheJaHMBcBAq');
INSERT INTO "user" ("username", "email", "salt", "password")
VALUES ('andodevel', 'ando.devel@gmail.com', 'tothemoon', '$2a$10$jx5agtJfsTXHdo5Acy0ZouS3pIQbpALAq7ViPpsLfhheJaHMBcBAq');

CREATE TABLE user_info (
    user_id       uuid        PRIMARY KEY REFERENCES "user"(id) ON DELETE CASCADE,
    firstname     text,
    lastname      text,
    properties    json,
    created       timestamp WITHOUT time zone DEFAULT (now() at time zone 'utc')
);
ALTER TABLE user_info OWNER TO superuser;
INSERT INTO "user_info" ("user_id", "firstname")
VALUES ((SELECT "id" from "user" where "username" = 'superchatadmin'), 'Admin');
INSERT INTO "user_info" ("user_id", "firstname", "lastname")
VALUES ((SELECT "id" from "user" where "username" = 'andodevel'), 'An', 'Do');

------------------------------------------------------------
-- Message service tables
CREATE TABLE message (
    id            uuid              PRIMARY KEY DEFAULT uuid_generate_v4(),
    sender_id     uuid              NOT NULL,
    receiver_id   uuid              NOT NULL,
    type          varchar(8)        NOT NULL DEFAULT 'DM',
    source        varchar(8)        NOT NULL DEFAULT 'SC',
    room_id       uuid              NOT NULL,
    content       text              NOT NULL DEFAULT '',
    created       timestamp WITHOUT time zone DEFAULT (now() at time zone 'utc')
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
    created       timestamp WITHOUT time zone DEFAULT (now() at time zone 'utc')
);
ALTER TABLE webhook OWNER TO superuser;
CREATE INDEX webhook_user_id ON webhook (user_id);
