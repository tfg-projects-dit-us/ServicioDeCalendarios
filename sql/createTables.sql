CREATE TABLE absence 
  ( 
     end       date NOT NULL, 
     start     date NOT NULL, 
     doctor_id BIGINT NOT NULL, 
     PRIMARY KEY (doctor_id) 
  ) 
engine=InnoDB; 

CREATE TABLE allowed_shift 
  ( 
     id    INTEGER NOT NULL auto_increment, 
     shift VARCHAR(255) NOT NULL, 
     PRIMARY KEY (id) 
  ) 
engine=InnoDB; 

CREATE TABLE calendar 
  ( 
     month INTEGER NOT NULL, 
     year  INTEGER NOT NULL, 
     PRIMARY KEY (month, year) 
  ) 
engine=InnoDB; 

CREATE TABLE cycle_change 
  ( 
     day_configuration_day            INTEGER NOT NULL, 
     day_configuration_calendar_month INTEGER NOT NULL, 
     day_configuration_calendar_year  INTEGER NOT NULL, 
     cycle_giver_id                   BIGINT, 
     cycle_receiver_id                BIGINT, 
     PRIMARY KEY (day_configuration_day, day_configuration_calendar_month, 
     day_configuration_calendar_year) 
  ) 
engine=InnoDB; 

CREATE TABLE day_configuration 
  ( 
     day               INTEGER NOT NULL, 
     calendar_month    INTEGER NOT NULL, 
     calendar_year     INTEGER NOT NULL, 
     is_working_day    bit NOT NULL, 
     num_consultations INTEGER NOT NULL, 
     num_shifts        INTEGER NOT NULL, 
     PRIMARY KEY (day, calendar_month, calendar_year) 
  ) 
engine=InnoDB; 

CREATE TABLE day_configuration_mandatory_shifts 
  ( 
     day_configuration_day            INTEGER NOT NULL, 
     day_configuration_calendar_month INTEGER NOT NULL, 
     day_configuration_calendar_year  INTEGER NOT NULL, 
     mandatory_shifts_id              BIGINT NOT NULL, 
     PRIMARY KEY (day_configuration_day, day_configuration_calendar_month, 
     day_configuration_calendar_year, mandatory_shifts_id) 
  ) 
engine=InnoDB; 

CREATE TABLE day_configuration_unavailable_shifts 
  ( 
     day_configuration_day            INTEGER NOT NULL, 
     day_configuration_calendar_month INTEGER NOT NULL, 
     day_configuration_calendar_year  INTEGER NOT NULL, 
     unavailable_shifts_id            BIGINT NOT NULL, 
     PRIMARY KEY (day_configuration_day, day_configuration_calendar_month, 
     day_configuration_calendar_year, unavailable_shifts_id) 
  ) 
engine=InnoDB; 

CREATE TABLE day_configuration_unwanted_shifts 
  ( 
     day_configuration_day            INTEGER NOT NULL, 
     day_configuration_calendar_month INTEGER NOT NULL, 
     day_configuration_calendar_year  INTEGER NOT NULL, 
     unwanted_shifts_id               BIGINT NOT NULL, 
     PRIMARY KEY (day_configuration_day, day_configuration_calendar_month, 
     day_configuration_calendar_year, unwanted_shifts_id) 
  ) 
engine=InnoDB; 

CREATE TABLE day_configuration_wanted_shifts 
  ( 
     day_configuration_day            INTEGER NOT NULL, 
     day_configuration_calendar_month INTEGER NOT NULL, 
     day_configuration_calendar_year  INTEGER NOT NULL, 
     wanted_shifts_id                 BIGINT NOT NULL, 
     PRIMARY KEY (day_configuration_day, day_configuration_calendar_month, 
     day_configuration_calendar_year, wanted_shifts_id) 
  ) 
engine=InnoDB; 

CREATE TABLE doctor 
  ( 
     id         BIGINT NOT NULL auto_increment, 
     email      VARCHAR(255) NOT NULL, 
     first_name VARCHAR(255) NOT NULL, 
     last_names VARCHAR(255) NOT NULL, 
     start_date date NOT NULL, 
     status     INTEGER NOT NULL, 
     PRIMARY KEY (id) 
  ) 
engine=InnoDB; 

CREATE TABLE schedule 
  ( 
     calendar_month INTEGER NOT NULL, 
     calendar_year  INTEGER NOT NULL, 
     status         VARCHAR(255) NOT NULL, 
     PRIMARY KEY (calendar_month, calendar_year) 
  ) 
