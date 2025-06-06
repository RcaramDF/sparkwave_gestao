-- Inserir usuários de teste
-- Senha: admin123 (codificada com BCrypt)
INSERT INTO users (username, email, password, full_name, is_active) 
VALUES ('admin', 'admin@sparkwave.com', '$2a$10$yfB0rUXWZ5.1.Ry/6zMYAOXJ.Y1YHENoIZFW1Mxt9Xr4qIGKzGFfO', 'Administrador SparkWave', true);

-- Senha: user123 (codificada com BCrypt)
INSERT INTO users (username, email, password, full_name, is_active) 
VALUES ('cliente1', 'cliente1@exemplo.com', '$2a$10$3NOQGdYwXFU3WIeELhKj7eUbONBHtbONGgPIx1pybRWO1HWcJn5e6', 'Cliente Exemplo', true);

-- Inserir roles para os usuários
INSERT INTO user_roles (user_id, role) VALUES (1, 'ADMIN');
INSERT INTO user_roles (user_id, role) VALUES (2, 'USER');

