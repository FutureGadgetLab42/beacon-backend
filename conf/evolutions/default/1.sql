# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table beacon (
  id                            bigint auto_increment not null,
  beacon_key                    varchar(255),
  beacon_name                   varchar(255),
  description                   varchar(255),
  creation_date                 datetime not null,
  constraint pk_beacon primary key (id)
);

create table beacon_rendezvous (
  id                            bigint auto_increment not null,
  beacon_key                    varchar(255),
  remote_address                varchar(255),
  creation_date                 datetime not null,
  constraint pk_beacon_rendezvous primary key (id)
);


# --- !Downs

drop table if exists beacon;

drop table if exists beacon_rendezvous;

