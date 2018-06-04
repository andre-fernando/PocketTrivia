package com.andre_fernando.pockettrivia.helpers;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.andre_fernando.pockettrivia.data.enums.JsonTags;
import com.andre_fernando.pockettrivia.data.enums.Queries;
import com.andre_fernando.pockettrivia.data.objects.Question;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * This is a helper class that gets the desired url
 * or gets the data from the server.
 */
public class OpenTriviaHelper {

    private final Context context;

    public static final String dailyTriviaUrl = "https://opentdb.com/api.php?amount=1";


    public OpenTriviaHelper(Context c){
        this.context = c;
    }

    //region Public Methods

    public ArrayList<Question> getData(String url_str){
        String jsonString = fetchJson(url_str);
        return parseJson(jsonString);
    }

    public String getAssembledURL(String number_of_questions, int categoryId,
                               String difficulty, String type){
        String BASE_URL = "https://opentdb.com/api.php";
        Uri.Builder builder = Uri.parse(BASE_URL).buildUpon();

        //for Number of Questions
        builder.appendQueryParameter(Queries.Amount,number_of_questions);

        //for Category
        if (categoryId!=0){
            String category = Integer.toString(categoryId+8);
            builder.appendQueryParameter(Queries.Category,category);
        }

        //for Difficulty
        if (!difficulty.equals("any")){
            builder.appendQueryParameter(Queries.Difficulty,difficulty);
        }

        //for Type
        if (!type.equals("any")){
            builder.appendQueryParameter(Queries.Type,type);
        }

        return builder.build().toString();
    }

    //endregion Public Methods

    //region Private Methods

    /**
     * Get's json from server
     * @param UrlString URL to request
     * @return a json String containing the Trivia.
     */
    private String fetchJson(String UrlString){
        HttpURLConnection urlConnection;
        String jsonStringResponse;
        try {
            URL url = new URL(UrlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in).useDelimiter("\\A");

            jsonStringResponse = scanner.hasNext() ? scanner.next() : "";
            urlConnection.disconnect();
            return jsonStringResponse;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(OpenTriviaHelper.class.getSimpleName(), "fetchJson: Failed to fetch Json!");
            return null;
        }
    }

    private ArrayList<Question> parseJson(String jsonString){
        if (jsonString == null){
            Log.e(OpenTriviaHelper.class.getSimpleName(), "parseJson: Received String is null");
            return new ArrayList<>();
        }
        try {
            ArrayList<Question> toReturn = new ArrayList<>();
            JSONObject overview = new JSONObject(jsonString);
            int responseCode = overview.getInt(JsonTags.ResponseCode);
            if (responseCode == 0){
                JSONArray list = overview.getJSONArray(JsonTags.Results);
                for (int i=0;i<list.length();i++){
                    //Uses the builder method to build the questions
                    toReturn.add(QuestionBuilder.build(list.getJSONObject(i),i));
                }
            } else {
                handleResponseCodeError(responseCode);
            }
            return toReturn;
        } catch (JSONException e){
            e.printStackTrace();
            Log.e(OpenTriviaHelper.class.getSimpleName(), "parseJson: Failed to parse Json");
            return null;
        }
    }

    private void handleResponseCodeError(int responseCode){
        if (responseCode == 1){
            Toast.makeText(context, "No results available!", Toast.LENGTH_SHORT).show();
            Log.w(this.getClass().getSimpleName(), "Response code 1: No results found.");
        }else {
            Toast.makeText(context, "Oops something went wrong, please contact the developer."
                    , Toast.LENGTH_SHORT).show();
            Log.e(this.getClass().getSimpleName(),"Unsupported response code: "+responseCode);
        }
    }

    //endregion Private Methods

}
