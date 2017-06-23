package me.redraskal.arcadia.api.scoreboard;

public class SidebarSettings {

    private final Class<? extends Sidebar> clazz;
    private final WinMethod winMethod;
    private final int gameMinutes;
    private final int gameSeconds;

    public SidebarSettings(Class<? extends Sidebar> clazz,
        WinMethod winMethod, int gameMinutes, int gameSeconds) {
        this.clazz = clazz;
        this.winMethod = winMethod;
        this.gameMinutes = gameMinutes;
        this.gameSeconds = gameSeconds+1;
    }

    public Class<? extends Sidebar> getClazz() {
        return this.clazz;
    }

    public WinMethod getWinMethod() {
        return this.winMethod;
    }

    public int getGameMinutes() {
        return this.gameMinutes;
    }

    public int getGameSeconds() {
        return this.gameSeconds;
    }
}