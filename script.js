/**
 * Script para gerenciar a autenticação de usuários no sistema SparkWave
 */

// Configuração da API
const API_URL = 'http://localhost:8080/api';
const AUTH_ENDPOINT = '/auth/signin';

// Elementos do DOM
const loginButton = document.getElementById('login-button');
const usernameInput = document.getElementById('username');
const passwordInput = document.getElementById('password');
const usernameError = document.getElementById('username-error');
const passwordError = document.getElementById('password-error');
const loginError = document.getElementById('login-error');
const logoPlaceholder = document.getElementById('logo-placeholder');

// Gerar um logo temporário com as iniciais da empresa
function generateLogoPlaceholder() {
    // Verificar se o logo existe, caso contrário, criar um canvas com as iniciais
    const canvas = document.createElement('canvas');
    canvas.width = 180;
    canvas.height = 100;
    const ctx = canvas.getContext('2d');
    
    // Cor de fundo (usando a cor secundária)
    ctx.fillStyle = '#F4F6FC';
    ctx.fillRect(0, 0, canvas.width, canvas.height);
    
    // Texto (usando a cor primária)
    ctx.fillStyle = '#050A30';
    ctx.font = 'bold 40px Roboto, sans-serif';
    ctx.textAlign = 'center';
    ctx.textBaseline = 'middle';
    ctx.fillText('SW', canvas.width / 2, canvas.height / 2);
    
    // Borda (usando a cor terciária)
    ctx.strokeStyle = '#8B8383';
    ctx.lineWidth = 2;
    ctx.strokeRect(0, 0, canvas.width, canvas.height);
    
    // Substituir a imagem pelo canvas
    logoPlaceholder.src = canvas.toDataURL();
}

// Validar campos do formulário
function validateForm() {
    let isValid = true;
    
    // Validar nome de usuário
    if (!usernameInput.value.trim()) {
        usernameError.classList.add('visible');
        isValid = false;
    } else {
        usernameError.classList.remove('visible');
    }
    
    // Validar senha
    if (!passwordInput.value.trim()) {
        passwordError.classList.add('visible');
        isValid = false;
    } else {
        passwordError.classList.remove('visible');
    }
    
    return isValid;
}

// Mostrar estado de carregamento
function setLoading(isLoading) {
    if (isLoading) {
        loginButton.classList.add('loading');
        loginButton.disabled = true;
        loginButton.textContent = 'Entrando ';
        loginButton.appendChild(document.querySelector('.spinner'));
    } else {
        loginButton.classList.remove('loading');
        loginButton.disabled = false;
        loginButton.textContent = 'Entrar';
    }
}

// Processar o login
async function handleLogin() {
    // Esconder mensagens de erro anteriores
    loginError.classList.remove('visible');
    
    // Validar formulário
    if (!validateForm()) {
        return;
    }
    
    // Mostrar estado de carregamento
    setLoading(true);
    
    // Preparar dados para envio
    const loginData = {
        username: usernameInput.value.trim(),
        password: passwordInput.value.trim()
    };
    
    try {
        // Enviar requisição para a API
        const response = await fetch(`${API_URL}${AUTH_ENDPOINT}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(loginData)
        });
        
        // Processar resposta
        if (response.ok) {
            const data = await response.json();
            
            // Armazenar token JWT no localStorage
            localStorage.setItem('jwt_token', data.token);
            localStorage.setItem('user_info', JSON.stringify({
                id: data.id,
                username: data.username,
                email: data.email,
                roles: data.roles
            }));
            
            // Redirecionar para a URL especificada na resposta
            window.location.href = data.redirectUrl || 'https://school-finance-wizard.lovable.app/calculos';
        } else {
            // Mostrar mensagem de erro
            loginError.classList.add('visible');
            setLoading(false);
        }
    } catch (error) {
        console.error('Erro ao fazer login:', error);
        loginError.textContent = 'Erro de conexão. Verifique sua internet e tente novamente.';
        loginError.classList.add('visible');
        setLoading(false);
    }
}

// Inicializar a página
function init() {
    // Gerar logo placeholder
    generateLogoPlaceholder();
    
    // Adicionar event listeners
    loginButton.addEventListener('click', handleLogin);
    
    // Permitir login com a tecla Enter
    passwordInput.addEventListener('keypress', function(event) {
        if (event.key === 'Enter') {
            handleLogin();
        }
    });
    
    usernameInput.addEventListener('keypress', function(event) {
        if (event.key === 'Enter') {
            passwordInput.focus();
        }
    });
    
    // Limpar mensagens de erro ao digitar
    usernameInput.addEventListener('input', function() {
        usernameError.classList.remove('visible');
        loginError.classList.remove('visible');
    });
    
    passwordInput.addEventListener('input', function() {
        passwordError.classList.remove('visible');
        loginError.classList.remove('visible');
    });
}

// Inicializar quando o DOM estiver pronto
document.addEventListener('DOMContentLoaded', init);

