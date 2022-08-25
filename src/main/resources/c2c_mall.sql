create database c2c_mall;

use c2c_mall;

create table if not exists report_task
(
	id int unsigned auto_increment
		primary key,
	type varchar(50) default '' not null,
	report_user_id int not null,
	report_content varchar(255) default '' not null,
	target_id int not null,
	vote_result tinyint not null
);

create table if not exists report_task_vote
(
	id int unsigned auto_increment
		primary key,
	reviewer_id int not null,
	report_task_id int not null,
	vote_result tinyint not null
);

create table if not exists reviewer_task_status
(
	id int unsigned auto_increment
		primary key,
	reviewer_id int not null,
	report_task_id int not null,
	status tinyint not null
);

create table if not exists reward_coin
(
	id int unsigned auto_increment
		primary key,
	reviewer_id int not null,
	coins int not null
);

