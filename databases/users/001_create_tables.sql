
CREATE TABLE user_profiles (
    id VARCHAR(40) PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100),
    email VARCHAR(320) NOT NULL UNIQUE,
    profile_picture_url TEXT
);
