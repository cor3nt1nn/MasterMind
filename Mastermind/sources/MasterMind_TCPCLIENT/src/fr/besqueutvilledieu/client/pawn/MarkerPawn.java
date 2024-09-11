package fr.besqueutvilledieu.client.pawn;

import fr.besqueutvilledieu.client.utils.Color;
import fr.besqueutvilledieu.client.utils.PawnType;

class InvalidColorMarkerPawnException extends IllegalArgumentException {
    private static final long serialVersionUID = 1L;

    public InvalidColorMarkerPawnException() {
        super("This color is reserved for Game Pawns");
    }
}

public class MarkerPawn extends Pawn {
    public MarkerPawn(Color color, int xPos, int carCode) {
        super(carCode);
        if (color.getColorMembership() != PawnType.MARKER && color.getColorMembership() != PawnType.BOTH) {
            throw new InvalidColorMarkerPawnException();
        } else {
            super.setColor(color);
        }
    }
}
