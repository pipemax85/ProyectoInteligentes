package util.buscadorcamino;

import java.util.ArrayList;
import java.util.Collections;

import util.buscadorcamino.heuristicas.HeuristicaCercana;

/** Una implementación de buscador de ruta que utiliza el algoritmo basado en heurísticas AStar para determinar una ruta. */
public class AEstrellaBuscadorCamino implements BuscadorRuta {
	
	private ArrayList cerrado = new ArrayList();/** El conjunto de nodos que se han buscado */
	private SortedList abierto = new SortedList();/** El conjunto de nodos que aún no hemos considerado completamente buscado */
	private MapaConBaldosas mapa;/** El mapa que se busca */
	private int maxDistanciaBusqueda;/** La profundidad máxima de la búsqueda que estamos dispuestos a aceptar antes de renunciar */
	private Nodo[][] nodos;/** El conjunto completo de nodos en el mapa */
	private boolean permiteMoviDiagonal;/** Es cierto si permitimos el movimiento diagonal */
	private AEstrellaHeuristica heuristica;/** La heurística que estamos aplicando para determinar qué nodos buscar primero */
	
	/**
	 * Crear un buscador de rutas con la heurística predeterminada - más cercana al destino.
	 * 
	 * @param mapa El mapa que se busca
	 * @param maxDistanciaBusqueda La profundidad máxima que buscaremos antes de renunciar
	 * @param permiteMoviDiagonal Es cierto si la búsqueda debe intentar el movimiento diagonal
	 */
	public AEstrellaBuscadorCamino(MapaConBaldosas mapa, int maxDistanciaBusqueda, boolean permiteMoviDiagonal) {
		this(mapa, maxDistanciaBusqueda, permiteMoviDiagonal, new HeuristicaCercana());
	}

	/**
	 * Crear un buscador de rutas
	 * 
	 * @param heuristica La heurística utilizada para determinar el orden de búsqueda del mapa
	 * @param mapa El mapa que se busca
	 * @param maxDistanciaBusqueda La profundidad máxima que buscaremos antes de renunciar
	 * @param permiteMoviDiagonal Es cierto si la búsqueda debe intentar el movimiento diagonal
	 */
	public AEstrellaBuscadorCamino(MapaConBaldosas mapa, int maxDistanciaBusqueda, 
						   boolean permiteMoviDiagonal, AEstrellaHeuristica heuristica) {
		this.heuristica = heuristica;
		this.mapa = mapa;
		this.maxDistanciaBusqueda = maxDistanciaBusqueda;
		this.permiteMoviDiagonal = permiteMoviDiagonal;
		
		nodos = new Nodo[mapa.getAnchoEnBaldosas()][mapa.getAlturaEnBaldosas()];
		for (int x=0;x<mapa.getAnchoEnBaldosas();x++) {
			for (int y=0;y<mapa.getAlturaEnBaldosas();y++) {
				nodos[x][y] = new Nodo(x,y);
			}
		}
	}
	
