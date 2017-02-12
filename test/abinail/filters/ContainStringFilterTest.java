package abinail.filters;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Sergii on 08.02.2017.
 */
class ContainStringFilterTest {
    ContainStringFilter filter=new ContainStringFilter("word1 word2");
    @Test
    void test() {
        assertTrue( filter.test("here is word1"));
        assertTrue( filter.test("here is word2"));
        assertFalse(filter.test("there are no word here"));
    }

}