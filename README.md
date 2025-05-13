# SnackStack

Brown CS0320 Group Final Project: an application that helps users track inventory and recommend recipes

## Backend Setup

1. Run database migration: cd server && mvn compile exec:java -Dexec.mainClass="com.snackstack.server.config.RunMigration"
2. Start the server: mvn clean compile && mvn exec:java -Dexec.mainClass="com.snackstack.server.Server"
