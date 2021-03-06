package cl.usach.sd;

import java.util.ArrayList;

import peersim.core.CommonState;
import peersim.core.GeneralNode;
import peersim.core.Network;

public class ExampleNode extends GeneralNode {
	// mensaje
	private int value;
	// rol que asume el nodo
	private int type;
	// es topico
	private boolean isTopic;
	
	//Lista de nodos a los que publica
	private ArrayList<Integer> publishTo;
	//Lista de topicos a los que está suscrito
	private ArrayList<Integer> suscribeTo;
	//Lista de tópicos inscritos
	public ArrayList <Integer> topicos;
	//Lista de publciaciones
	private ArrayList <Integer> publicaciones;
	//Listas para topicos
	private ArrayList<Integer> publicadores;
	private ArrayList<Integer> suscriptores;
	//Lista de mensajes almacenados
	private ArrayList<String> mensajes;

	public ExampleNode(String prefix) {
		super(prefix);
		this.setValue(0);
	}
	

	//Inicializa nuevos nodos con sus listas para publicar y suscribirse
	public void initNode(){
		setṔublishTo(new ArrayList<Integer>());
		setSuscribeTo(new ArrayList<Integer>());
		setPublicaciones(new ArrayList<Integer>());
		setTopicos(new ArrayList<Integer>());
		setPublicadores(new ArrayList<Integer>());
		setSuscriptores(new ArrayList<Integer>());
		setMensajes(new ArrayList<String>());
	}
	
	

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public String getType() {
		switch(type){
		case 0:
			return "Publicador";
		case 1:
			return "Suscriptor";
		case 2:
			return "Tópico";
		default:
			return "Unknown";
		}
		
	}

	public void setType(int type) {
		this.type = type;
	}
	
	///////////////// Métodos del nodo//////////////////
	// Crea nuevo tópico y lo retorna
	public int creaTopico(int emisor){
		// Decide si añadir nuevo nodo a la red o utilizar uno ya creado
		int accion = CommonState.r.nextInt(2);
		ExampleNode receptor;
		if(accion==0){
			// Inicia nuevo nodo y añade a la red
			System.out.println("Se crea nuevo nodo");
			receptor = new ExampleNode("init.1statebuilder");
			Network.add(receptor);
			receptor.setIsTopic(true);
			// inicia listas de nodo
			receptor.initNode();
		}else{
			// Utiliza un nodo existente
			System.out.println("Utiliza nodo existente");
			int nodoElegido = CommonState.r.nextInt(Network.size());
			receptor = (ExampleNode)Network.get(nodoElegido);
		}
		//Se añade a lista para publicar del emisor
		((ExampleNode) Network.get(emisor)).getPublishTo().add((int) receptor.getID());
		System.out.println("Nodo: "+((ExampleNode) Network.get(emisor)).getID()+ " añade tópico : "+(int) receptor.getID());
		System.out.println("Tópicos a los que publica: "+((ExampleNode) Network.get(emisor)).getPublishTo());
		
		return (int)receptor.getID();
	}
	
	// Se activa cuando recibe mensaje del emisor y lo guarda en su lista de publicadores
	public void recibeSuscripcion(int publicador, int topico){
		//actualiza lista de publicadores
		((ExampleNode) Network.get(topico)).getPublicadores().add(publicador);
		System.out.println("Publicadores de tópico "+topico+" : "+((ExampleNode) Network.get(topico)).getPublicadores());
	}
	
	// Se registra en tópico existente
	public int registraTopico(int emisor){
		int receptor;
		setTopicos(buscarTopicos());
		if(topicos.isEmpty()){
			System.out.println("No se han encontrado tópicos");
			return -1;
		}else{
			// Receptor es un random entre los topicos existentes
			receptor = topicos.get(CommonState.r.nextInt(topicos.size()));
			if(!((ExampleNode) Network.get(emisor)).getPublishTo().contains(receptor)){
				//Se añade a lista para publicar del emisor
				((ExampleNode) Network.get(emisor)).getPublishTo().add(receptor);
				//Se añade a la lista de tópicos
				((ExampleNode) Network.get(receptor)).setIsTopic(true);
				System.out.println("Nodo: "+((ExampleNode) Network.get(emisor)).getID()+ " añade tópico : "+receptor);
				System.out.println("Tópicos a los que publica: "+((ExampleNode) Network.get(emisor)).getPublishTo());
				
				return receptor;
			}else{
				System.out.println("Nodo "+emisor+" intenta añadir nodo "+receptor+", pero ya esta incluido");
				System.out.println("Topicos a los que publica : "+((ExampleNode)Network.get(emisor)).getPublishTo());
				return -1;
			}
		}
	}
	

	
	public ArrayList<Integer> buscarTopicos(){
		ArrayList<Integer> topicos = new ArrayList<Integer>();
		for(int i=0; i<Network.size();i++){
			ExampleNode mainNode = (ExampleNode)Network.get(i);
			if(mainNode.getIsTopic()){
				topicos.add(i);
			}
		}
		return topicos;
	}
	
