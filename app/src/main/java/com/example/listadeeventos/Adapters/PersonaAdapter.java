package com.example.listadeeventos.Adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.listadeeventos.Lista_Personas;
import com.example.listadeeventos.Models.Persona;
import com.example.listadeeventos.R;
import com.example.listadeeventos.editActivity;

import java.util.List;

public class PersonaAdapter extends RecyclerView.Adapter<PersonaAdapter.ViewHolder> {
    private List<Persona> listaPersonas;
    private OnItemActionListener listener;

    public interface OnItemActionListener  {
        void onDeleteClick(Persona persona, int position);
        void onEditClick(Persona persona, int position);
    }

    public PersonaAdapter(List<Persona> listaPersonas, OnItemActionListener listener) {
        this.listaPersonas = listaPersonas;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtApellido, txtEdad;
        ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtApellido = itemView.findViewById(R.id.txtApellido);
            txtEdad = itemView.findViewById(R.id.txtEdad);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    @NonNull
    @Override
    public PersonaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itempersona, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonaAdapter.ViewHolder holder, int position) {
        Persona persona = listaPersonas.get(position);
        holder.txtNombre.setText(persona.getNombre());
        holder.txtApellido.setText(persona.getAPellido());
        holder.txtEdad.setText(String.valueOf(persona.getEdad()));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(persona, holder.getAdapterPosition());
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(persona, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaPersonas.size();
    }

    public void removeItem(int position) {
        if (position >= 0 && position < listaPersonas.size()) {
            listaPersonas.remove(position);
            notifyItemRemoved(position);
        }
    }
}