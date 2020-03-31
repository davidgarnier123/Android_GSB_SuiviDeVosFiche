package fr.cned.emdsgil.suividevosfrais;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Locale;

public class AuthActivity extends AppCompatActivity {

    Button button;
    EditText Name , password;
    ArrayList<String> infoConnect;
    private static String url_to_database = "http://192.168.1.11/GSB-server/test.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        setTitle("GSB : Transfert des données");
        //Permet de faire les requetes dans le m^me thread
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //initialisation
        infoConnect = new ArrayList<String>();
        button = (Button) findViewById(R.id.btn_auth);
        Name = (EditText) findViewById(R.id.txtName);
        password = (EditText) findViewById(R.id.txtPassword);

        // button click event
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
               System.out.println("clicked");
               //Récuperation des saisie name et pass
               infoConnect.add(0, Name.getText().toString());
               infoConnect.add(1, password.getText().toString());
               //mise des informations dans un tableau json
               JSONArray jsonArray = new JSONArray(infoConnect);



               try {
                   //Si l'authentification est réussie
                   if( post(url_to_database, jsonArray, "Auth").contains("réussie") ){
                       Toast.makeText(AuthActivity.this, "Synchronisation en cours !", Toast.LENGTH_LONG).show();
                   } else {
                       Toast.makeText(AuthActivity.this, "Erreur d'authentification", Toast.LENGTH_LONG).show();
                   }


               } catch (IOException e){
                   System.out.println(e.toString());
               }


            }
        });

    }

    /**
     * Methode qui permet d'effectuer une reqûete GET vers le serveur distant
     * @param url
     * @return
     * @throws IOException
     */
    public String get(String url) throws IOException{
        InputStream is = null;
        try{
            final HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
           conn.setReadTimeout(10000);
           conn.setConnectTimeout(15000);
           conn.setRequestMethod("GET");
           conn.setDoInput(true);

            conn.connect();
            is = conn.getInputStream();
            return readIt(is);
         } finally {
            if (is != null){
                is.close();
            }
        }
    }

    public String post(String url, JSONArray jsonArray, String type) throws  IOException{

        String response = "";
        BufferedReader reader = null;
        HttpURLConnection conn = null;
        String data = null;
        String typePost = type;

        try {

            URL urlObj = new URL(url);

            conn = (HttpURLConnection) urlObj.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

            // typePost permet de gérer les différents type de POST
            // Auth pour l'authentification
            // Sync pour l'insertion dans la base de donnée
            if(typePost == "Auth"){
                //concatenation de chaine de charactere qui est transmis en POST : $_POST["Auth"] contient un jsonArray
                data += "&" + URLEncoder.encode(typePost, "UTF-8") + "="
                        + URLEncoder.encode(jsonArray.toString(), "UTF-8");
            }



            wr.write(data);
            wr.flush();

            // si code 200 = ok
           // System.out.println("post response code " + conn.getResponseCode() + " ");
            //int responseCode = conn.getResponseCode();


            // Lecture de la response
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;

            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            response = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            //System.out.println( "error");
        } finally {
            try {
                reader.close();
                if (conn != null) {
                    conn.disconnect();
                }
            } catch (Exception ex) {
            }
        }
       // System.out.println("La reponse : "  + response);
        if(response.contains("réussie%")){
            response = "Authentification réussie !";
        } else {
            response = "Echec d'authentification !";
        }

        return response;

    }
    /**
     * Méthode qui permet de lire un flux de donnée et de le restranscrire dans une string
     * @param is
     * @return
     * @throws IOException
     */
    private String readIt(InputStream is) throws  IOException{
        BufferedReader r = new BufferedReader(new InputStreamReader(is));
        StringBuilder response = new StringBuilder();
        String line;
        while((line = r.readLine()) != null){
            response.append(line).append('\n');
        }
        return response.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().equals(getString(R.string.retour_accueil))) {
            retourActivityPrincipale();
        }
        return super.onOptionsItemSelected(item);
    }




    /**
     * Retour à l'activité principale (le menu)
     */
    private void retourActivityPrincipale() {
        Intent intent = new Intent(AuthActivity.this, MainActivity.class);
        startActivity(intent);
    }
}