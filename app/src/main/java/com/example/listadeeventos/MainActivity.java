package com.example.listadeeventos;

import static java.util.Locale.filter;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
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

import com.example.listadeeventos.network.ApiEventbrite;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements ListaAdapter.OnItemActionListener {

    private RecyclerView recyclerEvento;
    private ListaAdapter listaAdapter;
    private List<Evento> listaEventos;
    private MaterialButton btnAgregar;
    private MaterialToolbar toolbar;
    private EditText editTextSearch;
    private ActivityResultLauncher<Intent> addEventoLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.lista_eventos);
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
            Intent intent = new Intent(MainActivity.this, Formulario_evento.class);
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
        listaAdapter.removeItem(position);
        Toast.makeText(this, "Elemento eliminado: " + evento.getTitulo(), Toast.LENGTH_SHORT).show();
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

    private void fetchEvents() {
        OkHttpClient client = new OkHttpClient();
        Gson gson = new Gson();
        String url = "http://10.0.2.2:5000/eventos";

        Request request = new Request.Builder().url(url).build();
        Log.d("DEBUG_URL", "Conectando a: " + url);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error al conectar el servidor", Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String data = response.body().string();
                    Log.d("DEBUG_JSON", data);

                    Type listType = new TypeToken<List<Evento>>() {}.getType();
                    List<Evento> events = gson.fromJson(data, listType);

                    runOnUiThread(() -> {
                        listaEventos.addAll(events);
                        listaAdapter.notifyDataSetChanged();
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error al recibir datos del servidor", Toast.LENGTH_LONG).show());
                }
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
