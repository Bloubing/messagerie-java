package fr.uga.miashs.dciss.chatservice.common;

public class MessageGroupe extends Message {
private int groupId;
	public MessageGroupe(int id, int srcId, int destId, int groupId, String message) {
		super(id, srcId, destId, message);
		this.groupId = groupId;
	}

}
