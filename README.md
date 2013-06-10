A Runescape™ private server base, with client loader. This project contains no proprietary files - the loader downloads the game client at runtime, andswaps the in-memory encryption keys for our own, and redirects connection to our own server.

The server is split in to 2 parts:

- server: A Dropwizard service for handling client connections and requests.
- dataserver: A Hibernate based Dropwizard service for handling game data.

[![Build Status](https://api.travis-ci.org/reines/rsc.png)](https://travis-ci.org/reines/rsc)

*Note: This is __far__ from complete, and isn't likely to be developed much further. It was a weekend project.*
