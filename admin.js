/**
 * JavaScript para a área de administração da SparkWave Consultoria Empresarial
 */

// Configuração da API
const API_URL = '/api'; // Alterar para a URL correta da API em produção
const AUTH_ENDPOINT = '/auth';
const ADMIN_ENDPOINT = '/admin';

// Variáveis globais
let currentPage = 'dashboard';
let usersData = [];
let logsData = [];
let usersPagination = { page: 0, size: 10, totalPages: 1 };
let logsPagination = { page: 0, size: 10, totalPages: 1 };
let currentAction = null;
let currentUserId = null;

// Elementos DOM
const navLinks = document.querySelectorAll('.admin-nav a');
const pages = document.querySelectorAll('.admin-page');
const currentUserElement = document.getElementById('current-user');
const addUserBtn = document.getElementById('add-user-btn');
const userModal = document.getElementById('user-modal');
const confirmModal = document.getElementById('confirm-modal');
const closeModal = document.querySelector('.close-modal');
const userForm = document.getElementById('user-form');
const cancelBtn = document.getElementById('cancel-btn');
const confirmCancelBtn = document.getElementById('confirm-cancel-btn');
const confirmOkBtn = document.getElementById('confirm-ok-btn');
const logoutLink = document.getElementById('logout-link');
const searchBtn = document.getElementById('search-btn');
const userSearch = document.getElementById('user-search');
const prevPageBtn = document.getElementById('prev-page');
const nextPageBtn = document.getElementById('next-page');
const pageInfo = document.getElementById('page-info');
const logsPrevPageBtn = document.getElementById('logs-prev-page');
const logsNextPageBtn = document.getElementById('logs-next-page');
const logsPageInfo = document.getElementById('logs-page-info');
const filterBtn = document.getElementById('filter-btn');
const dateFrom = document.getElementById('date-from');
const dateTo = document.getElementById('date-to');
const exportUsersBtn = document.getElementById('export-users-btn');
const exportLogsBtn = document.getElementById('export-logs-btn');
const exportDateFrom = document.getElementById('export-date-from');
const exportDateTo = document.getElementById('export-date-to');

// Verificar autenticação
function checkAuth() {
    const token = localStorage.getItem('jwt_token');
    if (!token) {
        window.location.href = '../index.html';
        return;
    }
    
    // Verificar se o usuário é administrador
    const userInfo = JSON.parse(localStorage.getItem('user_info') || '{}');
    if (!userInfo.roles || !userInfo.roles.includes('ADMIN')) {
        alert('Acesso restrito a administradores.');
        window.location.href = '../index.html';
        return;
    }
    
    // Exibir nome do usuário
    currentUserElement.textContent = userInfo.username || 'Admin';
    
    // Carregar dados iniciais
    loadDashboard();
}

// Carregar dashboard
async function loadDashboard() {
    try {
        const response = await fetchWithAuth(`${API_URL}${ADMIN_ENDPOINT}/dashboard/stats`);
        const data = await response.json();
        
        // Atualizar estatísticas
        document.getElementById('total-users').textContent = data.totalUsers || 0;
        document.getElementById('active-users').textContent = data.activeUsers || 0;
        document.getElementById('logins-today').textContent = data.loginsByDay?.[new Date().toISOString().split('T')[0]] || 0;
        document.getElementById('failed-logins').textContent = data.failedLogins || 0;
        
        // Criar gráfico de acessos por dia
        createLoginsChart(data.loginsByDay || {});
        
        // Criar gráfico de usuários por perfil
        createRolesChart(data.usersByRole || {});
    } catch (error) {
        console.error('Erro ao carregar dashboard:', error);
        showError('Não foi possível carregar os dados do dashboard.');
    }
}

// Criar gráfico de acessos por dia
function createLoginsChart(loginsByDay) {
    const ctx = document.getElementById('logins-chart').getContext('2d');
    
    // Ordenar as datas
    const sortedDates = Object.keys(loginsByDay).sort();
    const data = sortedDates.map(date => loginsByDay[date]);
    
    // Formatar as datas para exibição
    const labels = sortedDates.map(date => {
        const [year, month, day] = date.split('-');
        return `${day}/${month}`;
    });
    
    new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [{
                label: 'Acessos',
                data: data,
                backgroundColor: 'rgba(5, 10, 48, 0.2)',
                borderColor: '#050A30',
                borderWidth: 2,
                tension: 0.3
            }]
        },
        options: {
            responsive: true,
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        precision: 0
                    }
                }
            }
        }
    });
}

