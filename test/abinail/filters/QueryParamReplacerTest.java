package abinail.filters;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QueryParamReplacerTest {

    QueryParamReplacer replacer=new QueryParamReplacer("cur");
    QueryParamReplacer replacer2=new QueryParamReplacer("cur prm1");

    @Test
    void removeParam() {
        assertEquals("/domain.com/folder?",replacer.removeParam("/domain.com/folder?cur"));
        assertEquals("/domain.com/folder?",replacer.removeParam("/domain.com/folder?cur="));
        assertEquals("/domain.com/folder?",replacer.removeParam("/domain.com/folder?cur=12"));
        assertEquals("/domain.com/folder?currency",replacer.removeParam("/domain.com/folder?cur&currency"));
        assertEquals("/domain.com/folder?currency",replacer.removeParam("/domain.com/folder?cur=&currency"));
        assertEquals("/domain.com/folder?currency",replacer.removeParam("/domain.com/folder?cur=12&currency"));


        assertEquals("/domain.com/folder?param1=45",
                replacer.removeParam("/domain.com/folder?param1=45&cur"));
        assertEquals("/domain.com/folder?param1",
                replacer.removeParam("/domain.com/folder?param1&cur="));
        assertEquals("/domain.com/folder?prm1",
                replacer.removeParam("/domain.com/folder?prm1&cur=12"));
        assertEquals("/dom.com/fold?prm1=45&currency",
                replacer.removeParam("/dom.com/fold?prm1=45&cur&currency"));
        assertEquals("/dom.com/fold?prm1&currency",
                replacer.removeParam("/dom.com/fold?prm1&cur=&currency"));
        assertEquals("/dom.com/fold?prm1&currency",
                replacer.removeParam("/dom.com/fold?prm1&cur=12&currency"));


        assertEquals("/domain.com/folder?",
                replacer2.removeParam("/domain.com/folder?prm1&cur=12"));
        assertEquals("/dom.com/fold?currency",
                replacer2.removeParam("/dom.com/fold?prm1=45&cur&currency"));
        assertEquals("/dom.com/fold?currency",
                replacer2.removeParam("/dom.com/fold?prm1&cur=&currency"));
        assertEquals("/dom.com/fold?currency",
                replacer2.removeParam("/dom.com/fold?prm1&cur=12&currency"));
    }

}