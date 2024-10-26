CREATE DATABASE AuctionKoi
Go

USE AuctionKoi
Go

CREATE TABLE Users(
	id  int IDENTITY(1,1) primary key,
	username varchar(70) not null,
	[password] varchar(100) not null,
	[role] int not null,
	fullname nvarchar(250) null,
	phone varchar(20) null,
	[address] nvarchar(250) null,
	avatar_url varchar(1000) null,
	is_active bit default 1 not null,
	user_created_date datetime default GETDATE() not null,
	user_updated_date datetime default GETDATE() not null
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

CREATE TABLE Wallets(
	id int IDENTITY(1,1) primary key,
	[user_id] int not null,
	balance float default 0 not null,
	FOREIGN KEY ([user_id]) REFERENCES Users(id)
);
Go

CREATE TABLE Koi_Fish(
	id int IDENTITY(1,1) primary key,
	breeder_id int not null,
	[name] varchar(250) not null,
	sex int not null,
	size float not null,
	age int not null,
	[description] nvarchar(2000),
	[image_url] varchar(2000) not null,
	[video_url] varchar(2000) not null,
	[status] int not null,
	fish_created_date datetime default GETDATE() not null,
	fish_updated_date datetime default GETDATE() not null,
	FOREIGN KEY (breeder_id) REFERENCES Users(id),
);
Go

CREATE TRIGGER trg_UpdateFishUpdatedDate
ON Koi_Fish
AFTER UPDATE
AS
BEGIN
    -- Update the FishUpdatedDate to the current date/time when any field is updated
    UPDATE Koi_Fish
    SET fish_updated_date = GETDATE()
    FROM Koi_Fish f
    INNER JOIN inserted i ON f.id = i.id;
END;
GO

CREATE TABLE Auctions(
	id int IDENTITY(1,1) primary key,
	fish_id int not null,
	winner_id int null,
	current_price float null,
	[status] int not null,
	extension_seconds int DEFAULT 60,
	highest_prices float,
	deposit_amount float,
	FOREIGN KEY (fish_id) REFERENCES Koi_Fish(id),
	FOREIGN KEY (winner_id) REFERENCES Users(id)
);
Go



CREATE TABLE Auction_Participants (
    id int IDENTITY(1,1) primary key,
    auction_id int not null,
    user_id int not null,
    join_date datetime default GETDATE() not null,
    FOREIGN KEY (auction_id) REFERENCES Auctions(id),
    FOREIGN KEY (user_id) REFERENCES Users(id)
);
GO

CREATE TABLE Auction_Requests(
	id int IDENTITY(1,1) primary key,
	[user_id] int not null,
	[approved_staff_id] int null,
	fish_id int not null,
	auction_id int,
	buy_out float not null,
	start_price float not null,
	method_type int not null,
	start_time datetime not null,
	end_time datetime not null,
	request_status int not null,
	request_created_date datetime default GETDATE() not null,
	request_updated_date datetime default GETDATE() not null,
	FOREIGN KEY (auction_id) REFERENCES Auctions(id),
	FOREIGN KEY ([user_id]) REFERENCES Users(id),
	FOREIGN KEY (fish_id) REFERENCES Koi_Fish(id),
	FOREIGN KEY (approved_staff_id) REFERENCES Users(id),

);
Go

CREATE TRIGGER trg_UpdateRequestUpdatedDate
ON Auction_Requests
AFTER UPDATE
AS
BEGIN
    -- Update the RequestUpdatedDate to the current date/time when any field is updated
    UPDATE Auction_Requests
    SET request_updated_date = GETDATE()
    FROM Auction_Requests r
    INNER JOIN inserted i ON r.id = i.id;
END;
GO


CREATE TRIGGER trg_update_deposit_amount
ON Auction_Requests
AFTER INSERT, UPDATE
AS
BEGIN
    -- Update the deposit_amount in Auctions
    UPDATE Auctions
    SET deposit_amount = i.buy_out * 0.15
    FROM Auctions a
    INNER JOIN inserted i ON a.id = i.auction_id
    WHERE i.request_status = 1 AND i.auction_id IS NOT NULL;
END;
GO

CREATE TABLE Bids(
	id int IDENTITY(1,1) primary key,
	auction_id int not null,
	[user_id] int not null,
	bid_amount float not null,
	bid_created_date datetime default GETDATE() not null,
	is_auto_bid bit default 0,
	auto_bid_max float,
	increment_autobid float,
	FOREIGN KEY (auction_id) REFERENCES Auctions(id),
	FOREIGN KEY ([user_id]) REFERENCES Users(id)
);
Go



CREATE TABLE Transactions(
	id int IDENTITY(1,1) primary key,
	auction_id int null,
	[user_id] int not null,
	payment_id int null,
	wallet_id int not null,
	amount float not null,
	transaction_fee float not null,
	transaction_date datetime default GETDATE() not null,
	transaction_type int not null,
	FOREIGN KEY (auction_id) REFERENCES Auctions(id),
	FOREIGN KEY ([user_id]) REFERENCES Users(id),
);
Go

CREATE TABLE Payments(
	id int IDENTITY(1,1) primary key,
	[user_id] int not null,
	amount float not null,
	payment_status int not null,
	FOREIGN KEY ([user_id]) REFERENCES Users(id),
);
Go

CREATE TABLE Deliveries(
	id int IDENTITY(1,1) primary key,
	transaction_id int not null,
	from_address nvarchar(250) not null,
	to_address nvarchar(250) not null,
	[status] int not null,
	delivery_date date,
	FOREIGN KEY (transaction_id) REFERENCES Transactions(id),
);
Go

CREATE TABLE InvalidatedToken(
	id int IDENTITY(1,1) primary key,
	expiryTime datetime default GETDATE() not null
);
Go

INSERT INTO Users (username, password, role, fullname, phone, [address], avatar_url)
VALUES 
('user1', '$2a$12$NmGYappOYUoVZw1fomNYeuQgO9aHus8ajvfyWu8W6nWeY0EM/fCMe', 2, 'John Doe', '0123456789', '123 Street, City', 'http://avatar1.jpg'),
('user2', '$2a$12$NmGYappOYUoVZw1fomNYeuQgO9aHus8ajvfyWu8W6nWeY0EM/fCMe', 2, 'Jane Smith', '0987654321', '456 Avenue, City', 'http://avatar2.jpg'),
('user3', '$2a$12$NmGYappOYUoVZw1fomNYeuQgO9aHus8ajvfyWu8W6nWeY0EM/fCMe', 1, 'Tom Brown', '0934567890', '789 Road, City', 'http://avatar3.jpg'),
('user4', '$2a$12$NmGYappOYUoVZw1fomNYeuQgO9aHus8ajvfyWu8W6nWeY0EM/fCMe', 1, 'Alice Green', '0789123456', '101 Highway, City', 'http://avatar4.jpg'),
('user5', '$2a$12$NmGYappOYUoVZw1fomNYeuQgO9aHus8ajvfyWu8W6nWeY0EM/fCMe', 0, 'Bob White', '0678912345', '202 Lane, City', 'http://avatar5.jpg'),
('user6', '$2a$12$NmGYappOYUoVZw1fomNYeuQgO9aHus8ajvfyWu8W6nWeY0EM/fCMe', 0, 'Charlie Black', '0567891234', '303 Trail, City', 'http://avatar6.jpg');
Go

INSERT INTO Wallets (user_id, balance)
VALUES 
(3, 1500000000000),
(4, 2500000000000),
(5, 3000000000000),
(6, 1800000000000);
Go

INSERT INTO Koi_Fish (breeder_id, [name], sex, size, age, [description], [image_url], [video_url], [status])
VALUES 
(1, 'Koi 1', 0, 25, 2, 'Beautiful Koi 1', 'http://koi1.jpg', 'http://koi1.mp4', 0),
(2, 'Koi 2', 0, 30, 3, 'Beautiful Koi 2', 'http://koi2.jpg', 'http://koi2.mp4', 0),
(3, 'Koi 3', 1, 28, 2, 'Beautiful Koi 3', 'http://koi3.jpg', 'http://koi3.mp4', 1),
(4, 'Koi 4', 1, 26, 3, 'Beautiful Koi 4', 'http://koi4.jpg', 'http://koi4.mp4', 1),
(5, 'Koi 5', 0, 29, 2, 'Beautiful Koi 5', 'http://koi5.jpg', 'http://koi5.mp4', 0),
(6, 'Koi 6', 1, 27, 3, 'Beautiful Koi 6', 'http://koi6.jpg', 'http://koi6.mp4', 1);
Go

INSERT INTO Auction_Requests (user_id, fish_id, approved_staff_id, auction_id, buy_out, start_price, method_type, start_time, end_time, request_status)
VALUES 
(1, 1, null, null, 1200, 1000, 0, GETDATE(), DATEADD(day, 7, GETDATE()), 0),
(2, 2, null, null, 1800, 1500, 1, GETDATE(), DATEADD(day, 7, GETDATE()), 0),
(3, 3, null, null, 1300, 1200, 2, GETDATE(), DATEADD(day, 7, GETDATE()), 0),
(4, 4, null, null, 1900, 1800, 0, GETDATE(), DATEADD(day, 7, GETDATE()), 0),
(5, 5, null, null, 2100, 2000, 1, GETDATE(), DATEADD(day, 7, GETDATE()), 0),
(6, 6, null, null, 1750, 1700, 2, GETDATE(), DATEADD(day, 7, GETDATE()), 0);
Go