// Criar gráfico de usuários por perfil
function createRolesChart(usersByRole) {
    const ctx = document.getElementById('roles-chart').getContext('2d');
    
    const labels = Object.keys(usersByRole);
    const data = Object.values(usersByRole);
    
    new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: labels,
            datasets: [{
                data: data,
                backgroundColor: [
                    '#050A30',
                    '#0A1550',
                    '#8B8383',
                    '#F4F6FC'
                ],
                borderWidth: 1
            }]
        },
        options: {
            responsive: true
        }
    });
}

// Carregar usuários
async function loadUsers(page = 0, search = '') {
    try {
        let url = `${API_URL}${ADMIN_ENDPOINT}/users?page=${page}&size=${usersPagination.size}`;
        if (search) {
            url += `&search=${encodeURIComponent(search)}`;
        }
        
        const response = await fetchWithAuth(url);
        const data = await response.json();
        
        usersData = data.content || data;
        usersPagination.page = data.number || 0;
        usersPagination.totalPages = data.totalPages || 1;
        
        updateUsersTable();
        updateUsersPagination();
    } catch (error) {
        console.error('Erro ao carregar usuários:', error);
        showError('Não foi possível carregar a lista de usuários.');
    }
}

// Atualizar tabela de usuários
function updateUsersTable() {
    const tbody = document.querySelector('#users-table tbody');
    tbody.innerHTML = '';
    
    if (usersData.length === 0) {
        const tr = document.createElement('tr');
        tr.innerHTML = '<td colspan="7" class="text-center">Nenhum usuário encontrado.</td>';
        tbody.appendChild(tr);
        return;
    }
    
    usersData.forEach(user => {
        const tr = document.createElement('tr');
        
        tr.innerHTML = `
            <td>${user.id}</td>
            <td>${user.username}</td>
            <td>${user.fullName || '-'}</td>
            <td>${user.email}</td>
            <td>${user.roles?.join(', ') || 'USER'}</td>
            <td>
                <span class="status-badge ${user.active ? 'status-active' : 'status-inactive'}">
                    ${user.active ? 'Ativo' : 'Inativo'}
                </span>
            </td>
            <td class="action-buttons">
                <button class="edit-btn" data-id="${user.id}">Editar</button>
                <button class="status-btn" data-id="${user.id}" data-active="${!user.active}">
                    ${user.active ? 'Desativar' : 'Ativar'}
                </button>
                <button class="delete-btn" data-id="${user.id}">Excluir</button>
            </td>
        `;
        
        tbody.appendChild(tr);
    });
    
    // Adicionar event listeners
    document.querySelectorAll('.edit-btn').forEach(btn => {
        btn.addEventListener('click', () => editUser(btn.dataset.id));
    });
    
    document.querySelectorAll('.status-btn').forEach(btn => {
        btn.addEventListener('click', () => updateUserStatus(btn.dataset.id, btn.dataset.active === 'true'));
    });
    
    document.querySelectorAll('.delete-btn').forEach(btn => {
        btn.addEventListener('click', () => deleteUser(btn.dataset.id));
    });
}

// Atualizar paginação de usuários
function updateUsersPagination() {
    pageInfo.textContent = `Página ${usersPagination.page + 1} de ${usersPagination.totalPages}`;
    prevPageBtn.disabled = usersPagination.page === 0;
    nextPageBtn.disabled = usersPagination.page >= usersPagination.totalPages - 1;
}

