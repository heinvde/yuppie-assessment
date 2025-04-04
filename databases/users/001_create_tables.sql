
CREATE TABLE user_profiles (
    id VARCHAR(40) PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100),
    email VARCHAR(320) NOT NULL UNIQUE,
    profile_picture_url TEXT
);

CREATE TABLE user_profile_pictures (
    user_profile_id VARCHAR(40) NOT NULL,
    storage_provider_id VARCHAR(100) NOT NULL,
    url TEXT NOT NULL,
    FOREIGN KEY (user_profile_id) REFERENCES user_profiles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_profile_id, storage_provider_id)
)
