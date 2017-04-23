package cl.usach.sd;

import java.util.ArrayList;

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
		//System.out.println(message.getMensaje());
		

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
		ExampleNode nodoActual = (ExampleNode)currentNode;
		
		// Random que decide que hará el nodo
		int accion = CommonState.r.nextInt(2);
		
		// Nodo Receptor
		int receptor;
		
		// Cuando nodo recibe un mensaje y el destino coincide con su ID
		if(mensaje.getDestination()==nodoActual.getID() && mensaje.getDestination()!=0){
			System.out.println("\nMensaje recibido");
			System.out.println("Nodo "+nodoActual.getID());
			switch(mensaje.getAccion()){
			// caso 0 y 1 hacen lo mismo, sólo que el 0 es cuando se crea un nuevo topico y 1 cuando ya existe
			case 0:
				System.out.println("Me inscribo como tópico para el  nodo publicador : "+mensaje.getSender());
				// Notifica que se suscribieron como publicador
				nodoActual.recibeSuscripcion((int)mensaje.getSender(), (int) nodoActual.getID());
				// No envía mensajes
				break;
			case 1:
				System.out.println("Me inscribo como tópico para el nodo publicador: "+mensaje.getSender());
				// Notifica que se suscribieron como publicador
				nodoActual.recibeSuscripcion((int)mensaje.getSender(), (int) nodoActual.getID());
				// No envía mensajes
				break;
			case 2:
				System.out.println("Me inscribo como tópico para el nodo suscriptor: "+mensaje.getSender());
				// Notifica que se agregaron como suscriptor
				nodoActual.agregarSuscriptor((int)mensaje.getSender(),(int)nodoActual.getID());
				// No envía mensajes
				break;
			case 3:
				System.out.println("Reenvíar publicación a suscriptores");
				ArrayList<Integer> suscriptores = nodoActual.topicoRecibePublicacion((int)mensaje.getSender(), (int)nodoActual.getID());
				if(suscriptores.isEmpty()){
					System.out.println("No posee suscriptores");
				}else{
					// Envía publicación a todos sus suscriptores
					//Acción 4 anuncia llegada de publicación a nodo
					mensaje.setAccion(4);
					// Se setea sender
					mensaje.setSender(nodoActual.getID());
					for(int i=0;i<suscriptores.size();i++){
						//Se obtiene suscriptor
						int suscriptor = suscriptores.get(i);
						//Se setea receptor del mensaje
						mensaje.setDestination(suscriptor);
				        //Se setea contenido del mensaje
						mensaje.setMensaje("Mensaje desde tópico "+nodoActual.getID());
						// Se envía a destino
						System.out.println("Enviando a : "+suscriptor);
						((Transport) nodoActual.getProtocol(transportId)).send(nodoActual, Network.get(suscriptor), mensaje, layerId);
					
					}
				}
				break;
			case 4:
				System.out.println("Publicación recibida");
				nodoActual.agregaPublicación((int)nodoActual.getID(), mensaje.getMensaje());
				break;
			case 5:
				System.out.println("Remover publicador");
				nodoActual.removerPublicadorDeTopico((int)mensaje.getSender(), (int)nodoActual.getID());
				break;
			case 6:
				System.out.println("Remover suscriptor");
				nodoActual.removerSuscriptor((int)nodoActual.getID(),(int) mensaje.getSender());
				break;
				
			case 7:
				System.out.println("Remover publicación");
				ArrayList<Integer> suscriptors = nodoActual.removerPublicaciónTopico((int)nodoActual.getID());
				if(suscriptors.isEmpty()){
					System.out.println("No posee suscriptores");
				
				}else{
					// Envía publicación a todos sus suscriptores
					//Acción 8 anuncia eliminación de publicación a nodo
					mensaje.setAccion(8);
					// Se setea sender
					mensaje.setSender(nodoActual.getID());
					for(int i=0;i<suscriptors.size();i++){
						//Se obtiene suscriptor
						int suscriptor = suscriptors.get(i);
						//Se setea receptor del mensaje
						mensaje.setDestination(suscriptor);
				        //Se setea contenido del mensaje
						mensaje.setMensaje("Mensaje desde tópico "+nodoActual.getID());
						// Se envía a destino
						System.out.println("Enviando notificación de borrar publicación a nodo : "+suscriptor);
						((Transport) nodoActual.getProtocol(transportId)).send(nodoActual, Network.get(suscriptor), mensaje, layerId);
					}
				}
			case 8:
				System.out.println("Publicación borrada");
				break;
			}
		}else{
		//Realizar alguna acción sin depender de un mensaje
			switch(accion){
			// Acciones publicador
			case 0:
				// Verifica resultado
				int resultado;
				int subaccion = CommonState.r.nextInt(5);
				switch(subaccion){
				// Registrarse en un nuevo tópico
				case 0:
					System.out.println("\nRegistrar nuevo tópico");
					// Nuevo nodo receptor y topico
					receptor =  (int)Network.get(nodoActual.creaTopico((int)nodoActual.getID())).getID();
					// Se envía a receptor
					mensaje.setDestination(receptor);
					// Acción 0 indicará a receptor que es un nuevo tópico
					mensaje.setAccion(0);
					//Envía mensaje a nodo agregado
					((Transport) nodoActual.getProtocol(transportId)).send(nodoActual, Network.get(receptor), mensaje, layerId);
					
					break;
				// Registrarse en un topico existente	
				case 1:
					System.out.println("\nRegistrare en tópico existente");
					resultado = (int)nodoActual.registraTopico((int)nodoActual.getID());
					if(resultado<0){
						// mensaje de error desde ExampleNode
					}else{
						receptor = resultado;
						// Se envía a receptor
						mensaje.setDestination(receptor);
						// Acción 1 indicará a receptor que se agrega el tópico
						mensaje.setAccion(1);
						// si nodo no esta añadido le envía un mensaje
						((Transport)nodoActual.getProtocol(transportId)).send(nodoActual,Network.get(receptor),mensaje,layerId);
					} 
					
					break;
				// Publicar en un tópico
				case 2:
					System.out.println("\nPublicar en tópico");
					int topico = nodoActual.publicarEnTopico((int)nodoActual.getID());
					if(topico<0){
						// mensaje de error desde ExampleNode
					}else{
						// Se envía a topico
						mensaje.setDestination(topico);
						// Acción 3 indicará a topico la llegada de publicación
						mensaje.setAccion(3);
						// Se envía mensaje a tópico
						((Transport)nodoActual.getProtocol(transportId)).send(nodoActual, Network.get(topico), mensaje, layerId);
					}
					
					break;
				// Borrar contenido
				case 3:
					System.out.println("\nBorrar contenido");
					// Se obtiene topico
					int topic = nodoActual.borrarContenido((int)nodoActual.getID());
					if(topic<0){
						//mensaje de error desde ExampleNode
					}else{
						//Se envía a tópico
						mensaje.setDestination(topic);
						//Acción 7 indicará a topico la notifcacion de eliminación de contenido
						mensaje.setAccion(7);
						//Se envía mensaje
						((Transport)nodoActual.getProtocol(transportId)).send(nodoActual, Network.get(topic), mensaje, layerId);
					}
					
					break;
				// Desinscribirse
				case 4:
					System.out.println("\nDesinscribirse como publicador");
					//Obtiene tópico a remover
					int topicoARemover = nodoActual.desInscribeComoPublicador((int)nodoActual.getID());
					if(topicoARemover<0){
						// mensaje de error desde ExampleNode
					}else{
						// Envía mensaje 
						mensaje.setDestination(topicoARemover);
						//mensaje 5 indica que se removera tópico
						mensaje.setAccion(5);
						// Luego debe enviar mensaje a tópico para notificar la eliminación
						((Transport)nodoActual.getProtocol(transportId)).send(nodoActual, Network.get(topicoARemover), mensaje, layerId);
					}
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
					System.out.println("\nRegistrarse como suscriptor");
					resultado = (int)nodoActual.suscribirseATopico((int)nodoActual.getID());
					if(resultado<0){
						// mensaje de error desde ExampleNode
					}else{
						receptor = resultado;
						// Se envía a receptor
						mensaje.setDestination(receptor);
						// Acción 2 indicará a receptor que se suscriben a su contenido
						mensaje.setAccion(2);
						
						((Transport)nodoActual.getProtocol(transportId)).send(nodoActual,Network.get(receptor),mensaje,layerId);
					}
					break;
				// Cancelar registro 
				case 1:
					System.out.println("\nDesinscribirse como suscriptor");
					int topico = nodoActual.desinscribirseComoSuscriptor((int)nodoActual.getID());
					if(topico<0){
						// Mensaje de error desde ExampleNode
					}else{
						// se avisa a topico
						mensaje.setDestination(topico);
						// Acción 6 indica que debe borrar suscriptor
						mensaje.setAccion(6);
						//Envía mensaje
						((Transport)nodoActual.getProtocol(transportId)).send(nodoActual,Network.get(topico),mensaje,layerId);
					}
					break;
				// Request update
				case 2:
					System.out.println("\nRequest update");
					nodoActual.requestUpdate((int)nodoActual.getID());
					break;
				}
				break;
			
			default:{
				System.out.println("Saltar acción");
			}
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
