package cl.usach.sd;

import peersim.config.Configuration;
import peersim.config.FastConfig;
import peersim.core.CommonState;
import peersim.core.Linkable;
import peersim.core.Network;
import peersim.core.Node;
import peersim.dynamics.WireKOut;
import peersim.edsim.EDProtocol;
import peersim.transport.Transport;

public class Layer implements Cloneable, EDProtocol {
	private static final String PAR_TRANSPORT = "transport";
	private static String prefix = null;
	private int transportId;
	private int layerId;

	/**
	 * M�todo en el cual se va a procesar el mensaje que ha llegado al Nodo
	 * desde otro Nodo. Cabe destacar que el mensaje va a ser el evento descrito
	 * en la clase, a trav�s de la simulaci�n de eventos discretos.
	 */
	@Override
	public void processEvent(Node myNode, int layerId, Object event) {
		/**Este metodo trabajar� sobre el mensaje*/
		/**A modo de ejemplo, elegiremos cualquier nodo, y a ese nodo le enviaremos el mensaje 
		 * con las siguientes condiciones de ejemplo: 
		 * Si el nodo actual es del tipo 0: suma 1 a su valor
		 * Si el nodo actual es del tipo 1: resta 1 a su valor
		 * */	
		Message message = (Message) event;
		//Imprimie mensaje
		System.out.println(message.getMensaje());
		

		sendmessage(myNode, layerId, message);
		getStats();
	}

	private void getStats() {
		Observer.message.add(1);
	}

	public void sendmessage(Node currentNode, int layerId, Object message) {
		/**Con este m�todo se enviar� el mensaje de un nodo a otro
		 * CurrentNode, es el nodo actual
		 * message, es el mensaje que viene como objeto, por lo cual se debe trabajar sobre �l
		 */
		
		// Castear mensaje
		Message mensaje = (Message)message;
		// Casteo de nodo
		ExampleNode emisor = (ExampleNode)currentNode;
		
		// Random que decide que hará el nodo
		int accion = CommonState.r.nextInt(2);
		
		// Nodo Receptor
		int receptor;
		
		// Verifica resultado
		int resultado;
		switch(accion){
		// Acciones publicador
		case 0:
			int subaccion = CommonState.r.nextInt(5);
			switch(subaccion){
			// Registrarse en un nuevo tópico
			case 0:
				System.out.println("\nRegistrare a nuevo tópico");
				// Nuevo nodo receptor y topico
				receptor =  (int)Network.get(emisor.creaTopico((int)emisor.getID())).getID();
				((Transport) emisor.getProtocol(transportId)).send(emisor, Network.get(receptor), message, layerId);
				
				break;
			// Registrarse en un topico existente	
			case 1:
				System.out.println("\nRegistrare en tópico existente");
				resultado = (int)emisor.registraTopico((int)emisor.getID());
				if(resultado<0){
					// mensaje de error desde ExampleNode
				}else{
					receptor = resultado;
					((Transport)emisor.getProtocol(transportId)).send(emisor,Network.get(receptor),message,layerId);
				}
				
				break;
			// Publicar en un tópico
			case 2:
				System.out.println("\nPublicar en tópico");
				break;
			// Borrar contenido
			case 3:
				System.out.println("\nBorrar contenido");
				break;
			// Desinscribirse
			case 4:
				System.out.println("\nDesinscribirse como publicador");
				break;
			default:
				break;
			}
			break;
		//Acciones suscriber	
		case 1:
			int subaction = CommonState.r.nextInt(3);
			switch(subaction){
			// Registrarse en un tópico
			case 0:
				System.out.println("\nRegistrare como suscriptor");
				resultado = (int)emisor.suscribirseATopico((int)emisor.getID());
				if(resultado<0){
					// mensaje de error desde ExampleNode
				}else{
					receptor = resultado;
					((Transport)emisor.getProtocol(transportId)).send(emisor,Network.get(receptor),message,layerId);
				}
				break;
			// Cancelar registro 
			case 1:
				System.out.println("\nDesinscribirse como suscriptor");
				break;
			// Request update
			case 2:
				System.out.println("\nRequest update");
				break;
			default:
				System.out.println("\nRegistrare como suscriptor");
				break;
			}
			break;
		
		default:{
			System.out.println("Saltar acción");
		}
		}
	}

	public Layer(String prefix) {
		/**
		 * Inicialización del Nodo
		 */
		Layer.prefix = prefix;
		transportId = Configuration.getPid(prefix + "." + PAR_TRANSPORT);
		/**
		 * Siguiente capa del protocolo
		 */
		layerId = transportId + 1;
	}

	

	/**
	 * Definir Clone() para la replicacion de protocolo en nodos
	 */
	public Object clone() {
		Layer dolly = new Layer(Layer.prefix);
		return dolly;
	}
}
