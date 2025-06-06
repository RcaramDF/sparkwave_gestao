# Guia Rápido de Deploy - SparkWave Login

Este guia fornece instruções simplificadas para colocar o sistema de login da SparkWave Consultoria Empresarial em produção usando Docker.

## Pré-requisitos

- Docker 20.10 ou superior
- Docker Compose 2.0 ou superior
- Servidor com pelo menos 4GB de RAM e 20GB de espaço em disco

## Passos para Deploy

### 1. Clonar o Repositório

```bash
git clone [URL_DO_REPOSITORIO] sparkwave-login
cd sparkwave-login
```

### 2. Configurar Variáveis de Ambiente (Opcional)

Edite o arquivo `docker-compose.yml` para personalizar:

- Senhas do banco de dados
- Chave secreta JWT
- URLs permitidas para CORS
- URL de redirecionamento após login

### 3. Executar o Script de Deploy

```bash
# Tornar o script executável
chmod +x deploy.sh

# Executar o script
./deploy.sh
```

### 4. Selecionar Opção no Menu

No menu interativo, selecione a opção `1` para iniciar todos os serviços.

### 5. Verificar Funcionamento

Após a inicialização, acesse:

- Frontend: http://seu-servidor
- Backend: http://seu-servidor:8080

## Comandos Úteis

### Iniciar Serviços Manualmente

```bash
docker-compose up -d
```

### Parar Serviços

```bash
docker-compose down
```

### Ver Logs

```bash
# Backend
docker-compose logs -f backend

# Frontend
docker-compose logs -f frontend
```

### Reiniciar Serviços

```bash
docker-compose restart
```

## Configuração de Domínio e HTTPS

Para configurar um domínio e HTTPS:

1. Configure seu DNS para apontar para o IP do servidor
2. Instale o Certbot:
   ```bash
   sudo apt update
   sudo apt install certbot python3-certbot-nginx
   ```
3. Obtenha um certificado SSL:
   ```bash
   sudo certbot --nginx -d seu-dominio.com
   ```

## Solução de Problemas

### Serviços não iniciam

Verifique os logs para identificar o problema:

```bash
docker-compose logs
```

### Erro de conexão com o banco de dados

Verifique se o serviço do PostgreSQL está em execução:

```bash
docker-compose ps postgres
```

### Problemas de permissão

Verifique se os volumes do Docker têm as permissões corretas:

```bash
sudo chown -R 999:999 /var/lib/docker/volumes/sparkwave-login_postgres_data
```

## Suporte

Para obter ajuda adicional, consulte o guia detalhado em `deploy_guide.md` ou entre em contato com o suporte técnico da SparkWave Consultoria Empresarial.