engine=InnoDB; 

CREATE TABLE schedule_day 
  ( 
     day                     INTEGER NOT NULL, 
     schedule_calendar_month INTEGER NOT NULL, 
     schedule_calendar_year  INTEGER NOT NULL, 
     is_working_day          bit NOT NULL, 
     PRIMARY KEY (day, schedule_calendar_month, schedule_calendar_year) 
  ) 
engine=InnoDB; 

CREATE TABLE schedule_day_consultations 
  ( 
     schedule_day_day                     INTEGER NOT NULL, 
     schedule_day_schedule_calendar_month INTEGER NOT NULL, 
     schedule_day_schedule_calendar_year  INTEGER NOT NULL, 
     consultations_id                     BIGINT NOT NULL, 
     PRIMARY KEY (schedule_day_day, schedule_day_schedule_calendar_month, 
     schedule_day_schedule_calendar_year, consultations_id) 
  ) 
engine=InnoDB; 

CREATE TABLE schedule_day_cycle 
  ( 
     schedule_day_day                     INTEGER NOT NULL, 
     schedule_day_schedule_calendar_month INTEGER NOT NULL, 
     schedule_day_schedule_calendar_year  INTEGER NOT NULL, 
     cycle_id                             BIGINT NOT NULL, 
     PRIMARY KEY (schedule_day_day, schedule_day_schedule_calendar_month, 
     schedule_day_schedule_calendar_year, cycle_id) 
  ) 
engine=InnoDB; 

CREATE TABLE schedule_day_shifts 
  ( 
     schedule_day_day                     INTEGER NOT NULL, 
     schedule_day_schedule_calendar_month INTEGER NOT NULL, 
     schedule_day_schedule_calendar_year  INTEGER NOT NULL, 
     shifts_id                            BIGINT NOT NULL, 
     PRIMARY KEY (schedule_day_day, schedule_day_schedule_calendar_month, 
     schedule_day_schedule_calendar_year, shifts_id) 
  ) 
engine=InnoDB; 

CREATE TABLE shift_configuration 
  ( 
     does_cycle_shifts                 bit NOT NULL, 
     has_shifts_only_when_cycle_shifts bit NOT NULL, 
     max_shifts                        INTEGER NOT NULL, 
     min_shifts                        INTEGER NOT NULL, 
     num_consultations                 INTEGER NOT NULL, 
     doctor_id                         BIGINT NOT NULL, 
     PRIMARY KEY (doctor_id) 
  ) 
engine=InnoDB; 

CREATE TABLE shift_configuration_mandatory_shifts 
  ( 
     shift_configuration_doctor_id BIGINT NOT NULL, 
     mandatory_shifts_id           INTEGER NOT NULL, 
     PRIMARY KEY (shift_configuration_doctor_id, mandatory_shifts_id) 
  ) 
engine=InnoDB; 

CREATE TABLE shift_configuration_unavailable_shifts 
  ( 
     shift_configuration_doctor_id BIGINT NOT NULL, 
     unavailable_shifts_id         INTEGER NOT NULL, 
     PRIMARY KEY (shift_configuration_doctor_id, unavailable_shifts_id) 
  ) 
engine=InnoDB; 

CREATE TABLE shift_configuration_unwanted_shifts 
  ( 
     shift_configuration_doctor_id BIGINT NOT NULL, 
     unwanted_shifts_id            INTEGER NOT NULL, 
     PRIMARY KEY (shift_configuration_doctor_id, unwanted_shifts_id) 
  ) 
engine=InnoDB; 

CREATE TABLE shift_configuration_wanted_consultations 
  ( 
     shift_configuration_doctor_id BIGINT NOT NULL, 
     wanted_consultations_id       INTEGER NOT NULL, 
     PRIMARY KEY (shift_configuration_doctor_id, wanted_consultations_id) 
  ) 
engine=InnoDB; 

CREATE TABLE shift_configuration_wanted_shifts 
  ( 
     shift_configuration_doctor_id BIGINT NOT NULL, 
     wanted_shifts_id              INTEGER NOT NULL, 
     PRIMARY KEY (shift_configuration_doctor_id, wanted_shifts_id) 
  ) 
engine=InnoDB; 

ALTER TABLE allowed_shift 
  ADD CONSTRAINT UK_2iiol8es382ett0bgqous6uvv UNIQUE (shift); 

