package eu.riffer.drawingapp;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

public class ApiUtility {
    private ApiUtility() {}

    public static final String SET_STROKE_URL = "http://riffer.eu/riffer/api/strokes";
    public static final String POST_STROKE_URL = "http://riffer.eu/riffer/api/strokes";
    public static final String GET_CONFIG_URL = "http://riffer.eu/riffer/api/config";

    public static String getJson(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        try {
            InputStream stream = connection.getInputStream();
            Scanner scanner = new Scanner(stream);
            scanner.useDelimiter("\\A");

            boolean hasData = scanner.hasNext();
            if (hasData) {
                return scanner.next();
            } else {
                return null;
            }
        }
        catch (Exception e) {
            Log.d("Error", e.toString());
            return null;
        }
        finally {
            connection.disconnect();
        }
    }


    public static AppConfiguration getConfiguration() {
        try {
            String result = makeGetRequest(GET_CONFIG_URL);
            if (result != null) {
                Gson gson = new Gson();
                AppConfiguration config = gson.fromJson(result, AppConfiguration.class);
                return config;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        // default config
        return new AppConfiguration();
    }

    public static Stroke setStroke(Stroke stroke) {
        Stroke newStroke = stroke;
        try {
            // https://futurestud.io/tutorials/gson-getting-started-with-java-json-serialization-deserialization
            Gson gson = new Gson();
            String json = gson.toJson(stroke);

            String result = makeRequest(SET_STROKE_URL, json);
            if (result != null) {
                newStroke = gson.fromJson(result, Stroke.class);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        // on error return the orginal stroke
        if (newStroke == null) {
            return stroke;
        }
        return newStroke;
    }

    public static List<Stroke> exchangeStrokes(List<Stroke> strokeList) {
        List<Stroke> newStokeList = new LinkedList<Stroke>();
        Gson gson = new Gson();
        String json = gson.toJson(strokeList);

        String result = makeRequest(POST_STROKE_URL, json);
        if (result != null) {
            // https://stackoverflow.com/questions/22271779/is-it-possible-to-use-gson-fromjson-to-get-arraylistarrayliststring#22271806
            LinkedList<Stroke> list = gson.fromJson(result, new TypeToken<LinkedList<Stroke>>() {}.getType());
            return list;
        }
        return null;
    }

    public static String makeGetRequest(String uri) {
        HttpURLConnection urlConnection;
        String result = null;
        try {
            URL url = new URL(uri);
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestMethod("GET");
            int statusCode = urlConnection.getResponseCode();
            if (statusCode == 200) {
                result = getResponse(urlConnection, result);
            }

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    public static String makeRequest(String uri, String json) {
        HttpURLConnection urlConnection;
        String result = null;
        try {
            URL url = new URL(uri);
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestMethod("POST");
            urlConnection.connect();

            // Write
            OutputStream outputStream = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            writer.write(json);
            writer.close();
            outputStream.close();

            result = getResponse(urlConnection, result);

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @NonNull
    private static String getResponse(HttpURLConnection urlConnection, String result) throws IOException {
        //Read
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
        String line = null;
        StringBuilder sb = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line);
        }
        bufferedReader.close();
        result = sb.toString();
        return result;
    }

}
