package aed3;

public class Console {
    public static void clear() {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                // Try to use cmd cls which generally clears the console on Windows
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                // ANSI escape sequence for most UNIX terminals
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            // fallback: print many new lines
            for (int i = 0; i < 80; i++)
                System.out.println();
        }
    }
}
