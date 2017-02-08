package abinail.filters;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Sergii on 24.01.2017.
 */
public class QueryParamReplacer {
    //    private Pattern pattern;
    private String format = "(?<=\\?)%s[^\\s\\&]*(\\&)?|(?<=\\&)%s[^\\s\\&]*(\\&)?";
    private List<Pattern> patternList = new LinkedList<>();

    public QueryParamReplacer(String queryParamsToRemove) {
        if (queryParamsToRemove != null && !queryParamsToRemove.isEmpty()) {
//            StringBuilder patternStringBuilder = new StringBuilder(100);
            String[] queries = queryParamsToRemove.split(" ");

            for (String param : queries) {
                if (!param.isEmpty()) {
                    /*if (patternStringBuilder.length() > 0) patternStringBuilder.append("|");
                    patternStringBuilder.append("(?<=\\?)").append(param).append("[^\\s\\&]*(\\&)?|(?<=\\&)")
                            .append(param).append("[^\\s\\&]*(\\&)?");*/
                    String patternString = "(?<=\\?)" + param + "=[^\\s\\&]*(\\&){0,1}";
                    patternList.add(Pattern.compile(patternString, Pattern.CASE_INSENSITIVE));
                    patternString = "(?<=\\?)" + param + "$";
                    patternList.add(Pattern.compile(patternString, Pattern.CASE_INSENSITIVE));
                    patternString = "(?<=\\?)" + param + "\\&";
                    patternList.add(Pattern.compile(patternString, Pattern.CASE_INSENSITIVE));
                    //
                    patternString = "\\&" + param + "=[^\\s\\&]*";
                    patternList.add(Pattern.compile(patternString, Pattern.CASE_INSENSITIVE));
                    patternString = "\\&" + param + "$";
                    patternList.add(Pattern.compile(patternString, Pattern.CASE_INSENSITIVE));
                    patternString = "\\&" + param + "(?=\\&)";
                    patternList.add(Pattern.compile(patternString, Pattern.CASE_INSENSITIVE));

                }
            }
//            pattern = Pattern.compile(patternStringBuilder.toString(), Pattern.CASE_INSENSITIVE);
        }
    }

    public String removeParam(String s) {
        for (Pattern pattern : patternList) {
            s = pattern.matcher(s).replaceAll("");
        }
        return s;
//        return pattern.matcher(s).replaceAll("");
    }
}
