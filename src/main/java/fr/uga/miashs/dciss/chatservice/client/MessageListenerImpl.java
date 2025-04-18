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
    System.out.println("rentré dans messageListener Impl");
    if (p.data == null || p.data.length == 0) {
        System.out.println("Paquet vide reçu.");
            return;
        }

        
        ByteBuffer buf = ByteBuffer.wrap(p.data);

        if (p.destId < 0) {
            // Si un nom de groupe est présent ->  ignorer pour commencer par type
            byte nameLen = buf.get();
            buf.position(buf.position() + nameLen * 2);
        }

        int type = buf.getInt();
        System.out.println("Type détecté =" + type);
        if (type == 10) { // Transfert de fichier
            System.out.println("mon type est 10");
            try {
                
                byte nameLen = buf.get();
                byte[] nameBytes = new byte[nameLen];
                System.out.println("atached file length: " + nameLen);
                buf.get(nameBytes);
                String fileNameString = new String(nameBytes, StandardCharsets.UTF_8);
                System.out.println("atached file name: " + fileNameString);
                byte[] fileContent = new byte[buf.remaining()];
                int i  = 0;
                while(buf.hasRemaining()) {
                    fileContent[i] = buf.get();
                    i +=1;
                }

                Files.write(Paths.get("reçu_" + fileNameString), fileContent);
                System.out.println("Fichier reçu de " + p.srcId + " : " + fileNameString);

            } catch (IOException | IndexOutOfBoundsException e) {
                System.err.println("Erreur lors de la réception du fichier : " + e.getMessage());
            }
        }
    }
}
