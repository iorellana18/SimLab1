package cl.usach.sd;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;

public class Initialization implements Control {
	String prefix;

	int idLayer;
	int idTransport;

	//Valores que sacaremos del archivo de configuraci�n	
	int argExample;
	int initValue;

	public Initialization(String prefix) {
		this.prefix = prefix;
		/**
		 * Para obtener valores que deseamos como argumento del archivo de
		 * configuraci�n, podemos colocar el prefijo de la inicializaci�n en
		 * este caso "init.1statebuilder" y luego la variable de entrada
		 */
		// Configuration.getPid retornar al n�mero de la capa
		// que corresponden esa parte del protocolo
		this.idLayer = Configuration.getPid(prefix + ".protocol");
		this.idTransport = Configuration.getPid(prefix + ".transport");
		// Configuration.getInt retorna el n�mero del argumento
		// que se encuentra en el archivo de configuraci�n.
		// Tambi�n hay Configuration.getBoolean, .getString, etc...
		
		// en el archivo de configuraci�n init.1statebuilder.argExample 100 y se puede usar ese valor.
		this.argExample = Configuration.getInt(prefix + ".argExample");
		this.initValue = Configuration.getInt(prefix + ".initValue");
		System.out.println("Arg: " + argExample);
		System.out.println("Valor inicial: "+ initValue);
	}

	/**
	 * Ejecuci�n de la inicializaci�n en el momento de crear el overlay en el
	 * sistema
	 */
	@Override
	public boolean execute() {
		System.out.println("Construyendo red");
		/**
		 * Para comenzar tomaremos un nodo cualquiera de la red, a trav�s de un random
		 */
		//int nodoInicial = CommonState.r.nextInt(Network.size());
		
		
	
		/**Es conveniente inicializar los nodos, puesto que los nodos 
		 * son una clase clonable y si asignan valores desde el constructor
		 *  todas tomaran los mismos valores, puesto que tomaran la misma direcci�n
		 * de memoria*/
		System.out.println("Inicializamos los nodos:");
		for (int i = 0; i < Network.size(); i++) {	
			//Método que inicializa listas para publicar/suscribirse
			((ExampleNode)Network.get(i)).initNode();
			System.out.println("\tNodo: "+((ExampleNode) Network.get(i)).getID()+ " inicializado!");
		}
		
		return true;
	}

}
