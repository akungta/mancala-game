# MancalaGame

https://en.wikipedia.org/wiki/Mancala

## Rules

### Board Setup
* Each of the two players has the six pits in front of them. 
* Right of the six pits, each player has a big pit. 
* Each of the six pits has six stones.

### Play

* The player who begins the move picks up all the stones in anyone of the own six pits, and sows the stones on to the right, one in each of the following pits anti-clockwise, including the own big pit. 
* No stones are put in the opponent's big pit. If the player's last stone lands in their own big pit, the player gets another turn. This can be repeated several times before it's the other player's turn.
* During the game the player captures opponent's stones and puts them in his own pit, when the last stone lands in an own empty pit.
* The game is over as soon as one of the player run out of stones. Other player then puts all the stones in their big pit.
* Winner of the game is the player who has the most stones in their big pit.

How to start the MancalaGame application
---

1. Run `mvn clean install` to build your application
1. Start application with `java -jar target/mancala-game-1.0-SNAPSHOT.jar server config.yml`
1. To check that your application is running enter url `http://localhost:8080/swagger`

Health Check
---

To see your applications health enter url `http://localhost:8081/healthcheck`

