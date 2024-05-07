package com.example.epoka;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String URL_AUTHENTIFICATION = "http://172.16.47.1/epoka/authentification.php"; // Remplacez l'URL par celle de votre serveur
    private EditText etNom, etMotDePasse;
    private Button btnConnexion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etNom = findViewById(R.id.etIdentifiant);
        etMotDePasse = findViewById(R.id.etPassword);
        btnConnexion = findViewById(R.id.btnConnexion);

        btnConnexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nom = etNom.getText().toString();
                String motDePasse = etMotDePasse.getText().toString();
                new AuthentificationTask(MainActivity.this).execute(URL_AUTHENTIFICATION, nom, motDePasse);
            }
        });
    }


}
