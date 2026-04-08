// Obter nome do usuário da URL ou sessionStorage
document.addEventListener('DOMContentLoaded', function() {
    const params = new URLSearchParams(window.location.search);
    let userName = params.get('name');

    // Se não encontrar na URL, tenta no sessionStorage
    if (!userName) {
        userName = sessionStorage.getItem('userName');
    }

    // Se ainda não tiver, usa um padrão
    if (!userName) {
        userName = 'FULANO';
    }

    // Atualiza o nome em todos os lugares
    document.getElementById('userName').textContent = userName.toUpperCase();
    document.getElementById('userNameLarge').textContent = userName.toUpperCase();

    // Armazena na sessão para persistência
    sessionStorage.setItem('userName', userName);
});

// Funções de navegação
function logout() {
    if (confirm('Tem certeza que deseja sair?')) {
        sessionStorage.removeItem('userName');
        window.location.href = 'index.html';
    }
}

function goToProfile() {
    alert('Funcionalidade de Perfil em desenvolvimento');
}

function goToConsultations() {
    alert('Funcionalidade de Consultas em desenvolvimento');
}

function goToHistory() {
    alert('Funcionalidade de Histórico em desenvolvimento');
}

function goToSettings() {
    alert('Funcionalidade de Configurações em desenvolvimento');
}
