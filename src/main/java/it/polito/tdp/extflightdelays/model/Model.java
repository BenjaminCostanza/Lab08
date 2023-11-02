package it.polito.tdp.extflightdelays.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;

public class Model {
	
	private Graph<Airport, DefaultWeightedEdge> grafo;
	private ExtFlightDelaysDAO dao;
	private Map<Integer, Airport> idMap;
	
	
	public Model() {
		this.dao = new ExtFlightDelaysDAO();
		this.idMap = this.dao.loadAllAirportsToMap();
	}

	
	
	/**
	 * Metodo che crea il grafo sfruttando dao.getRotteAggregated(idMap), che 
	 * aggrega i voli opposti di una stessa tratta direttamente con una query.
	 * Quindi, la costruzione degli archi qui diventa molto semplice (a fronte però
	 * di una complessità molto superiore nella query)
	 * @param avgD
	 */
	public void creaGrafo(double avgD) {
		// creazione grafo
		this.grafo = new SimpleWeightedGraph<Airport, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		// aggiunta vertici
		Graphs.addAllVertices(this.grafo, this.idMap.values());
		
		// aggiunta archi
		List<Rotta> edges = this.dao.getRotteAggregated(idMap);
		for (Rotta e : edges) {
			Airport A1 = e.getA1();
			Airport A2 = e.getA2();
			double weight = e.getAvgDistance();
			if (weight > avgD) {
				Graphs.addEdgeWithVertices(this.grafo, A1, A2, e.getAvgDistance());
			}
		}
		
		// stampa
		System.out.println("Grafo creato");
		System.out.println("Ci sono " + this.grafo.vertexSet().size() + " vertici.");
		System.out.println("Ci sono " + this.grafo.edgeSet().size() + " archi.");
	}
	
	
	
	/**
	 * Metodo che crea il grafo sfruttando dao.getRotte(idMap), che 
	 * NON aggrega i voli opposti di una stessa tratta direttamente con una query.
	 * Quindi, la query è molto semplice, ma costruzione degli archi qui diventa 
	 * più complessa (ma si fa comunque facilmente con qualche ciclo)
	 * @param avgD
	 */
	
	public int getNumberOfVertex() {
		return this.grafo.vertexSet().size();
	}
	
	public int getNumberOfEdges() {
		return this.grafo.edgeSet().size();
	}
	
	
	/**
	 * Metodo per leggere gli archi come Rotte, in modo da poterli
	 * stampare a schermo. Chiaramente, negli archi del grafo non abbiamo più
	 * l'informazione sulla distanza totale e sul numero totale dei voli, ma solo la distanza media 
	 * (ovvero il peso che abbiamo assegnato all'arco). Quindi usiamo un costruttore che prende tre argomenti
	 * (aeroporto 1, aeroporto 2, e distanza media).
	 * @return
	 */
	public List<Rotta> getArchi() {
		List<Rotta> archi = new ArrayList<Rotta>();
		for (DefaultWeightedEdge e : this.grafo.edgeSet()) {
			//Uso l'altro costruttore a tre argomenti
			archi.add(new Rotta( this.grafo.getEdgeSource(e),
					this.grafo.getEdgeTarget(e),
					this.grafo.getEdgeWeight(e)));
		}
		return archi;
	}
	
}
