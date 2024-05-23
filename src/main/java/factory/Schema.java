package factory;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Schema {

    private Connection connection;
    public static String sqlSchema;

    public Schema(Connection connection) {
        this.connection = connection;
    }

    public void generateDatabaseSchema() throws SQLException {
        StringBuilder schemaBuilder = new StringBuilder();
        DatabaseMetaData metaData = connection.getMetaData();

        ResultSet tablesResultSet = metaData.getTables(null, null, "%", new String[]{"TABLE"});
        while (tablesResultSet.next()) {
            String tableName = tablesResultSet.getString("TABLE_NAME");
            schemaBuilder.append("CREATE TABLE `").append(tableName).append("` (\n");

            ResultSet columnsResultSet = metaData.getColumns(null, null, tableName, "%");
            while (columnsResultSet.next()) {
                String columnName = columnsResultSet.getString("COLUMN_NAME");
                String columnType = columnsResultSet.getString("TYPE_NAME");
                int columnSize = columnsResultSet.getInt("COLUMN_SIZE");
                schemaBuilder.append(" `").append(columnName).append("` ").append(columnType);
                if (columnType.equalsIgnoreCase("VARCHAR") || columnType.equalsIgnoreCase("CHAR")) {
                    schemaBuilder.append("(").append(columnSize).append(")");
                }
                schemaBuilder.append(",\n");
            }
            columnsResultSet.close();

            schemaBuilder.setLength(schemaBuilder.length() - 2);
            schemaBuilder.append("\n);\n");
        }
        tablesResultSet.close();

        this.sqlSchema = schemaBuilder.toString();
    }

    public static String getSqlSchema() {
        return sqlSchema;
    }

/*
    public static void main(String[] args) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        try (Connection connection = connectionFactory.getConnection()) {
            Schema schema = new Schema(connection);
            schema.generateDatabaseSchema();

            // Usa a variável sqlSchema
            String sqlSchema = schema.getSqlSchema();
            System.out.println(sqlSchema);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
*/
}