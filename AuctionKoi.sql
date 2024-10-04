CREATE DATABASE AuctionKoi
Go

USE AuctionKoi
Go

CREATE TABLE Users(
	id  int IDENTITY(1,1) primary key,
	username varchar(70) not null,
	[password] varchar(100) not null,
	[role] int,
	fullname nvarchar(250) not null,
	phone varchar(20),
	[address] nvarchar(250),
	is_active bit default 1,
	user_created_date datetime default GETDATE(),
	user_updated_date datetime default GETDATE()
);
Go

CREATE TRIGGER trg_UpdateUserUpdatedDate
ON Users
AFTER UPDATE
AS
BEGIN
    -- Update the UserUpdatedDate to the current date/time when any field is updated
    UPDATE Users
    SET user_updated_date = GETDATE()
    FROM Users u
    INNER JOIN inserted i ON u.id = i.id;
END;
GO

CREATE TABLE KoiFishs(
	id int IDENTITY(1,1) primary key,
	breeder_id int not null,
	[name] varchar(250) not null,
	sex int not null,
	size float not null,
	age int not null,
	[description] nvarchar(2000),
	[image] varchar(2000) not null,
	[status] int,
	fish_created_date datetime default GETDATE(),
	fish_updated_date datetime default GETDATE(),
	FOREIGN KEY (breeder_id) REFERENCES Users(id),
);
Go

CREATE TRIGGER trg_UpdateFishUpdatedDate
ON KoiFishs
AFTER UPDATE
AS
BEGIN
    -- Update the FishUpdatedDate to the current date/time when any field is updated
    UPDATE KoiFishs
    SET fish_updated_date = GETDATE()
    FROM KoiFishs f
    INNER JOIN inserted i ON f.id = i.id;
END;
GO

CREATE TABLE KoiFishImages(
	id int IDENTITY(1,1) primary key,
	koiFish_id int not null,
	imageUrl varchar(255),
	uploaded_date datetime default GETDATE(),
	FOREIGN KEY (koiFish_id) REFERENCES KoiFishs(id),
);
Go

CREATE TABLE KoiFishVideos(
	id int IDENTITY(1,1) primary key,
	koiFish_id int not null,
	videoUrl varchar(255),
	uploaded_date datetime default GETDATE(),
	FOREIGN KEY (koiFish_id) REFERENCES KoiFishs(id),
);
Go

CREATE TABLE Auctions(
	id int IDENTITY(1,1) primary key,
	fish_id int not null,
	winner_id int null,
	start_time datetime not null,
	end_time datetime not null,
	current_price float not null,
	[status] int,
	FOREIGN KEY (fish_id) REFERENCES KoiFishs(id),
	FOREIGN KEY (winner_id) REFERENCES Users(id)
);
Go

CREATE TABLE AuctionParticipants (
    id int IDENTITY(1,1) primary key,
    auction_id int not null,
    user_id int not null,
    deposit_amount float not null,
    join_date datetime default GETDATE(),
    FOREIGN KEY (auction_id) REFERENCES Auctions(id),
    FOREIGN KEY (user_id) REFERENCES Users(id)
);
GO

CREATE TABLE AuctionRequests(
	id int IDENTITY(1,1) primary key,
	breeder_id int not null,
	fish_id int not null,
	auction_id int not null,
	buy_out float not null,
	start_price float not null,
	increment_price float not null,
	method_type int not null,
	request_created_date datetime default GETDATE(),
	request_updated_date datetime default GETDATE(),
	request_status int not null,
	FOREIGN KEY (auction_id) REFERENCES Auctions(id),
	FOREIGN KEY (breeder_id) REFERENCES Users(id),
	FOREIGN KEY (fish_id) REFERENCES KoiFishs(id)
);
Go

CREATE TRIGGER trg_UpdateRequestUpdatedDate
ON AuctionRequests
AFTER UPDATE
AS
BEGIN
    -- Update the RequestUpdatedDate to the current date/time when any field is updated
    UPDATE AuctionRequests
    SET request_updated_date = GETDATE()
    FROM AuctionRequests r
    INNER JOIN inserted i ON r.id = i.id;
END;
GO

CREATE TABLE Bids(
	id int IDENTITY(1,1) primary key,
	auction_id int not null,
	user_id int not null,
	bid_amount float not null,
	bid_created_date datetime default GETDATE(),
	is_auto_bid bit default 0,
	auto_bid_max float,
	increment_autobix float,
	bid_price float not null,
	FOREIGN KEY (auction_id) REFERENCES Auctions(id),
	FOREIGN KEY (user_id) REFERENCES Users(id)
);
Go

CREATE TABLE Wallets(
	id int IDENTITY(1,1) primary key,
	member_id int not null,
	balance float default 0 not null,
	FOREIGN KEY (member_id) REFERENCES Users(id)
);
Go

CREATE TABLE Transactions(
	id int IDENTITY(1,1) primary key,
	auction_id int not null,
	member_id int not null,
	payment_id int not null,
	wallet_id int not null,
	transaction_fee float not null,
	transaction_float int not null,
	transaction_date datetime default GETDATE(),
	FOREIGN KEY (auction_id) REFERENCES Auctions(id),
	FOREIGN KEY (member_id) REFERENCES Users(id),
);
Go

CREATE TABLE Payments(
	id int IDENTITY(1,1) primary key,
	member_id int not null,
	amount float not null,
	payment_status int not null,
	FOREIGN KEY (member_id) REFERENCES Users(id),
);
Go

CREATE TABLE Deliveries(
	id int IDENTITY(1,1) primary key,
	transaction_id int not null,
	from_address nvarchar(250) not null,
	to_address nvarchar(250) not null,
	[status] int,
	delivery_date date not null,
	delivery_fee float not null,
	FOREIGN KEY (transaction_id) REFERENCES Transactions(id),
);
Go

CREATE TABLE InvalidatedToken(
	id int IDENTITY(1,1) primary key,
	expiryTime datetime default GETDATE()
)