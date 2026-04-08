document.getElementById("cadastroForm").addEventListener("submit", async function(e) {
    e.preventDefault();

    const nome = document.getElementById("nome").value.trim();
    const cpf = document.getElementById("cpf").value.trim();
    const dataNascimento = document.getElementById("dataNascimento").value;
    const tipoSanguineo = document.getElementById("tipoSanguineo").value.trim();
    const senha = document.getElementById("senha").value;
    const confirmarSenha = document.getElementById("confirmarSenha").value;

    // Validações básicas
    if (!nome || !cpf || !senha) {
        alert("Preencha nome, CPF e senha!");
        return;
    }

    if (senha !== confirmarSenha) {
        alert("As senhas não coincidem!");
        return;
    }

    if (cpf.length < 11) {
        alert("CPF deve ter pelo menos 11 dígitos!");
        return;
    }

    const usuario = {
        nome: nome,
        cpf: cpf,
        data_nascimento: dataNascimento || null,
        tipo_sanguineo: tipoSanguineo || null,
        senha: senha
    };

    try {
        const apiBase = "http://localhost:8080";
        const resposta = await fetch(`${apiBase}/usuarios/cadastro`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(usuario)
        });

        const data = await resposta.json();

        if (resposta.ok) {
            alert("Cadastro realizado com sucesso!");
            window.location.href = "index.html";
        } else {
            alert("Erro ao cadastrar: " + (data.mensagem || "Tente novamente"));
        }
    } catch (error) {
        console.error("Erro:", error);
        alert("Erro ao conectar com o servidor");
    }
});
