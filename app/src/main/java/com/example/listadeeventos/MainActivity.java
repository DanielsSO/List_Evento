package com.example.listadeeventos;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.listadeeventos.Adapters.ListaAdapter;
import com.example.listadeeventos.Adapters.PersonaAdapter;
import com.example.listadeeventos.Models.Evento;

import com.example.listadeeventos.Models.Persona;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ListaAdapter.OnItemActionListener {


    private RecyclerView recyclerEvento;
    private ListaAdapter ListaAdapter;
    private List<Evento> listaEventos;
    private MaterialButton btnAgregar;
    private MaterialToolbar toolbar;

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


        addEventoLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                (ActivityResult result) -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            String nombre = data.getStringExtra("nombre");
                            String fecha = data.getStringExtra("fecha");
                            String hora = data.getStringExtra("hora");
                            int editPosition = data.getIntExtra("position", -1);

                            if (editPosition != -1) {
                                // edit
                                Evento editada = new Evento(nombre, fecha, hora);
                                listaEventos.set(editPosition, editada);
                                ListaAdapter.notifyItemChanged(editPosition);
                                Toast.makeText(this, "Evento editado", Toast.LENGTH_SHORT).show();
                            } else {
                                // Crear
                                Evento nuevoEvento = new Evento(nombre, fecha, hora);
                                listaEventos.add(nuevoEvento);
                                ListaAdapter.notifyItemInserted(listaEventos.size() - 1);
                               recyclerEvento.scrollToPosition(listaEventos.size() - 1);
                                Toast.makeText(this, "Evento " + nombre + " agregado.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
        );

        recyclerEvento = findViewById(R.id.recyclerEvento);
        recyclerEvento.setLayoutManager(new LinearLayoutManager(this));

        listaEventos = new ArrayList<>();
        listaEventos.add(new Evento("lll", "17/02/2025", "30"));
        listaEventos.add(new Evento("lll", "17/02/2025", "30"));

        ListaAdapter = new ListaAdapter(listaEventos, this);
        recyclerEvento.setAdapter(ListaAdapter);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnAgregar = findViewById(R.id.btnAgregar);
        btnAgregar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, com.example.listadeeventos.Formulario_evento.class);
            addEventoLauncher.launch(intent);
        });
    }

    @Override
    public void onDeleteClick(Evento evento, int position) {
        ListaAdapter.removeItem(position);
        Toast.makeText(this, "Elemento eliminado: " + evento.getNombre(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEditClick(Evento evento, int position) {
        Intent intent = new Intent(this, com.example.listadeeventos.editEvento_Activity.class);
        intent.putExtra("nombre", evento.getNombre());
        intent.putExtra("fecha", evento.getFecha());
        intent.putExtra("hora", evento.getHora());
        intent.putExtra("position", position);
        addEventoLauncher.launch(intent);
    }
}