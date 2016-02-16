create database webplatform_test;
use webplatform_test;

######## record 部分 
create table record
(
	id int unsigned auto_increment,
	name varchar(50) default null,
	age int default null,

	primary key (id)
);

create table student
(
	id int unsigned auto_increment,
	name varchar(50) default null,
	depart varchar(50) default null,

	primary key (id)
);

create table name
(
	id int unsigned auto_increment,
	value varchar(50) not null,
	text varchar(50) not null,

	primary key (id)
);

create table info
(
	id int unsigned auto_increment,
	info varchar(50) not null,
	record_id int unsigned not null comment '外键',

	primary key (id),
	foreign key (record_id) references record (id) on delete cascade on update cascade
);

#insert into user(name, age) values('abcd', 11),('qwer', 30),('qindeyu',21),('sunwenqin',20);

#insert into student(name,depart) values('qindeyu','ise'),('sunwenqin','ise');

# 创建视图
create view v_record as
	select record.name, record.age, student.depart, record.id as record_id, student.id as stu_id 
from record
join student on record.name=student.name
;


###### plain core部分 
# 创建用户表
create table core1
(
	id int unsigned auto_increment,
	username varchar(50) unique not null comment '用户名',
	passwd varchar(50) not null comment '密码',
	name varchar(50) not null default '' comment '姓名',
	email varchar(50) not null default '' comment '电子邮箱',
	
	primary key (id)
);

# 创建其他信息表
create table info1
(
	id int unsigned auto_increment,
	department varchar(50) not null comment '系别',
	room varchar(50) not null comment '宿舍',
	core1_id int unsigned not null comment 'core1 id，外键',

	primary key (id),
	foreign key (core1_id) references core1 (id) on delete cascade on update cascade
);


# 视图
create view v_core1 as
select core1.username, core1.passwd, core1.name, core1.email, core1.id,
	info1.department, info1.room, info1.id as info1_id
from core1
join info1 on info1.core1_id = core1.id
;

###### user 部分 
create table user
(
	id int unsigned auto_increment,
	username varchar(50) unique not null comment '用户名',
	passwd varchar(50) not null comment '密码',
	status set('有效','无效') not null default '有效' comment '状态',
	name varchar(50) not null default '' comment '姓名',
	email varchar(50) not null default '' comment '电子邮箱',
	
	primary key (id)
);

# 创建其他用户表
create table user12
(
	id int unsigned auto_increment,
	username varchar(50) unique not null comment '用户名',
	passwd varchar(50) not null comment '密码',

	primary key (id)
);

# 创建用户的其他信息表
create table info12
(
	id int unsigned auto_increment,
	identity_number varchar(50) unique not null comment '身份证号',
	user12_id int unsigned not null comment 'user1表的外键',

	primary key (id),
	foreign key (user12_id) references user12 (id) on delete cascade on update cascade
	
);

# 用户表
create view v_user12 as
select user12.id, user12.username, user12.passwd, info12.identity_number, info12.id as info12_id
from user12
join info12 on info12.user12_id = user12.id
;



##### plain service 部分
# 创建用户表
create table core1s
(
	id int unsigned auto_increment,
	username varchar(50) unique not null comment '用户名',
	passwd varchar(50) not null comment '密码',
	name varchar(50) not null default '' comment '姓名',
	email varchar(50) not null default '' comment '电子邮箱',
	
	primary key (id)
);

# 创建其他信息表
create table info1s
(
	id int unsigned auto_increment,
	department varchar(50) not null comment '系别',
	room varchar(50) not null comment '宿舍',
	core1s_id int unsigned not null comment 'core1s id，外键',

	primary key (id),
	foreign key (core1s_id) references core1s (id) on delete cascade on update cascade
);


# 视图
create view v_core1s as
select core1s.username, core1s.passwd, core1s.name, core1s.email, core1s.id,
	info1s.department, info1s.room, info1s.id as info1s_id
from core1s
join info1s on info1s.core1s_id = core1s.id
;



###### web 部分
create table core1w
(
	id int unsigned auto_increment,
	username varchar(50) unique not null comment '用户名',
	passwd varchar(50) not null comment '密码',
	name varchar(50) not null default '' comment '姓名',
	email varchar(50) not null default '' comment '电子邮箱',
	
	primary key (id)
);