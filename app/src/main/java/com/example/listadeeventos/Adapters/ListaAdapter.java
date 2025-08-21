package com.example.listadeeventos.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.listadeeventos.Models.Evento;
import com.example.listadeeventos.R;


import java.util.List;

public class ListaAdapter extends RecyclerView.Adapter<ListaAdapter.ViewHolder> {
    private List<Evento> listaEventos;
    private OnItemActionListener listener;

    public interface OnItemActionListener {
        void onItemClick(Evento evento );
        void onDeleteClick(Evento evento, int position);
        void onEditClick(Evento evento, int position);
    }

    public ListaAdapter(List<Evento> listaEventos, OnItemActionListener listener) {
        this.listaEventos = listaEventos;
        this.listener = listener;
    }

    public void filter(List<Evento> filteredList) {
        this.listaEventos.clear();
        this.listaEventos.addAll(filteredList);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtFecha, txtHora;
        ImageButton btnDelete, btnEdit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtFecha = itemView.findViewById(R.id.txtFecha);
            txtHora = itemView.findViewById(R.id.txtHora);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }

    @NonNull
    @Override
    public ListaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_evento, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListaAdapter.ViewHolder holder, int position) {
        Evento evento = listaEventos.get(position);
        holder.txtNombre.setText(evento.getTitulo());
        holder.txtFecha.setText(evento.getFecha());
        holder.txtHora.setText(evento.getHora());

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(evento, holder.getAdapterPosition());
            }
        });

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(evento, holder.getAdapterPosition());
            }
        });

        holder.itemView.setOnClickListener(v -> listener.onItemClick(evento));
    }

    @Override
    public int getItemCount() {
        return listaEventos.size();
    }

    public void removeItem(int position) {
        if (position >= 0 && position < listaEventos.size()) {
            listaEventos.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void actualizarLista(List<Evento> nuevosEventos) {
        this.listaEventos = nuevosEventos;
        notifyDataSetChanged();
    }
}
