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

## Running app and infrastructure in docker

You can also run the infrastructure and the app within a docker ecosystem.

You will still need to [set up the app environment](./app/README.md#setting-up-the-environment) before running the container.

This includes the following services
- MySQL
- RabbitMQ
- Yuppie assessment App

Using docker compose

```bash
# Start
docker-compose -f docker-compose.app.yml up
# Stop
docker-compose -f docker-compose.app.yml down --volumes
# Stop and keep data
docker-compose  -f docker-compose.app.yml down
```

Then you can access the local app on [http://localhost:8080](http://localhost:8080)
