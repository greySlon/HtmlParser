package abinail.filters;

import abinail.model.Content;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Sergii on 08.02.2017.
 */
class BaseResolverTest {
    BaseResolver resolver = new BaseResolver();
    List<Content> contentList = new ArrayList<>();

    @BeforeEach
    void setUp() throws Exception {
        contentList.add(new Content(new URL("http://domain.com/folder/file.html"),
                "some text<base href=\"http://another.com\">"));

        contentList.add(new Content(new URL("http://domain.com/folder/file.html"),
                "some text<base href=\"./folder2\">"));

        contentList.add(new Content(new URL("http://domain.com/folder/file.html"),
                "some text"));

        contentList.add(new Content(new URL("http://domain.com/folder/file.html"),
                "some text<base href=\"/folder2\">"));

        contentList.add(new Content(new URL("http://domain.com/folder/file.html"),
                "some text<base href=\"folder2\">"));
    }

    @Test
    void getBaseUrl() {
        assertEquals("http://another.com", resolver.getBaseUrl(contentList.get(0)).toString());
        assertEquals("http://domain.com/folder/folder2", resolver.getBaseUrl(contentList.get(1)).toString());
        assertEquals("http://domain.com/folder/file.html", resolver.getBaseUrl(contentList.get(2)).toString());
        assertEquals("http://domain.com/folder2", resolver.getBaseUrl(contentList.get(3)).toString());
        assertEquals("http://domain.com/folder/folder2", resolver.getBaseUrl(contentList.get(4)).toString());
    }

}