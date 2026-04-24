import requests
import time

# Configuration locale
BASE_URL = "http://localhost:8081"
ADMIN_USER = "admin"
ADMIN_PASS = "admin123"
COUNT = 1000  # Nombre de jeux à envoyer pour le test

def stress_test():
    try:
        # 1. Connexion pour récupérer le Token JWT
        print(f"🔑 Connexion en tant que {ADMIN_USER}...")
        auth_resp = requests.post(f"{BASE_URL}/auth/login", json={
            "username": ADMIN_USER,
            "password": ADMIN_PASS
        })
        auth_resp.raise_for_status()
        token = auth_resp.json()["token"]
        headers = {"Authorization": f"Bearer {token}"}

        print(f"🚀 Envoi de {COUNT} jeux vers la pipeline Kafka...")
        start_time = time.time()

        for i in range(COUNT):
            game = {
                "gameId": f"STRESS-TEST-{i}-{int(time.time())}",
                "title": f"Stress Test Game #{i}",
                "releaseYear": 2024,
                "genres": ["Test"],
                "platforms": ["Kafka"],
                "publisher": "Stress Tester",
                "description": "Validation de la montée en charge asynchrone."
            }
            # Envoi vers l'endpoint qui utilise Kafka (enqueueGame)
            requests.post(f"{BASE_URL}/partner/games", json=game, headers=headers)
            
            if (i + 1) % 100 == 0:
                print(f"✅ {i + 1} jeux envoyés...")

        end_time = time.time()
        print(f"\n✨ Succès ! {COUNT} messages envoyés en {end_time - start_time:.2f} secondes.")
        print("\n💡 OBSERVATION POUR LA DÉMO :")
        print("1. Le script s'est fini très vite car l'API répond immédiatement (202 Accepted).")
        print("2. Regardez les logs du backend (docker compose logs -f backend) : il continue de travailler !")
        print("3. Regardez Kafka UI : le topic 'game-ingestion-topic' s'est rempli instantanément.")

    except Exception as e:
        print(f"❌ Erreur : {e}")
        print("Assurez-vous que le backend est lancé sur http://localhost:8081")

if __name__ == "__main__":
    stress_test()
