package com.abinail.filters;

import java.util.regex.Pattern;

/**
 * Created by Sergii on 24.01.2017.
 */
public class QueryParamReplacer {
    private Pattern pattern;

    public QueryParamReplacer(String queryParamsToRemove) {
        if (queryParamsToRemove != null && !queryParamsToRemove.isEmpty()){
            StringBuilder patternStringBuilder=new StringBuilder(100);
            String[] queries = queryParamsToRemove.split(" ");
            for (int i = 0; i < queries.length; i++) {
                String param=queries[i];
                if(param.isEmpty())
                    continue;
                patternStringBuilder.append("(?<=\\?)").append(param).append("[^\\s\\&]*(\\&)?|(?<=\\&)").append(param).append("[^\\s\\&]*(\\&)?");
                if(i<queries.length-1)
                    patternStringBuilder.append("|");
            }
            pattern = Pattern.compile(patternStringBuilder.toString(), Pattern.CASE_INSENSITIVE);
        }
    }


    public String removeParam(String s) {
        return pattern.matcher(s).replaceAll("");
    }
}
