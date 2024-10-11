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
	imageUrl varchar(255),
	videoUrl varchar(255),
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


INSERT INTO Users (username, [password], [role], fullname, phone, [address])
VALUES 
('breeder1', 'pass123', 1, 'John Doe', '123-456-7890', '123 Koi St, Aqua City'),
('breeder2', 'pass456', 1, 'Jane Smith', '234-567-8901', '456 Pond Ave, Aqua City'),
('customer1', 'pass789', 2, 'Michael Brown', '345-678-9012', '789 Lake Blvd, Fish Town'),
('customer2', 'pass321', 2, 'Emily Davis', '456-789-0123', '101 Fish Rd, River City'),
('admin', 'adminpass', 0, 'Admin User', '567-890-1234', '999 Admin Lane, Aqua City');

INSERT INTO KoiFishs (breeder_id, [name], sex, size, age, [description], imageUrl, videoUrl, [status])
VALUES 
(1, 'Kohaku', 1, 15.5, 3, 'Beautiful Kohaku koi with red and white patterns', 'kohaku.jpg', 'kohaku.mp4', 1),
(1, 'Sanke', 2, 17.3, 2, 'High-quality Sanke koi with vibrant colors', 'sanke.jpg', 'sanke.mp4', 1),
(2, 'Showa', 1, 14.2, 4, 'Black Showa koi with red accents', 'showa.jpg', 'showa.mp4', 1),
(2, 'Shusui', 2, 16.0, 3, 'Rare Shusui with stunning scales', 'shusui.jpg', 'shusui.mp4', 1),
(1, 'Tancho', 1, 18.1, 5, 'Tancho koi with red spot on head', 'tancho.jpg', 'tancho.mp4', 1);

INSERT INTO Auctions (fish_id, start_time, end_time, current_price, [status])
VALUES 
(1, '2024-10-15 09:00', '2024-10-15 18:00', 500.0, 1), 
(2, '2024-10-16 09:00', '2024-10-16 18:00', 600.0, 1), 
(3, '2024-10-17 09:00', '2024-10-17 18:00', 550.0, 1), 
(4, '2024-10-18 09:00', '2024-10-18 18:00', 650.0, 1), 
(5, '2024-10-19 09:00', '2024-10-19 18:00', 700.0, 1);

INSERT INTO AuctionParticipants (auction_id, user_id, deposit_amount)
VALUES 
(1, 3, 100.0), 
(1, 4, 150.0), 
(2, 3, 200.0), 
(2, 4, 250.0), 
(3, 3, 300.0);

INSERT INTO AuctionRequests (breeder_id, fish_id, auction_id, buy_out, start_price, increment_price, method_type, request_status)
VALUES 
(1, 1, 1, 1000.0, 500.0, 50.0, 1, 1),
(1, 2, 2, 1200.0, 600.0, 60.0, 1, 1),
(2, 3, 3, 1100.0, 550.0, 55.0, 1, 1),
(2, 4, 4, 1300.0, 650.0, 65.0, 1, 1),
(1, 5, 5, 1400.0, 700.0, 70.0, 1, 1);

INSERT INTO Bids (auction_id, user_id, bid_amount, bid_price, is_auto_bid)
VALUES 
(1, 3, 510.0, 510.0, 0), 
(1, 4, 520.0, 520.0, 0), 
(2, 3, 610.0, 610.0, 0), 
(2, 4, 620.0, 620.0, 0), 
(3, 3, 560.0, 560.0, 0);

INSERT INTO Wallets (member_id, balance)
VALUES 
(3, 5000.0), 
(4, 4500.0), 
(1, 10000.0), 
(2, 8000.0), 
(5, 20000.0);

INSERT INTO Transactions (auction_id, member_id, payment_id, wallet_id, transaction_fee, transaction_float)
VALUES 
(1, 3, 1, 1, 50.0, 1000), 
(2, 4, 2, 2, 60.0, 1200), 
(3, 3, 3, 3, 55.0, 1100), 
(4, 4, 4, 4, 65.0, 1300), 
(5, 3, 5, 5, 70.0, 1400);

INSERT INTO Payments (member_id, amount, payment_status)
VALUES 
(3, 510.0, 1), 
(4, 520.0, 1), 
(3, 610.0, 1), 
(4, 620.0, 1), 
(3, 560.0, 1);

INSERT INTO Deliveries (transaction_id, from_address, to_address, [status], delivery_date, delivery_fee)
VALUES 
(1, '123 Koi St, Aqua City', '789 Lake Blvd, Fish Town', 1, '2024-10-20', 25.0), 
(2, '456 Pond Ave, Aqua City', '101 Fish Rd, River City', 1, '2024-10-21', 30.0), 
(3, '789 Lake Blvd, Fish Town', '999 Admin Lane, Aqua City', 1, '2024-10-22', 35.0), 
(4, '101 Fish Rd, River City', '123 Koi St, Aqua City', 1, '2024-10-23', 40.0), 
(5, '999 Admin Lane, Aqua City', '456 Pond Ave, Aqua City', 1, '2024-10-24', 45.0);
