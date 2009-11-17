CREATE DATABASE plugin;
USE plugin;

DROP TABLE IF EXISTS eclipse_ProjectTable;
CREATE TABLE eclipse_ProjectTable (
   project varchar(40) NOT NULL,
   file varchar(120) NOT NULL,
   username varchar(30) NOT NULL,
   modified timestamp NOT NULL,
   PRIMARY KEY (project, file, username, modified)
);