	/**
	 * @see BuscadorRuta#encontrarCamino(Mover, int, int, int, int)
	 */
	public Camino encontrarCamino(Mover mover, int sx, int sy, int tx, int ty) {
		if (mapa.bloquea(mover, tx, ty)) {// Si el destino esta bloqueado no podemos llegar
			return null;
		}		
		// initial state for A*. The closed group is empty. Only the starting
		// tile is in the open list and it'e're already there
		nodos[sx][sy].costo = 0;
		nodos[sx][sy].profundidad = 0;
		cerrado.clear();
		abierto.limpiar();
		abierto.agregar(nodos[sx][sy]);
		
		nodos[tx][ty].padre = null;
		
		// while we haven'n't exceeded our max search depth
		int maxProfundidad = 0;
		while ((maxProfundidad < maxDistanciaBusqueda) && (abierto.size() != 0)) {
			// pull out the first node in our open list, this is determined to 
			// be the most likely to be the next step based on our heuristic

			Nodo actual = getPrimeroAbierto();
			if (actual == nodos[tx][ty]) {
				break;
			}
			
			eliminarAbierto(actual);
			agregarCerrado(actual);
			
			// search through all the neighbours of the current node evaluating
			// them as next steps

			for (int x=-1;x<2;x++) {
				for (int y=-1;y<2;y++) {
					// not a neighbour, its the current tile
					if ((x == 0) && (y == 0)) {
						continue;
					}	
					// if we're not allowing diaganol movement then only 
					// one of x or y can be set
					if (!permiteMoviDiagonal) {
						if ((x != 0) && (y != 0)) {
							continue;
						}
					}
					// determine the location of the neighbour and evaluate it
					int xp = x + actual.x;
					int yp = y + actual.y;
					
					if (isValidLocation(mover,sx,sy,xp,yp)) {
						// the cost to get to this node is cost the current plus the movement
						// cost to reach this node. Note that the heursitic value is only used
						// in the sorted open list

						float costoSiguientePaso = actual.costo + getCostoMovimiento(mover, actual.x, actual.y, xp, yp);
						Nodo vecino = nodos[xp][yp];
						mapa.buscadorRutaVisitado(xp, yp);
						
						// if the new cost we've determined for this node is lower than 
						// it has been previously makes sure the node hasn'e've
						// determined that there might have been a better path to get to
						// this node so it needs to be re-evaluated

						if (costoSiguientePaso < vecino.costo) {
							if (inListaAbierto(vecino)) {
								eliminarAbierto(vecino);
							}
							if (inClosedList(vecino)) {
								eliminarCerrado(vecino);
							}
						}
						
						// if the node hasn't already been processed and discarded then
						// reset it's cost to our current cost and add it as a next possible
						// step (i.e. to the open list)

						if (!inListaAbierto(vecino) && !(inClosedList(vecino))) {
							vecino.costo = costoSiguientePaso;
							vecino.heuristica = getCostoHeuristica(mover, xp, yp, tx, ty);
							maxProfundidad = Math.max(maxProfundidad, vecino.setPadre(actual));
							agregarAbierto(vecino);
						}
					}
				}
			}
		}

		// since we'e've run out of search 
		// there was no path. Just return null

		if (nodos[tx][ty].padre == null) {
			return null;
		}
		
		// At this point we've definitely found a path so we can uses the parent
		// references of the nodes to find out way from the target location back
		// to the start recording the nodes on the way.

		Camino camino = new Camino();
		Nodo objetivo = nodos[tx][ty];
		while (objetivo != nodos[sx][sy]) {
			camino.pasoPrevio(objetivo.x, objetivo.y);
			objetivo = objetivo.padre;
		}
		camino.pasoPrevio(sx,sy);
		return camino;
	}

	/**
	 * Get the first element from the open list. This is the next
	 * one to be searched.
	 * 
	 * @return The first element in the open list
	 */
	protected Nodo getPrimeroAbierto() {
		return (Nodo) abierto.primero();
	}
	
	/**
	 * Add a node to the open list
	 * 
	 * @param node The node to be added to the open list
	 */
	protected void agregarAbierto(Nodo node) {
		abierto.agregar(node);
	}
	
	/**
	 * Check if a node is in the open list
	 * 
	 * @param node The node to check for
	 * @return True if the node given is in the open list
	 */
	protected boolean inListaAbierto(Nodo node) {
		return abierto.contiene(node);
	}
	
	/**
	 * Remove a node from the open list
	 * 
	 * @param node The node to remove from the open list
	 */
	protected void eliminarAbierto(Nodo node) {
		abierto.eliminar(node);
	}
	
	/**
	 * Add a node to the closed list
	 * 
	 * @param node The node to add to the closed list
	 */
	protected void agregarCerrado(Nodo node) {
		cerrado.add(node);
	}
	
	/**
	 * Check if the node supplied is in the closed list
	 * 
	 * @param node The node to search for
	 * @return True if the node specified is in the closed list
	 */
	protected boolean inClosedList(Nodo node) {
		return cerrado.contains(node);
	}
	
	/**
	 * Remove a node from the closed list
	 * 
	 * @param node The node to remove from the closed list
	 */
	protected void eliminarCerrado(Nodo node) {
		cerrado.remove(node);
	}
	
