# Business schema

# --- !Ups

CREATE SEQUENCE biz_id_seq;
CREATE TABLE biz (
  id integer NOT NULL DEFAULT nextval('biz_id_seq'),
  name varchar(255)
);

# --- !Downs
DROP TABLE biz;
DROP SEQUENCE biz_id_seq;
