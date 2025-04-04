package com.scanner.scannerapp.components;

public class CanvasUpdateEvent {

    private final Position position;
    private final int colorCode;

    public CanvasUpdateEvent(Position position, int colorCode) {
        this.position = new Position(position.getX(), position.getY()); // defensive copy
        this.colorCode = colorCode;
    }


    public Position getPosition() {
        return position;
    }

    public int getColorCode() {
        return colorCode;
    }
}
