package com.example.listadeeventos;

import static java.util.Locale.filter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

import com.example.listadeeventos.Adapters.PersonaAdapter;
import com.example.listadeeventos.Models.Evento;
import com.example.listadeeventos.Models.Persona;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Lista_Personas extends AppCompatActivity implements PersonaAdapter.OnItemActionListener {

    private RecyclerView recyclerPersonas;
    private PersonaAdapter personaAdapter;
    private List<Persona> listaPersonas;
    private MaterialButton btnAgregar;
    private MaterialToolbar toolbar;
    private Evento evento;
    private static final String API_KEY = "MI_TOKEN";
    private EditText editTextSearch;

    private ActivityResultLauncher<Intent> addPersonaLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        evento = (Evento) getIntent().getSerializableExtra("evento");
        if(evento == null) {
            Toast.makeText(this, "Evento no recibido", Toast.LENGTH_SHORT).show();
            finish();
        }

        TextView txtEventoNombre = findViewById(R.id.txtListaEventos);
        txtEventoNombre.setText(evento.getTitulo());

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        recyclerPersonas = findViewById(R.id.recyclerPersonas);
        recyclerPersonas.setLayoutManager(new LinearLayoutManager(this));

        listaPersonas = new ArrayList<>();
        listaPersonas.add(new Persona("Maria", "Gonzalez", "30"));
        listaPersonas.add(new Persona("Pedro", "Lopez", "28"));
        personaAdapter = new PersonaAdapter(listaPersonas, this);
        recyclerPersonas.setAdapter(personaAdapter);

        btnAgregar = findViewById(R.id.btnAgregar);
        btnAgregar.setOnClickListener(v -> {
            Intent intent = new Intent(Lista_Personas.this, com.example.listadeeventos.MainActivity2.class);
            addPersonaLauncher.launch(intent);
        });

        editTextSearch = findViewById(R.id.editTextSearch);
        editTextSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {

                String text = s.toString();
                if (text.isEmpty()) {
                    personaAdapter.resetFilter();
                } else {
                    List<Persona> filteredList = new ArrayList<>();
                    for (Persona persona : listaPersonas) {
                        if (persona.getNombre().toLowerCase().contains(text.toLowerCase()) ||
                                persona.getAPellido().toLowerCase().contains(text.toLowerCase())) {
                            filteredList.add(persona);
                        }
                    }
                    personaAdapter.filter(filteredList);
                }
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });

        addPersonaLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                (ActivityResult result) -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        String nombre = data.getStringExtra("nombre");
                        String apellido = data.getStringExtra("apellido");
                        String edad = data.getStringExtra("edad");
                        int editPosition = data.getIntExtra("position", -1);

                        if (editPosition != -1) {
                            Persona editada = new Persona(nombre, apellido, edad);
                            listaPersonas.set(editPosition, editada);
                            personaAdapter.notifyItemChanged(editPosition);
                            Toast.makeText(this, "Persona editada", Toast.LENGTH_SHORT).show();
                        } else {
                            Persona nuevaPersona = new Persona(nombre, apellido, edad);
                            listaPersonas.add(nuevaPersona);
                            personaAdapter.notifyItemInserted(listaPersonas.size() - 1);
                            recyclerPersonas.scrollToPosition(listaPersonas.size() - 1);
                            Toast.makeText(this, "Persona " + nombre + " agregada.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        fetchAttendees(evento.getId());
    }

    private void fetchAttendees(String eventId) {
        OkHttpClient client = new OkHttpClient();
        Gson gson = new Gson();

        Request request = new Request.Builder()
                .url("https://www.googleapis.com/calendar/v3/calendars/primary/events/" + eventId + "/attendees?key=")
                .addHeader("Authorization", "Bearer " + API_KEY)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(Lista_Personas.this, "Error al obtener personas", Toast.LENGTH_SHORT).show());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String data = response.body().string();
                    Log.d("DEBUG_PERSONAS", data);

                    JsonObject jsonObject = gson.fromJson(data, JsonObject.class);
                    JsonArray attendees = jsonObject.getAsJsonArray("items");

                    for (int i = 0; i < attendees.size(); i++) {
                        JsonObject personaObject = attendees.get(i).getAsJsonObject();
                        // Cambia los campos según tu JSON real
                        String nombre = personaObject.getAsJsonObject("profile").get("first_name").getAsString();
                        String apellido = personaObject.getAsJsonObject("profile").get("last_name").getAsString();
                        String edad = "N/A"; // si no viene edad

                        listaPersonas.add(new Persona(nombre, apellido, edad));
                    }

                    runOnUiThread(() -> personaAdapter.notifyDataSetChanged());
                }
            }
        });
    }

    private void filter(String text) {
        List<Persona> filteredList = new ArrayList<>();
        for (Persona persona : listaPersonas) {
            if (persona.getNombre().toLowerCase().contains(text.toLowerCase()) ||
                    persona.getAPellido().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(persona);
            }
        }
        personaAdapter.filter(filteredList);
    }

    @Override
    public void onDeleteClick(Persona persona, int position) {
        personaAdapter.removeItem(position);
        Toast.makeText(this, "Elemento eliminado: " + persona.getNombre(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEditClick(Persona persona, int position) {
        Intent intent = new Intent(this, editActivity.class);
        intent.putExtra("nombre", persona.getNombre());
        intent.putExtra("apellido", persona.getAPellido());
        intent.putExtra("edad", persona.getEdad());
        intent.putExtra("position", position);
        addPersonaLauncher.launch(intent);
    }

//    //crashlytics
//
//    FirebaeCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
//    crashlytics.setUserId("user 12345");
//    crashlytics.setCustomKey("screen", "Lista_Personas");
//
//    Button btnCrash = findViewById(R.id.btnCrash);
//    btnCrash.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            throw new RuntimeException("Forzado de Crash");
//        }
//    });
//
//    Button btnCapturar = findViewById(R.id.btnCapturar);
//    btnCapturar.setOnClickListener(v -> {
//        try {
//            int resultado = 10 / 0;
//        } catch (Exception e) {
//            //registramos el error sin crashear la app
//            FirebaseCrashlytics.getInstance().recordException(e);
//        }
//    }
}

