package com.scanner.scannerapp.entities;

public class KeyPressEvent {

    public enum Direction { UP, DOWN, LEFT, RIGHT }

    private Direction direction;
    private long timestamp;

    public KeyPressEvent(Direction direction) {
        this.direction = direction;
        this.timestamp = System.currentTimeMillis(); // Assign timestamp automatically
    }

    // Default constructor for deserialization
    public KeyPressEvent() {}

    public Direction getDirection() {
        return direction;
    }

    public long getTimestamp() {
        return timestamp;
    }

    // Optional: override toString for better logs
    @Override
    public String toString() {
        return "KeyPressEvent{" +
                "direction=" + direction +
                ", timestamp=" + timestamp +
                '}';
    }


}
