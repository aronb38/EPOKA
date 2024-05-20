package com.example.epoka;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AjouterMission extends AppCompatActivity {

    private TextView tvNomPrenom;
    private Button btnAjouterMission;
    private Button btnQuitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ajouter_mission);

        tvNomPrenom = findViewById(R.id.tv_nom_prenom);
        btnAjouterMission = findViewById(R.id.btn_ajouter_mission);
        btnQuitter = findViewById(R.id.btn_quitter);

        String id = getIntent().getStringExtra("id");
        Log.d(TAG, "ID récupéré dans AjouterMission : " + id); // Ajouter ce log pour vérifier si l'ID est correctement récupéré dans AjouterMission
        String nom = getIntent().getStringExtra("nom");
        String prenom = getIntent().getStringExtra("prenom");
        tvNomPrenom.setText("Bienvenue " + nom + " " + prenom);

        btnQuitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
            }
        });


                    btnAjouterMission.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Récupérer l'id, nom et prénom depuis l'intent courant
                    String id = getIntent().getStringExtra("id");
                    String nom = getIntent().getStringExtra("nom");
                    String prenom = getIntent().getStringExtra("prenom");

                    // Assurez-vous que idSalarie est un int
                    int idSalarie = Integer.parseInt(id);

                    // Créer un nouvel Intent pour l'activité NouvelleMission
                    Intent intent = new Intent(AjouterMission.this, NouvelleMission.class);

                    // Ajouter l'id, nom et prénom à l'Intent
                    intent.putExtra("id", idSalarie);
                    intent.putExtra("nom", nom);
                    intent.putExtra("prenom", prenom);

                    // Démarrer l'activité NouvelleMission
                    startActivity(intent);
                }
            });





    }
}
