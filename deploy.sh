#!/bin/bash

# Script de deploy do sistema SparkWave Login
# Autor: Manus AI
# Data: 06/06/2025

# Cores para saída
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Função para exibir mensagens
print_message() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[AVISO]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERRO]${NC} $1"
}

# Verificar se o Docker está instalado
if ! command -v docker &> /dev/null; then
    print_error "Docker não está instalado. Por favor, instale o Docker primeiro."
    exit 1
fi

# Verificar se o Docker Compose está instalado
if ! command -v docker-compose &> /dev/null; then
    print_error "Docker Compose não está instalado. Por favor, instale o Docker Compose primeiro."
    exit 1
fi

# Verificar se estamos no diretório correto
if [ ! -f "docker-compose.yml" ]; then
    print_error "Arquivo docker-compose.yml não encontrado. Execute este script no diretório raiz do projeto."
    exit 1
fi

# Menu de opções
show_menu() {
    echo "============================================"
    echo "      Sistema SparkWave Login - Deploy      "
    echo "============================================"
    echo "1. Iniciar todos os serviços"
    echo "2. Parar todos os serviços"
    echo "3. Reiniciar todos os serviços"
    echo "4. Ver logs do backend"
    echo "5. Ver logs do frontend"
    echo "6. Ver status dos serviços"
    echo "7. Reconstruir e iniciar serviços"
    echo "0. Sair"
    echo "============================================"
    echo -n "Escolha uma opção: "
}

# Iniciar serviços
start_services() {
    print_message "Iniciando serviços..."
    docker-compose up -d
    if [ $? -eq 0 ]; then
        print_message "Serviços iniciados com sucesso!"
        print_message "Frontend: http://localhost"
        print_message "Backend: http://localhost:8080"
    else
        print_error "Falha ao iniciar serviços."
    fi
}

# Parar serviços
stop_services() {
    print_message "Parando serviços..."
    docker-compose down
    if [ $? -eq 0 ]; then
        print_message "Serviços parados com sucesso!"
    else
        print_error "Falha ao parar serviços."
    fi
}

# Reiniciar serviços
restart_services() {
    print_message "Reiniciando serviços..."
    docker-compose restart
    if [ $? -eq 0 ]; then
        print_message "Serviços reiniciados com sucesso!"
    else
        print_error "Falha ao reiniciar serviços."
    fi
}

# Ver logs do backend
view_backend_logs() {
    print_message "Exibindo logs do backend (Ctrl+C para sair)..."
    docker-compose logs -f backend
}

# Ver logs do frontend
view_frontend_logs() {
    print_message "Exibindo logs do frontend (Ctrl+C para sair)..."
    docker-compose logs -f frontend
}

# Ver status dos serviços
view_status() {
    print_message "Status dos serviços:"
    docker-compose ps
}

# Reconstruir e iniciar serviços
rebuild_services() {
    print_message "Reconstruindo e iniciando serviços..."
    docker-compose down
    docker-compose build --no-cache
    docker-compose up -d
    if [ $? -eq 0 ]; then
        print_message "Serviços reconstruídos e iniciados com sucesso!"
    else
        print_error "Falha ao reconstruir serviços."
    fi
}

# Loop principal
while true; do
    show_menu
    read -r option
    
    case $option in
        1) start_services ;;
        2) stop_services ;;
        3) restart_services ;;
        4) view_backend_logs ;;
        5) view_frontend_logs ;;
        6) view_status ;;
        7) rebuild_services ;;
        0) 
            print_message "Saindo..."
            exit 0
            ;;
        *)
            print_warning "Opção inválida. Por favor, tente novamente."
            ;;
    esac
    
    echo
    read -p "Pressione Enter para continuar..."
    clear
done

