# Dataserver

The dataserver is a [Dropwizard](http://dropwizard.codahale.com) service, which connects to a database, and provides persistent data storage/access via a REST API.

## API

  PUT /users
  GET /users/{username}
  PUT /sessions
