package ma.projet.util;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class IdExtractor {

    // Lit les variables d'environnement (ou utilise les valeurs par défaut)
    private static final String DB_HOST = System.getenv("DB_HOST") != null ? System.getenv("DB_HOST") : "localhost";
    private static final String DB_PORT = System.getenv("DB_PORT") != null ? System.getenv("DB_PORT") : "5433";
    private static final String DB_USER = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "postgres";
    private static final String DB_PASS = System.getenv("DB_PASS") != null ? System.getenv("DB_PASS") : "bench";
    private static final String DB_NAME = "bench";

    private static final String JDBC_URL = String.format("jdbc:postgresql://%s:%s/%s", DB_HOST, DB_PORT, DB_NAME);
    private static final String JMETER_DATA_PATH = "jmeter/data/";

    public static void main(String[] args) {

        // Assurez-vous que le répertoire de sortie existe
        new java.io.File(JMETER_DATA_PATH).mkdirs();

        try {
            // Force le chargement du driver pour les anciennes versions, mais est souvent inutile avec Java 17+
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("FATAL: Driver PostgreSQL introuvable.");
            return;
        }

        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS)) {
            System.out.println("Connexion réussie. Démarrage de l'extraction des IDs...");

            // 1. Extraction et écriture des Item IDs
            extractAndWriteIds(conn, "item", "item_ids.csv");

            // 2. Extraction et écriture des Category IDs
            extractAndWriteIds(conn, "category", "category_ids.csv");

        } catch (SQLException e) {
            System.err.println("FATAL: Erreur SQL/Connexion. Vérifiez le statut de PostgreSQL.");
            System.err.println("Détails de l'erreur : " + e.getMessage());
        }
    }

    private static void extractAndWriteIds(Connection conn, String tableName, String fileName) throws SQLException {
        String sql = "SELECT id FROM " + tableName + " ORDER BY id;";
        List<Long> ids = new ArrayList<>();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ids.add(rs.getLong("id"));
            }

            if (ids.isEmpty()) {
                System.out.println("AVERTISSEMENT : Aucune donnée trouvée dans la table " + tableName + ". Le peuplement a-t-il réussi ?");
                return;
            }

            try (FileWriter writer = new FileWriter(JMETER_DATA_PATH + fileName)) {
                for (Long id : ids) {
                    writer.append(String.valueOf(id));
                    writer.append('\n'); // Nouvelle ligne pour chaque ID
                }
                writer.flush();
                System.out.println(String.format("Fichier %s créé dans %s avec %d IDs.", fileName, JMETER_DATA_PATH, ids.size()));

            } catch (IOException e) {
                System.err.println("Erreur d'écriture du fichier CSV : " + e.getMessage());
            }

        }
    }
}