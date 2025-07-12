# E-commerce Java Swing

Application de gestion de stock et de commandes pour un magasin, réalisée en Java (Swing) avec une base de données MySQL et Maven.

## Fonctionnalités
- Gestion des produits, clients, fournisseurs, utilisateurs et commandes
- Interface graphique (Swing)
- Authentification (Admin, Vendeur)

## Prérequis
- Java 8 ou supérieur
- Maven
- MySQL

## Installation
1. **Cloner le projet**
   ```sh
   git clone <votre-url-github>
   ```
2. **Configurer la base de données**
   - Importez le fichier `db.sql` dans votre MySQL :
     ```sh
     mysql -u root -p < ../db.sql
     ```
   - Vérifiez le port et les identifiants dans `src/main/java/com/ecommerce/utils/DBConnection.java`

3. **Compiler et lancer l'application**
   ```sh
   mvn clean package
   mvn exec:java -Dexec.mainClass="com.ecommerce.MainApp"
   ```

## Connexion
- Utilisateurs par défaut (voir `db.sql`) :
  - Admin : othmane/othmane, youssef/youssef, zineb/zineb
  - Vendeur : vendeur1/vendeur1

## Structure du projet
- `src/main/java/com/ecommerce/` : code source (MVC)
- `db.sql` : script de création et peuplement de la base
- `pom.xml` : configuration Maven

---
© 2024 - Projet Java E-commerce 