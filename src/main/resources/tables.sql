-- Tabla para almacenar información general de cada tabla (table1, table2, etc.)
CREATE TABLE table_info (
                            id INT AUTO_INCREMENT PRIMARY KEY,
                            table_name VARCHAR(50) NOT NULL,
                            timestamp BIGINT NOT NULL,
                            country VARCHAR(100) NOT NULL
);

-- Tabla para almacenar las tendencias específicas de cada tabla
CREATE TABLE trends (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        table_info_id INT NOT NULL,
                        trend_id VARCHAR(10) NOT NULL, -- Identificador del trend (t1, t2, etc.)
                        name VARCHAR(255) NOT NULL, -- Nombre decodificado del trend
                        count INT NOT NULL, -- Número de menciones
                        raw_name VARCHAR(255) NOT NULL, -- Nombre en bruto (codificado)
                        FOREIGN KEY (table_info_id) REFERENCES table_info(id) ON DELETE CASCADE
);

-- Tabla para almacenar información global de tendencias
CREATE TABLE world_trends_info (
                                   id INT AUTO_INCREMENT PRIMARY KEY,
                                   timestamp BIGINT NOT NULL,
                                   country VARCHAR(100) NOT NULL
);

-- Tabla para almacenar las tendencias globales
CREATE TABLE world_trends (
                              id INT AUTO_INCREMENT PRIMARY KEY,
                              world_trends_info_id INT NOT NULL,
                              trend_id VARCHAR(10) NOT NULL, -- Identificador del trend global (t1, t2, etc.)
                              name VARCHAR(255) NOT NULL, -- Nombre decodificado del trend
                              FOREIGN KEY (world_trends_info_id) REFERENCES world_trends_info(id) ON DELETE CASCADE
);