	// Se suscribe en topico existente
	public int suscribirseATopico(int emisor){
		setTopicos(buscarTopicos());
		if(topicos.isEmpty()){
			System.out.println("No se han encontrado tópicos");
			return -1;
		}
		// Se elige topico entre los existentes para inscribirse
		int topico = topicos.get(CommonState.r.nextInt(topicos.size()));
		if(!((ExampleNode) Network.get(emisor)).getSuscribeTo().contains(topico)){
			if(topico==emisor){
				System.out.println("error, un nodo no se puede susribir a sí mismo");
				return -1;
			}
			//Se añade en suscriptor el topico a la lista y topico a la lista
			((ExampleNode) Network.get(topico)).setIsTopic(true);
			((ExampleNode) Network.get(emisor)).getSuscribeTo().add(topico);
			System.out.println("Nodo: "+((ExampleNode) Network.get(emisor)).getID()+ " se suscribe a tópico : "+topico);
			System.out.println("Tópicos a los que esta suscrito: "+((ExampleNode) Network.get(emisor)).getSuscribeTo());
			return topico;
		}else{
			System.out.println("Nodo "+emisor+" intenta suscribirse a nodo "+topico+", pero ya esta suscrito");
			System.out.println("Tópicos a los que esta suscrito : "+((ExampleNode)Network.get(emisor)).getSuscribeTo());
			return -1;
			
		}
	}
	
	// Se activa cuando le llega una solicitud de suscripción a un tópico
	public void agregarSuscriptor(int suscriptor, int topico){
		//Se añade en topico a la lista de suscriptores
		((ExampleNode) Network.get(topico)).getSuscriptores().add(suscriptor);
		System.out.println("Tópico "+topico+" atualiza lista de suscriptores : " +((ExampleNode)Network.get(topico)).getSuscriptores());
	}
	
	public int publicarEnTopico(int emisor){
		// Se escoge entre los tópicos agregados para publicar
		ArrayList<Integer> topicos = ((ExampleNode) Network.get(emisor)).getPublishTo();
		
		if(topicos.isEmpty()){
			System.out.println("No se han encontrado tópicos");
			return -1;
		}else{

			int topico = topicos.get(CommonState.r.nextInt(topicos.size()));
			System.out.println("Nodo: "+((ExampleNode) Network.get(emisor)).getID()+ " publica a tópico : "+topico);
			return topico;
		}
	}
	
	public ArrayList<Integer> topicoRecibePublicacion(int publicador, int topico){
		System.out.println("Tópico "+topico+" recibe publicación de nodo "+publicador);
		// Agrega lista de suscriptores
		ArrayList <Integer> suscriptores = ((ExampleNode)Network.get(topico)).getSuscriptores();
		System.out.println("Preparándose para reenviar publicación a nodo(s) "+suscriptores);
		
		return suscriptores;
		
	}
	
	public int desInscribeComoPublicador(int emisor){
		// Obtiene lista de topicos a los que publica
		ArrayList<Integer> topicos = ((ExampleNode)Network.get(emisor)).getPublishTo();
		if(topicos.isEmpty()){
			System.out.println("Nodo "+emisor);
			System.out.println("No hay tópicos que desinscribir");
			return -1;
		}else{
			// Elige el tópico que va a remover
			int topicoARemover = topicos.get(CommonState.r.nextInt(topicos.size()));
			System.out.println("Nodo "+emisor+" remueve tópico "+topicoARemover);
			//Indice del tópico a remover
			int indice = ((ExampleNode)Network.get(emisor)).getPublishTo().indexOf(topicoARemover);
			//Remueve tópico
			((ExampleNode)Network.get(emisor)).getPublishTo().remove(indice);
			System.out.println("Tópicos a los que publica actualmente :"+((ExampleNode)Network.get(emisor)).getPublishTo());
			return topicoARemover;
		}
	}
	
	// elimina publicador de un topico
	public void removerPublicadorDeTopico(int publicador, int topico){
		//Obtiene la lista de publicadores del tópico
		ArrayList <Integer> publicadores = ((ExampleNode)Network.get(topico)).getPublicadores();
		//Obtiene indice de publicador a eliminat
		int indice = publicadores.indexOf(publicador);
		//Elimina publicador
		((ExampleNode)Network.get(topico)).getPublicadores().remove(indice);
		System.out.println("Tópico "+topico+" elimina nodo publicador "+publicador);
		System.out.println("Lista de publicadores actualizada : "+publicadores);
	}
	
