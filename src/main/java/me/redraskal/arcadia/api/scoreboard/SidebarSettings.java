package me.redraskal.arcadia.api.scoreboard;

public class SidebarSettings {

    private final Class<? extends Sidebar> clazz;
    private final int gameMinutes;
    private final int gameSeconds;

    public SidebarSettings(Class<? extends Sidebar> clazz,
        int gameMinutes, int gameSeconds) {
        this.clazz = clazz;
        this.gameMinutes = gameMinutes;
        this.gameSeconds = gameSeconds+1;
    }

    public Class<? extends Sidebar> getClazz() {
        return this.clazz;
    }

    public int getGameMinutes() {
        return this.gameMinutes;
    }

    public int getGameSeconds() {
        return this.gameSeconds;
    }
}