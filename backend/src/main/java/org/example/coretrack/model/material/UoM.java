package org.example.coretrack.model.material;

public enum UoM {
    KG("Kilogram"),
    G("Gram"),
    LITER("Liter"),
    ML("Milliliter"),
    PIECE("Piece"),
    BOX("Box"),
    METER("Meter"),
    CM("Centimeter"),
    PACK("Pack");

    private final String displayName;

    UoM(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
