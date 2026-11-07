import app.lawnchair.y700.DrawerRank;

public final class DrawerRankTest {
    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
    public static void main(String[] args) {
        long now = 1800000000000L;
        long day = 86400000L;
        check(DrawerRank.score(0, 0, now) == 0, "Unknown apps must start at zero");
        check(DrawerRank.score(2, now, now) > DrawerRank.score(2, now - 7 * day, now), "Recent use must rank higher");
        check(DrawerRank.score(10, now, now) > DrawerRank.score(1, now, now), "Frequency must affect ranking");
        check(DrawerRank.score(1, now, now) > DrawerRank.score(10, now - 30 * day, now), "Recent use must compete with stale frequency");
        check(DrawerRank.score(1, now + day, now) == DrawerRank.score(1, now, now), "Clock rollback must not inflate score");
        check(Double.isFinite(DrawerRank.score(Integer.MAX_VALUE, 1, now)), "Large histories must remain finite");
        check(DrawerRank.columns(0, 3) == 3, "Minimum grid size");
        check(DrawerRank.columns(400, 3) == 5, "Phone grid width");
        check(DrawerRank.columns(720, 3) == 9, "Tablet portrait grid width");
        check(DrawerRank.columns(1280, 3) == 12, "Landscape grid maximum");
        check(DrawerRank.columns(64, 1) == 1, "Narrow frequent row minimum");
        check(DrawerRank.columns(887f / 2, 1) == 5, "Landscape IME compact row keeps labels readable");
        System.out.println("12 drawer ranking and responsive layout checks passed");
    }
}
