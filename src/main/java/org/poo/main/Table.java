package org.poo.main;

import org.poo.fileio.Coordinates;

import java.util.ArrayList;

import static java.lang.Math.abs;

public class Table {
    private ArrayList<ArrayList<Card>> table = new ArrayList<>(4);

    public Table() {
        for (int i = 0; i < 4; i++)
            table.add(new ArrayList<Card>());
    }

    public void unattackedAllCards() {
        for (int i = 0; i < 4; i++) {
            for (Card card: table.get(i)) {
                card.setHasAttacked(false);
            }
        }
    }

    public void unfreezePlayerTwoCards() {
        for (int i = 0; i < 2; i++) {
            for (Card card: table.get(i)) {
                card.setFrozen(false);
            }
        }
    }

    public void unfreezePlayerOneCards() {
        for (int i = 2; i < 4; i++) {
            for (Card card: table.get(i)) {
                card.setFrozen(false);
            }
        }
    }

    /**
     * Determines whether an action between two cards constitutes friendly fire.
     * Friendly fire occurs when an attacker targets another card from the same
     * team or an invalid target due to position constraints.
     *
     * @param cardAttackerCoords the coordinates of the attacking card, where
     *                           {@code getX()} represents the row.
     * @param cardAttackedCoords the coordinates of the attacked card, where
     *                           {@code getX()} represents the row.
     * @return {@code true} if the action is classified as friendly fire,
     *         {@code false} otherwise.
     *
     * <p>The conditions for friendly fire are as follows:
     * <ul>
     *     <li>If the attacker is in row 0 or 3 (back rows for each team), the
     *     attacker and the attacked must be within 1 row of each other.</li>
     *     <li>If the attacker is in row 1, they cannot target cards in higher
     *     rows (rows with greater indices).</li>
     *     <li>If the attacker is in row 2, they cannot target cards in lower
     *     rows (rows with smaller indices).</li>
     * </ul>
     * </p>
     */
    public boolean isFriendlyFire(Coordinates cardAttackerCoords, Coordinates cardAttackedCoords) {
        int attackerRow = cardAttackerCoords.getX();
        int attackedRow = cardAttackedCoords.getX();
        return (attackerRow == 0 || attackerRow == 3) && abs(attackerRow - attackedRow) < 2
                || (attackerRow == 1 && attackerRow > attackedRow)
                || (attackerRow == 2 && attackerRow < attackedRow);
    }

    public ArrayList<ArrayList<Card>> getTable() {
        return table;
    }
}
