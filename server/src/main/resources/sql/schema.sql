-- ----------------------------
-- 系统信息
-- ----------------------------
DROP TABLE IF EXISTS sys_info;

CREATE TABLE sys_info(
    version VARCHAR(255) not null
);

-- ----------------------------
-- Permission
-- ----------------------------
DROP TABLE IF EXISTS sys_permission;
CREATE TABLE sys_permission
(
    id          SERIAL PRIMARY KEY,
    path        VARCHAR(255) NOT NULL,
    description VARCHAR(100)
);

-- ----------------------------
-- Role
-- ----------------------------
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(50) NOT NULL,
    description VARCHAR(100)
);

-- ----------------------------
-- User
-- ----------------------------
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user
(
    id            SERIAL PRIMARY KEY,
    username      VARCHAR(50) NOT NULL,
    password      VARCHAR(50) NOT NULL,
    nickname      VARCHAR(50) NOT NULL,
    avatar        VARCHAR(255),
    email         VARCHAR(255),
    status        VARCHAR(20) NOT NULL,
    bio           TEXT,
    created_at    TIMESTAMP   NOT NULL,
    updated_at    TIMESTAMP   NOT NULL,
    last_login_at TIMESTAMP
);

-- ----------------------------
-- User And Role Relation
-- ----------------------------
DROP TABLE IF EXISTS rel_user_role;
CREATE TABLE rel_user_role
(
    id      SERIAL PRIMARY KEY,
    user_id int4 NOT NULL,
    role_id int4 NOT NULL
);

-- ----------------------------
-- Category
-- ----------------------------
DROP TABLE IF EXISTS category;
CREATE TABLE category
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    created_at  TIMESTAMP    NOT NULL,
    updated_at  TIMESTAMP    NOT NULL
);

-- ----------------------------
-- Tag
-- ----------------------------
DROP TABLE IF EXISTS tag;
CREATE TABLE tag
(
    id         SERIAL PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP    NOT NULL
);

-- ----------------------------
-- Article
-- ----------------------------
DROP TABLE IF EXISTS article;
CREATE TABLE article
(
    id           SERIAL PRIMARY KEY,
    title        VARCHAR(255) NOT NULL,
    content      VARCHAR(255) NOT NULL,
    content_type VARCHAR(20)  NOT NULL,
    created_at   TIMESTAMP    NOT NULL,
    updated_at   TIMESTAMP    NOT NULL,
    author_id    int4         NOT NULL,
    category_id  int4         NOT NULL
);

-- ----------------------------
-- User And Role Relation
-- ----------------------------
DROP TABLE IF EXISTS rel_tag_article;
CREATE TABLE rel_tag_article
(
    id         SERIAL PRIMARY KEY,
    article_id int4 NOT NULL,
    tag_id     int4 NOT NULL
);

-- ----------------------------
-- Comment
-- ----------------------------
DROP TABLE IF EXISTS comment;
CREATE TABLE comment
(
    id           SERIAL PRIMARY KEY,
    content      VARCHAR(255) NOT NULL,
    content_type VARCHAR(20)  NOT NULL,
    created_at   TIMESTAMP    NOT NULL,
    updated_at   TIMESTAMP    NOT NULL,
    author_id    int4         NOT NULL,
    article_id   int4         NOT NULL,
    parent_id    int4
);