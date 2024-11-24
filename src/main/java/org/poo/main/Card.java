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

    public Card() { }

    //copy constructor for Card object
    public Card(final CardInput card) {
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
    public void applyHeroAbility(final Table table, final int affectedRow) {
        switch (name) {
            case "Lord Royce":
                for (Card card: table.getTable().get(affectedRow)) {
                    card.isFrozen = true;
                }
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
                for (Card card: table.getTable().get(affectedRow)) {
                    card.health += 1;
                }
                break;
            case "General Kocioraw":
                for (Card card: table.getTable().get(affectedRow)) {
                    card.attackDamage += 1;
                }
                break;
            default:
                System.out.println("Hero not recognised.");
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
    public void applyCardAbility(final Table table, final Coordinates cardAttackedCoords) {
        int x = cardAttackedCoords.getX();
        int y = cardAttackedCoords.getY();
        Card cardAffected = table.getTable().get(x).get(y);

        switch (name) {
            case "Disciple":
                cardAffected.health += 2;
                break;
            case "The Ripper":
                if (cardAffected.attackDamage <= 2) {
                    cardAffected.attackDamage = 0;
                } else {
                    cardAffected.attackDamage -= 2;
                }
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
            default:
                System.out.println("Minion not recognised.");
        }

    }

    /**
     * Checks if the card is not a disciple.
     *
     * @return {@code true} if the card is not a disciple; {@code false} otherwise
     */
    public boolean isNotDisciple() {
        return name.equals("The Ripper")
                || name.equals("Miraj")
                || name.equals("The Cursed One");
    }

    /**
     * Determines if the card has tank properties based on its name.
     */
    private void isTank() {
        switch (name) {
            case "Goliath" :
                isTank = true;
                break;
            case "Warden" :
                isTank = true;
                break;
            default:
                isTank = false;
                break;
        }
    }

    /**
     * Determines if the card has druid properties based on its name.
     */
    private void isDruid() {
        switch (name) {
            case "The Ripper" :
                isDruid = true;
                break;
            case "Miraj" :
                isDruid = true;
                break;
            default:
                isDruid = false;
        }
    }

    /***
     * Getter
     */
    public boolean getTankStatus() {
        return isTank;
    }

    /***
     * Getter
     */    public boolean getDruidStatus() {
        return isDruid;
    }

    /***
     * Getter
     */
    public int getMana() {
        return mana;
    }

    /***
     * Getter
     */
    public boolean getAttackStatus() {
        return hasAttacked;
    }

    /***
     * Getter
     */
    public int getAttackDamage() {
        return attackDamage;
    }

    /***
     * Getter
     */
    public int getHealth() {
        return health;
    }

    /***
     * Getter
     */
    public String getName() {
        return name;
    }

    /***
     * Getter
     */
    public String getDescription() {
        return description;
    }

    /***
     * Getter
     */
    public ArrayList<String> getColors() {
        return colors;
    }

    /***
     * Getter
     */
    public boolean getFrozenStatus() {
        return isFrozen;
    }

    /***
     * Getter
     */
    public void setHealth(final int health) {
        this.health = health;
    }

    /***
     * Setter
     */
    public void setAttackDamage(final int attackDamage) {
        this.attackDamage = attackDamage;
    }

    /***
     * Setter
     */
    public void setHasAttacked(final boolean hasAttacked) {
        this.hasAttacked = hasAttacked;
    }

    /***
     * Setter
     */
    public void setFrozen(final boolean frozen) {
        isFrozen = frozen;
    }

    /**
     * Creates a JSON representation of the card using the provided ObjectMapper.
     * The JSON object includes details about the card such as mana, attack damage,
     * health, description, colors, and name.
     *
     * @param mapper an {@code ObjectMapper} instance used to create the JSON nodes
     * @return an {@code ObjectNode} representing the card in JSON format
     */
    public ObjectNode createCardNode(final ObjectMapper mapper) {
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
