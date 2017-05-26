package com.abinail.crawler.models.filters;

import com.abinail.crawler.interfaces.Filter;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component("imgFilter")
public class ImgFilter implements Filter<String> {
    private Pattern extPattern = Pattern.compile(".jpg$|.jpeg$|.png$|.bmp$|.gif$", Pattern.CASE_INSENSITIVE);

    @Override
    public boolean test(String s) {
        return extPattern.matcher(s).find();
    }
}
