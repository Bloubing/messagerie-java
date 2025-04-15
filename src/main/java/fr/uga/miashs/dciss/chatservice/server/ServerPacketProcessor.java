/*
 * Copyright (c) 2024.  Jerome David. Univ. Grenoble Alpes.
 * This file is part of DcissChatService.
 *
 * DcissChatService is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * DcissChatService is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */

package fr.uga.miashs.dciss.chatservice.server;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Logger;

import fr.uga.miashs.dciss.chatservice.common.Packet;

public class ServerPacketProcessor implements PacketProcessor {
	private final static Logger LOG = Logger.getLogger(ServerPacketProcessor.class.getName());
	private ServerMsg server;

	public ServerPacketProcessor(ServerMsg s) {
		this.server = s;
	}

	@Override
	public void process(Packet p) {
		// ByteBufferVersion. On aurait pu utiliser un ByteArrayInputStream + DataInputStream à la place
		ByteBuffer buf = ByteBuffer.wrap(p.data);
		
		int type = buf.getInt();
		System.out.println("le type est"+type);
		if (type == 1) { // cas creation de groupe
			this.createGroup(p.srcId, buf);
		} else if (type == 2) {
			this.removeUser(p.srcId, buf);
		} else if (type == 3) {
			this.removeGroup(p.srcId, buf);
		} else if (type == 4) {
			this.addMember(p.srcId, buf);
		} else if (type == 5) {
			this.removeOtherMember(p.srcId, buf);
		} else if (type == 6) {
			this.renameGroup(p.srcId, buf);
		}
		else {
			LOG.warning("Server message of type=" + type + " not handled by procesor");
		}
	}
	


	// type1
	public void createGroup(int ownerId, ByteBuffer data) {
		int nbMembres = data.getInt();
		
		GroupMsg g = server.createGroup(ownerId);
		for (int i = 0; i < nbMembres; i++) {
			g.addMember(server.getUser(data.getInt()));
		}

		// Lire le nom du groupe
		StringBuffer groupNameBuffer = new StringBuffer();
		
		while (data.hasRemaining()) {
			groupNameBuffer.append(data.getChar());
		}
		String groupName = groupNameBuffer.toString();
		g.setName(groupName);
	}

	//type 2 : quitter un groupe
	public void removeUser(int usrId, ByteBuffer data) {
		UserMsg usr = server.getUser(usrId);
		int groupId = data.getInt();
		// On récupère le group, null sinon
		GroupMsg group = usr.getGroup(groupId);
		if (group != null && group.getMembers().contains(usr)) {
        group.removeMember(usr);
    	}
	}

	// type3
	public void removeGroup(int ownerId, ByteBuffer data) {
		int idGroupe = data.getInt();
		server.removeGroup(idGroupe, ownerId);
	}

	//type4
	public void addMember(int usrId, ByteBuffer data) {
		int groupIdAdd = data.getInt();       // id de groupe dans lequel on veut ajouter un membre
		int memberIdToAdd = data.getInt();      // id de l’utilisateur à ajouter
		UserMsg userAjouteur = server.getUser(usrId); // L'utilisateur qui exécute l'action addMembre
		
		// On cherche le groupe dans le serveur où il faut ajouter un nouveau membre
		// parmi les groupes de l'utilisateur qui ajoute le nouveau membre
		GroupMsg group = userAjouteur.getGroup(groupIdAdd);
			
		
		// Si le groupe existe et que l'ajouteur en fait partie
		if (group != null && group.getMembers().contains(userAjouteur)) {
			UserMsg newMember = server.getUser(memberIdToAdd); // On récupère le nouveau membre à ajouter
			// On n’ajoute le nouveau membre que s’il n’est pas déjà membre de ce groupe
			if (!group.getMembers().contains(newMember)) {
				group.addMember(newMember);
			}
		}
	}


	//type5
	public void removeOtherMember(int usrId, ByteBuffer data) {
		int groupId = data.getInt();
		int memberToRemoveId = data.getInt();
		UserMsg user = server.getUser(usrId);
		UserMsg memberToRemove = server.getUser(memberToRemoveId);
		// On cherche le groupe qui a groupID parmi tous les groupes auxquels appartient "user", null sinon
		GroupMsg group = user.getGroup(groupId);

		// Si le groupe existe et que l'utilisateur user est owner de ce group
		if (group != null && group.getOwner().getId() == usrId) {
			group.removeMember(memberToRemove);
		}
	}
	
	// type 6
	public void renameGroup(int usrId, ByteBuffer data) {
		int groupId = data.getInt();
		UserMsg user = server.getUser(usrId);
		GroupMsg group = user.getGroup(groupId);
		if (group != null) {
			//TODO readname
			group.setName(null);
		}
	}



}






