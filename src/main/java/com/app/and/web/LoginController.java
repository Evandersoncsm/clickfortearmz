package com.app.and.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Tag(name = "Autenticacao", description = "Login por formulario e sessao HTTP")
public class LoginController {

    @GetMapping("/login")
    @Operation(
            summary = "Exibe a pagina de login",
            description = "O POST de autenticacao em /login e processado pelo Spring Security, "
                    + "que cria o cookie de sessao JSESSIONID.")
    @ApiResponse(responseCode = "200", description = "Pagina HTML de login")
    public String login() {
        return "login";
    }
}
