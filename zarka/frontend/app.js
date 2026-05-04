const statusEl = document.getElementById("status");
const produtosEl = document.getElementById("produtos");
const carrinhoEl = document.getElementById("carrinho");
const pedidosEl = document.getElementById("pedidos");

function usuarioId() {
    return document.getElementById("usuarioId").value;
}

function metodoPagamento() {
    return document.getElementById("metodoPagamento").value;
}

function setStatus(message, isError = false) {
    statusEl.textContent = message;
    statusEl.style.color = isError ? "#fca5a5" : "#9ae6b4";
}

async function api(path, options = {}) {
    const response = await fetch(`/api${path}`, {
        headers: {
            "Content-Type": "application/json"
        },
        ...options
    });

    if (!response.ok) {
        const text = await response.text();
        throw new Error(text || `Erro HTTP ${response.status}`);
    }

    const contentType = response.headers.get("content-type") || "";
    if (contentType.includes("application/json")) {
        return response.json();
    }

    return response.text();
}

async function carregarProdutos() {
    try {
        const produtos = await api("/produtos");
        produtosEl.innerHTML = "";

        produtos.forEach((produto) => {
            const card = document.createElement("div");
            card.className = "product-card";
            card.innerHTML = `
                <h3>${produto.nome}</h3>
                <div class="price">R$ ${Number(produto.preco).toFixed(2)}</div>
                <div class="inline-form">
                    <input id="qtd-${produto.id}" type="number" value="1" min="1">
                    <button data-produto-id="${produto.id}">Adicionar</button>
                </div>
            `;
            produtosEl.appendChild(card);
        });

        produtosEl.querySelectorAll("button[data-produto-id]").forEach((button) => {
            button.addEventListener("click", async () => {
                const produtoId = button.getAttribute("data-produto-id");
                const quantidade = document.getElementById(`qtd-${produtoId}`).value;
                await adicionarAoCarrinho(produtoId, quantidade);
            });
        });

        setStatus("Produtos carregados com sucesso.");
    } catch (error) {
        setStatus(`Falha ao carregar produtos: ${error.message}`, true);
    }
}

async function adicionarAoCarrinho(produtoId, quantidade) {
    try {
        await api(`/carrinho/adicionar?usuarioId=${usuarioId()}&produtoId=${produtoId}&quantidade=${quantidade}`, {
            method: "POST"
        });
        setStatus("Produto adicionado ao carrinho.");
        await listarCarrinho();
    } catch (error) {
        setStatus(`Falha ao adicionar produto: ${error.message}`, true);
    }
}

async function listarCarrinho() {
    try {
        const itens = await api(`/carrinho?usuarioId=${usuarioId()}`);
        carrinhoEl.innerHTML = "";

        if (itens.length === 0) {
            carrinhoEl.innerHTML = "<p>Carrinho vazio.</p>";
            return;
        }

        itens.forEach((item) => {
            const card = document.createElement("div");
            card.className = "item-card";
            card.innerHTML = `
                <h3>${item.produto.nome}</h3>
                <p>Quantidade: ${item.quantidade}</p>
                <p>Preco unitario: R$ ${Number(item.produto.preco).toFixed(2)}</p>
            `;
            carrinhoEl.appendChild(card);
        });

        setStatus("Carrinho atualizado.");
    } catch (error) {
        setStatus(`Falha ao listar carrinho: ${error.message}`, true);
    }
}

async function limparCarrinho() {
    try {
        await api(`/carrinho/limpar?usuarioId=${usuarioId()}`, { method: "DELETE" });
        setStatus("Carrinho limpo.");
        await listarCarrinho();
    } catch (error) {
        setStatus(`Falha ao limpar carrinho: ${error.message}`, true);
    }
}

async function fecharPedido() {
    try {
        const pedido = await api(`/pedidos/fechar?usuarioId=${usuarioId()}&metodoPagamento=${encodeURIComponent(metodoPagamento())}`, {
            method: "POST"
        });
        setStatus(`Pedido ${pedido.id} criado com sucesso.`);
        await listarCarrinho();
        await listarPedidos();
    } catch (error) {
        setStatus(`Falha ao fechar pedido: ${error.message}`, true);
    }
}

async function listarPedidos() {
    try {
        const pedidos = await api(`/pedidos?usuarioId=${usuarioId()}`);
        pedidosEl.innerHTML = "";

        if (pedidos.length === 0) {
            pedidosEl.innerHTML = "<p>Nenhum pedido encontrado.</p>";
            return;
        }

        pedidos.forEach((pedido) => {
            const itens = (pedido.itens || [])
                .map((item) => `${item.produto.nome} x${item.quantidade}`)
                .join(", ");

            const card = document.createElement("div");
            card.className = "order-card";
            card.innerHTML = `
                <h3>Pedido #${pedido.id}</h3>
                <p>Status: ${pedido.status}</p>
                <p>Total: R$ ${Number(pedido.total || 0).toFixed(2)}</p>
                <p>Pagamento: ${pedido.pagamento ? pedido.pagamento.metodo : "N/A"}</p>
                <p>Itens: ${itens || "Sem itens"}</p>
            `;
            pedidosEl.appendChild(card);
        });

        setStatus("Pedidos atualizados.");
    } catch (error) {
        setStatus(`Falha ao listar pedidos: ${error.message}`, true);
    }
}

document.getElementById("recarregarProdutos").addEventListener("click", carregarProdutos);
document.getElementById("listarCarrinho").addEventListener("click", listarCarrinho);
document.getElementById("limparCarrinho").addEventListener("click", limparCarrinho);
document.getElementById("fecharPedido").addEventListener("click", fecharPedido);
document.getElementById("listarPedidos").addEventListener("click", listarPedidos);

carregarProdutos();
listarCarrinho();
listarPedidos();
