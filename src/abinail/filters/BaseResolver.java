package abinail.filters;

import abinail.model.Content;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Sergii on 23.01.2017.
 */
public class BaseResolver {
    private Pattern pattern = Pattern.compile("(?<=<base.{1,20}href[^\"]{1,4}\"\\s{0,3})[^\"]+");

    public URL getBaseUrl(Content in) {
        String content=in.content;
        URL url=in.url;
        String s=null;

        Matcher matcher = pattern.matcher(content);
        if (matcher.find())
            s = matcher.group();
        URL baseUrl;
        if(s!=null){
            try {
                baseUrl=new URL(s);
            } catch (MalformedURLException e) {
                try {
                    baseUrl=new URL(url, s);
                } catch (MalformedURLException e1) {
                    baseUrl=url;
                }
            }
        }else {
            baseUrl=url;
        }
        return baseUrl;
    }
    public static void main(String[]a) throws MalformedURLException{
        URL base=new URL("http://qwer.com/");
        System.out.println(new URL(base,"").toString());
        System.out.println(new URL(base,"/").toString());
    }
}
