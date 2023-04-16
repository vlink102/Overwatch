package me.vlink102.personal.overwatch.minecraftlink;

import net.md_5.bungee.api.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TableGenerator {
    private static final List<Character> char9 = Arrays.asList('✚');
    private static final List<Character> char8 = Arrays.asList('⛨');
    private static final List<Character> char7 = Arrays.asList('°', '~', '@');
    private static final List<Character> char5 = Arrays.asList('"', '{', '}', '(', ')', '*', 'f', 'k', '<', '>');
    private static final List<Character> char4 = Arrays.asList('I', 't', ' ', '[', ']', '€');
    private static final List<Character> char3 = Arrays.asList('l', '`', '³', '\'');
    private static final List<Character> char2 = Arrays.asList(',', '.', '!', 'i', '´', ':', ';', '|');
    private static final char char1 = '\u17f2';
    private static final Pattern regex = Pattern.compile(char1+"(?:§r)?(\\s*)"
            + "(?:§r§8)?"+char1+"(?:§r)?(\\s*)"
            + "(?:§r§8)?"+char1+"(?:§r)?(\\s*)"
            + "(?:§r§8)?"+char1);
    private final Alignment[] alignments;
    private final List<Row> table = new ArrayList<>();
    private final int columns;

    public TableGenerator(Alignment... alignments) {
        if (alignments == null || alignments.length < 1)
            throw new IllegalArgumentException("Must atleast provide 1 alignment.");

        this.columns = alignments.length;
        this.alignments = alignments;
    }

    public List<String> generate(Receiver receiver, boolean ignoreColors,
                                 boolean coloredDistances) {
        if (receiver == null) {
            throw new IllegalArgumentException("Receiver must not be null.");
        }

        //String colors = "[&§][\\da-fA-Fk-oK-OrR]";
        Integer[] columWidths = new Integer[columns];

        columWidths[0] = getCustomLength( "            §f§l!!!§r   ", Receiver.CLIENT);
        columWidths[1] = getCustomLength("§7Waiting for players...   ", Receiver.CLIENT);
        columWidths[2] = getCustomLength("   0   ", Receiver.CLIENT);
        columWidths[3] = getCustomLength("   0   ", Receiver.CLIENT);
        columWidths[4] = getCustomLength("   0   ", Receiver.CLIENT);
        columWidths[5] = getCustomLength("       ", Receiver.CLIENT);
        columWidths[6] = getCustomLength("     0     ", Receiver.CLIENT);
        columWidths[7] = getCustomLength("     0     ", Receiver.CLIENT);
        columWidths[8] = getCustomLength("     0     ", Receiver.CLIENT);
        /*
        for (Row r : table) {
            for (int i = 0; i < columns; i++) {
                String text = r.texts.get(i);
                int length;

                if (ignoreColors)
                    length = getCustomLength(text.replaceAll(colors, ""),
                            receiver);
                else length = getCustomLength(text, receiver);

                if (columWidths[i] == null) {
                    columWidths[i] = length;
                }

                else if (length > columWidths[i]) {
                    columWidths[i] = length;
                }
            }
        }
        */

        List<String> lines = new ArrayList<>();

        for (Row r : table) {
            StringBuilder sb = new StringBuilder();

            if (r.empty) {
                lines.add("");
                continue;
            }

            for (int i = 0; i < columns; i++) {
                Alignment agn = alignments[i];
                String text = r.texts.get(i);
                int length;

                if (ignoreColors)
                    length = getCustomLength(ChatColor.stripColor(text),
                            receiver);
                else length = getCustomLength(text,
                        receiver);

                int empty = columWidths[i] - length;
                int spacesAmount = empty;
                if (receiver == Receiver.CLIENT)
                    spacesAmount = (int) Math.floor(empty / 4d);
                int char1Amount = 0;
                if (receiver == Receiver.CLIENT)
                    char1Amount = empty - 4 * spacesAmount;

                String spaces = concatChars(' ', spacesAmount);
                String char1s = concatChars(char1, char1Amount);

                if (coloredDistances)
                    char1s = "§r§8" + char1s + "§r";

                if (agn == Alignment.LEFT) {
                    sb.append(text);
                    if (i < columns - 1)
                        sb.append(char1s).append(spaces);
                }
                if (agn == Alignment.RIGHT) {
                    sb.append(spaces).append(char1s).append(text);
                }
                if (agn == Alignment.CENTER) {
                    int leftAmount = empty / 2;
                    int rightAmount = empty - leftAmount;

                    int spacesLeftAmount = leftAmount;
                    int spacesRightAmount = rightAmount;
                    if (receiver == Receiver.CLIENT) {
                        spacesLeftAmount = (int) Math.floor(spacesLeftAmount / 4d);
                        spacesRightAmount = (int) Math.floor(spacesRightAmount / 4d);
                    }

                    int char1LeftAmount = 0;
                    int char1RightAmount = 0;
                    if (receiver == Receiver.CLIENT) {
                        char1LeftAmount = leftAmount - 4 * spacesLeftAmount;
                        char1RightAmount = rightAmount - 4 * spacesRightAmount;
                    }

                    String spacesLeft = concatChars(' ', spacesLeftAmount);
                    String spacesRight = concatChars(' ', spacesRightAmount);
                    String char1Left = concatChars(char1, char1LeftAmount);
                    String char1Right = concatChars(char1, char1RightAmount);

                    if (coloredDistances) {
                        char1Left = "§r§8" + char1Left + "§r";
                        char1Right = "§r§8" + char1Right + "§r";
                    }

                    sb.append(spacesLeft).append(char1Left).append(text);
                    if (i < columns - 1)
                        sb.append(char1Right).append(spacesRight);
                }

                String delimiter = "";
                if (i < columns - 1) sb.append("§r").append(delimiter);
            }

            String line = sb.toString();
            if (receiver == Receiver.CLIENT) {
                for (int i = 0; i < 2; i++) {
                    Matcher matcher = regex.matcher(line);
                    line = matcher.replaceAll("$1$2$3 ").replace("§r§8§r", "§r")
                            .replaceAll("§r(\\s*)§r", "§r$1");
                }
            }
            lines.add(line);
        }
        return lines;
    }

    protected static int getCustomLength(String text, Receiver receiver) {
        if (text == null) {
            throw new IllegalArgumentException("Text must not be null.");
        }
        if (receiver == null) {
            throw new IllegalArgumentException("Receiver must not be null.");
        }
        if (receiver == Receiver.CONSOLE) return text.length();

        int length = 0;

        text = text.replaceAll("%animation:Loading%", "&7• &7• &7•");

        for (char c : text.toCharArray())
            length += getCustomCharLength(c);

        return length;
    }

    protected static int getCustomCharLength(char c) {
        if (char1 == c) return 1;
        if (char2.contains(c)) return 2;
        if (char3.contains(c)) return 3;
        if (char4.contains(c)) return 4;
        if (char5.contains(c)) return 5;
        if (char7.contains(c)) return 7;

        return 6;
    }

    protected String concatChars(char c, int length) {
        StringBuilder s = new StringBuilder();
        if (length < 1) return s.toString();

        s.append(Character.toString(c).repeat(length));
        return s.toString();
    }

    public void addRow(String... texts) {
        if (texts == null) {
            throw new IllegalArgumentException("Texts must not be null.");
        }
        if (texts.length > columns) {
            throw new IllegalArgumentException("Too big for the table.");
        }

        Row r = new Row(texts);

        table.add(r);
    }

    private class Row {
        public List<String> texts = new ArrayList<>();
        public boolean empty = true;

        public Row(String... texts) {
            if (texts == null) {
                for (int i = 0; i < columns; i++)
                    this.texts.add("");
                return;
            }

            for (String text : texts) {
                if (text != null) empty = false;

                this.texts.add(text);
            }

            for (int i = 0; i < columns; i++) {
                if (i >= texts.length) this.texts.add("");
            }
        }
    }

    public enum Receiver {

        CONSOLE, CLIENT
    }

    public enum Alignment {
        CENTER,
        RIGHT,
        LEFT
    }
}