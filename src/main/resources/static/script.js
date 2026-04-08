document.getElementById("loginForm").addEventListener("submit", async function(e) {
    e.preventDefault();

    const cpf = document.getElementById("cpf").value;
    const senha = document.getElementById("senha").value;

    const apiBase = "http://localhost:8080";

    const resposta = await fetch(`${apiBase}/usuarios/login`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ cpf, senha })
    });

    if (resposta.ok) {
        const data = await resposta.json();
        // Armazena o nome do usuário
        sessionStorage.setItem('userName', data.nome);
        // Redireciona para a landing page com o nome
        window.location.href = `landing.html?name=${encodeURIComponent(data.nome)}`;
    } else {
        alert("CPF ou senha inválidos");
    }
});