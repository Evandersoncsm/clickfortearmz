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
            summary = "Exibe a configuracao da integracao Omie",
            description = "Requer sessao autenticada. Nunca devolve o App Secret salvo; informa apenas se ele existe.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pagina HTML de configuracao"),
            @ApiResponse(responseCode = "302", description = "Sem sessao; redireciona para /login")
    })
    public String form(@Parameter(hidden = true) Model model) {
        model.addAttribute("appKey", servico.appKey());
        model.addAttribute("temAppSecret", servico.temAppSecret());
        return "integracoes";
    }

    @PostMapping("/integracoes")
    @Operation(
            summary = "Salva as credenciais da integracao Omie",
            description = "Requer sessao e token CSRF. O App Secret e cifrado antes de ser persistido; "
                    + "quando enviado em branco, o segredo atual e preservado.")
    @ApiResponses({
            @ApiResponse(responseCode = "302", description = "Configuracao salva; redireciona para /integracoes"),
            @ApiResponse(responseCode = "403", description = "Sessao ou token CSRF invalido")
    })
    public String salvar(
            @Parameter(description = "App Key fornecida pela Omie")
            @RequestParam(value = "appKey", required = false) String appKey,
            @Parameter(description = "App Secret; em branco mantem o valor salvo")
            @RequestParam(value = "appSecret", required = false) String appSecret,
            @Parameter(hidden = true) RedirectAttributes redirect) {
        servico.salvar(appKey, appSecret);
        redirect.addFlashAttribute("sucesso", "Integração salva com segurança.");
        return "redirect:/integracoes";
    }
}
