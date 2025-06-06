# SparkWave Consultoria Empresarial - Sistema de Login

![SparkWave Logo](frontend/img/logo.png)

## Sobre o Projeto

Sistema de autenticação e gerenciamento de usuários desenvolvido para a SparkWave Consultoria Empresarial. Este sistema permite que clientes acessem serviços de consultoria financeira através de uma interface segura e responsiva.

## Funcionalidades

### Autenticação e Segurança
- Login seguro com JWT (JSON Web Tokens)
- Controle de acesso baseado em perfis (administrador, cliente)
- Proteção contra ataques comuns (CSRF, XSS, etc.)
- Registro de histórico de acessos para auditoria

### Área de Administração
- Dashboard com estatísticas de uso
- Gerenciamento completo de usuários (CRUD)
- Histórico de acessos com filtros
- Exportação de dados em formato CSV
- Notificações por e-mail

### Interface de Usuário
- Design responsivo para desktop e dispositivos móveis
- Paleta de cores corporativa (#050A30, #F4F6FC, #8B8383)
- Redirecionamento automático para calculadora financeira após login

## Tecnologias Utilizadas

### Backend
- Java 17
- Spring Boot 3.x
- Spring Security
- JWT Authentication
- JPA / Hibernate
- PostgreSQL (produção) / H2 (desenvolvimento)

### Frontend
- HTML5, CSS3, JavaScript
- Design responsivo
- Gráficos interativos com Chart.js

### DevOps
- Docker e Docker Compose
- Nginx
- Scripts de automação para deploy

## Estrutura do Projeto

```
sparkwave-login/
├── backend/                # API Java Spring Boot
│   ├── src/                # Código-fonte do backend
│   ├── pom.xml             # Configuração Maven
│   └── Dockerfile          # Configuração Docker para backend
├── frontend/               # Interface de usuário
│   ├── admin/              # Área de administração
│   ├── css/                # Estilos CSS
│   ├── js/                 # Scripts JavaScript
│   ├── img/                # Imagens e recursos visuais
│   ├── index.html          # Página de login
│   └── Dockerfile          # Configuração Docker para frontend
├── docker-compose.yml      # Configuração Docker Compose
├── deploy.sh               # Script de deploy interativo
├── deploy_guide.md         # Guia detalhado de deploy
├── DEPLOY_RAPIDO.md        # Guia rápido de deploy
└── README.md               # Este arquivo
```

## Requisitos de Sistema

### Desenvolvimento
- JDK 17 ou superior
- Maven 3.8+
- Node.js 18+ (opcional, para desenvolvimento frontend)
- Docker e Docker Compose (opcional)

### Produção
- Servidor Linux com 2GB+ RAM
- Docker 20.10+ e Docker Compose 2.0+
- Ou: JDK 17+, PostgreSQL 12+, Nginx/Apache

## Instalação e Execução

### Usando Docker (Recomendado)

1. Clone o repositório:
   ```bash
   git clone [URL_DO_REPOSITORIO] sparkwave-login
   cd sparkwave-login
   ```

2. Execute o script de deploy:
   ```bash
   chmod +x deploy.sh
   ./deploy.sh
   ```

3. Selecione a opção 1 para iniciar todos os serviços.

4. Acesse:
   - Frontend: http://localhost
   - Backend: http://localhost:8080

### Instalação Manual

Consulte o arquivo `deploy_guide.md` para instruções detalhadas sobre instalação manual.

## Usuários Padrão

O sistema vem pré-configurado com os seguintes usuários para teste:

| Usuário | Senha    | Perfil        |
|---------|----------|---------------|
| admin   | admin123 | Administrador |
| cliente1| user123  | Cliente       |

**Importante:** Altere estas senhas antes de usar em produção!

## Documentação

- `deploy_guide.md` - Guia detalhado de deploy
- `DEPLOY_RAPIDO.md` - Guia rápido de deploy
- `backend/src/main/resources/application.properties` - Configurações do backend

## Licença

© 2025 SparkWave Consultoria Empresarial. Todos os direitos reservados.

## Contato

Para suporte técnico ou dúvidas sobre o sistema, entre em contato com:
- Email: suporte@sparkwave.com.br
- Telefone: (XX) XXXX-XXXX

