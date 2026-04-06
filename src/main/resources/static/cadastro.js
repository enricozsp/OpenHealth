document.getElementById("cadastroForm").addEventListener("submit", async function(e) {
    e.preventDefault();

    const nome = document.getElementById("nome").value;
    const cpf = document.getElementById("cpf").value;
    const senha = document.getElementById("senha").value;
    const confirmarSenha = document.getElementById("confirmarSenha").value;

    if (senha !== confirmarSenha) {
        alert("As senhas não coincidem!");
        return;
    }

    const usuario = {
        nome: nome,
        cpf: cpf,
        senha: senha
    };

    const resposta = await fetch("/usuarios/cadastro", { // 🔥 IMPORTANTE
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(usuario)
    });

    if (resposta.ok) {
        alert("Cadastro realizado!");
        window.location.href = "index.html";
    } else {
        alert("Erro ao cadastrar");
    }
});