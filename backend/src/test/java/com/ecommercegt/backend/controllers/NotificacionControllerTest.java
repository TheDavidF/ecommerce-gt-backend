package com.ecommercegt.backend.controllers;

import com.ecommercegt.backend.dto.request.NotificacionRequest;
import com.ecommercegt.backend.dto.response.NotificacionResponse;
import com.ecommercegt.backend.models.entidades.Notificacion;
import com.ecommercegt.backend.service.NotificacionService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(NotificacionController.class)
public class NotificacionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificacionService notificacionService;

    @Test
    void crearNotificacion_debeRetornar200YNotificacion() throws Exception {
        UUID usuarioId = UUID.randomUUID();
        NotificacionRequest request = new NotificacionRequest();
        request.setUsuarioId(usuarioId);
        request.setTipo(com.ecommercegt.backend.models.enums.TipoNotificacion.PEDIDO_ESTADO_CAMBIADO);
        request.setTitulo("Tu pedido ha cambiado de estado");
        request.setMensaje("El pedido #123 ahora está en camino.");
        request.setUrl("/pedidos/123");
        request.setDatos("{\"pedidoId\": \"123\", \"estadoAnterior\": \"Procesando\", \"estadoNuevo\": \"En camino\", \"total\": 5000}");

        Notificacion notificacion = new Notificacion();
        notificacion.setId(1L);
        notificacion.setUsuario(null); // Puedes mockear el usuario si lo necesitas
        notificacion.setTipo(request.getTipo());
        notificacion.setTitulo(request.getTitulo());
        notificacion.setMensaje(request.getMensaje());
        notificacion.setUrl(request.getUrl());
        notificacion.setDatos(request.getDatos());
        notificacion.setLeida(false);

        Mockito.when(notificacionService.crearNotificacionConDatos(
                Mockito.eq(usuarioId),
                Mockito.eq(request.getTipo()),
                Mockito.eq(request.getTitulo()),
                Mockito.eq(request.getMensaje()),
                Mockito.eq(request.getUrl()),
                Mockito.eq(request.getDatos())
        )).thenReturn(notificacion);

        String json = "{" +
                "\"usuarioId\": \"" + usuarioId + "\"," +
                "\"tipo\": \"PEDIDO_ESTADO_CAMBIADO\"," +
                "\"titulo\": \"Tu pedido ha cambiado de estado\"," +
                "\"mensaje\": \"El pedido #123 ahora está en camino.\"," +
                "\"url\": \"/pedidos/123\"," +
                "\"datos\": \"{\\\"pedidoId\\\": \\\"123\\\", \\\"estadoAnterior\\\": \\\"Procesando\\\", \\\"estadoNuevo\\\": \\\"En camino\\\", \\\"total\\\": 5000}\"" +
                "}";

        mockMvc.perform(post("/api/notificaciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Tu pedido ha cambiado de estado"))
                .andExpect(jsonPath("$.datos").value(request.getDatos()));
    }
}
