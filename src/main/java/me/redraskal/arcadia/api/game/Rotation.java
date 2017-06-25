package me.redraskal.arcadia.api.game;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Rotation {

    private int current = 0;
    private List<Class<? extends BaseGame>> games = new ArrayList<>();
    private RotationOrder rotationOrder = RotationOrder.DEFAULT;

    /**
     * Returns the current rotation amount.
     * @return
     */
    public int getCurrentID() {
        return this.current;
    }

    /**
     * Returns the current game.
     * @return
     */
    public Class<? extends BaseGame> getCurrentGame() {
        return this.games.get(current);
    }

    /**
     * Returns the rotation size.
     * @return
     */
    public int getSize() {
        return this.games.size();
    }

    /**
     * Cycles the rotation by one.
     * @return
     */
    public Class<? extends BaseGame> nextGame() {
        current++;
        if(current >= games.size()) {
            if(this.rotationOrder == RotationOrder.RANDOM) this.shuffle();
            current = 0;
        }
        return this.getCurrentGame();
    }

    /**
     * Sets the rotation order.
     * @param rotationOrder
     */
    public void setRotationOrder(RotationOrder rotationOrder) {
        this.rotationOrder = rotationOrder;
    }

    /**
     * Adds a game to the rotation.
     * @param game
     */
    public void addGame(Class<? extends BaseGame> game) {
        Preconditions.checkNotNull(game, "Game cannot be null");
        games.add(game);
    }

    /**
     * Removes a game from the rotation.
     * @param game
     * @return
     */
    public boolean removeGame(Class<? extends BaseGame> game) {
        Preconditions.checkNotNull(game, "Game cannot be null");
        if(!games.contains(game)) return false;
        return removeGame(games.indexOf(game));
    }

    /**
     * Removes a game from the rotation.
     * @param id
     * @return
     */
    public boolean removeGame(int id) {
        if(games.size() <= id) return false;
        games.remove(id);
        return true;
    }

    /**
     * Shuffles the rotation :D
     */
    public void shuffle() {
        Collections.shuffle(games);
    }
}