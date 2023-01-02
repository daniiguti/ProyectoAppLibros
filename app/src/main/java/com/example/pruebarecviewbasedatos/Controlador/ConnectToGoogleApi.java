package com.example.pruebarecviewbasedatos.Controlador;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ConnectToGoogleApi {
    //url estática que siempre va a ser asi
    private static final String URL_BASE = "https://www.googleapis.com/books/v1/volumes";

    //metodo getRequest, a partir de un endpoint, hace una peticion a la api
    public static String getRequest(String endpoint )
    {
        HttpURLConnection http = null;
        String content = null;

        try {

            URL url = new URL( URL_BASE + endpoint );

            http = (HttpURLConnection)url.openConnection();
            //Cabecera
            http.setRequestProperty("Content-Type", "application/json");

            http.setRequestProperty("Accept", "application/json");

            if( http.getResponseCode() == HttpURLConnection.HTTP_OK ) {
                //leemos lo que nos devuelve la consulta
                StringBuilder sb = new StringBuilder();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader( http.getInputStream() ));
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                content = sb.toString();
                System.out.println(content);
                reader.close();
            }

        }catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            if( http != null ) http.disconnect();
        }
        return content;
    }
}
