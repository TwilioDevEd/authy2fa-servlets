# Two-Factor Authentication with Authy and Servlets

[![Build
Status](https://travis-ci.org/TwilioDevEd/authy2fa-servlets.svg?branch=master)](https://travis-ci.org/TwilioDevEd/authy2fa-servlets)

This example application demonstrates how to use [Authy](http://www.authy.com)
as the two-factor authentication provider using Servlets.

## Local Development

1. First clone this repository and `cd` into its directory:
   ```bash
   $ git clone git@github.com:TwilioDevEd/authy2fa-servlets.git
   $ cd authy2fa-servlets
   ```

2. Create the database.
   ```bash
   $ createdb authy2fa
   ```

   _The application uses PostgreSQL as the persistence layer. If you
   don't have it already, you should install it. The easiest way is by
   using [Postgres.app](http://postgresapp.com/)._

3. Edit the sample configuration file `.env.example` to match your configuration.

   Once you have edited the `.env.example` file, if you are using a UNIX operating system,
   just use the `source` command to load the variables into your environment:

   ```bash
   $ source .env.example
   ```

   _If you are using a different operating system, make sure that all the
   variables from the `.env.example` file are loaded into your environment._

4. Execute the migrations.
   ```bash
   $ mvn compile flyway:migrate
   ```

5. Run the application.
   ```bash
   $ mvn compile jetty:run
   ```

6. Check it out at [http://localhost:8080/home](http://localhost:8080/home)

7. To let Authy OneTouch to use the callback endpoint you exposed, your development server will need to be publicly accessible. [We recommend using ngrok to solve this problem](https://www.twilio.com/blog/2015/09/6-awesome-reasons-to-use-ngrok-when-testing-webhooks.html).
   ```bash
   $ ngrok http 8080
   ```
