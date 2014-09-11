package filtering;


import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.ScreenWriter;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalSize;

import java.util.ArrayList;

public class GUI {

    private static String TAG = "GUI";

    private Filterer filterer;
    private Screen screen;
    private TerminalSize size;
    private ScreenWriter writer;
    private Timer timer = new Timer();

    private static String prefix = "> ";
    private String query = "";
    private ArrayList<MetaString> results = new ArrayList<MetaString>();
    private boolean areResultsDirty = false;
    private long duration;

    public GUI(Filterer filterer) {
        this.filterer = filterer;
    }

    public void run() throws InterruptedException {
        initialize();

        boolean keepRunning = true;
        Key key;

        updateScreen();
        while (keepRunning) {
            Thread.sleep(1);
            key = screen.readInput();
            if (key == null) continue;

            if (key.getKind() == Key.Kind.Escape) {
                keepRunning = false;
            } else if (key.getKind() == Key.Kind.Backspace) {
                if (query.length() > 0) {
                    areResultsDirty = true;
                    query = query.substring(0, query.length() - 1);
                }
            } else if (key.getKind() == Key.Kind.NormalKey) {
                areResultsDirty = true;
                query += key.getCharacter();
            }

            updateScreen();
        }

        finish();
    }

    private void initialize() {
        screen = TerminalFacade.createScreen();
        writer = new ScreenWriter(screen);
        screen.startScreen();
        size = screen.getTerminalSize();
    }

    private void finish() {
        screen.stopScreen();
    }

    private void updateResults() {
        if (query.length() >= 2 && areResultsDirty) {
            timer.start("updateResults " + query);
            results = filterer.search(query);
            duration = timer.finish("updateResults " + query);
            areResultsDirty = false;
        } else {
            results = new ArrayList<MetaString>();
            duration = 0;
        }
    }

    private int paintScreen() {
        screen.clear();

        int cursor = prefix.length() + query.length() + 1;
        writer.setForegroundColor(Terminal.Color.CYAN);
        writer.drawString(1, 1, prefix + query);

        String durationString = "[" + duration + "ms]";
        writer.setForegroundColor(Terminal.Color.RED);
        writer.drawString(cursor + 2, 1, durationString);

        int row = 3;
        writer.setForegroundColor(Terminal.Color.WHITE);
        for (MetaString ms : results) {
            if (row >= size.getRows()) break;
            writer.drawString(1, row, ms.getOriginal());
            row++;
        }

        return cursor;
    }

    private void updateScreen() {
        updateResults();
        int cursorPos = paintScreen();
        screen.setCursorPosition(cursorPos, 1);
        screen.refresh();
    }
}
