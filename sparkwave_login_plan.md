# Plano de Desenvolvimento do Sistema de Acesso SparkWave Consultoria Empresarial

## 1. Introdução

Este documento detalha o plano de desenvolvimento para a criação de um sistema de acesso seguro com login e senha, destinado aos clientes da SparkWave Consultoria Empresarial. O objetivo principal é fornecer uma interface de autenticação robusta e intuitiva, que, após a validação das credenciais, redirecione o usuário para a plataforma de cálculos financeiros existente em `https://school-finance-wizard.lovable.app/calculos`. O sistema será desenvolvido utilizando tecnologias Java para o backend e uma abordagem de frontend leve e responsiva, garantindo uma experiência de usuário consistente e segura. A paleta de cores da SparkWave Consultoria Empresarial será rigorosamente aplicada para manter a identidade visual da marca, utilizando as cores `#050A30`, `#F4F6FC` e `#8B8383`.

Como um desenvolvedor com mais de três décadas de experiência, a abordagem aqui delineada prioriza a segurança, a escalabilidade e a manutenibilidade do código. A escolha das tecnologias e a metodologia de desenvolvimento refletem as melhores práticas da indústria, visando a longevidade e a adaptabilidade do sistema às futuras necessidades da SparkWave. Além do desenvolvimento, este plano incluirá orientações detalhadas para a implantação (deploy) do sistema em ambiente de produção, garantindo que a SparkWave Consultoria Empresarial possa colocar a solução no ar com confiança e autonomia.

## 2. Arquitetura do Sistema

A arquitetura proposta para o sistema de acesso da SparkWave Consultoria Empresarial será baseada em uma abordagem cliente-servidor, dividida em duas camadas principais: Backend (Servidor) e Frontend (Cliente). Esta separação permite maior flexibilidade, escalabilidade e facilita a manutenção e evolução independente de cada componente.

### 2.1. Backend (Servidor)

O backend será responsável pela lógica de negócios, gerenciamento de usuários, autenticação e autorização. Será desenvolvido em Java, aproveitando a robustez e a maturidade do ecossistema Java para aplicações corporativas. A escolha de um framework leve e eficiente é crucial para garantir um desempenho otimizado e um tempo de desenvolvimento ágil.

**Tecnologias Propostas para o Backend:**

*   **Linguagem de Programação:** Java 17+ (versão LTS para suporte de longo prazo e recursos modernos).
*   **Framework Web:** Spring Boot 3.x. O Spring Boot é amplamente reconhecido por sua capacidade de criar aplicações Java autônomas e prontas para produção com o mínimo de configuração. Ele simplifica o desenvolvimento de APIs RESTful, que serão essenciais para a comunicação com o frontend.
*   **Segurança:** Spring Security. Integrado ao Spring Boot, o Spring Security oferece um conjunto abrangente de recursos de segurança, incluindo autenticação baseada em formulário, autenticação básica HTTP, OAuth2, JWT (JSON Web Tokens) e controle de acesso baseado em funções. Para este projeto, utilizaremos autenticação baseada em usuário/senha, com armazenamento seguro de senhas (hashing).
*   **Banco de Dados:** PostgreSQL. Um sistema de gerenciamento de banco de dados relacional (SGBDR) de código aberto, robusto, escalável e confiável. É uma excelente escolha para armazenar informações de usuários e credenciais. Para fins de desenvolvimento e testes iniciais, podemos considerar o uso de um banco de dados em memória como o H2, mas o deploy final será com PostgreSQL.
*   **ORM (Object-Relational Mapping):** Spring Data JPA com Hibernate. Facilita a interação com o banco de dados, mapeando objetos Java para tabelas do banco de dados e vice-versa, reduzindo a quantidade de código boilerplate para operações de persistência.
*   **Ferramenta de Build:** Maven ou Gradle. Ambas são excelentes opções para gerenciamento de dependências e automação de build. Para este projeto, Maven será a escolha padrão devido à sua ampla adoção e simplicidade de configuração para projetos Spring Boot.

**Funcionalidades do Backend:**

*   **API de Autenticação:** Endpoint RESTful para receber credenciais de login (usuário e senha) e validar contra o banco de dados.
*   **Gerenciamento de Usuários:** Capacidade de adicionar, remover e atualizar usuários (inicialmente, isso pode ser feito manualmente no banco de dados ou via script, com a possibilidade de uma interface administrativa futura).
*   **Geração de Token (Opcional, mas recomendado):** Para uma experiência de usuário mais fluida e segura, o backend pode gerar um JWT após a autenticação bem-sucedida. Este token seria enviado ao frontend e usado para autenticar requisições subsequentes, eliminando a necessidade de reenviar credenciais a cada requisição.

### 2.2. Frontend (Cliente)

O frontend será a interface com a qual os clientes da SparkWave interagirão. Será uma aplicação web leve, focada na usabilidade e na aderência à identidade visual da empresa. A escolha de tecnologias que garantam responsividade é fundamental para que o sistema funcione bem em diferentes dispositivos (desktops, tablets, smartphones).

**Tecnologias Propostas para o Frontend:**

