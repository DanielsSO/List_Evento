package com.example.listadeeventos.network;

import com.example.listadeeventos.Models.Evento;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Body;
import retrofit2.http.Path;

public interface EventoApi {
        @GET("evento")
        Call<List<Evento>> getEventos();

        @DELETE("evento/{id}")
        Call<Void> borrarEvento(@Path("id") String id);
}