ALTER TABLE doctor 
  ADD CONSTRAINT UK_jdtgexk368pq6d2yb3neec59d UNIQUE (email); 

ALTER TABLE absence 
  ADD CONSTRAINT FKdyb8xj6nsciuo73uqjk7m7hgb FOREIGN KEY (doctor_id) REFERENCES 
  doctor (id); 

ALTER TABLE cycle_change 
  ADD CONSTRAINT FK8ibvn5wa9l7ynsj79kghg90p5 FOREIGN KEY (cycle_giver_id) 
  REFERENCES doctor (id); 

ALTER TABLE cycle_change 
  ADD CONSTRAINT FKg66n5cum7ckj55rmuv6kyji86 FOREIGN KEY (cycle_receiver_id) 
  REFERENCES doctor (id); 

ALTER TABLE cycle_change 
  ADD CONSTRAINT FKqk8qjjbn9bl2bfj6lycy14pal FOREIGN KEY (day_configuration_day, 
  day_configuration_calendar_month, day_configuration_calendar_year) REFERENCES 
  day_configuration (day, calendar_month, calendar_year); 

ALTER TABLE day_configuration 
  ADD CONSTRAINT FKme7sdmxq0leh31a8gm6jmnt4x FOREIGN KEY (calendar_month, 
  calendar_year) REFERENCES calendar (month, year); 

ALTER TABLE day_configuration_mandatory_shifts 
  ADD CONSTRAINT FKjyjmaofk8govijc1c82ee5vhh FOREIGN KEY (mandatory_shifts_id) 
  REFERENCES doctor (id); 

ALTER TABLE day_configuration_mandatory_shifts 
  ADD CONSTRAINT FK9whppx6m1y77a0mitti2s6c0 FOREIGN KEY (day_configuration_day, 
  day_configuration_calendar_month, day_configuration_calendar_year) REFERENCES 
  day_configuration (day, calendar_month, calendar_year); 

ALTER TABLE day_configuration_unavailable_shifts 
  ADD CONSTRAINT FK3c2t1n12cxm8s20jnexqc43ip FOREIGN KEY (unavailable_shifts_id) 
  REFERENCES doctor (id); 

ALTER TABLE day_configuration_unavailable_shifts 
  ADD CONSTRAINT FKnl8hv8u3uobgg1mb487xg0ot1 FOREIGN KEY (day_configuration_day, 
  day_configuration_calendar_month, day_configuration_calendar_year) REFERENCES 
  day_configuration (day, calendar_month, calendar_year); 

ALTER TABLE day_configuration_unwanted_shifts 
  ADD CONSTRAINT FK2xxxl83a3dlphan57bex7r7jn FOREIGN KEY (unwanted_shifts_id) 
  REFERENCES doctor (id); 

ALTER TABLE day_configuration_unwanted_shifts 
  ADD CONSTRAINT FKe95cd2b9y0idrbb0faf1jeb8i FOREIGN KEY (day_configuration_day, 
  day_configuration_calendar_month, day_configuration_calendar_year) REFERENCES 
  day_configuration (day, calendar_month, calendar_year); 

ALTER TABLE day_configuration_wanted_shifts 
  ADD CONSTRAINT FKcalmhkyf1njbxxormf61o96wd FOREIGN KEY (wanted_shifts_id) 
  REFERENCES doctor (id); 

ALTER TABLE day_configuration_wanted_shifts 
  ADD CONSTRAINT FKt4pr628jcfiq5sdqkspxsolan FOREIGN KEY (day_configuration_day, 
  day_configuration_calendar_month, day_configuration_calendar_year) REFERENCES 
  day_configuration (day, calendar_month, calendar_year); 

ALTER TABLE schedule 
  ADD CONSTRAINT FKp6urnuo6hijoru1yl0x3svk8q FOREIGN KEY (calendar_month, 
  calendar_year) REFERENCES calendar (month, year); 

ALTER TABLE schedule_day 
  ADD CONSTRAINT FKcxca3aycgaes34acwytalf1bk FOREIGN KEY ( 
  schedule_calendar_month, schedule_calendar_year) REFERENCES schedule ( 
  calendar_month, calendar_year); 

ALTER TABLE schedule_day_consultations 
  ADD CONSTRAINT FK1ew13celnhap5upxssier9l33 FOREIGN KEY (consultations_id) 
  REFERENCES doctor (id); 

