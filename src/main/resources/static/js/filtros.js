// Adiciona uma linha de inputs de filtro no cabeçalho de toda tabela
// marcada com a classe "tabela-filtravel" e filtra as linhas por coluna.
(function () {
    function ativarFiltros(tabela) {
        if (!tabela.tHead || tabela.tHead.rows.length === 0) {
            return;
        }
        const headRow = tabela.tHead.rows[0];
        const filtroRow = tabela.tHead.insertRow();
        filtroRow.className = "filtros";
        for (let c = 0; c < headRow.cells.length; c++) {
            const th = document.createElement("th");
            const input = document.createElement("input");
            input.type = "text";
            input.className = "form-control form-control-sm";
            input.placeholder = "Filtrar...";
            input.dataset.col = c;
            input.addEventListener("input", () => filtrar(tabela));
            th.appendChild(input);
            filtroRow.appendChild(th);
        }
    }

    function filtrar(tabela) {
        const termos = [...tabela.tHead.querySelectorAll("tr.filtros input")]
            .map((i) => i.value.trim().toLowerCase());
        const corpo = tabela.tBodies[0];
        if (!corpo) {
            return;
        }
        for (const tr of corpo.rows) {
            let mostra = true;
            for (let c = 0; c < termos.length; c++) {
                if (!termos[c]) {
                    continue;
                }
                const celula = tr.cells[c];
                if (!celula || !celula.textContent.toLowerCase().includes(termos[c])) {
                    mostra = false;
                    break;
                }
            }
            tr.style.display = mostra ? "" : "none";
        }
    }

    document.addEventListener("DOMContentLoaded", () => {
        document.querySelectorAll("table.tabela-filtravel").forEach(ativarFiltros);
    });
})();