	/**
	 * Check if a given location is valid for the supplied mover
	 * 
	 * @param mover The mover that would hold a given location
	 * @param sx The starting x coordinate
	 * @param sy The starting y coordinate
	 * @param x The x coordinate of the location to check
	 * @param y The y coordinate of the location to check
	 * @return True if the location is valid for the given mover
	 */
	protected boolean isValidLocation(Mover mover, int sx, int sy, int x, int y) {
		boolean invalid = (x < 0) || (y < 0) || (x >= mapa.getAnchoEnBaldosas()) || (y >= mapa.getAlturaEnBaldosas());
		
		if ((!invalid) && ((sx != x) || (sy != y))) {
			invalid = mapa.bloquea(mover, x, y);
		}
		
		return !invalid;
	}
	
	/**
	 * Get the cost to move through a given location
	 * 
	 * @param mover The entity that is being moved
	 * @param sx The x coordinate of the tile whose cost is being determined
	 * @param sy The y coordiante of the tile whose cost is being determined
	 * @param tx The x coordinate of the target location
	 * @param ty The y coordinate of the target location
	 * @return The cost of movement through the given tile
	 */
	public float getCostoMovimiento(Mover mover, int sx, int sy, int tx, int ty) {
		return mapa.getCosto(mover, sx, sy, tx, ty);
	}

	/**
	 * Get the heuristic cost for the given location. This determines in which 
	 * order the locations are processed.
	 * 
	 * @param mover The entity that is being moved
	 * @param x The x coordinate of the tile whose cost is being determined
	 * @param y The y coordiante of the tile whose cost is being determined
	 * @param tx The x coordinate of the target location
	 * @param ty The y coordinate of the target location
	 * @return The heuristic cost assigned to the tile
	 */
	public float getCostoHeuristica(Mover mover, int x, int y, int tx, int ty) {
		return heuristica.getCosto(mapa, mover, x, y, tx, ty);
	}
	
	/** A simple sorted list */
	private class SortedList {
		/** The list of elements */
		private ArrayList list = new ArrayList();
		
		/**
		 * Retrieve the first element from the list
		 *  
		 * @return The first element from the list
		 */
		public Object primero() {
			return list.get(0);
		}
		
		/**
		 * Empty the list
		 */
		public void limpiar() {
			list.clear();
		}
		
		/**
		 * Add an element to the list - causes sorting
		 * 
		 * @param o The element to add
		 */
		public void agregar(Object o) {
			list.add(o);
			Collections.sort(list);
		}
		
		/**
		 * Remove an element from the list
		 * 
		 * @param o The element to remove
		 */
		public void eliminar(Object o) {
			list.remove(o);
		}
	
		/**
		 * Get the number of elements in the list
		 * 
		 * @return The number of element in the list
 		 */
		public int size() {
			return list.size();
		}
		
		/**
		 * Check if an element is in the list
		 * 
		 * @param o The element to search for
		 * @return True if the element is in the list
		 */
		public boolean contiene(Object o) {
			return list.contains(o);
		}
	}
	
	/**
	 * A single node in the search graph
	 */
	private class Nodo implements Comparable {
		
		private int x;/** The x coordinate of the node */		
		private int y;/** The y coordinate of the node */
		private float costo;/** The path cost for this node */
		private Nodo padre;/** The parent of this node, how we reached it in the search */
		private float heuristica;/** The heuristic cost of this node */
		private int profundidad;/** The search depth of this node */
		
		/**
		 * Create a new node
		 * 
		 * @param x The x coordinate of the node
		 * @param y The y coordinate of the node
		 */
		public Nodo(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		/**
		 * Set the parent of this node
		 * 
		 * @param parent The parent node which lead us to this node
		 * @return The depth we have no reached in searching
		 */
		public int setPadre(Nodo padre) {
			profundidad = padre.profundidad + 1;
			this.padre = padre;
			
			return profundidad;
		}
		
		/**
		 * @see Comparable#compareTo(Object)
		 */
		public int compareTo(Object other) {
			Nodo o = (Nodo) other;
			
			float f = heuristica + costo;
			float of = o.heuristica + o.costo;
			
			if (f < of) {
				return -1;
			} else if (f > of) {
				return 1;
			} else {
				return 0;
			}
		}
	}
}
