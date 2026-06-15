package com.app.and.integracao;

import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OmieTelaCatalogoTest {

    private final OmieTelaCatalogo catalogo = new OmieTelaCatalogo();

    @Test
    void mantemSlugsUnicosEContratosPostCompletos() {
        var telas = catalogo.listar();
        var slugs = new HashSet<String>();

        assertFalse(telas.isEmpty());
        telas.forEach(tela -> {
            assertTrue(slugs.add(tela.slug()), "Slug repetido: " + tela.slug());
            assertTrue(tela.endpoint().startsWith("https://app.omie.com.br/api/v1/"));
            assertFalse(tela.chamadaPrincipal().isBlank());
            assertTrue(tela.exemploPost().startsWith("POST "));
        });
        assertEquals(telas.size(), slugs.size());
    }
}
