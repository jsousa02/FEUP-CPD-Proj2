# CPD Project 1

## How to Run

First, we need to build the with the ```gradle installDist``` command.

To start the server, we need to execute ```./server/build/install/server/bin/server "-[r/s] <number_of_players> <port>"```.
The -r option starts a server in ranked mode. The -s option starts a server in simple mode.

To start the client, we need to execute ```./client/build/install/client/bin/client```.

You will be asked for a username and password and, after logging in, you will be waiting for enough
players to join in order to start a new game.
