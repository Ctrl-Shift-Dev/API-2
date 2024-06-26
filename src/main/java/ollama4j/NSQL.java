package ollama4j;

import factory.ConnectionFactory;
import factory.Schema;
import io.github.amithkoujalgi.ollama4j.core.OllamaAPI;
import io.github.amithkoujalgi.ollama4j.core.exceptions.OllamaBaseException;
import io.github.amithkoujalgi.ollama4j.core.models.OllamaResult;
import io.github.amithkoujalgi.ollama4j.core.utils.OptionsBuilder;
import io.github.amithkoujalgi.ollama4j.core.utils.PromptBuilder;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class NSQL {

    private String request;
    private final String databaseType;
    private final String aiType;

    public NSQL(String databaseType, String aiType) {
        this.databaseType = databaseType;
        this.aiType = aiType;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getRequest() {
        return request;
    }

    public String aiAnswer() throws OllamaBaseException, IOException, InterruptedException, SQLException {
        String host = "http://localhost:11434/";

        OllamaAPI ollamaAPI = new OllamaAPI(host);
        ollamaAPI.setRequestTimeoutSeconds(100000);

        try (Connection connection = new ConnectionFactory(databaseType).getConnection()) {
            Schema schema = new Schema(connection);
            schema.generateDatabaseSchema();

            String sqlSchema = Schema.getSqlSchema();
            String modelName = getModelNameFromString(aiType);

            PromptBuilder promptBuilder = new PromptBuilder()
                    .addLine("/set system \"\"\"Here is the database schema that the SQL query will run on:")
                    .addSeparator()
                    .addLine("```sql")
                    .addLine(sqlSchema)
                    .addLine("```")
                    .addLine("Generate a SQL query that answers the question: {" + getRequest() + "}")
                    .addLine("Only use the tables and columns provided in the schema.")
                    .addLine("After generating the SQL query, convert it into a natural language answer.")
                    .addSeparator();

            OllamaResult result = ollamaAPI.generate(modelName, promptBuilder.build(), new OptionsBuilder().build());

            return result.getResponse();
        }
    }

    private String getModelNameFromString(String aiType) {
        return switch (aiType.toUpperCase()) {
            case "NSQL", "SQLCODER" -> "duckdb-nsql";
            default -> throw new IllegalArgumentException("Tipo de IA não reconhecido: " + aiType);
        };
    }

    @Override
    public String toString() {
        try {
            return aiAnswer();
        } catch (OllamaBaseException | InterruptedException | IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