// Carregar logs de acesso
async function loadLogs(page = 0, startDate = null, endDate = null) {
    try {
        let url = `${API_URL}${ADMIN_ENDPOINT}/access-logs?page=${page}&size=${logsPagination.size}`;
        
        if (startDate && endDate) {
            const start = new Date(startDate);
            start.setHours(0, 0, 0, 0);
            
            const end = new Date(endDate);
            end.setHours(23, 59, 59, 999);
            
            url += `&start=${start.toISOString()}&end=${end.toISOString()}`;
        }
        
        const response = await fetchWithAuth(url);
        const data = await response.json();
        
        logsData = data.content || data;
        logsPagination.page = data.number || 0;
        logsPagination.totalPages = data.totalPages || 1;
        
        updateLogsTable();
        updateLogsPagination();
    } catch (error) {
        console.error('Erro ao carregar logs de acesso:', error);
        showError('Não foi possível carregar o histórico de acessos.');
    }
}

// Atualizar tabela de logs
function updateLogsTable() {
    const tbody = document.querySelector('#logs-table tbody');
    tbody.innerHTML = '';
    
    if (logsData.length === 0) {
        const tr = document.createElement('tr');
        tr.innerHTML = '<td colspan="6" class="text-center">Nenhum registro encontrado.</td>';
        tbody.appendChild(tr);
        return;
    }
    
    logsData.forEach(log => {
        const tr = document.createElement('tr');
        
        // Formatar data/hora
        const date = new Date(log.accessTime);
        const formattedDate = date.toLocaleDateString('pt-BR') + ' ' + date.toLocaleTimeString('pt-BR');
        
        tr.innerHTML = `
            <td>${log.id}</td>
            <td>${log.user?.username || '-'}</td>
            <td>${formattedDate}</td>
            <td>${log.ipAddress || '-'}</td>
            <td>${log.action || '-'}</td>
            <td>
                <span class="status-badge ${log.status === 'SUCCESS' ? 'status-active' : 'status-inactive'}">
                    ${log.status || '-'}
                </span>
            </td>
        `;
        
        tbody.appendChild(tr);
    });
}

// Atualizar paginação de logs
function updateLogsPagination() {
    logsPageInfo.textContent = `Página ${logsPagination.page + 1} de ${logsPagination.totalPages}`;
    logsPrevPageBtn.disabled = logsPagination.page === 0;
    logsNextPageBtn.disabled = logsPagination.page >= logsPagination.totalPages - 1;
}

// Abrir modal para adicionar usuário
function openAddUserModal() {
    document.getElementById('modal-title').textContent = 'Adicionar Usuário';
    document.getElementById('user-id').value = '';
    document.getElementById('username').value = '';
    document.getElementById('email').value = '';
    document.getElementById('fullname').value = '';
    document.getElementById('password').value = '';
    document.getElementById('password').required = true;
    document.getElementById('password-hint').style.display = 'none';
    
    // Resetar checkboxes
    document.querySelectorAll('input[name="role"]').forEach(checkbox => {
        checkbox.checked = checkbox.value === 'USER';
    });
    
    // Resetar radio buttons
    document.querySelectorAll('input[name="status"]').forEach(radio => {
        radio.checked = radio.value === 'active';
    });
    
    userModal.style.display = 'block';
}

// Abrir modal para editar usuário
function editUser(userId) {
    const user = usersData.find(u => u.id == userId);
    if (!user) return;
    
    document.getElementById('modal-title').textContent = 'Editar Usuário';
    document.getElementById('user-id').value = user.id;
    document.getElementById('username').value = user.username;
    document.getElementById('email').value = user.email;
    document.getElementById('fullname').value = user.fullName || '';
    document.getElementById('password').value = '';
    document.getElementById('password').required = false;
    document.getElementById('password-hint').style.display = 'block';
    
    // Definir checkboxes
    document.querySelectorAll('input[name="role"]').forEach(checkbox => {
        checkbox.checked = user.roles?.includes(checkbox.value) || false;
    });
    
    // Definir radio buttons
    document.querySelectorAll('input[name="status"]').forEach(radio => {
        radio.checked = (radio.value === 'active' && user.active) || (radio.value === 'inactive' && !user.active);
    });
    
    userModal.style.display = 'block';
}

