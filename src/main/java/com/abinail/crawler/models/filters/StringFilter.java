package com.abinail.crawler.models.filters;

import com.abinail.crawler.interfaces.Filter;
import com.abinail.crawler.interfaces.MessageProvider;

import java.util.regex.Pattern;

public class StringFilter implements Filter<String> {
    private Pattern pattern;

    /*
    * parametr - strings separated with space,
    * that will be joined to regex
    * */
    public StringFilter(MessageProvider provider) {
        String containString = provider.getMessage();

        if (containString != null && !containString.isEmpty()) {
            StringBuilder sb = new StringBuilder(100);
            String[] subStr = containString.split(" ");
            for (String s : subStr) {
                if (!s.isEmpty()) {
                    if (sb.length() > 0) sb.append("|");
                    sb.append(s);
                }
            }
            pattern = Pattern.compile(sb.toString(), Pattern.CASE_INSENSITIVE);
        }
    }

    @Override
    public boolean test(String s) {
        return pattern == null ? true : pattern.matcher(s).find();
    }
}
