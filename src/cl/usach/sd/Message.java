package cl.usach.sd;

/**
 * Clase la cual vamos a utilizar para enviar datos de un Peer a otro
 */
public class Message {
	private int value;
	private int destination;
	
	
	private String mensaje;
	//quien envía el mensaje
	private long sender;
	
	public Message(int value,  int destination) {

		this.setValue(value);
		this.setDestination(destination);
	}
	
	public Message(String mensaje, long sender){
		
		setMensaje(mensaje);
		setSender(sender);
	}

	public int getDestination() {
		return destination;
	}

	public void setDestination(int destination) {
		this.destination = destination;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
	public void setMensaje(String mensaje){this.mensaje=mensaje;}
	public String getMensaje(){return mensaje;}
	
	public void setSender(long sender){this.sender=sender;}
	public long getSender(){return sender;}
}
