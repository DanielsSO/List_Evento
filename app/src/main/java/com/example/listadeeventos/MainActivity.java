package com.example.listadeeventos;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.listadeeventos.Adapters.ListaAdapter;
import com.example.listadeeventos.Models.Evento;
import com.example.listadeeventos.network.EventoApi;
import com.example.listadeeventos.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements ListaAdapter.OnItemActionListener {

    private RecyclerView recyclerView;
    private ListaAdapter adapter;
    private List<Evento> listaEventos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_eventos);

        recyclerView = findViewById(R.id.recyclerEvento);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ListaAdapter(listaEventos, this);
        recyclerView.setAdapter(adapter);

        // Traer eventos desde la API
        cargarEventos();
    }

    private void cargarEventos() {
        EventoApi api = RetrofitClient.getClient().create(EventoApi.class);

        api.getEventos().enqueue(new Callback<List<Evento>>() {
            @Override
            public void onResponse(Call<List<Evento>> call, Response<List<Evento>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaEventos.clear();
                    listaEventos.addAll(response.body());
                    adapter.actualizarLista(listaEventos);
                } else {
                    Toast.makeText(MainActivity.this, "Error al cargar eventos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Evento>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(Evento evento) {
        Toast.makeText(this, "Evento: " + evento.getTitulo(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteClick(Evento evento, int position) {
        EventoApi api = RetrofitClient.getClient().create(EventoApi.class);

        api.borrarEvento(evento.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    adapter.removeItem(position);
                    Toast.makeText(MainActivity.this, "Evento eliminado", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Error al eliminar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onEditClick(Evento evento, int position) {
        Toast.makeText(this, "Editar: " + evento.getTitulo(), Toast.LENGTH_SHORT).show();
    }
}
