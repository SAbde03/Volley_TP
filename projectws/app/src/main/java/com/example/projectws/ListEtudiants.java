package com.example.projectws;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import beans.Etudiant;

// ... imports (inchangés)

public class ListEtudiants extends AppCompatActivity implements EtudiantAdapter.OnEtudiantListener{

    private static final String TAG = "ListEtudiants";
    private RecyclerView recyclerView;
    private EtudiantAdapter adapter;
    private List<Etudiant> etudiantList = new ArrayList<>();
    private final String fetchUrl = "http://10.0.2.2/projet/Source%20Files/ws/loadEtudiant.php";
    private final String deleteUrl = "http://10.0.2.2/projet/Source Files/controller/deleteEtudiant.php";
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_etudiants);

        Log.d(TAG, "onCreate: Initializing UI components");

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new EtudiantAdapter(ListEtudiants.this, etudiantList, this);
        recyclerView.setAdapter(adapter);

        fetchEtudiants();
    }

    private void fetchEtudiants() {
        RequestQueue queue = Volley.newRequestQueue(this);

        Log.d(TAG, "fetchEtudiants: Sending request to " + fetchUrl);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, fetchUrl, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            Log.d(TAG, "fetchEtudiants: Response received: " + response.toString());

                            etudiantList.clear();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject obj = response.getJSONObject(i);
                                Etudiant etudiant = new Etudiant(
                                        obj.getInt("id"),
                                        obj.getString("nom"),
                                        obj.getString("prenom"),
                                        obj.getString("ville"),
                                        obj.getString("sexe")
                                );
                                etudiantList.add(etudiant);
                            }

                            adapter.notifyDataSetChanged();
                            Log.d(TAG, "fetchEtudiants: Adapter updated with new data");

                        } catch (JSONException e) {
                            Log.e(TAG, "fetchEtudiants: JSON parsing error: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "fetchEtudiants: Volley error: " + error.toString());
                    }
                }
        );

        queue.add(request);
    }

    @Override
    public void onEtudiantClick(int position) {
        Log.d(TAG, "onEtudiantClick: Clicked position = " + position);
        Etudiant etudiant = etudiantList.get(position);
        showOptionsDialog(etudiant, position);
    }

    private void showOptionsDialog(final Etudiant etudiant, final int position) {
        Log.d(TAG, "showOptionsDialog: Showing options for " + etudiant.getNom());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Options pour " + etudiant.getNom() + " " + etudiant.getPrenom());

        String[] options = {"Modifier", "Supprimer"};

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "showOptionsDialog: Option selected = " + options[which]);
                switch (which) {
                    case 0:
                        showEditDialog(etudiant);
                        break;
                    case 1:
                        showDeleteConfirmation(etudiant);
                        break;
                }
            }
        });

        builder.setNegativeButton("Annuler", null);
        builder.create().show();
    }

    private void showEditDialog(final Etudiant etudiant) {
        Log.d(TAG, "showEditDialog: Editing " + etudiant.getNom());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Modifier Étudiant");

        View view = LayoutInflater.from(this).inflate(R.layout.etudiant_modal, null);
        final EditText editNom = view.findViewById(R.id.edit_nom);
        final EditText editPrenom = view.findViewById(R.id.edit_prenom);
        final Spinner ville=view.findViewById(R.id.ville);
        final RadioButton m=view.findViewById(R.id.m);
        final RadioButton f=view.findViewById(R.id.f);

        editNom.setText(etudiant.getNom());
        editPrenom.setText(etudiant.getPrenom());


        builder.setView(view);
        builder.setPositiveButton("Sauvegarder", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String updatedNom = editNom.getText().toString();
                String updatedPrenom = editPrenom.getText().toString();
                String updatedVille = ville.getSelectedItem().toString();
                String sexe = m.isChecked() ? "homme" : "femme";

                Log.d(TAG, "showEditDialog: New values - Nom: " + updatedNom + ", Prenom: " + updatedPrenom);
                updateEtudiantOnServer(etudiant, updatedNom, updatedPrenom, updatedVille, sexe);
            }
        });
        builder.setNegativeButton("Annuler", null);
        builder.create().show();
    }

    private void updateEtudiantOnServer(final Etudiant etudiant, final String updatedNom, final String updatedPrenom, final String updatedVille, final String sexe) {
        String updateUrl = "http://10.0.2.2/projet/Source%20Files/ws/updateEtudiant.php";
        Log.d(TAG, "updateEtudiantOnServer: Sending update to " + updateUrl);

        StringRequest request = new StringRequest(Request.Method.POST, updateUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "updateEtudiantOnServer: Update successful, response: " + response);
                        etudiant.setNom(updatedNom);
                        etudiant.setPrenom(updatedPrenom);
                        etudiant.setVille(updatedVille);
                        etudiant.setSexe(sexe);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(ListEtudiants.this, "Étudiant modifié avec succès", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "updateEtudiantOnServer: Error: " + error.toString());
                        Toast.makeText(ListEtudiants.this, "Erreur de mise à jour", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(etudiant.getId()));
                params.put("nom", updatedNom);
                params.put("prenom", updatedPrenom);
                params.put("ville", updatedVille);
                params.put("sexe",sexe);
                Log.d(TAG, "updateEtudiantOnServer: Params = " + params.toString());
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private void showDeleteConfirmation(final Etudiant etudiant) {
        Log.d(TAG, "showDeleteConfirmation: Deleting " + etudiant.getNom());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation")
                .setMessage("Êtes-vous sûr de vouloir supprimer " +
                        etudiant.getNom() + " " + etudiant.getPrenom() + " ?")
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteEtudiant(etudiant);
                    }
                })
                .setNegativeButton("Non", null)
                .show();
    }

    private void deleteEtudiant(final Etudiant etudiant) {
        Log.d(TAG, "deleteEtudiant: Sending delete request for ID: " + etudiant.getId());

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST, deleteUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "deleteEtudiant: Delete successful, response: " + response);
                        fetchEtudiants();
                        Toast.makeText(ListEtudiants.this, "Étudiant supprimé avec succès", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "deleteEtudiant: Error: " + error.toString());
                        Toast.makeText(ListEtudiants.this, "Erreur de connexion au serveur", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(etudiant.getId()));
                Log.d(TAG, "deleteEtudiant: Params = " + params.toString());
                return params;
            }
        };

        requestQueue.add(request);
    }

    private JSONObject createJsonBody(int id) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("id", id);
        } catch (JSONException e) {
            Log.e(TAG, "createJsonBody: JSON error: " + e.getMessage());
        }
        return jsonBody;
    }
}

