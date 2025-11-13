import psycopg2
import csv
import os
import sys

# --- Configuration de la Base de Données (Lit les variables d'environnement) ---
# UTILISATEUR = postgres, MDP = bench, HOTE = localhost, PORT = 5433 (valeurs par défaut)
DB_NAME = "bench"
DB_USER = os.getenv("DB_USER", "postgres")
DB_PASS = os.getenv("DB_PASS", "bench")
DB_HOST = os.getenv("DB_HOST", "localhost")
DB_PORT = os.getenv("DB_PORT", "5433")

def extract_ids():
    """Extrait les IDs des tables Item et Category vers des fichiers CSV."""
    conn = None

    # Chemin où JMeter attend les fichiers (relatif à la racine du projet)
    JMETER_DATA_PATH = os.path.join(os.path.dirname(__file__), 'jmeter', 'data')

    # Créer le répertoire de destination si nécessaire
    os.makedirs(JMETER_DATA_PATH, exist_ok=True)

    try:
        # 1. Connexion
        print(f"Tentative de connexion à {DB_HOST}:{DB_PORT}...")
        conn = psycopg2.connect(
            dbname=DB_NAME, user=DB_USER, password=DB_PASS, host=DB_HOST, port=DB_PORT
        )
        cursor = conn.cursor()
        print("Connexion réussie. Démarrage de l'extraction des IDs...")

        # Tables à exporter
        tables = [('item', 'item.csv', 100000), ('category', 'category.csv', 2000)]

        for table_name, file_name, expected_count in tables:

            sql = f"SELECT id FROM {table_name} ORDER BY id;"
            cursor.execute(sql)

            ids = [str(row[0]) for row in cursor.fetchall()]

            if not ids:
                print(f"AVERTISSEMENT : Aucune donnée trouvée dans la table {table_name}.")
                continue

            output_file_path = os.path.join(JMETER_DATA_PATH, file_name)

            with open(output_file_path, 'w', newline='') as f:
                writer = csv.writer(f)
                for id in ids:
                    writer.writerow([id])

            print(f"Fichier {file_name} créé : {len(ids)} IDs extraits (attendu : {expected_count}).")

    except Exception as e:
        print(f"\nFATAL: Erreur lors de l'extraction des IDs.")
        print(f"Détails de l'erreur : {e}", file=sys.stderr)

    finally:
        if conn:
            conn.close()

if __name__ == '__main__':
    extract_ids()