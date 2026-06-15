package com.app.and.web;

import com.app.and.application.port.in.ConsultarProducaoUseCase;
import com.app.and.application.port.in.ImportarProducaoUseCase;
import com.app.and.domain.model.ProgramacaoProducao;
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
@Tag(name = "Producao", description = "Importacao e consulta da programacao de producao")
public class ProducaoController {

    private final ImportarProducaoUseCase importarProducao;
    private final ConsultarProducaoUseCase consultarProducao;

    public ProducaoController(ImportarProducaoUseCase importarProducao, ConsultarProducaoUseCase consultarProducao) {
        this.importarProducao = importarProducao;
        this.consultarProducao = consultarProducao;
    }

    @PostMapping("/producao/importar")
    @Operation(
            summary = "Importa a planilha de producao",
            description = "Recebe um arquivo .xlsx, le a primeira aba e guarda a ultima programacao em memoria. "
                    + "O arquivo deve seguir o layout descrito em docs/ARCHITECTURE.md.")
    @ApiResponses({
            @ApiResponse(responseCode = "302", description = "Importacao processada; redireciona para /producao"),
            @ApiResponse(responseCode = "413", description = "Arquivo acima do limite configurado")
    })
    public String importar(
            @Parameter(
                    description = "Planilha Excel de programacao de producao (.xlsx)",
                    required = true,
                    content = @Content(schema = @Schema(type = "string", format = "binary")))
            @RequestParam("planilha") MultipartFile planilha,
            @Parameter(hidden = true) RedirectAttributes redirect) {
        if (planilha.isEmpty()) {
            redirect.addFlashAttribute("erro", "Selecione a planilha de produção (.xlsx) antes de importar.");
            return "redirect:/";
        }
        try {
            ProgramacaoProducao prog = importarProducao.importar(planilha.getInputStream());
            redirect.addFlashAttribute("sucesso",
                    "Produção importada: %d itens.".formatted(prog.getTotalItens()));
        } catch (PlanilhaInvalidaException | IllegalArgumentException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        } catch (IOException e) {
            redirect.addFlashAttribute("erro", "Não foi possível ler o arquivo de produção enviado.");
        }
        return "redirect:/producao";
    }

    @GetMapping("/producao")
    @Operation(summary = "Exibe a ultima programacao de producao importada")
    @ApiResponse(responseCode = "200", description = "Pagina HTML com totais e itens da producao")
    public String producao(@Parameter(hidden = true) Model model) {
        model.addAttribute("programacao", consultarProducao.ultima());
        return "producao";
    }
}
