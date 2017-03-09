#------------------------------------------------------------
#        Script MySQL.
#------------------------------------------------------------


#------------------------------------------------------------
# Table: User
#------------------------------------------------------------

CREATE TABLE User(
        userId       int (11) Auto_increment  NOT NULL ,
        userName     Varchar (50) NOT NULL ,
        userPassword Varchar (256) NOT NULL ,
        PRIMARY KEY (userId ) ,
        UNIQUE (userName )
)ENGINE=InnoDB;


#------------------------------------------------------------
# Table: ForumPost
#------------------------------------------------------------

CREATE TABLE ForumPost(
        forumPostId         int (11) Auto_increment  NOT NULL ,
        forumPostCreateDate Date NOT NULL ,
        forumPostModifyDate Date NOT NULL ,
        forumPostContent    Mediumtext NOT NULL ,
        forumThreadId       Int NOT NULL ,
        userId              Int NOT NULL ,
        PRIMARY KEY (forumPostId )
)ENGINE=InnoDB;


#------------------------------------------------------------
# Table: ForumThread
#------------------------------------------------------------

CREATE TABLE ForumThread(
        forumThreadId   int (11) Auto_increment  NOT NULL ,
        forumThreadName Varchar (50) NOT NULL ,
        projectId       Int NOT NULL ,
        userId          Int NOT NULL ,
        PRIMARY KEY (forumThreadId )
)ENGINE=InnoDB;


#------------------------------------------------------------
# Table: Project
#------------------------------------------------------------

CREATE TABLE Project(
        projectId          int (11) Auto_increment  NOT NULL ,
        projectName        Varchar (50) NOT NULL ,
        projectPath        Varchar (255) NOT NULL ,
        projectDescription Text NOT NULL ,
        PRIMARY KEY (projectId ) ,
        UNIQUE (projectName )
)ENGINE=InnoDB;


#------------------------------------------------------------
# Table: WikiPage
#------------------------------------------------------------

CREATE TABLE WikiPage(
        wikiPageId      int (11) Auto_increment  NOT NULL ,
        wikiPageName    Varchar (50) NOT NULL ,
        wikiPageContent Mediumtext NOT NULL ,
        projectId       Int NOT NULL ,
        PRIMARY KEY (wikiPageId ) ,
        UNIQUE (wikiPageName )
)ENGINE=InnoDB;


#------------------------------------------------------------
# Table: Ticket
#------------------------------------------------------------

CREATE TABLE Ticket(
        ticketId      int (11) Auto_increment  NOT NULL ,
        ticketName    Varchar (50) NOT NULL ,
        ticketBadge   Varchar (50) ,
        ticketContent Mediumtext ,
        projectId     Int NOT NULL ,
        userId        Int NOT NULL ,
        userId_User   Int ,
        PRIMARY KEY (ticketId )
)ENGINE=InnoDB;


#------------------------------------------------------------
# Table: Todo
#------------------------------------------------------------

CREATE TABLE Todo(
        todoId      int (11) Auto_increment  NOT NULL ,
        todoName    Varchar (50) NOT NULL ,
        todoContent Text NOT NULL ,
        projectId   Int NOT NULL ,
        PRIMARY KEY (todoId )
)ENGINE=InnoDB;


#------------------------------------------------------------
# Table: Belongs
#------------------------------------------------------------

CREATE TABLE Belongs(
        belongsRight Integer NOT NULL ,
        projectId    Int NOT NULL ,
        userId       Int NOT NULL ,
        PRIMARY KEY (projectId ,userId )
)ENGINE=InnoDB;

ALTER TABLE ForumPost ADD CONSTRAINT FK_ForumPost_forumThreadId FOREIGN KEY (forumThreadId) REFERENCES ForumThread(forumThreadId);
ALTER TABLE ForumPost ADD CONSTRAINT FK_ForumPost_userId FOREIGN KEY (userId) REFERENCES User(userId);
ALTER TABLE ForumThread ADD CONSTRAINT FK_ForumThread_projectId FOREIGN KEY (projectId) REFERENCES Project(projectId);
ALTER TABLE ForumThread ADD CONSTRAINT FK_ForumThread_userId FOREIGN KEY (userId) REFERENCES User(userId);
ALTER TABLE WikiPage ADD CONSTRAINT FK_WikiPage_projectId FOREIGN KEY (projectId) REFERENCES Project(projectId);
ALTER TABLE Ticket ADD CONSTRAINT FK_Ticket_projectId FOREIGN KEY (projectId) REFERENCES Project(projectId);
ALTER TABLE Ticket ADD CONSTRAINT FK_Ticket_userId FOREIGN KEY (userId) REFERENCES User(userId);
ALTER TABLE Ticket ADD CONSTRAINT FK_Ticket_userId_User FOREIGN KEY (userId_User) REFERENCES User(userId);
ALTER TABLE Todo ADD CONSTRAINT FK_Todo_projectId FOREIGN KEY (projectId) REFERENCES Project(projectId);
ALTER TABLE Belongs ADD CONSTRAINT FK_Belongs_projectId FOREIGN KEY (projectId) REFERENCES Project(projectId);
ALTER TABLE Belongs ADD CONSTRAINT FK_Belongs_userId FOREIGN KEY (userId) REFERENCES User(userId);
