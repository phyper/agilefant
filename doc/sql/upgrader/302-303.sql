INSERT INTO settings (`name`, `value`, `description`) VALUES ('AgilefantDatabaseVersion', '303', 'Agilefant database version') ON DUPLICATE KEY UPDATE `value`="303";
create table team_user_AUD (REV integer not null, Team_id integer not null, User_id integer not null, REVTYPE tinyint, primary key (REV, Team_id, User_id)) ENGINE=InnoDB;
create table teams_AUD (id integer not null, REV integer not null, REVTYPE tinyint, description longtext, name varchar(255), primary key (id, REV)) ENGINE=InnoDB;