*   **Linguagens:** HTML5, CSS3, JavaScript (ES6+).
*   **Framework/Biblioteca (Opcional, mas recomendado para agilizar):** Uma biblioteca leve como o Alpine.js ou mesmo JavaScript puro para manipulação do DOM. Evitaremos frameworks mais pesados como React, Angular ou Vue.js para manter a simplicidade e o tempo de carregamento rápido, dado o escopo focado na tela de login.
*   **Estilização:** CSS puro com variáveis CSS para a paleta de cores. Isso garantirá total controle sobre o design e a aplicação das cores `#050A30`, `#F4F6FC` e `#8B8383` de forma consistente.
*   **Requisições HTTP:** `fetch` API do JavaScript para comunicação assíncrona com o backend.

**Funcionalidades do Frontend:**

*   **Tela de Login:** Formulário com campos para nome de usuário e senha, e um botão de submissão. Design responsivo e aderente à paleta de cores da SparkWave.
*   **Feedback ao Usuário:** Mensagens de erro claras em caso de falha na autenticação (ex: credenciais inválidas).
*   **Redirecionamento Pós-Login:** Após a autenticação bem-sucedida, o frontend será responsável por redirecionar o navegador do usuário para `https://school-finance-wizard.lovable.app/calculos`.

## 3. Paleta de Cores e Identidade Visual

A identidade visual da SparkWave Consultoria Empresarial será um pilar fundamental no design do frontend. As cores especificadas serão aplicadas de forma estratégica para criar uma experiência visual agradável e profissional.

*   **`#050A30` (Azul Escuro/Quase Preto):** Esta cor será utilizada para elementos de destaque, como o fundo principal da tela de login, cabeçalhos, ou botões primários. Transmite seriedade, confiança e profissionalismo.
*   **`#F4F6FC` (Branco Off-White/Cinza Claro):** Ideal para o fundo de campos de entrada, texto principal, ou como cor de contraste para elementos que precisam se destacar sobre o azul escuro. Oferece clareza e legibilidade.
*   **`#8B8383` (Cinza Médio):** Perfeita para bordas de campos, texto secundário, ícones ou elementos de design que necessitam de um contraste suave, sem ser tão forte quanto o branco. Adiciona um toque de sofisticação e equilíbrio.

Serão utilizadas variáveis CSS para gerenciar essas cores, facilitando futuras alterações e garantindo consistência em todo o frontend.

## 4. Fluxo de Autenticação e Redirecionamento

O fluxo de autenticação e redirecionamento seguirá os seguintes passos:

1.  **Acesso à Tela de Login:** O cliente acessa a URL do sistema de login da SparkWave.
2.  **Preenchimento de Credenciais:** O cliente insere seu nome de usuário e senha no formulário.
3.  **Submissão:** Ao clicar no botão de login, o frontend coleta as credenciais e as envia para o endpoint de autenticação do backend via requisição HTTP POST.
4.  **Validação no Backend:** O backend recebe as credenciais, realiza a validação contra o banco de dados. Se as credenciais forem válidas, o backend pode gerar um JWT (JSON Web Token) e enviá-lo de volta ao frontend. Se forem inválidas, uma mensagem de erro é retornada.
5.  **Resposta do Backend:**
    *   **Sucesso:** O backend retorna um status HTTP 200 (OK) e, opcionalmente, o JWT. O frontend armazena este token (ex: em `localStorage` ou `sessionStorage`).
    *   **Falha:** O backend retorna um status HTTP 401 (Unauthorized) ou 403 (Forbidden) com uma mensagem de erro, que é exibida ao usuário no frontend.
6.  **Redirecionamento:** Se a autenticação for bem-sucedida, o frontend utiliza JavaScript para redirecionar o navegador do cliente para `https://school-finance-wizard.lovable.app/calculos`. O JWT pode ser incluído como um parâmetro de URL ou em um cabeçalho, dependendo de como a aplicação de cálculos espera receber a autenticação (se ela precisar de autenticação).

## 5. Estrutura do Projeto (Exemplo)

Para ilustrar, a estrutura de diretórios do projeto pode ser organizada da seguinte forma:

```
sparkwave-login/
├── backend/
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/
│       │   │   └── com/
│       │   │       └── sparkwave/
│       │   │           └── login/
│       │   │               ├── SparkwaveLoginApplication.java
│       │   │               ├── controller/
│       │   │               │   └── AuthController.java
│       │   │               ├── model/
│       │   │               │   └── User.java
│       │   │               ├── repository/
│       │   │               │   └── UserRepository.java
│       │   │               └── service/
│       │   │                   └── UserService.java
│       │   └── resources/
│       │       ├── application.properties
│       │       └── data.sql (para dados iniciais de teste)
│       └── test/
│           └── ...
├── frontend/
│   ├── index.html
│   ├── style.css
│   └── script.js
├── deploy/
│   ├── README.md
│   └── docker-compose.yml (opcional)
└── README.md
```

Esta estrutura separa claramente o backend do frontend, facilitando o desenvolvimento e o deploy independentes. O diretório `deploy` conterá os scripts e a documentação necessária para colocar o sistema em produção.

## 6. Próximos Passos

Com este plano arquitetural em mãos, os próximos passos envolvem a configuração do ambiente de desenvolvimento e a implementação do backend, começando pela autenticação e a API. Em seguida, focaremos no desenvolvimento do frontend, integrando-o ao backend e aplicando a identidade visual da SparkWave. Finalmente, abordaremos o redirecionamento pós-login e a documentação de deploy.

Este documento será atualizado conforme o projeto avança, incorporando detalhes de implementação e decisões técnicas adicionais.


