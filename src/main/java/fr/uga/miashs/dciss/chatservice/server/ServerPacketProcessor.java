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
	
	public String readGroupNameFromData(ByteBuffer data) {
		// Lire le nom du groupe
		StringBuffer groupNameBuffer = new StringBuffer();

		while (data.hasRemaining()) {
			groupNameBuffer.append(data.getChar());
		}
		String groupName = groupNameBuffer.toString();

		return groupName;
	}
	
	public String readGroupNameFromData(ByteBuffer data, int length) {
		// Lire le nom du groupe
		StringBuffer groupNameBuffer = new StringBuffer();

		for (int i = 0; i < length; i++) {
			groupNameBuffer.append(data.getChar());
		}
		String groupName = groupNameBuffer.toString();
		
		return groupName;
	}

	// type 1 : créer un groupe
	public void createGroup(int ownerId, ByteBuffer data) {
		int nbMembres = data.getInt();

		// Lire tous les ids des membres
		GroupMsg g = server.createGroup(ownerId);
		for (int i = 0; i < nbMembres; i++) {
			g.addMember(server.getUser(data.getInt()));
		}
		
		String groupNameRead = this.readGroupNameFromData(data);
		g.setName(groupNameRead);
		
		LOG.info("Groupe créé avec le nom de" + g.getName());
	}
	
	//type 2 : quitter un groupe
	public void removeUser(int userId, ByteBuffer data) {
		System.out.println("rentré dans type2");
		UserMsg user = server.getUser(userId);
		String groupNameRead = this.readGroupNameFromData(data);
		// On cherche le groupe à quitter parmi 
		// les groupes auxquels appartient l'user
		// Si non trouvé : groupe == null
		GroupMsg group = user.getGroup(groupNameRead);

		// Si le groupe existe
		if (group != null) {
			group.removeMember(user);
			LOG.info("User: " + userId + "a quitté le groupe " + group.getName());
		}
	}

	// type 3 : supprimer un groupe
	public void removeGroup(int ownerId, ByteBuffer data) {
		String groupNameRead = this.readGroupNameFromData(data);
		UserMsg ownerUser = server.getUser(ownerId);
		int groupId = ownerUser.getGroup(groupNameRead).getId();

		// On cherche le groupe à supprimer parmi 
		// les groupes auxquels appartient l'user
		// Si non trouvé : groupe == null
		GroupMsg group = ownerUser.getGroup(groupNameRead);

		// Si le groupe existe
		// et que l'utilisateur est owner
		if (group != null && group.getOwner().getId() == ownerId) {
			server.removeGroup(groupId, ownerId);
			LOG.info("Le groupe" + group.getName() + " a été supprimé");

		}


	}

	//type 4 : ajouter un membre
	public void addMember(int userId, ByteBuffer data) {
		int memberIdToAdd = data.getInt();      // id du membre à ajouter
		UserMsg userAjouteur = server.getUser(userId); // L'utilisateur qui exécute l'action addMembre
		String groupNameRead = this.readGroupNameFromData(data);

		// On cherche le groupe où il faut ajouter un nouveau membre
		// parmi les groupes de l'utilisateur qui ajoute le nouveau membre
		// Si pas trouvé : group == null
		GroupMsg group = userAjouteur.getGroup(groupNameRead);
			
		
		// Si le groupe existe
		if (group != null) {
			UserMsg newMember = server.getUser(memberIdToAdd); // On récupère le nouveau membre à ajouter
			// On n’ajoute le nouveau membre que s’il n’est pas déjà membre de ce groupe
			if (!group.getMembers().contains(newMember)) {
				group.addMember(newMember);
			LOG.info("Membre: " + memberIdToAdd + "a été ajouté au groupe " + group.getName() + " par " + userId);

			}
		}

	}


	// type 5 : retirer un membre
	public void removeOtherMember(int ownerId, ByteBuffer data) {
		// Lecture du paquet
		int memberToRemoveId = data.getInt();
		String groupNameRead = this.readGroupNameFromData(data);

		UserMsg user = server.getUser(ownerId);
		UserMsg memberToRemove = server.getUser(memberToRemoveId);

		// On cherche le groupe qui a groupe ID parmi tous les groupes auxquels appartient "user", null sinon
		GroupMsg group = user.getGroup(groupNameRead);

		// Si le groupe existe et que l'utilisateur user est owner de ce groupe
		if (group != null && group.getOwner().getId() == ownerId) {
			group.removeMember(memberToRemove);
			LOG.info("User: " + memberToRemoveId + "a été retiré du groupe " + group.getName() + " par " + ownerId);
		}
	}
	
	// type 6
	public void renameGroup(int userId, ByteBuffer data) {
		// Lecture du paquet
		int lengthGroupNameToChange = data.get();
		String groupNameToChangeRead = this.readGroupNameFromData(data, lengthGroupNameToChange);
		String newGroupNameRead = this.readGroupNameFromData(data);

		UserMsg user = server.getUser(userId);
		// On tente de récupérer le groupe à partir des
		// groupes auxquels appartient le user
		// => pour renommer un groupe, l'user doit faire partie
		// du groupe 
		// Si pas trouvé groupe == null
		GroupMsg group = user.getGroup(groupNameToChangeRead);
		if (group != null) {
			// Le groupe à modifier existe
			// On modifie son nom
			group.setName(newGroupNameRead);
			LOG.info("User: " + userId + "a renommé le groupe " + groupNameToChangeRead + "en " + group.getName());

		}
	}

}





