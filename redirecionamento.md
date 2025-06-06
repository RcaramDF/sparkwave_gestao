# Implementação do Redirecionamento Pós-Login

Este documento explica como foi implementado o redirecionamento automático para a URL `https://school-finance-wizard.lovable.app/calculos` após o login bem-sucedido no sistema da SparkWave Consultoria Empresarial.

## 1. Configuração no Backend

### 1.1. Definição da URL no arquivo de configuração

A URL de redirecionamento foi definida como uma propriedade no arquivo `application.properties` do Spring Boot:

```properties
# URL de redirecionamento após login
sparkwave.app.redirect.url=https://school-finance-wizard.lovable.app/calculos
```

Esta abordagem permite alterar facilmente a URL de redirecionamento sem modificar o código-fonte, apenas atualizando o arquivo de configuração.

### 1.2. Injeção da URL no controlador de autenticação

A URL é injetada no controlador de autenticação (`AuthController`) usando a anotação `@Value` do Spring:

```java
@Value("${sparkwave.app.redirect.url}")
private String redirectUrl;
```

### 1.3. Inclusão da URL na resposta de autenticação

Quando o usuário é autenticado com sucesso, a URL de redirecionamento é incluída na resposta JSON enviada ao frontend:

```java
return ResponseEntity.ok(new JwtResponse(jwt, 
                                         user.getId(), 
                                         user.getUsername(), 
                                         user.getEmail(), 
                                         roles,
                                         redirectUrl));
```

O objeto `JwtResponse` foi modificado para incluir o campo `redirectUrl`:

```java
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private List<String> roles;
    private String redirectUrl;

    public JwtResponse(String token, Long id, String username, String email, List<String> roles, String redirectUrl) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
        this.redirectUrl = redirectUrl;
    }
}
```

## 2. Implementação no Frontend

### 2.1. Processamento da resposta de autenticação

No frontend, após receber a resposta de autenticação bem-sucedida, o JavaScript extrai a URL de redirecionamento da resposta e redireciona o navegador:

```javascript
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
}
```

Note que há um fallback para a URL hardcoded caso a propriedade `redirectUrl` não esteja presente na resposta.

## 3. Fluxo Completo do Redirecionamento

1. O usuário preenche o formulário de login e clica no botão "Entrar"
2. O frontend envia as credenciais para o endpoint `/auth/signin` do backend
3. O backend valida as credenciais e, se corretas, gera um token JWT
4. O backend inclui o token JWT e a URL de redirecionamento na resposta
5. O frontend recebe a resposta, armazena o token JWT no localStorage
6. O frontend redireciona o navegador para a URL especificada

## 4. Vantagens desta Implementação

1. **Centralização**: A URL de redirecionamento é definida em um único lugar no backend
2. **Flexibilidade**: A URL pode ser alterada sem modificar o código-fonte
3. **Segurança**: O redirecionamento só ocorre após autenticação bem-sucedida
4. **Manutenibilidade**: Fácil de atualizar ou modificar o comportamento de redirecionamento

## 5. Considerações para Produção

Em um ambiente de produção, é recomendável:

1. Configurar URLs diferentes para ambientes de desenvolvimento, teste e produção
2. Considerar a implementação de uma lista de URLs permitidas para redirecionamento
3. Implementar validação da URL de redirecionamento para evitar ataques de redirecionamento aberto
4. Registrar (log) os redirecionamentos para fins de auditoria e segurança

