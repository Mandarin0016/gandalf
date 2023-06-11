create table gandalf.trivia_questions
(
    id             VARCHAR(36)  not null,
    title          VARCHAR(256) not null,
    color          VARCHAR(15)  not null,
    points         int          not null,
    answer_a       VARCHAR(500) not null,
    answer_c       VARCHAR(500) not null,
    answer_b       VARCHAR(500) not null,
    answer_d       VARCHAR(500) not null,
    correct_answer VARCHAR(1)   not null,
    created_on     TIMESTAMP    not null,
    author         VARCHAR(50)  not null,
    image_url      VARCHAR(250) not null
);

create index trivia_questions_id_index
    on gandalf.trivia_questions (id);

alter table gandalf.trivia_questions
    add constraint trivia_questions_pk
        primary key (id);

