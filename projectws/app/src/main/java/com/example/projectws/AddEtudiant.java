package com.example.projectws;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.projectws.R;

import java.util.HashMap;
import java.util.Map;

public class AddEtudiant extends AppCompatActivity implements View.OnClickListener {

    private EditText nom;
    private EditText prenom;
    private Spinner ville;
    private RadioButton m;
    private RadioButton f;
    private Button add;

    private RequestQueue requestQueue;
    private final String insertUrl = "http://10.0.2.2/projet/Source Files/ws/createEtudiant.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_etudiant);

        // Init views
        nom = findViewById(R.id.nom);
        prenom = findViewById(R.id.prenom);
        ville = findViewById(R.id.ville);
        add = findViewById(R.id.add);
        m = findViewById(R.id.m);
        f = findViewById(R.id.f);

        // Set listener
        add.setOnClickListener(this);

        Button showAll = findViewById(R.id.list);
        showAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddEtudiant.this,ListEtudiants.class));
            }
        });



    }

    @Override
    public void onClick(View v) {
        if (v == add) {
            Log.d("AddEtudiant", "Button clicked");
            requestQueue = Volley.newRequestQueue(getApplicationContext());

            StringRequest request = new StringRequest(Request.Method.POST, insertUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("AddEtudiant", "Response: " + response);
                            // You can add a Toast or redirect here
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("AddEtudiant", "Error: " + error.getMessage());
                            // Optionally show a Toast here
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    String sexe = m.isChecked() ? "homme" : "femme";

                    Map<String, String> params = new HashMap<>();
                    params.put("nom", nom.getText().toString());
                    params.put("prenom", prenom.getText().toString());
                    params.put("ville", ville.getSelectedItem().toString());
                    params.put("sexe", sexe);

                    return params;
                }
            };

            requestQueue.add(request);
        }
    }



}
