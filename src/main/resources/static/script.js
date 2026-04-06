document.getElementById("loginForm").addEventListener("submit", async function(e) {
    e.preventDefault();

    const cpf = document.getElementById("cpf").value;
    const senha = document.getElementById("senha").value;

    const resposta = await fetch("/usuarios/login", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ cpf, senha })
    });

    if (resposta.ok) {
        alert("Login realizado!");
    } else {
        alert("CPF ou senha inválidos");
    }
});