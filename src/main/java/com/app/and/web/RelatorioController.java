package com.app.and.web;

import com.app.and.application.port.in.GerarRelatorioUseCase;
import com.app.and.application.port.in.ImportarContagemUseCase;
import com.app.and.application.port.in.ListarDiasContagemUseCase;
import com.app.and.domain.model.ContagemDiaria;
import com.app.and.infrastructure.spreadsheet.PlanilhaInvalidaException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@Tag(name = "Estoque e relatorios", description = "Importacao da contagem e relatorios de estoque")
public class RelatorioController {

    private final ImportarContagemUseCase importarContagem;
    private final GerarRelatorioUseCase gerarRelatorio;
    private final ListarDiasContagemUseCase listarDias;

    public RelatorioController(ImportarContagemUseCase importarContagem, GerarRelatorioUseCase gerarRelatorio,
                               ListarDiasContagemUseCase listarDias) {
        this.importarContagem = importarContagem;
        this.gerarRelatorio = gerarRelatorio;
        this.listarDias = listarDias;
    }

    @GetMapping("/")
    @Operation(summary = "Exibe a pagina inicial de importacao")
    @ApiResponse(responseCode = "200", description = "Pagina HTML inicial")
    public String index() {
        return "index";
    }

    @PostMapping("/importar")
    @Operation(
            summary = "Importa a planilha de contagem de estoque",
            description = "Recebe um arquivo .xlsx, usa a ultima aba como o dia mais recente e faz upsert "
                    + "da contagem no banco pela data ou pelo nome da aba.")
    @ApiResponses({
            @ApiResponse(responseCode = "302", description = "Importacao processada; redireciona para /relatorio"),
            @ApiResponse(responseCode = "413", description = "Arquivo acima do limite configurado")
    })
    public String importar(
            @Parameter(
                    description = "Planilha Excel de contagem de estoque (.xlsx)",
                    required = true,
                    content = @Content(schema = @Schema(type = "string", format = "binary")))
            @RequestParam("planilha") MultipartFile planilha,
            @Parameter(hidden = true) RedirectAttributes redirect) {
        if (planilha.isEmpty()) {
            redirect.addFlashAttribute("erro", "Selecione a planilha (.xlsx) antes de importar.");
            return "redirect:/";
        }
        try {
            ContagemDiaria contagem = importarContagem.importar(planilha.getInputStream());
            redirect.addFlashAttribute("sucesso",
                    "Importação concluída: aba '%s' (%d itens) usada como dia mais recente."
                            .formatted(contagem.getAba(), contagem.getItens().size()));
        } catch (PlanilhaInvalidaException | IllegalArgumentException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        } catch (IOException e) {
            redirect.addFlashAttribute("erro", "Não foi possível ler o arquivo enviado.");
        }
        return "redirect:/relatorio";
    }

    @GetMapping("/relatorio")
    @Operation(
            summary = "Exibe o relatorio de estoque",
            description = "Sem o parametro dia, usa a contagem mais recente. Com dia, busca a chave persistida "
                    + "no formato ISO yyyy-MM-dd ou o nome da aba quando a planilha nao possui data.")
    @ApiResponse(responseCode = "200", description = "Pagina HTML do relatorio")
    public String relatorio(
            @Parameter(description = "Chave do dia persistido", example = "2026-06-15")
            @RequestParam(value = "dia", required = false) String dia,
            @Parameter(hidden = true) Model model) {
        model.addAttribute("relatorio", gerarRelatorio.gerar(dia));
        model.addAttribute("dias", listarDias.listar());
        model.addAttribute("diaSelecionado", dia);
        return "relatorio";
    }
}
