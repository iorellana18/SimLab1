package cl.usach.sd;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDSimulator;

public class TrafficGenerator implements Control {
	private final static String PAR_PROT = "protocol";
	private final int layerId;

	public TrafficGenerator(String prefix) {
		layerId = Configuration.getPid(prefix + "." + PAR_PROT);

	}

	@Override
	public boolean execute() {
		
		// Consideraremos cualquier nodo de manera aleatoria de la red para comenzar
		Node initNode = Network.get(CommonState.r.nextInt(Network.size())); 
		// Se añade mensaje
		// Parámetros: Mensaje, nodo emisor, nodo receptor (se decide después), accion (se decide después)
		Message message = new Message("Mensaje",((ExampleNode)initNode).getID(),-1,-1);
		EDSimulator.add(0, message, initNode, layerId);
		return false;
	}

}
