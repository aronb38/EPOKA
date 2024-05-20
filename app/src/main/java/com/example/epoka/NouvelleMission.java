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

        // Récupérer id à partir de l'Intent et l'assigner à la variable idSalarie
        idSalarie = getIntent().getIntExtra("id", -1);

        // Affichez ces données pour vérification
        Log.d("NouvelleMission", "ID: " + idSalarie);

        // Exécutez la tâche asynchrone pour récupérer les villes à partir du serveur
        new FetchVillesTask().execute("http://192.168.1.96/epoka/importerville.php");
    }

    private class FetchVillesTask extends AsyncTask<String, Void, List<Ville>> { //tache en arriere plan ducoup

        @Override
        protected List<Ville> doInBackground(String... urls) {
            List<Ville> villes = new ArrayList<>(); //cree une nouvelle liste vide

            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) { //verifi si la requete reussi
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder jsonResponse = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        jsonResponse.append(line);
                    }
                    reader.close();

                    JSONArray jsonArray = new JSONArray(jsonResponse.toString()); //converti le json en tableau json
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject villeJson = jsonArray.getJSONObject(i);
                        int idVille = villeJson.getInt("idVille"); //extrait l'id de la ville
                        String nom = villeJson.getString("nom"); //puis le nom
                        String codePostal = villeJson.getString("code_postal"); //et cp
                        Ville ville = new Ville(idVille, nom, codePostal); //cree un nvl objet  ville avec les données
                        villes.add(ville); //ajoute l'objet a la liste
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(NouvelleMission.this, "Erreur de connexion au serveur", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                connection.disconnect(); //FIN de la connexion Http
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return villes; //retourne la liste ville a partir du json
        }

        @Override
        protected void onPostExecute(List<Ville> villes) {
            ArrayAdapter<Ville> adapter = new ArrayAdapter<>(NouvelleMission.this, android.R.layout.simple_spinner_item, villes);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerLieu.setAdapter(adapter);
        }
    }


    public void addMission(View button) {
        EditText dateDebut = findViewById(R.id.et_date_debut);
        EditText dateFin = findViewById(R.id.et_date_fin);

        // Récupérer la ville sélectionnée dans le spinner + ville en haut
        Ville selectedVille = (Ville) spinnerLieu.getSelectedItem();
        int idVille = selectedVille.getIdVille();

        // Vérifier si l'ID du salarié est valide
        if (idSalarie != -1) {
            // Construire l'URL pour l'ajout de mission
            String debut = dateDebut.getText().toString();
            String fin = dateFin.getText().toString();

            String urlServiceAjout = "http://192.168.1.96/epoka/AjoutMission.php?debut=" + debut + "&fin=" + fin + "&idVille=" + idVille + "&idSalarie=" + idSalarie;
        //Construit une URL pour envoyer les données au serveur et lance une tâche asynchrone pour ajouter la mission.
            new AddMissionTask().execute(urlServiceAjout);


        } else {
            Log.e("AddMissionTask", "Erreur lors de la récupération de l'ID de la ville");
            Toast.makeText(NouvelleMission.this, "Erreur lors de la récupération de l'ID de la ville", Toast.LENGTH_SHORT).show();
        }

    }



    private class AddMissionTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... urls) { //envoi la requete HTTP et verifie la reponse du server
            try {
                String urlString = urls[0];
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