// Salvar usuário (criar ou atualizar)
async function saveUser(event) {
    event.preventDefault();
    
    const userId = document.getElementById('user-id').value;
    const isUpdate = userId !== '';
    
    // Coletar dados do formulário
    const userData = {
        username: document.getElementById('username').value,
        email: document.getElementById('email').value,
        fullName: document.getElementById('fullname').value,
        password: document.getElementById('password').value,
        roles: Array.from(document.querySelectorAll('input[name="role"]:checked')).map(cb => cb.value),
        active: document.querySelector('input[name="status"]:checked').value === 'active'
    };
    
    // Se for atualização e a senha estiver vazia, remover do objeto
    if (isUpdate && !userData.password) {
        delete userData.password;
    }
    
    try {
        let response;
        
        if (isUpdate) {
            response = await fetchWithAuth(`${API_URL}${ADMIN_ENDPOINT}/users/${userId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(userData)
            });
        } else {
            response = await fetchWithAuth(`${API_URL}${ADMIN_ENDPOINT}/users`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(userData)
            });
        }
        
        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Erro ao salvar usuário');
        }
        
        userModal.style.display = 'none';
        showSuccess(isUpdate ? 'Usuário atualizado com sucesso!' : 'Usuário criado com sucesso!');
        loadUsers(usersPagination.page);
    } catch (error) {
        console.error('Erro ao salvar usuário:', error);
        showError(error.message || 'Não foi possível salvar o usuário.');
    }
}

// Atualizar status do usuário
async function updateUserStatus(userId, active) {
    try {
        const response = await fetchWithAuth(`${API_URL}${ADMIN_ENDPOINT}/users/${userId}/status?active=${active}`, {
            method: 'PATCH'
        });
        
        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Erro ao atualizar status');
        }
        
        showSuccess(`Usuário ${active ? 'ativado' : 'desativado'} com sucesso!`);
        loadUsers(usersPagination.page);
    } catch (error) {
        console.error('Erro ao atualizar status:', error);
        showError(error.message || 'Não foi possível atualizar o status do usuário.');
    }
}

// Confirmar exclusão de usuário
function deleteUser(userId) {
    currentAction = 'delete-user';
    currentUserId = userId;
    
    const user = usersData.find(u => u.id == userId);
    document.getElementById('confirm-message').textContent = `Tem certeza que deseja excluir o usuário "${user?.username}"?`;
    
    confirmModal.style.display = 'block';
}

// Executar ação confirmada
async function executeConfirmedAction() {
    if (currentAction === 'delete-user' && currentUserId) {
        try {
            const response = await fetchWithAuth(`${API_URL}${ADMIN_ENDPOINT}/users/${currentUserId}`, {
                method: 'DELETE'
            });
            
            if (!response.ok) {
                const error = await response.json();
                throw new Error(error.message || 'Erro ao excluir usuário');
            }
            
            showSuccess('Usuário excluído com sucesso!');
            loadUsers(usersPagination.page);
        } catch (error) {
            console.error('Erro ao excluir usuário:', error);
            showError(error.message || 'Não foi possível excluir o usuário.');
        }
    } else if (currentAction === 'logout') {
        logout();
    }
    
    confirmModal.style.display = 'none';
}

// Exportar usuários
function exportUsers() {
    window.open(`${API_URL}${ADMIN_ENDPOINT}/export/users/csv`, '_blank');
}

// Exportar logs
function exportLogs() {
    let url = `${API_URL}${ADMIN_ENDPOINT}/export/access-logs/csv`;
    
    const startDate = exportDateFrom.value;
    const endDate = exportDateTo.value;
    
    if (startDate && endDate) {
        const start = new Date(startDate);
        start.setHours(0, 0, 0, 0);
        
        const end = new Date(endDate);
        end.setHours(23, 59, 59, 999);
        
        url += `?start=${start.toISOString()}&end=${end.toISOString()}`;
    }
    
    window.open(url, '_blank');
}

// Confirmar logout
function confirmLogout() {
    currentAction = 'logout';
    document.getElementById('confirm-message').textContent = 'Tem certeza que deseja sair?';
    confirmModal.style.display = 'block';
}

// Fazer logout
async function logout() {
    try {
        await fetchWithAuth(`${API_URL}${AUTH_ENDPOINT}/signout`, {
            method: 'POST'
        });
    } catch (error) {
        console.error('Erro ao fazer logout:', error);
    } finally {
        localStorage.removeItem('jwt_token');
        localStorage.removeItem('user_info');
        window.location.href = '../index.html';
    }
}

// Utilitário para fazer requisições autenticadas
async function fetchWithAuth(url, options = {}) {
    const token = localStorage.getItem('jwt_token');
    
    if (!token) {
        throw new Error('Não autenticado');
    }
    
    const headers = {
        ...options.headers,
        'Authorization': `Bearer ${token}`
    };
    
    const response = await fetch(url, {
        ...options,
        headers
    });
    
    if (response.status === 401) {
        // Token expirado ou inválido
        localStorage.removeItem('jwt_token');
        localStorage.removeItem('user_info');
        window.location.href = '../index.html';
        throw new Error('Sessão expirada');
    }
    
    return response;
}

// Exibir mensagem de sucesso
function showSuccess(message) {
    alert(message); // Substituir por uma notificação mais elegante
}

// Exibir mensagem de erro
function showError(message) {
    alert(`Erro: ${message}`); // Substituir por uma notificação mais elegante
}

// Alternar entre páginas
function switchPage(pageName) {
    currentPage = pageName;
    
    // Atualizar navegação
    navLinks.forEach(link => {
        link.classList.toggle('active', link.dataset.page === pageName);
    });
    
    // Mostrar página selecionada
    pages.forEach(page => {
        page.style.display = page.id === `${pageName}-page` ? 'block' : 'none';
    });
    
    // Carregar dados da página
    if (pageName === 'dashboard') {
        loadDashboard();
    } else if (pageName === 'users') {
        loadUsers();
    } else if (pageName === 'access-logs') {
        loadLogs();
    }
}

// Event listeners
document.addEventListener('DOMContentLoaded', () => {
    // Verificar autenticação
    checkAuth();
    
    // Navegação
    navLinks.forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            switchPage(link.dataset.page);
        });
    });
    
    // Adicionar usuário
    addUserBtn.addEventListener('click', openAddUserModal);
    
    // Fechar modal
    closeModal.addEventListener('click', () => userModal.style.display = 'none');
    cancelBtn.addEventListener('click', () => userModal.style.display = 'none');
    
    // Fechar modal de confirmação
    confirmCancelBtn.addEventListener('click', () => confirmModal.style.display = 'none');
    
    // Confirmar ação
    confirmOkBtn.addEventListener('click', executeConfirmedAction);
    
    // Salvar usuário
    userForm.addEventListener('submit', saveUser);
    
    // Logout
    logoutLink.addEventListener('click', (e) => {
        e.preventDefault();
        confirmLogout();
    });
    
    // Buscar usuários
    searchBtn.addEventListener('click', () => loadUsers(0, userSearch.value));
    userSearch.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            loadUsers(0, userSearch.value);
        }
    });
    
    // Paginação de usuários
    prevPageBtn.addEventListener('click', () => {
        if (usersPagination.page > 0) {
            loadUsers(usersPagination.page - 1, userSearch.value);
        }
    });
    
    nextPageBtn.addEventListener('click', () => {
        if (usersPagination.page < usersPagination.totalPages - 1) {
            loadUsers(usersPagination.page + 1, userSearch.value);
        }
    });
    
    // Paginação de logs
    logsPrevPageBtn.addEventListener('click', () => {
        if (logsPagination.page > 0) {
            loadLogs(logsPagination.page - 1, dateFrom.value, dateTo.value);
        }
    });
    
    logsNextPageBtn.addEventListener('click', () => {
        if (logsPagination.page < logsPagination.totalPages - 1) {
            loadLogs(logsPagination.page + 1, dateFrom.value, dateTo.value);
        }
    });
    
    // Filtrar logs
    filterBtn.addEventListener('click', () => {
        loadLogs(0, dateFrom.value, dateTo.value);
    });
    
    // Exportar dados
    exportUsersBtn.addEventListener('click', exportUsers);
    exportLogsBtn.addEventListener('click', exportLogs);
    
    // Fechar modais ao clicar fora
    window.addEventListener('click', (e) => {
        if (e.target === userModal) {
            userModal.style.display = 'none';
        }
        if (e.target === confirmModal) {
            confirmModal.style.display = 'none';
        }
    });
});

