# Yuppie Chef Assessment

## Getting Started

See the [Application Documentation](app/README.md) for detailed instructions on running and testing the application.

Before running the application, make sure to start the [local infrastructure](#running-local-infrastructure).

## Features

The application includes the following functionality:

- **Google OAuth2 Authentication**
  Authenticates users using their Google account.

- **User Profile Storage**
  Stores authenticated users' Google profile data in a local MySQL database.

- **Event-Driven Architecture**
  Sends a `profile-created` event to a local RabbitMQ exchange after storing the profile.

- **Profile Picture Upload**
  A consumer for the `profile-created` queue uploads the user's Google profile picture to Cloudinary.

- **Profile Data Retrieval**
  Provides a JSON API to retrieve and display the stored user profile data.

## Running local infrastructure

This includes the following services
- MySQL
- RabbitMQ

Using docker compose

```bash
# Start
docker-compose up -d
# Start with log tail
docker-compose up
# Stop
docker-compose down --volumes
# Stop and keep data
docker-compose down
```
