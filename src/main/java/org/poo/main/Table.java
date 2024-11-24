package org.poo.main;

import org.poo.fileio.Coordinates;

import java.util.ArrayList;

import static java.lang.Math.abs;

public class Table {
    private static final int ROW_NUMBERS = 4;
    private static final int BACK_ROW_P1 = 3;
    private static final int FRONT_ROW_P1 = 2;
    private static final int FRONT_ROW_P2 = 1;
    private static final int BACK_ROW_P2 = 0;

    private ArrayList<ArrayList<Card>> table = new ArrayList<>(ROW_NUMBERS);

    public Table() {
        for (int i = 0; i < ROW_NUMBERS; i++) {
            table.add(new ArrayList<Card>());
        }
    }

    /**
     * Resets the attack state of all cards on the table,
     * marking them as not having attacked.
     */
    public void unattackedAllCards() {
        for (int i = 0; i < ROW_NUMBERS; i++) {
            for (Card card: table.get(i)) {
                card.setHasAttacked(false);
            }
        }
    }

    /**
     * Unfreezes all cards in the rows (rows 0 and 1) of Player Two.
     */
    public void unfreezePlayerTwoCards() {
        for (int i = 0; i < ROW_NUMBERS / 2; i++) {
            for (Card card: table.get(i)) {
                card.setFrozen(false);
            }
        }
    }

    /**
     * Unfreezes all cards in the rows (rows 2 and 3) of Player One.
     */
    public void unfreezePlayerOneCards() {
        for (int i = 2; i < ROW_NUMBERS; i++) {
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
    public boolean isFriendlyFire(final Coordinates cardAttackerCoords,
                                  final Coordinates cardAttackedCoords) {
        int attackerRow = cardAttackerCoords.getX();
        int attackedRow = cardAttackedCoords.getX();
        return (attackerRow == BACK_ROW_P2 || attackerRow == BACK_ROW_P1)
                && abs(attackerRow - attackedRow) < 2
                || (attackerRow == FRONT_ROW_P2 && attackerRow > attackedRow)
                || (attackerRow == FRONT_ROW_P1 && attackerRow < attackedRow);
    }

    /**
     * Getter
     */
    public ArrayList<ArrayList<Card>> getTable() {
        return table;
    }
}
