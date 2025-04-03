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
