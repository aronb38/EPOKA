package com.example.epoka;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.lang.ref.WeakReference;

public class AuthentificationTask extends AsyncTask<String, Void, JSONObject> {
    //permet de mettre authentificationtark en sous classe de asynctask pr faire des tache et afficher dans le thread principal

    private static final String TAG = "AuthentificationTask";
    private WeakReference<Context> mContextRef;

    public AuthentificationTask(Context context) {
        mContextRef = new WeakReference<>(context);
    }



    @Override
    protected JSONObject doInBackground(String... params) { //en arriere plan
        String urlString = params[0];
        String id = params[1]; // Modifié de 'nom' à 'id'
        String motDePasse = params[2];
        String data = null;
        try {
            data = "id=" + URLEncoder.encode(id, "UTF-8") + "&mot_de_passe=" + URLEncoder.encode(motDePasse, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST"); //requete post pr envoyer vers le server les donnes
            connection.setDoOutput(true);

            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(data.getBytes());
            outputStream.flush();
            outputStream.close();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream())); //lis la reponse du server
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }

            reader.close();
            connection.disconnect();
            return new JSONObject(stringBuilder.toString());
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Erreur lors de la connexion au serveur ou l'analyse du JSON", e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        Context context = mContextRef.get();
        if (context != null) {
            if (result != null) { //verifi si le resultat de la tache est non null
                try {
                    String message = result.getString("message");
                    if (message.equals("Connexion réussie")) {
                        String id = result.getString("id");
                        Log.d(TAG, "ID récupéré : " + id); // Ajouter ce log pour vérifier si l'ID est correctement récupéré
                        String nom = result.getString("nom");
                        String prenom = result.getString("prenom");

                        // Afficher un Toast en bas de l'écran
                        Toast toast = Toast.makeText(context, "Connexion réussie", Toast.LENGTH_SHORT); //si connexion reussi toast et prochaine activité
                        toast.setGravity(Gravity.BOTTOM, 0, 50);
                        toast.show();

                        // Lancer l'activité AjouterMission et passer les données id, nom et prenom
                        Intent intent = new Intent(context, AjouterMission.class);
                        intent.putExtra("id", id);
                        intent.putExtra("nom", nom);
                        intent.putExtra("prenom", prenom);
                        context.startActivity(intent); // creation des intent pr les recuperer ensuite
                    } else {
                        afficherAlerte(context, message);
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Erreur lors de l'extraction du message du JSON", e);
                }
            } else {
                Log.e(TAG, "Le résultat de l'authentification est nul");
            }
        } else {
            Log.e(TAG, "Contexte nul lors de l'affichage de l'alerte");
        }
    }

    private void afficherAlerte(Context context, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }



}


