CREATE TABLE Users(
    name VARCHAR PRIMARY KEY,
    password VARCHAR
);

CREATE TABLE Tasks(
    id UUID PRIMARY KEY,
    userName VARCHAR REFERENCES Users(name) ON UPDATE CASCADE ON DELETE CASCADE,
    title VARCHAR,
    done BOOLEAN,
    UNIQUE(userName, title)
);


INSERT INTO Users(name, password)
VALUES('admin', 'admin');

