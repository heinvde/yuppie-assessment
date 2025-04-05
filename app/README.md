# Yuppiechef Application

## Setting up the environment

To start the server with the correct environment variables:

1. Create a `profiles.clj` file in the `app/` directory (current directory).
2. Set up the `:profiles/dev` profile inside `profiles.clj` as follows:

```clj
{:profiles/dev {:env {:google-client-id "!Your google OAuth2 client ID"
                      :google-client-secret "!Your google OAuth2 client secret"
                      :google-oauth2-state-key "!Any string that will be used to verify requests"}}}
```

## Start the development server

Once profiles.clj is configured, you can start the development server using:

```sh
lein ring server
```

**NOTE:** You have to run the local database [Running local database](../README.md#running-local-database)

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

**NOTE:** You have to run local database for integration tests to pass see [Running local database](../README.md#running-local-database)

#### All tests

Run all tests

```sh
lein test :all
```

**NOTE:** You have to run local database for all tests to pass see [Running local database](../README.md#running-local-database)
