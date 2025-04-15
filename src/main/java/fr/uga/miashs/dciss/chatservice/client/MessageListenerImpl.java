package fr.uga.miashs.dciss.chatservice.client;

import fr.uga.miashs.dciss.chatservice.common.Packet;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MessageListenerImpl implements MessageListener {


// inplement interface messageListener.java
// messageListener dedité à lire un ficher chez un destinateur
// convert packet enrit en byte →　fichieｒ

@Override
public void messageReceived(Packet p) {
    if (p.data == null || p.data.length == 0) {
        System.out.println("Paquet vide reçu.");
            return;
        }

        byte type = p.data[0];

        if (type == 10) { // Transfert de fichier
            try {
                ByteBuffer buf = ByteBuffer.wrap(p.data);
                buf.get();     // sauter le type d’action

                int nameLen = buf.getInt();
                byte[] nameBytes = new byte[nameLen];
                buf.get(nameBytes);
                String fileName = new String(nameBytes, StandardCharsets.UTF_8);

                int fileLen = buf.getInt();
                byte[] fileContent = new byte[fileLen];
                buf.get(fileContent);

                Files.write(Paths.get("reçu_" + fileName), fileContent);
                System.out.println("Fichier reçu de " + p.srcId + " : " + fileName + " (" + fileLen + " octets)");

            } catch (IOException | IndexOutOfBoundsException e) {
                System.err.println("Erreur lors de la réception du fichier : " + e.getMessage());
            }
        } else {
            // Message texte
            String msg = new String(p.data, StandardCharsets.UTF_8);
            System.out.println("Message reçu de " + p.srcId + " : " + msg);
        }
    }
}