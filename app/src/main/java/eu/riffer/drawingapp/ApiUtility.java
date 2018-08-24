package eu.riffer.drawingapp;

import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Scanner;

public class ApiUtility {
    private ApiUtility() {}

    public static final String BASE_API_URL = "https://cs2-testing.azurewebsites.net/api/setStroke";
    public static final String SET_STROKE_URL = "https://cs2-testing.azurewebsites.net/api/setStroke";

    public static String buildUrl(String title) {
        String fullUrl = BASE_API_URL + "?q=" + title;
        URL url = null;
        try {
            url = new URL(fullUrl);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return url.toString();
    }

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

            //Read
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            bufferedReader.close();
            result = sb.toString();

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}
