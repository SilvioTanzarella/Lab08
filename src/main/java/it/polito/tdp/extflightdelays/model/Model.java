package it.polito.tdp.extflightdelays.model;

import java.util.ArrayList;
import java.util.HashMap;
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
	
	
	
	public Graph<Airport, DefaultWeightedEdge> getGrafo() {
		return grafo;
	}

	public Model() {
		this.dao = new ExtFlightDelaysDAO();
		this.idMap = new HashMap<Integer, Airport>();
	}
	
	public void creaGrafo(int distanza) {
		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		this.dao.loadAllAirports(idMap);
		Graphs.addAllVertices(this.grafo, this.idMap.values());
		for(Arco a: dao.getAllEdges(distanza)) {
			Graphs.addEdge(this.grafo, this.idMap.get(a.getA1()), this.idMap.get(a.getA2()), a.getPeso());
		}
	}
	
	public String printEdges(int distanza) {
		String s="";
		
		for(Arco a: dao.getAllEdges(distanza)) {
			s += idMap.get(a.getA1())+" - "+idMap.get(a.getA2())+": "+a.getPeso()+"\n";
		}
		return s;
	}

}
