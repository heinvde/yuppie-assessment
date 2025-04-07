# Yuppiechef Application

## Setting up the environment

To start the server with the correct environment variables:

1. Create a `profiles.clj` file in the `app/` directory (current directory).
2. Set up the `:profiles/dev` profile inside `profiles.clj` as follows:

```clj
{:profiles/dev {:env {:google-client-id "!Your google OAuth2 client ID"
                      :google-client-secret "!Your google OAuth2 client secret"
                      :google-oauth2-state-key "!Any string that will be used to verify requests"
                      :cloudinary-url "cloudinary://{API_KEY}:{API_SECRET}@{CLOUD_NAME}"}}}
```

## Start the development server

Once `profiles.clj` is configured, you can start the development server using:

```sh
lein ring server
```

**NOTE:** You have to run the local infrastructure [Running local infrastructure](../README.md#running-local-infrastructure)

## Running tests

#### Unit tests

Run unit tests with

```sh
lein test
```

#### Integration tests

Run integration tests with

```sh
lein test :integration
```

**NOTE:** You have to run local infrastructure for integration tests to pass see [Running local infrastructure](../README.md#running-local-infrastructure)

#### All tests

Run all tests

```sh
lein test :all
```

**NOTE:** You have to run local infrastructure for all tests to pass see [Running local infrastructure](../README.md#running-local-infrastructure)
