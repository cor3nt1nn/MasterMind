package fr.besqueutvilledieu.client.pawn;

import fr.besqueutvilledieu.client.utils.Color;
import fr.besqueutvilledieu.client.utils.PawnType;

class InvalidColorGamePawnException extends IllegalArgumentException {
    private static final long serialVersionUID = 1L;

    public InvalidColorGamePawnException() {
        super("This color is reserved for Markers Pawns");
    }
}

public class GamePawn extends Pawn {
    private int xPos;

    public GamePawn(Color color, int xPos) {
        super(9608);
        if (color.getColorMembership() != PawnType.GAME && color.getColorMembership() != PawnType.BOTH) {
            throw new InvalidColorGamePawnException();
        } else {
            super.setColor(color);
            this.xPos = xPos;
        }
    }

    public int getxPos() {
        return xPos;
    }
}
