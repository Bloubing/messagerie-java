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
			createGroup(p.srcId, buf);
		} else if (type == 2) {
			removeUser(p.srcId, buf);
		} else if (type == 3) {
			removeGroup(p.srcId, buf);
		} else if (type == 4) {
			addMember(p.srcId, buf);
		} else if (type == 5) {
			retirerUser(p.srcId, buf);
		} else if (type == 10) {
			retirerUser(p.srcId, buf);
		}
		else {
			LOG.warning("Server message of type=" + type + " not handled by procesor");
		}
	}
	


	// type1
	public void createGroup(int ownerId, ByteBuffer data) {
		int nb = data.getInt();
		GroupMsg g = server.createGroup(ownerId);
		for (int i = 0; i < nb; i++) {
			g.addMember(server.getUser(data.getInt()));
		}
	}

	//type2
	public void removeUser(int usrId, ByteBuffer data) {
		UserMsg usr = server.getUser(usrId);
		int groupId = data.getInt();
		GroupMsg group = null;
    	for (GroupMsg g : usr.getGroups()) {
      		  if (g.getId() == groupId) {
            	group = g;
            	break;
        	}
		}
		if (group != null && group.getMembers().contains(usr)) {
        group.removeMember(usr);
    	}
	}

	// type3
	public void removeGroup(int ownerId, ByteBuffer data) {
		int idGroupe = data.getInt();
	
		int nbMembres = data.getInt();
		server.removeGroup(idGroupe);
	}

	//type4
	public void addMember(int usrId, ByteBuffer data) {
		int groupIdAdd = data.getInt();       // id de groupe dans lequel on veut ajouter un membre
		int memberToAdd = data.getInt();      // id de l’utilisateur à ajouter
		UserMsg user = server.getUser(usrId); // L'utilisateur qui exécute l'action addMembre
		GroupMsg group = null;
		// On cherche le groupe ciblé parmi groups qui appartient "user"
		for (GroupMsg g : user.getGroups()) {
			if (g.getId() == groupIdAdd) {
				group = g;
				break;
			}
		}
		// Si le groupe existe et que l'utilisateur en fait partie
		if (group != null && group.getMembers().contains(user)) {
			UserMsg target = server.getUser(memberToAdd); // On récupère l'utilisateur à ajouter
			// On n’ajoute l’utilisateur que s’il n’est pas déjà membre de ce groupe
			if (!group.getMembers().contains(target)) {
				group.addMember(target);
			}
		}
	}


	//type5
	public void retirerUser(int usrId, ByteBuffer data) {
		int memberToRemoveId = data.getInt();
		int groupId = data.getInt();
		UserMsg user = server.getUser(usrId);
		UserMsg memberToRemove = server.getUser(memberToRemoveId);
		// On cherche le groupe ciblé parmi groups qui appartient "user"
		GroupMsg group = null;
		for (GroupMsg g : user.getGroups()) {
			if (g.getId() == groupId) {
				group = g;
				break;
			}
		}
		// Si le groupe existe et que l'utilisateur user est owner de ce group
		if (group != null && group.getOwner().equals(user)) {
			group.removeMember(memberToRemove);
		}
	} 


	// //type6
	//  public void setName(int usrId, ByteBuffer data){
	// 	byte[] nameBytes = new byte[data.remaining()]; //残りの長さ分のバイト配列を用意
	// 	data.get(nameBytes); //現在のポジションからnameBytes.length バイトを読み取りnameBytesに書き込み
	// 	String newName = new String(nameBytes, StandardCharsets.UTF_8); // バイト列を文字列（String）に変換
	// 	UserMsg u = server.getUser(usrId);
	// 	if (u != null) {
	// 		u.setName(newName);
	// 		System.out.println("User " + usrId + " changed name to " + newName);
	// 	}
	// }


// 	//type8
// 	public void sendGroupMembers(int usrId, ByteBuffer data) {
// 		int groupId = data.getInt();
// 		GroupMsg group = server.getGroup(groupId);
// 		if (group == null) { // si le groupe n'existe pas
// 			sendError(requesterId, "Le groupe demandé n'existe pas.");
// 			return;
// 		}
// 		List<UserMsg> members = group.getMembers(); //group というSetから、参加しているメンバーのリストを取得
// 		ByteBuffer response = ByteBuffer.allocate(1 + 4 + members.size() * 4);  //送信用のバイナリバッファを作成サイズを指定。
// 		//type+メンバー数+各ユーザーのID（int）
// 		response.put((byte)8); // ecrire type
// 		response.putInt(members.size()); // ecrirenb de membre
// 		for (UserMsg user : members) {  //ecrire userIds
// 			response.putInt(user.getId()); // si on a aussi nickname comme un attribut de UserMsg modifiez ici
// 		}
// 		Packet p = new Packet(0, usrId, response.array());
// 		UserMsg dest = server.getUser(requesterId);
// 		if (dest != null) {
// 			dest.process(p);  //usrIDを持つuserにパケットを渡す
// 		}
// 	}
// }

/* contenue de data(donnée de l'action)
* type1: 1. nb de membre
         2. ID de membre à ajouter
		 3. ID de membre à ajouter
		 .
		 . autant que le nb de membre...
		 .
* type2: 1. ID du groupe
* type3: 1. ID du groupe
         2. nb de membre
* type4: 1. ID du groupe
         2. ID de user qui va être ajouté
* type5: 1. ID de user qui sera être supprimé du groupe (pas utilisateur qui exécute l'action)
         2. ID du groupe
*/