ALTER TABLE schedule_day_consultations 
  ADD CONSTRAINT FKnty77mmm14c4n87e5p05r1odq FOREIGN KEY (schedule_day_day, 
  schedule_day_schedule_calendar_month, schedule_day_schedule_calendar_year) 
  REFERENCES schedule_day (day, schedule_calendar_month, schedule_calendar_year) 
; 

ALTER TABLE schedule_day_cycle 
  ADD CONSTRAINT FK6ws5v8axa6eaq423b35mi2tlo FOREIGN KEY (cycle_id) REFERENCES 
  doctor (id); 

ALTER TABLE schedule_day_cycle 
  ADD CONSTRAINT FK4msobwu8ttnxnaua9owv93j32 FOREIGN KEY (schedule_day_day, 
  schedule_day_schedule_calendar_month, schedule_day_schedule_calendar_year) 
  REFERENCES schedule_day (day, schedule_calendar_month, schedule_calendar_year) 
; 

ALTER TABLE schedule_day_shifts 
  ADD CONSTRAINT FKk6tgus969oavfef73ct57yosh FOREIGN KEY (shifts_id) REFERENCES 
  doctor (id); 

ALTER TABLE schedule_day_shifts 
  ADD CONSTRAINT FKsxehcv6d840vpx76waqral30g FOREIGN KEY (schedule_day_day, 
  schedule_day_schedule_calendar_month, schedule_day_schedule_calendar_year) 
  REFERENCES schedule_day (day, schedule_calendar_month, schedule_calendar_year) 
; 

ALTER TABLE shift_configuration 
  ADD CONSTRAINT FKgwmobdffixhkkaq2scmte58dx FOREIGN KEY (doctor_id) REFERENCES 
  doctor (id); 

ALTER TABLE shift_configuration_mandatory_shifts 
  ADD CONSTRAINT FK5y3jjm92e60bbciy0mnbqmofu FOREIGN KEY (mandatory_shifts_id) 
  REFERENCES allowed_shift (id); 

ALTER TABLE shift_configuration_mandatory_shifts 
  ADD CONSTRAINT FK9xwbrjlvbwrueq5c8y4nxhxui FOREIGN KEY ( 
  shift_configuration_doctor_id) REFERENCES shift_configuration (doctor_id); 

ALTER TABLE shift_configuration_unavailable_shifts 
  ADD CONSTRAINT FKf9gp9rryvqxuq4oyjhxw4qy8j FOREIGN KEY (unavailable_shifts_id) 
  REFERENCES allowed_shift (id); 

ALTER TABLE shift_configuration_unavailable_shifts 
  ADD CONSTRAINT FKncbnbdslijt732jcvrxlwlod2 FOREIGN KEY ( 
  shift_configuration_doctor_id) REFERENCES shift_configuration (doctor_id); 

ALTER TABLE shift_configuration_unwanted_shifts 
  ADD CONSTRAINT FKn4k4he12vfxbkuypkrai6dw7f FOREIGN KEY (unwanted_shifts_id) 
  REFERENCES allowed_shift (id); 

ALTER TABLE shift_configuration_unwanted_shifts 
  ADD CONSTRAINT FKot9ouq2rh8jx9eeh7yxn2e958 FOREIGN KEY ( 
  shift_configuration_doctor_id) REFERENCES shift_configuration (doctor_id); 

ALTER TABLE shift_configuration_wanted_consultations 
  ADD CONSTRAINT FKlijll42efppmi0bt99kcnylgw FOREIGN KEY ( 
  wanted_consultations_id) REFERENCES allowed_shift (id); 

ALTER TABLE shift_configuration_wanted_consultations 
  ADD CONSTRAINT FKmgxbt6dxsw3skiy2hap9p860i FOREIGN KEY ( 
  shift_configuration_doctor_id) REFERENCES shift_configuration (doctor_id); 

ALTER TABLE shift_configuration_wanted_shifts 
  ADD CONSTRAINT FK5wr8sptr3e4f1rhih0qtkiuu7 FOREIGN KEY (wanted_shifts_id) 
  REFERENCES allowed_shift (id); 

ALTER TABLE shift_configuration_wanted_shifts 
  ADD CONSTRAINT FKh6xyu8huv5soou1iq3jjt3ty2 FOREIGN KEY ( 
  shift_configuration_doctor_id) REFERENCES shift_configuration (doctor_id); 