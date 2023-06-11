create table `gandalf`.`trivia_answers`
(
    `button_id`      varchar(100)  not null,
    `user_id`        varchar(100) not null,
    `points`         int          not null,
    `question_id`    varchar(50)  not null,
    `correctness`    tinyint(1)   not null,
    `user_answer`    varchar(1)   not null,
    `correct_answer` varchar(1)   not null,
    `created_on`     timestamp    not null
);

create table `gandalf`.`trivia_questions`
(
    `id`             varchar(36)  not null
        primary key,
    `title`          varchar(256) not null,
    `color`          varchar(15)  not null,
    `points`         int          not null,
    `answer_a`       varchar(500) not null,
    `answer_c`       varchar(500) not null,
    `answer_b`       varchar(500) not null,
    `answer_d`       varchar(500) not null,
    `correct_answer` varchar(1)   not null,
    `created_on`     timestamp    not null,
    `author`         varchar(50)  not null,
    `image_url`      varchar(250) not null,
    `complexity`     varchar(10)  not null,
    `group`          varchar(15)  not null
);

create index `trivia_questions_id_index`
    on `gandalf`.`trivia_questions` (`id`);

