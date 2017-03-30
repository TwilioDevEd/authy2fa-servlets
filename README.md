# Two-Factor Authentication with Authy and Servlets

[![Build Status](https://travis-ci.org/TwilioDevEd/authy2fa-servlets.svg?branch=master)](https://travis-ci.org/TwilioDevEd/authy2fa-servlets)

This application example demonstrates how to use [Authy](http://www.authy.com)
as the two-factor authentication provider using Servlets.

## Local Development

1. First clone this repository and `cd`into it.

   ```bash
   $ git clone git@github.com:TwilioDevEd/authy2fa-servlets.git
   $ cd authy2fa-servlets
   ```

1. Create the database.

   ```bash
   $ createdb authy2fa
   ```

   _The application uses PostgreSQL as the persistence layer. If you
   don't have it already, you should install it. The easiest way is by
   using [Postgres.app](http://postgresapp.com/)._

1. Copy the sample configuration file and edit it to match your configuration.

    ```bash
    $ cp .env.example .env
    ```

   You'll need to set `JDBC_URL`, `DB_USERNAME`, and `DB_PASSWORD`.

   You can find your `AUTHY_API_KEY` in your
   [Authy Dashboard](https://dashboard.authy.com).

   Once you have populated all the values, load the variables with `source`.

    ```bash
    $ source .env
    ```

    _If you are using a different operating system, make sure that all the variables from the `.env` file are loaded into your environment._

1. Execute the migrations.

   ```bash
   $ mvn compile flyway:migrate
   ```

1. Run the application.
   ```bash
   $ mvn compile jetty:run
   ```

1. Check it out at [http://localhost:8080/](http://localhost:8080/)

1. To enable Authy OneTouch to use the callback endpoint you exposed, your development server will need to be publicly accessible. [We recommend using ngrok to solve this problem](//www.twilio.com/blog/2015/09/6-awesome-reasons-to-use-ngrok-when-testing-webhooks.html).

   ```bash
   $ ngrok http 8080
   ```

## Run the tests

Assuming you have configured the application for your local test
environment, you can then use Flyway to migrate the test database
(by setting the correct `JDBC_URL`) and then use Maven
to run the tests:

```
mvn test
```

## Meta

* No warranty expressed or implied. Software is as is. Diggity.
* [MIT License](http://www.opensource.org/licenses/mit-license.html)
* Lovingly crafted by Twilio Developer Education.
