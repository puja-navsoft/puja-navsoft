package com.afieat.ini.misc;

public class SearchResultsJsonParser {
    //private SearchItemParser searchItemParser = new SearchItemParser();

    /**
     * Parse the root result JSON object into a list of results.
     *
     * @param jsonObject The result's root object.
     * @return A list of results (potentially empty), or null in case of error.
     */


    /*public List<Search> parseResults(JSONArray hits)
    {
        if (hits == null)
            return null;

        List<Search> results = new ArrayList<>();
        //JSONArray hits = jsonObject.optJSONArray("hits");

        *//*if (hits == null)
            return null;*//*


        for (int i = 0; i < hits.length(); ++i) {
            JSONObject hit = hits.optJSONObject(i);
            if (hit == null)
                continue;

            Search search = searchItemParser.parse(hit);
            if (search == null)
                continue;



            results.add(search);
        }
        return results;
    }*/
}