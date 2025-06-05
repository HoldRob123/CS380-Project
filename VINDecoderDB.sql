-- Create a new database and use it
CREATE DATABASE IF NOT EXISTS vin_vehicle_db;
USE vin_vehicle_db;

-- Table: users
CREATE TABLE users (
    userID INT AUTO_INCREMENT PRIMARY KEY,
    userName VARCHAR(50),
    userPassword VARCHAR(255)
);

-- Table: vehicles
CREATE TABLE vehicles (
    VIN_NUMBER VARCHAR(17) PRIMARY KEY,
    V_make VARCHAR(50),
    V_model VARCHAR(50),
    V_year VARCHAR(10),
    V_trim VARCHAR(50),
    V_type VARCHAR(100),
    V_bodyClass VARCHAR(100),
    V_doors INT,
    V_fuelType VARCHAR(10),
    V_DriveType VARCHAR(30),
    V_EngineModel VARCHAR(50),
    V_CylinderNum INT,
    V_EngineDisplace DECIMAL(3,1),
    V_TransStyle VARCHAR(30),
    V_TransSpeeds INT,
    V_PlantCountry VARCHAR(60),
    V_Manufacturer VARCHAR(60),
    V_GVWR VARCHAR(60),
    V_rows INT,
    V_seats INT,
    userID INT,
    nickName VARCHAR(45),
    FOREIGN KEY (userID) REFERENCES users(userID)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);

-- Table: saved_vehicles
CREATE TABLE saved_vehicles (
    user_id INT,
    vin_number VARCHAR(17),
    PRIMARY KEY (user_id, vin_number),
    FOREIGN KEY (user_id) REFERENCES users(userID)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (vin_number) REFERENCES vehicles(VIN_NUMBER)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

-- Triggers to sync vehicles → saved_vehicles
DELIMITER $$

-- Trigger: after insert on vehicles
CREATE TRIGGER trg_after_vehicle_insert
AFTER INSERT ON vehicles
FOR EACH ROW
BEGIN
    INSERT IGNORE INTO saved_vehicles (user_id, vin_number)
    VALUES (NEW.userID, NEW.VIN_NUMBER);
END$$

-- Trigger: after delete on vehicles
CREATE TRIGGER trg_after_vehicle_delete
AFTER DELETE ON vehicles
FOR EACH ROW
BEGIN
    DELETE FROM saved_vehicles
    WHERE user_id = OLD.userID AND vin_number = OLD.VIN_NUMBER;
END$$

DELIMITER ;

