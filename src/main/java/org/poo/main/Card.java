package org.poo.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CardInput;
import org.poo.fileio.Coordinates;

import java.util.ArrayList;

public class Card {
    private int mana;
    private int attackDamage;
    private int health;
    private String description;
    private ArrayList<String> colors;
    private String name;
    private boolean isTank = false;
    private boolean isDruid = false;
    private boolean hasAttacked = false;
    private boolean isFrozen = false;

    public Card() {}

    //copy constructor for Card object
    public Card(CardInput card) {
        this.mana = card.getMana();
        this.attackDamage = card.getAttackDamage();
        this.health = card.getHealth();
        this.description = card.getDescription();
        this.colors = card.getColors();
        this.name = card.getName();
        isDruid();
        isTank();
    }

    /**
     * Applies the hero ability of this card to a specified row on the table.
     * The behavior of this method depends on the name of the hero.
     *
     * @param table the game table containing cards
     * @param affectedRow the index of the row affected by the ability
     */
    public void applyHeroAbility(Table table, int affectedRow) {
        switch (name) {
            case "Lord Royce":
                for (Card card: table.getTable().get(affectedRow))
                    card.isFrozen = true;
                break;
            case "Empress Thorina":
                int maxHealth = -1;
                Card cardWithMaxHealth = new Card();
                for (Card card: table.getTable().get(affectedRow)) {
                    if (card.health > maxHealth) {
                        maxHealth = card.health;
                        cardWithMaxHealth = card;
                    }
                }
                cardWithMaxHealth.setHealth(0);
                break;
            case "King Mudface":
                for (Card card: table.getTable().get(affectedRow))
                    card.health += 1;
                break;
            case "General Kocioraw":
                for (Card card: table.getTable().get(affectedRow))
                    card.attackDamage += 1;
                break;
        }
    }

    /**
     * Applies the ability of this card to a specific card on the table.
     * The behavior varies based on the name of the card.
     *
     * @param table the game table containing cards
     * @param cardAttackedCoords the coordinates of the card affected
     */
    public void applyCardAbility(Table table, Coordinates cardAttackedCoords) {
        int x = cardAttackedCoords.getX();
        int y = cardAttackedCoords.getY();
        Card cardAffected = table.getTable().get(x).get(y);

        switch (name) {
            case "Disciple":
                cardAffected.health += 2;
                break;
            case "The Ripper":
                if (cardAffected.attackDamage <= 2)
                    cardAffected.attackDamage = 0;
                else
                    cardAffected.attackDamage -= 2;
                break;
            case "Miraj":
                int auxHealth = this.health;
                this.health = cardAffected.health;
                cardAffected.health = auxHealth;
                break;
            case "The Cursed One":
                int auxAttackDamage = cardAffected.attackDamage;
                cardAffected.attackDamage = cardAffected.health;
                cardAffected.health = auxAttackDamage;
                break;
        }

    }

    public boolean isNotDisciple() {
        return name.equals("The Ripper") ||
               name.equals("Miraj") ||
               name.equals("The Cursed One");
    }

    private void isTank() {
        switch (name) {
            case "Goliath" :
                isTank = true;
            case "Warden" :
                isTank = true;
        }
    }

    private void isDruid() {
        switch (name) {
            case "The Ripper" :
                isDruid = true;
            case "Miraj" :
                isDruid = true;
        }
    }

    public boolean getTankStatus() {
        return isTank;
    }

    public boolean getDruidStatus() {
        return isDruid;
    }

    public int getMana() {
        return mana;
    }

    public boolean getAttackStatus() {
        return hasAttacked;
    }

    public int getAttackDamage() {
        return attackDamage;
    }

    public int getHealth() {
        return health;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<String> getColors() {
        return colors;
    }

    public boolean getFrozenStatus() {
        return isFrozen;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setAttackDamage(int attackDamage) {
        this.attackDamage = attackDamage;
    }

    public void setHasAttacked(boolean hasAttacked) {
        this.hasAttacked = hasAttacked;
    }

    public void setFrozen(boolean frozen) {
        isFrozen = frozen;
    }

    public ObjectNode createCardNode(ObjectMapper mapper) {
        ObjectNode cardNode = mapper.createObjectNode();
        cardNode.put("mana", mana);
        cardNode.put("attackDamage", attackDamage);
        cardNode.put("health", health);
        cardNode.put("description", description);

        ArrayNode colorsArray = mapper.createArrayNode();
        for (String color : colors) {
            colorsArray.add(color);
        }
        cardNode.set("colors", colorsArray);

        cardNode.put("name", name);

        return cardNode;
    }
}
