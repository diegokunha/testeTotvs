-- Create the 'conta' table
CREATE TABLE conta (
    id SERIAL PRIMARY KEY,
    data_vencimento DATE NOT NULL,
    data_pagamento DATE,
    valor DECIMAL(15, 2) NOT NULL,
    descricao VARCHAR(255),
    situacao VARCHAR(50)
);
