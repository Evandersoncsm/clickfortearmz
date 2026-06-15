package com.app.and.web;

import com.app.and.integracao.IntegracaoService;
import com.app.and.integracao.OmieTela;
import com.app.and.integracao.OmieTelaCatalogo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

@Controller
@Tag(name = "Prototipos Omie", description = "Catalogo visual para evolucao da integracao com a API Omie")
public class OmieTelasController {

    private final OmieTelaCatalogo catalogo;
    private final IntegracaoService integracoes;

    public OmieTelasController(OmieTelaCatalogo catalogo, IntegracaoService integracoes) {
        this.catalogo = catalogo;
        this.integracoes = integracoes;
    }

    @GetMapping("/omie")
    @Operation(summary = "Exibe o catalogo de telas sugeridas para a integracao Omie")
    @ApiResponse(responseCode = "200", description = "Pagina HTML com os prototipos disponíveis")
    public String catalogo(Model model) {
        preencherModelo(model, null);
        return "omie-telas";
    }

    @GetMapping("/omie/{slug}")
    @Operation(summary = "Exibe a especificacao visual de uma tela Omie")
    @ApiResponse(responseCode = "200", description = "Pagina HTML do prototipo selecionado")
    public String tela(@PathVariable String slug, Model model) {
        OmieTela selecionada = catalogo.buscar(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tela Omie não encontrada."));
        preencherModelo(model, selecionada);
        return "omie-telas";
    }

    private void preencherModelo(Model model, OmieTela selecionada) {
        model.addAttribute("telas", catalogo.listar());
        model.addAttribute("telaSelecionada", selecionada);
        model.addAttribute("contas", integracoes.listar());
    }
}
