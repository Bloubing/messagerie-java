public class Message implements Comparable<Message> {
	private int id;
	private int srcId;
	private int destId;
	private String message;

	public Message(int id, int srcId, int destId, String message) {
		this.id = id;
		this.srcId = srcId;
		this.destId = destId;
		this.message = message;
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public int compareTo(Message o) {
		// TODO Auto-generated method stub
		return ((Message) o).getId() - this.getId();
	}

	public int getId() {
		return id;
	}

	public int getSrcId() {
		return srcId;
	}

	public int getDestId() {
		return destId;
	}

	public String getMessage() {
		return message;
	}
	

}