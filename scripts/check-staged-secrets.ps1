$ErrorActionPreference = "Stop"

$files = @(git diff --cached --name-only --diff-filter=ACMR)
if ($LASTEXITCODE -ne 0) {
    throw "Nao foi possivel listar os arquivos preparados para commit."
}

$forbiddenPaths = @(
    '(?i)(^|/)(\.env($|\.)|data/|secrets?/)',
    '(?i)\.(mv\.db|trace\.db|pem|key|p12|jks)$'
)

$secretPatterns = @(
    '(?i)-----BEGIN [A-Z ]*PRIVATE KEY-----',
    '(?i)\b(ghp_|github_pat_|glpat-|xox[baprs]-)[A-Za-z0-9_-]{12,}',
    '(?i)\bAKIA[0-9A-Z]{16}\b',
    '(?im)^\s*(password|passwd|senha|secret|app_secret|api_key|apikey|token)\s*[:=]\s*["'']?(?!\$\{|troque|change|dev-|example|exemplo|dummy|test-|<)[^\s#"'']{8,}'
)

$problems = [System.Collections.Generic.List[string]]::new()

foreach ($file in $files) {
    $normalized = $file -replace '\\', '/'

    if ($normalized -eq '.env.example') {
        continue
    }

    foreach ($pattern in $forbiddenPaths) {
        if ($normalized -match $pattern) {
            $problems.Add("arquivo proibido: $file")
            break
        }
    }

    $content = git show ":$file" 2>$null | Out-String
    if ($LASTEXITCODE -ne 0) {
        continue
    }

    foreach ($pattern in $secretPatterns) {
        if ($content -match $pattern) {
            $problems.Add("possivel segredo em: $file")
            break
        }
    }
}

if ($problems.Count -gt 0) {
    Write-Error ("Commit bloqueado pela verificacao de seguranca:`n- " + ($problems -join "`n- "))
    exit 1
}

Write-Host "Verificacao de segredos concluida."
