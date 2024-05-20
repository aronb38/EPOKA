package com.example.epoka;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String URL_AUTHENTIFICATION = "http://192.168.1.96/epoka/authentification.php"; //connexion au service web
    private EditText etNom, etMotDePasse;
    private Button btnConnexion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //association des variables au XML
        etNom = findViewById(R.id.etIdentifiant);
        etMotDePasse = findViewById(R.id.etPassword);
        btnConnexion = findViewById(R.id.btnConnexion);

        btnConnexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //quand bouton est cliquer
                String nom = etNom.getText().toString();
                String motDePasse = etMotDePasse.getText().toString(); //on recup le etNom donc l'id et le mdp et en dessous de maniere asynchrone on gere l'authent
                new AuthentificationTask(MainActivity.this).execute(URL_AUTHENTIFICATION, nom, motDePasse);
            }
        });
    }


}
