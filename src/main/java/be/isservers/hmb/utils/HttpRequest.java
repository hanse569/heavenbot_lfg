package be.isservers.hmb.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@SuppressWarnings("ConstantConditions")
public class HttpRequest {

    public static String get(String url) throws IOException {

        StringBuilder source = new StringBuilder();
        URL oracle = new URL(url);
        URLConnection yc = oracle.openConnection();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        yc.getInputStream()));
        String inputLine;

        while ((inputLine = in.readLine()) != null)
            source.append(inputLine);
        in.close();
        return source.toString();
    }

    public static String post(String adress, List<String> keys, List<String> values) {
        StringBuilder result = new StringBuilder();
        OutputStreamWriter writer = null;
        BufferedReader reader = null;
        try {
//encodage des paramètres de la requête
            StringBuilder data= new StringBuilder();
            for(int i=0;i<keys.size();i++){
                if (i!=0) data.append("&");
                data.append(URLEncoder.encode(keys.get(i), StandardCharsets.UTF_8)).append("=").append(URLEncoder.encode(values.get(i), StandardCharsets.UTF_8));
            }
//création de la connection
            URL url = new URL(adress);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

//envoi de la requête
            writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(data.toString());
            writer.flush();

//lecture de la réponse
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String ligne;
            while ((ligne = reader.readLine()) != null) {
                result.append(ligne);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }finally{
            try{writer.close();}catch(Exception ignored){}
            try{reader.close();}catch(Exception ignored){}
        }
        return result.toString();
    }
}
