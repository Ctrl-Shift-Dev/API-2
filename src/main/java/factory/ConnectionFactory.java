package factory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionFactory {
    private static final Logger LOGGER = Logger.getLogger(ConnectionFactory.class.getName());
    private final String databaseType;

    public ConnectionFactory(String databaseType) {
        if (databaseType == null || databaseType.isEmpty()) {
            throw new IllegalArgumentException("Tipo de banco de dados não pode ser nulo ou vazio.");
        }
        this.databaseType = databaseType;
    }

    public Connection getConnection() {
        String url;
        String user = "root";
        String password = "Mysql2002@";

        url = switch (databaseType) {
            case "Livros" -> "jdbc:mysql://localhost:3306/livros";
            case "Esportes" -> "jdbc:mysql://localhost:3306/esportes";
            case "Viagens" -> "jdbc:mysql://localhost:3306/viagens";
            default -> throw new IllegalArgumentException("Banco de dados desconhecido: " + databaseType);
        };

        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
            LOGGER.log(Level.INFO, "Conexão com o banco de dados " + databaseType + " estabelecida com sucesso.");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao estabelecer conexão com o banco de dados " + databaseType, e);
        }
        return conn;
    }
}
