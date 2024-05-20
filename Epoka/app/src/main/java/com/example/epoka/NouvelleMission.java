package com.example.epoka;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.lang.String;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class NouvelleMission extends AppCompatActivity {

    private Spinner spinnerLieu;
    private int idSalarie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nouvelle_mission);
        spinnerLieu = findViewById(R.id.spinner_lieu);

        // Récupérer l'ID du salarié à partir de l'Intent et l'assigner à la variable idSalarie
        idSalarie = getIntent().getIntExtra("id", -1);

        // Affichez ces données pour vérification
        Log.d("NouvelleMission", "ID: " + idSalarie);

        // Exécutez la tâche AsyncTask pour récupérer les villes à partir du serveur
        new FetchVillesTask().execute("http://172.16.47.1/epoka/importerville.php");
    }

    private class FetchVillesTask extends AsyncTask<String, Void, List<String>> {

        @Override
        protected List<String> doInBackground(String... urls) {
            List<String> villes = new ArrayList<>();

            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder jsonResponse = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        jsonResponse.append(line);
                    }
                    reader.close();

                    JSONArray jsonArray = new JSONArray(jsonResponse.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject villeJson = jsonArray.getJSONObject(i);
                        String nom = villeJson.getString("nom");
                        String codePostal = villeJson.getString("code_postal");
                        villes.add(nom + " (" + codePostal + ")");
                    }
                } else {
                    // Gestion des erreurs de connexion
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(NouvelleMission.this, "Erreur de connexion au serveur", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                connection.disconnect();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return villes;
        }

        @Override
        protected void onPostExecute(List<String> villes) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(NouvelleMission.this, android.R.layout.simple_spinner_item, villes);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerLieu.setAdapter(adapter);
        }
    }

    public void addMission(View button) {
        EditText dateDebut = findViewById(R.id.et_date_debut);
        EditText dateFin = findViewById(R.id.et_date_fin);

        // Récupérer la ville sélectionnée
        String selectedString = (String) spinnerLieu.getSelectedItem();
        String nomVille;
        String idVille;

        // Utilisation d'une expression régulière pour extraire l'ID de la ville
        Pattern pattern = Pattern.compile("\\((\\d+)\\)");
        Matcher matcher = pattern.matcher(selectedString);
        if (matcher.find()) {
            idVille = matcher.group(1);
            nomVille = selectedString.substring(0, selectedString.indexOf("(")).trim();

            // Vérifier si l'ID du salarié est valide
            if (idSalarie != -1) {
                // Construire l'URL pour l'ajout de mission
                String debut = dateDebut.getText().toString();
                String fin = dateFin.getText().toString();
                String urlServiceAjout = "http://172.16.47.1/epoka/AjoutMission.php?debut=" + debut + "&fin=" + fin + "&idVille=" + idVille + "&idSalarie=" + idSalarie;

                new AddMissionTask().execute(urlServiceAjout);
            } else {
                Log.e("log-tag", "La clé 'id' n'a pas été passée dans l'Intent.");
            }
        } else {
            Log.e("AddMissionTask", "Erreur lors de la récupération de l'ID de la ville");
            Toast.makeText(NouvelleMission.this, "Erreur lors de la récupération de l'ID de la ville", Toast.LENGTH_SHORT).show();
        }
    }



    private class AddMissionTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... urls) {
            try {
                String urlString = urls[0];
               // StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
               // StrictMode.setThreadPolicy(policy);
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // Vérifier si l'ajout de la mission a réussi en vérifiant le code de réponse HTTP
                int responseCode = connection.getResponseCode();
                return responseCode == HttpURLConnection.HTTP_OK;

            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(NouvelleMission.this, "Ajout effectué", Toast.LENGTH_SHORT).show();
            } else {
                Log.e("AddMissionTask", "Erreur lors de l'ajout de la mission");
                Toast.makeText(NouvelleMission.this, "Erreur lors de l'ajout de la mission", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
