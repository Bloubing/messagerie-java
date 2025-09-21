# Messagerie Java

## Mise en place

### Pré-requis

- Java 11+
- Maven

### Installation

1.  Cloner le dépôt
```sh
git clone 
```
2. Compiler le projet
```sh
mvn package
```
3. Nettoyer le projet
```sh
mvn clean
```

## Utilisation

Lancer le serveur : 
```sh
mvn package exec:java -Dexec.mainClass="fr.uga.miashs.dciss.chatservice.server.ServerMsg"
```
ou (mvn package est requis pour recompiler si des changements ont été effectués)
```sh
java -cp target/chatservice-0.0.1-SNAPSHOT-jar-with-dependencies.jar fr.uga.miashs.dciss.chatservice.server.ServerMsg
```

Lancer le client :
```sh
mvn package exec:java -Dexec.mainClass="fr.uga.miashs.dciss.chatservice.client.ClientMsg"
```
ou (mvn package est requis pour recompiler si des changements ont été effectués)
```sh
java -cp target/chatservice-0.0.1-SNAPSHOT-jar-with-dependencies.jar fr.uga.miashs.dciss.chatservice.client.ClientMsg
```

## Licence

Distribuée sous la licence MIT. Voir `LICENSE.txt` pour plus d'informations.