	// Un suscriptor se sale de un topico
	public int desinscribirseComoSuscriptor(int suscriptor){
		//Obtiene la lista de topicos a los que está suscrito
		ArrayList <Integer> topicos = ((ExampleNode)Network.get(suscriptor)).getSuscribeTo();
		// Compruba si esta suscrito a tópicos
		if(topicos.isEmpty()){
			System.out.println("Nodo "+suscriptor+" no está suscrito a ningún tópico");
			return -1;
		}else{
			//Elige tópico para desinscribirse
			int topicoARemover = topicos.get(CommonState.r.nextInt(topicos.size()));
			int indice = topicos.indexOf(topicoARemover);
			//Elimina topico
			((ExampleNode)Network.get(suscriptor)).getSuscribeTo().remove(indice);
			System.out.println("Nodo "+suscriptor+" se desinscribe de tópico "+ topicoARemover);
			System.out.println("Se actualiza lista de tópicos a los que se está suscrito: "+ ((ExampleNode)Network.get(suscriptor)).getSuscribeTo());
			return topicoARemover;
		}
	}
	
	// Activa cuando a topico se le notifica que un nodo se salió como suscriptor
	public void removerSuscriptor(int topico, int suscriptor){
		//Obtiene la lista de suscriptores del tópico
		ArrayList <Integer> suscriptores= ((ExampleNode)Network.get(topico)).getSuscriptores();
		//Obtiene indice de publicador a eliminat
		int indice = suscriptores.indexOf(suscriptor);
		//Elimina publicador
		((ExampleNode)Network.get(topico)).getSuscriptores().remove(indice);
		System.out.println("Tópico "+topico+" elimina nodo suscriptor "+suscriptor);
		System.out.println("Lista de suscriptores actualizada : "+suscriptores);
	}
	
	//Agregar publicación
	public void agregaPublicación(int suscriptor, String mensaje){
		//Se agrega publicación recibida a suscriptor
		((ExampleNode)Network.get(suscriptor)).getMensajes().add(mensaje);
		System.out.println("Nodo "+suscriptor+" añade mensaje : "+mensaje);
		System.out.println("Lista de publicaciones guardadas : "+((ExampleNode)Network.get(suscriptor)).getMensajes());
	}
	
	
	//Request Update
	public void requestUpdate(int suscriptor){
		//Procesa mensajes recibidos
		ArrayList<String> publicaciones = ((ExampleNode)Network.get(suscriptor)).getMensajes();
		if(publicaciones.isEmpty()){
			System.out.println("Nodo "+suscriptor+" no posee publicaciones");
		}else{
			for(int i=0;i<publicaciones.size();i++){
				System.out.println("Procesando publicacion : "+publicaciones.get(i));
			}
		}
	}
	
	public int borrarContenido(int publicador){
		//Selecciona tópico del cual quiere borrar contenido
		ArrayList<Integer> listaTopicos = ((ExampleNode)Network.get(publicador)).getPublishTo();
		if(listaTopicos.isEmpty()){
			System.out.println("No posee tópicos para publicar");
			return -1;
		}
		int topico = listaTopicos.get(CommonState.r.nextInt(listaTopicos.size()));
		//Notifica a nodo
		System.out.println("Nodo "+publicador+ " notifica la eliminación de contenido al tópico "+ topico);
		return topico;
	}
	
	public ArrayList<Integer> removerPublicaciónTopico(int topico){
		//Selecciona suscriptores
		ArrayList<Integer> listaSuscriptores = ((ExampleNode)Network.get(topico)).getSuscriptores();
		return listaSuscriptores;
	}
	
	
	// Modificar/Obtener lista a los que se publica
	public void setṔublishTo(ArrayList<Integer> publishTo){this.publishTo=publishTo;}
	public ArrayList<Integer> getPublishTo(){return publishTo;}
	
	//Modificar/obtener lista a los que se está suscrito
	public void setSuscribeTo(ArrayList<Integer> suscribeTo){this.suscribeTo=suscribeTo;}
	public ArrayList<Integer> getSuscribeTo(){return suscribeTo;}
	
	// Modificar/Obtener tópico
	public void setTopicos(ArrayList<Integer> topicos){this.topicos=topicos;}
	public ArrayList<Integer> getTopicos(){return topicos;}
	
	// Modificar/Obtener estado de topico
	public void setIsTopic(Boolean isTopic){this.isTopic=isTopic;}
	public Boolean getIsTopic(){return isTopic;}
	
	// Modificar/Obtener publicaciones
	public void setPublicaciones(ArrayList<Integer> publicaciones){this.publicaciones=publicaciones;}
	public ArrayList<Integer> getPublicaciones(){return publicaciones;}
	
	// Modificar/Obtener para topicos
	public void setPublicadores(ArrayList<Integer> publicadores){this.publicadores=publicadores;}
	public ArrayList<Integer> getPublicadores(){return publicadores;}
	public void setSuscriptores(ArrayList<Integer> suscriptores){this.suscriptores=suscriptores;}
	public ArrayList<Integer> getSuscriptores(){return suscriptores;}
	
	//Modificar/Obtener mensajes
	public void setMensajes(ArrayList<String> mensajes){this.mensajes=mensajes;}
	public ArrayList<String> getMensajes(){return mensajes;}
}
