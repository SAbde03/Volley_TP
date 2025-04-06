package com.example.projectws;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import beans.Etudiant;

public class EtudiantAdapter extends RecyclerView.Adapter<EtudiantAdapter.EtudiantViewHolder> {
    private Context context;
    private List<Etudiant> etudiantList;
    private OnEtudiantListener onEtudiantListener;

    public EtudiantAdapter(Context context, List<Etudiant> etudiantList, OnEtudiantListener onEtudiantListener) {
        this.context = context;
        this.etudiantList = etudiantList;
        this.onEtudiantListener = onEtudiantListener;
    }

    @NonNull
    @Override
    public EtudiantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_etudiant, parent, false);
        return new EtudiantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EtudiantViewHolder holder, int position) {
        Etudiant etudiant = etudiantList.get(position);
        holder.nomPrenom.setText(etudiant.getNom() + " " + etudiant.getPrenom());
        holder.ville.setText(etudiant.getVille());
        holder.sexe.setText(etudiant.getSexe());

        holder.itemView.setOnClickListener(v -> onEtudiantListener.onEtudiantClick(position));
    }

    @Override
    public int getItemCount() {
        return etudiantList.size();
    }

    static class EtudiantViewHolder extends RecyclerView.ViewHolder {
        TextView nomPrenom;
        TextView ville;
        TextView sexe;

        public EtudiantViewHolder(@NonNull View itemView) {
            super(itemView);
            nomPrenom = itemView.findViewById(R.id.text_nom_prenom);
            ville = itemView.findViewById(R.id.text_ville);
            sexe = itemView.findViewById(R.id.text_sexe);
        }
    }

    public interface OnEtudiantListener {
        void onEtudiantClick(int position);

    }
}
