CREATE TABLE users (
    cpf VARCHAR(11)  UNIQUE PRIMARY KEY,
    name VARCHAR(255)  UNIQUE NOT NULL,
    role VARCHAR(20) NOT NULL,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE clients (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    image VARCHAR(255),
    cpf VARCHAR(11),
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_client_user FOREIGN KEY (cpf) REFERENCES "users" (cpf)
);

CREATE TABLE contacts (
    id SERIAL PRIMARY KEY,
    client_id INT NOT NULL,
    tag VARCHAR(50),
    type VARCHAR(20) NOT NULL,
    value VARCHAR(255) NOT NULL,
    CONSTRAINT fk_contact_client FOREIGN KEY (client_id) REFERENCES clients (id)
);

CREATE TABLE address (
    id SERIAL PRIMARY KEY,
    client_id INT NOT NULL,
    street VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    neighborhood VARCHAR(100),
    complement VARCHAR(255),
    tag VARCHAR(50),
    CONSTRAINT fk_address_client FOREIGN KEY (client_id) REFERENCES clients (id)
);

CREATE TABLE breeds (
    id SERIAL PRIMARY KEY,
    description VARCHAR(255) NOT NULL
);

CREATE TABLE pets (
    id SERIAL PRIMARY KEY,
    client_id INT NOT NULL,
    breed_id INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    image VARCHAR(255),
    birth_date DATE,
    CONSTRAINT fk_pets_client FOREIGN KEY (client_id) REFERENCES clients (id),
    CONSTRAINT fk_pets_breed FOREIGN KEY (breed_id) REFERENCES breeds (id)
);

CREATE TABLE appointments (
    id SERIAL PRIMARY KEY,
    pet_id INT NOT NULL,
    description TEXT NOT NULL,
    cost DECIMAL(10, 2) NOT NULL,
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_appointment_pet FOREIGN KEY (pet_id) REFERENCES pets (id)
);
