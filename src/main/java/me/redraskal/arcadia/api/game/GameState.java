package me.redraskal.arcadia.api.game;

public enum GameState {

    STARTING(0),
    INGAME(1),
    FINISHED(2);

    private final int id;

    private GameState(int id) {
        this.id = id;
    }

    public int getID() {
        return this.id;
    }
}