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
