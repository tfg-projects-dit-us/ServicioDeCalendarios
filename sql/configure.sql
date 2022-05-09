create database MYSQL_DB_NAME;
create user 'MYSQL_USERNAME'@'%' identified by 'MYSQL_PASSWORD';
grant select, insert, delete, update on MYSQL_DB_NAME.* to 'MYSQL_USERNAME'@'%';
