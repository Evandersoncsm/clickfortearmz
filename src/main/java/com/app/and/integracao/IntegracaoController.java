package com.app.and.integracao;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Tag(name = "Integracao Omie", description = "Configuracao protegida das credenciais da API Omie")
@SecurityRequirement(name = "sessionCookie")
public class IntegracaoController {

    private final IntegracaoService servico;

    public IntegracaoController(IntegracaoService servico) {
        this.servico = servico;
    }

    @GetMapping("/integracoes")
    @Operation(
            summary = "Lista e edita contas da integracao Omie",
            description = "Requer sessao autenticada. Suporta varias contas e nunca devolve os App Secrets salvos.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pagina HTML de configuracao"),
            @ApiResponse(responseCode = "302", description = "Sem sessao; redireciona para /login")
    })
    public String form(
            @Parameter(description = "Identificador da conta a editar")
            @RequestParam(value = "conta", required = false) Long contaId,
            @Parameter(hidden = true) Model model,
            @Parameter(hidden = true) RedirectAttributes redirect) {
        model.addAttribute("contas", servico.listar());
        try {
            model.addAttribute("conta", servico.buscar(contaId));
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
            return "redirect:/integracoes";
        }
        return "integracoes";
    }

    @PostMapping("/integracoes")
    @Operation(
            summary = "Cria ou atualiza uma conta Omie",
            description = "Requer sessao e token CSRF. Cada App Secret e cifrado separadamente; "
                    + "ao editar, um segredo em branco preserva o valor atual.")
    @ApiResponses({
            @ApiResponse(responseCode = "302", description = "Configuracao salva; redireciona para /integracoes"),
            @ApiResponse(responseCode = "403", description = "Sessao ou token CSRF invalido")
    })
    public String salvar(
            @Parameter(description = "Identificador da conta; vazio cria uma nova")
            @RequestParam(value = "id", required = false) Long id,
            @Parameter(description = "Nome interno para identificar a conta")
            @RequestParam("nome") String nome,
            @Parameter(description = "App Key fornecida pela Omie")
            @RequestParam("appKey") String appKey,
            @Parameter(description = "App Secret; em branco mantem o valor salvo")
            @RequestParam(value = "appSecret", required = false) String appSecret,
            @Parameter(hidden = true) RedirectAttributes redirect) {
        try {
            ContaOmie conta = servico.salvar(id, nome, appKey, appSecret);
            redirect.addFlashAttribute("sucesso", "Conta Omie salva com segurança.");
            return "redirect:/integracoes?conta=" + conta.id();
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
            return id == null ? "redirect:/integracoes" : "redirect:/integracoes?conta=" + id;
        }
    }

    @PostMapping("/integracoes/excluir")
    @Operation(summary = "Exclui uma conta Omie cadastrada")
    @ApiResponses({
            @ApiResponse(responseCode = "302", description = "Conta excluida; redireciona para /integracoes"),
            @ApiResponse(responseCode = "403", description = "Sessao ou token CSRF invalido")
    })
    public String excluir(
            @Parameter(description = "Identificador da conta") @RequestParam("id") Long id,
            @Parameter(hidden = true) RedirectAttributes redirect) {
        try {
            servico.excluir(id);
            redirect.addFlashAttribute("sucesso", "Conta Omie excluída.");
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/integracoes";
    }
}
