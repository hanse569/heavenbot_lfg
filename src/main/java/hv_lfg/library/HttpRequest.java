package hv_lfg.library;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;

public class HttpRequest {

    /*
String retourDeLaPage = get(« http://www.perdu.com »);
String retourDeLaPage = get(« http://www.perdu.com?pseudonyme=Idleman&pasword=danstesreves »);
     */

    public static String get(String url) throws IOException {

        String source ="";
        URL oracle = new URL(url);
        URLConnection yc = oracle.openConnection();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        yc.getInputStream()));
        String inputLine;

        while ((inputLine = in.readLine()) != null)
            source +=inputLine;
        in.close();
        return source;
    }

    /*
List<String> keys = new ArrayList<String>();

List<String> values= new ArrayList<String>();

keys.add("pseudonyme");

keys.add("password");

values.add("idleman");

values.add("danstesreves");

String retourDeLaPage = post("http://www.perdu.com",keys,values);
     */
    public static String post(String adress, List<String> keys, List<String> values) throws IOException{
        String result = "";
        OutputStreamWriter writer = null;
        BufferedReader reader = null;
        try {
//encodage des paramètres de la requête
            String data="";
            for(int i=0;i<keys.size();i++){
                if (i!=0) data += "&";
                data += URLEncoder.encode(keys.get(i), "UTF-8")+"="+URLEncoder.encode(values.get(i), "UTF-8");
            }
//création de la connection
            URL url = new URL(adress);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);


//envoi de la requête
            writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(data);
            writer.flush();




//lecture de la réponse
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String ligne;
            while ((ligne = reader.readLine()) != null) {
                result+=ligne;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }finally{
            try{writer.close();}catch(Exception e){}
            try{reader.close();}catch(Exception e){}
        }
        return result;
    }
}
