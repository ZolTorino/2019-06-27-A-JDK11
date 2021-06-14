package it.polito.tdp.crimes.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.crimes.db.EventsDao;

public class Model {
	EventsDao dao;
	private SimpleWeightedGraph<String, DefaultWeightedEdge> grafo;
	private HashSet<String> idSet;
	LinkedList<Arco> archi=new LinkedList<>();
	LinkedList<Arco> archimassimi=new LinkedList<>();
	public Model() {
		dao= new EventsDao();
		grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		idSet=new HashSet<>();
	}
	public List<String> offencecats()
	{
		return dao.offencecats();
	}
	public List<Integer> years()
	{
		return dao.dates();
	}
	public String creaGrafo(String category, int year) {
		String s="";
		idSet= new HashSet<>();
		grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		dao.vertices(category,year, idSet);
		Graphs.addAllVertices(grafo, idSet);
		s+="Vertici: "+grafo.vertexSet().size()+"\n";
		
		archi=new LinkedList<>();
		dao.edges(category, year, archi);
		int max=-1;
		for(Arco a:archi)
		{
			if(a.getPeso()>max)
				max =a.getPeso();
					
			if(grafo.containsVertex(a.getT1()) && grafo.containsVertex(a.getT2())) {
					Graphs.addEdge(grafo, a.getT1(), a.getT2(), a.getPeso());
				
			}
		}
		s+="Archi: "+grafo.edgeSet().size()+"\n";
		for(Arco a:archi)
		{
		
			if(grafo.containsVertex(a.getT1()) && grafo.containsVertex(a.getT2())) {
				if(a.getPeso()==max)
				{
					s+=a.t1+ " - "+a.t2+ " - "+a.peso+"\n";
					archimassimi.add(a);
				}
				
			}
		}
		return s;
	}
	public List<Arco> massimi()
	{
		return archimassimi;
	}
	int pesomin=-1;
	ArrayList<String> minore=new ArrayList<String>();
	public String percorso(Arco iniziale)
	{
		String s="";
		ArrayList<String> parziale=new ArrayList<>();
		parziale.add(iniziale.t1);
		int pesoparziale=0;
		minore=new ArrayList<String>();
		pesomin=-1;
		calcola(parziale,iniziale.t2, pesoparziale);
		s=""+minore.size();
		return s;
	}
	
	public void calcola(ArrayList<String> parziale, String dest ,int pesoparziale) {
		String ultimo = parziale.get(parziale.size() -1);
		if(ultimo.equals(dest))
		{
			if((parziale.size())==grafo.vertexSet().size())
			{
				if(pesomin==-1||pesoparziale<pesomin)
				{
					minore=new ArrayList<>(parziale);
					pesomin=pesoparziale;
					
				}

			}
			
			return;
			 
		}
		else {
			
			for(String next: Graphs.neighborListOf(grafo, ultimo))
			{
				
				if(!parziale.contains(next))
				{
					parziale.add(next);
					//int costo =(int)grafo.getEdgeWeight(grafo.getEdge(parziale.get(parziale.size()-1), next));
					calcola(parziale,dest,pesoparziale);
					parziale.remove(next);
				}
			}
		}
	}
	
	public int peso(ArrayList<Arco> list)
	{
		int sum=0;
		for(Arco a: list )
		{
			sum+=a.hashCode();
		}
		return sum;
	}
	
}
