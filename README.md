# GwentStone Lite

##  Table of Contents
- [About the Project](#about-the-project)
- [List of commands](#list-of-commands)
---

##  About the Project

**GwentStone** is a multiplayer game implemented in Java that takes input from a server in JSON format.  
It implements the main functionalities of a turn-based card game.  
It takes place on a table composed of a 4X5 grid.  
Two main types of entities can influence the result of a match: **Minions** and **Heroes** (of course, excluding the player).

###  Key Classes
- **Main**: parses the *JSON input* and extracts the relevant information into the corresponding *"file" classes*.
- **GameHandler**: manages all existing commands and their implementations. Also, *initializes the player object*.
- **Game**: contains *metadata* regarding the ongoing matches (wins and the number of games played).
- **Player**: contains the *deck and hero* information, as well as *health and mana*.
- **Table**: represents the *logical grid* the game is played on. Contains methods *managing placement inputs*.
- **Deck**: initializes the deck based on a given index and shuffle seed, which *shuffles* after every match.
- **Card**: contains all the *data* relevant to a card, also, the methods *applying attacks and spells*.
---

##  List of commands
- ***endPlayerTurn***: marks the end of a players turn, also, if both players finished, a new rounds begins

- ***placeCard***: places the chosen card from the hand onto the corresponding row.  
Removes it from hand if the placement is legal.

- ***cardUsesAttack***: checks first if the target is within reach (no Tanks on the front row), or if it is frozen,   
then applies attack.
- ***cardUsesAbility***: checks first if the target is within reach, and that the player has enough mana, or if it is frozen, 
then applies the spell.
- ***useAttackHero***: checks first if the target is within reach, or if the Hero is frozen, then applies attack.
- ***useHeroAbility***: checks first if the target is within reach, and that the player has enough mana, or if it is frozen,
  then applies the spell.
- ***getPlayerTurn***: returns the index of the current player.
- ***getPlayerMana***: returns the mana of the current player.
- ***getPlayerDeck***: returns the deck card data of the current player.
- ***getPlayerHero***: returns the Hero cards data of the current players

- ***getCardsInHand***: returns the data of the cards that are in the hand of the current player.
- ***getCardsOnTable***: returns the card data of cards placed on the table.
- ***getCardAtPosition***: returns the card data of the card at the given position.
- ***getFrozenCardsOnTable***: returns the card data of the currently frozen cards.

- ***getTotalGamesPlayed***: returns the number of the number of matches (games) played.
- ***getPlayerOneWins***: returns the number of wins of PlayerOne (Index = 0).
- ***getPlayerTwoWins***: returns the number of wins of PlayerTwo (Index = 1).
---




