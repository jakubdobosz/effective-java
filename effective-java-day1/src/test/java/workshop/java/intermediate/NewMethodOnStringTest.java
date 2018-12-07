package workshop.java.intermediate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NewMethodOnStringTest {

    @Test
    @DisplayName("prints unicode dinosaurs")
    public void dino() {
        String dinoAttack = "\u2000\u2000\u2000\uD83E\uDD95\u2000\u2000\u2000\uD83E\uDD96\u2000\u2000\u2000";

        System.out.println(dinoAttack);
        System.out.println(dinoAttack.strip());
        System.out.println(dinoAttack.replaceAll("\\s", ""));
    }

    @Test
    public void stripVsTrim() {
        String s = "\t abc \n";

        assertEquals("abc", s.trim());
        assertEquals("abc", s.strip());
    }

    @Test
    public void stripVsTrimDifferent() {
        char c = '\u2000'; // above \u0020
        String s = c + "abc" + c;

        assertTrue(Character.isWhitespace(c));
        assertEquals(s, s.trim());
        assertEquals("abc", s.strip());
    }

    @Test
    void repeat() {
        String s = "M";
        String repeated = s.repeat(10);

        assertEquals("MMMMMMMMMM", repeated);
    }

    @Test
    void blank() {
        String s = "   ";
        assertTrue(s.isBlank());
    }

    @Test
    void lines() {
        Stream.of("MIchał\nMichaluk".split("\n"));
        "MIchał\nMichaluk".lines();
    }
}
