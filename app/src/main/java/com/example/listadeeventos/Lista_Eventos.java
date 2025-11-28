package com.example.listadeeventos;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.listadeeventos.Adapters.ListaAdapter;
import com.example.listadeeventos.Models.Evento;
import com.example.listadeeventos.network.Analytics;
import com.example.listadeeventos.network.EventoApi;
import com.example.listadeeventos.network.Performance_Monitoring;
import com.example.listadeeventos.network.RetrofitClient;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Lista_Eventos  extends AppCompatActivity implements ListaAdapter.OnItemActionListener {
    private RecyclerView recyclerEvento;
    private ListaAdapter listaAdapter;
    private List<Evento> listaEventos;
    private MaterialButton btnAgregar;
    private MaterialToolbar toolbar;
    private EditText editTextSearch;
    private ActivityResultLauncher<Intent> addEventoLauncher;
    private Long screenStartTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.lista_eventos);

        // Analytics: Screen View
        screenStartTime = System.nanoTime();
        Analytics.getInstance().trackScreenView("Lista_Eventos", "Login", 0);

        // Configurar user properties
        Analytics.getInstance().setApiEnv("production");
        Analytics.getInstance().setDeviceType("phone");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());

        recyclerEvento = findViewById(R.id.recyclerEvento);
        recyclerEvento.setLayoutManager(new LinearLayoutManager(this));

        listaEventos = new ArrayList<>();
        listaAdapter = new ListaAdapter(listaEventos, this);
        recyclerEvento.setAdapter(listaAdapter);

        // Eventos locales
        listaEventos.add(new Evento("1", "Concierto Local 1", "17/02/2025", "20:00"));
        listaEventos.add(new Evento("2", "Concierto Local 2", "18/02/2025", "19:30"));
        listaAdapter.notifyDataSetChanged();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnAgregar = findViewById(R.id.btnAgregar);
        editTextSearch = findViewById(R.id.editTextSearch);

        fetchEvents();

        btnAgregar.setOnClickListener(v -> {
            Intent intent = new Intent(Lista_Eventos.this, Formulario_evento.class);
            addEventoLauncher.launch(intent);
        });

        // Buscar en lista
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        addEventoLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        String id = data.getStringExtra("id");
                        String nombre = data.getStringExtra("nombre");
                        String fecha = data.getStringExtra("fecha");
                        String hora = data.getStringExtra("hora");
                        int editPosition = data.getIntExtra("position", -1);

                        if (editPosition != -1) {
                            // Editar
                            Evento editada = new Evento(id, nombre, fecha, hora);
                            listaEventos.set(editPosition, editada);
                            listaAdapter.notifyItemChanged(editPosition);
                            Toast.makeText(this, "Evento editado", Toast.LENGTH_SHORT).show();
                        } else {
                            // Crear
                            Evento nuevoEvento = new Evento(id, nombre, fecha, hora);
                            listaEventos.add(nuevoEvento);
                            listaAdapter.notifyItemInserted(listaEventos.size() - 1);
                            recyclerEvento.scrollToPosition(listaEventos.size() - 1);
                            Toast.makeText(this, "Evento " + nombre + " agregado.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    @Override
    public void onItemClick(Evento evento) {

        Intent intent = new Intent(this, Lista_Personas.class);
        intent.putExtra("evento", evento);
        intent.putExtra("id", evento.getId());
        intent.putExtra("titulo", evento.getTitulo());
        intent.putExtra("fecha", evento.getFecha());
        intent.putExtra("hora", evento.getHora());
        startActivity(intent);
        Toast.makeText(this, "Evento: " + evento.getTitulo(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteClick(Evento evento, int position) {
        EventoApi api = RetrofitClient.getClient().create(EventoApi.class);

        api.borrarEvento(evento.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    listaAdapter.removeItem(position);
                    Toast.makeText(Lista_Eventos.this, "Evento eliminado correctamente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Lista_Eventos.this, "Error al eliminar el evento: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(Lista_Eventos.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onEditClick(Evento evento, int position) {
        Intent intent = new Intent(this, com.example.listadeeventos.editEvento_Activity.class);
        intent.putExtra("nombre", evento.getTitulo());
        intent.putExtra("fecha", evento.getFecha());
        intent.putExtra("hora", evento.getHora());
        intent.putExtra("position", position);
        addEventoLauncher.launch(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        long viewDuration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - screenStartTime);
        Analytics.getInstance().trackScreenView("Lista_Eventos", "Login", viewDuration);
    }

    private void fetchEvents() {

        EventoApi api = RetrofitClient.getClient().create(EventoApi.class);

        api.getEventos().enqueue(new retrofit2.Callback<List<Evento>>() {
            @Override
            public void onResponse(retrofit2.Call<List<Evento>> call, retrofit2.Response<List<Evento>> response) {

                if (response.isSuccessful() && response.body() != null) {
                    listaEventos.clear();
                    listaEventos.addAll(response.body());
                    listaAdapter.notifyDataSetChanged();
                    Toast.makeText(Lista_Eventos.this, "Eventos cargados desde MockAPI", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Lista_Eventos.this, "Error en datos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<Evento>> call, Throwable t) {
                Toast.makeText(Lista_Eventos.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filter(String text) {
        List<Evento> filteredList = new ArrayList<>();
        for (Evento evento : listaEventos) {
            if (evento.getTitulo().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(evento);
            }
        }
        listaAdapter.filter(filteredList);
    }
}
