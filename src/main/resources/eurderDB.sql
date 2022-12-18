create table city
(
    zip     text            NOT NULL,
    cityName       text            NOT NULL,
    UNIQUE(zip),
    CONSTRAINT PK_CITY primary key (zip)
);
create sequence city_seq start with 1 increment by 1;

create table role
(
    id     INT            NOT NULL,
    roleName      text            NOT NULL,
    UNIQUE(roleName),
    CONSTRAINT PK_ROLE primary key (id)
);
create sequence role_seq start with 1 increment by 1;

create table urgecy
(
    id     INT            NOT NULL,
    urgencyLvl      text            NOT NULL,
    UNIQUE(urgencyLvl),
    CONSTRAINT PK_URGENCY primary key (id)
);
create sequence urgency_seq start with 1 increment by 1;

create table person
(
    id              integer       NOT NULL,
    firstname      text          NOT NULL,
    lastname     text          NOT NULL,
    email     text          NOT NULL,
    phoneNumber    text          NULL,
    roleId     int          NOT NULL,
    street     text          NULL,
    houseNumber     text          NULL,
    zip  TEXT           NULL,
    UNIQUE(email),
    CONSTRAINT FK_PERSON_ROLE foreign key (roleId) references role(id),
    CONSTRAINT FK_PERSON_CITY foreign key (zip) references city(zip),
    CONSTRAINT PK_PERSON primary key (id)
);
create sequence person_seq start with 1 increment by 1;


create table item
(
    id     INT            NOT NULL,
    itemName      text            NOT NULL,
    description       text            NOT NULL,
    price       double precision            NOT NULL,
    amount       int           NOT NULL,
    CONSTRAINT PK_ITEM primary key (id)
);
create sequence item_seq start with 1 increment by 1;

create table orderLine
(
    id              integer       NOT NULL,
    personId      int          NOT NULL,
    orderDate     date          NOT NULL,
    CONSTRAINT FK_ORDERLINE_PERSON foreign key (personId) references person(id),
    CONSTRAINT PK_ORDERLINE primary key (id)
);
create sequence order_seq start with 1 increment by 1;


create table itemGroup
(
    id              integer       NOT NULL,
    itemId      int          NOT NULL,
    orderId      int          NOT NULL,
    amount     int          NOT NULL,
    shippingDate     date          NOT NULL,
    buyPrice     double precision          NOT NULL,
    urgencyId     int          NOT NULL,
    CONSTRAINT FK_ITEM_GROUP_ITEM foreign key (itemId) references item(id),
    CONSTRAINT FK_ITEM_GROUP_URGENCY foreign key (urgencyId) references urgecy(id),
    CONSTRAINT FK_ITEM_GROUP_ORDER_LINE foreign key (orderId) references orderLine(id),
    CONSTRAINT PK_ITEM_GROUP primary key (id)
);
create sequence itemGroup_seq start with 1 increment by 1;