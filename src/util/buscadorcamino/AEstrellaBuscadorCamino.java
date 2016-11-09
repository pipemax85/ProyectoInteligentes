package util.buscadorcamino;

import java.util.ArrayList;
import java.util.Collections;

import util.buscadorcamino.heuristicas.HeuristicaCercana;

/** Una implementaci�n de buscador de ruta que utiliza el algoritmo basado en heur�sticas Aestrella para determinar una ruta. */
public class AEstrellaBuscadorCamino implements BuscadorRuta {
	
	private ArrayList cerrado = new ArrayList();/** El conjunto de nodos que se han buscado */
	private SortedList abierto = new SortedList();/** El conjunto de nodos que a�n no hemos considerado completamente buscado */
	private MapaConBaldosas mapa;/** El mapa que se busca */
	private int maxDistanciaBusqueda;/** La profundidad m�xima de la b�squeda que estamos dispuestos a aceptar antes de renunciar */
	private Nodo[][] nodos;/** El conjunto completo de nodos en el mapa */
	private boolean permiteMoviDiagonal;/** Es cierto si permitimos el movimiento diagonal */
	private AEstrellaHeuristica heuristica;/** La heur�stica que estamos aplicando para determinar qu� nodos buscar primero */
	
	/**
	 * Crear un buscador de rutas con la heur�stica predeterminada - m�s cercana al destino.
	 * 
	 * @param mapa El mapa que se busca
	 * @param maxDistanciaBusqueda La profundidad m�xima que buscaremos antes de renunciar
	 * @param permiteMoviDiagonal Es cierto si la b�squeda debe intentar el movimiento diagonal
	 */
	public AEstrellaBuscadorCamino(MapaConBaldosas mapa, int maxDistanciaBusqueda, boolean permiteMoviDiagonal) {
		this(mapa, maxDistanciaBusqueda, permiteMoviDiagonal, new HeuristicaCercana());
	}

	/**
	 * Crear un buscador de rutas
	 * 
	 * @param heuristica La heur�stica utilizada para determinar el orden de b�squeda del mapa
	 * @param mapa El mapa que se busca
	 * @param maxDistanciaBusqueda La profundidad m�xima que buscaremos antes de renunciar
	 * @param permiteMoviDiagonal Es cierto si la b�squeda debe intentar el movimiento diagonal
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
	public Camino encontrarCamino(Entidad entidad, int sx, int sy, int tx, int ty) {
		if (mapa.bloquea(entidad, tx, ty)) {// Si el destino esta bloqueado no podemos llegar
			return null;
		}		
		// Estado inicial de A *. El grupo cerrado est� vac�o. S�lo la baldosa
		//de inicio est� en la lista abierta y ya est� all�
		nodos[sx][sy].costo = 0;
		nodos[sx][sy].profundidad = 0;
		cerrado.clear();
		abierto.limpiar();
		abierto.agregar(nodos[sx][sy]);
		
		nodos[tx][ty].padre = null;
		
		// Mientras que no hemos excedido la profundidad m�xima de la b�squeda
		int maxProfundidad = 0;
		while ((maxProfundidad < maxDistanciaBusqueda) && (abierto.size() != 0)) {
			// Extraer el primer nodo en nuestra lista abierta, se determina que es el m�s 
			//probable que sea el siguiente paso basado en nuestra heur�stica

			Nodo actual = getPrimeroAbierto();
			if (actual == nodos[tx][ty]) {
				break;
			}
			
			eliminarAbierto(actual);
			agregarCerrado(actual);
			
			// Busca a trav�s de todos los vecinos del nodo actual evaluando
			// Los siguientes pasos

			for (int x=-1;x<2;x++) {
				for (int y=-1;y<2;y++) {
					// no es un vecino, es el actual
					if ((x == 0) && (y == 0)) {
						continue;
					}	
					//si no estamos permitiendo el movimiento diagonal entonces s�lo uno de X o Y puede definirse
					if (!permiteMoviDiagonal) {
						if ((x != 0) && (y != 0)) {
							continue;
						}
					}
					// Determinar la ubicaci�n del vecino y evaluarlo
					int xp = x + actual.x;
					int yp = y + actual.y;
					
					if (isValidLocation(entidad,sx,sy,xp,yp)) {
						//El costo para llegar a este nodo es el costo 
						//de la corriente m�s el costo de movimiento para llegar a este nodo. 
						//Tenga en cuenta que el valor heur�stico s�lo se utiliza en la lista abierta ordenada

						float costoSiguientePaso = actual.costo + getCostoMovimiento(entidad, actual.x, actual.y, xp, yp);
						Nodo vecino = nodos[xp][yp];
						mapa.buscadorRutaVisitado(xp, yp);
						
						//Si el nuevo coste que hemos determinado para este nodo es menor de lo que se ha asegurado 
						//previamente, el nodo no ha determinado que podr�a haber habido un mejor camino para llegar
						//a este nodo, por lo que necesita ser reevaluado

						if (costoSiguientePaso < vecino.costo) {
							if (inListaAbierto(vecino)) {
								eliminarAbierto(vecino);
							}
							if (inListaCerrado(vecino)) {
								eliminarCerrado(vecino);
							}
						}
						
						//Si el nodo no ha sido procesado y desechado, restablezca su costo a nuestro costo actual y 
						//agr�guelo como un pr�ximo paso posible (es decir, a la lista abierta)

						if (!inListaAbierto(vecino) && !(inListaCerrado(vecino))) {
							vecino.costo = costoSiguientePaso;
							vecino.heuristica = getCostoHeuristica(entidad, xp, yp, tx, ty);
							maxProfundidad = Math.max(maxProfundidad, vecino.setPadre(actual));
							agregarAbierto(vecino);
						}
					}
				}
			}
		}

		//Ya que nos hemos quedado sin b�squeda no hab�a camino. Solo devuelve null

		if (nodos[tx][ty].padre == null) {
			return null;
		}
		
		//En este punto definitivamente hemos encontrado un camino para que podamos 
		//usar las referencias de los nodos para encontrar el camino desde la ubicaci�n 
		//de destino de nuevo a la grabaci�n de inicio de los nodos en el camino.

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
	 * Obtener el primer elemento de la lista abierta.
	 * 
	 * @return El primer elemento de la lista abierta
	 */
	protected Nodo getPrimeroAbierto() {
		return (Nodo) abierto.primero();
	}
	
	/**
	 * Agregar un nodo a la lista abierta
	 * 
	 * @param nodo El nodo que se agregar� a la lista abierta
	 */
	protected void agregarAbierto(Nodo nodo) {
		abierto.agregar(nodo);
	}
	
	/**
	 * Comprueba si un nodo est� en la lista abierta
	 * 
	 * @param nodo El nodo a comprobar
	 * @return True Si el nodo dado est� en la lista abierta
	 */
	protected boolean inListaAbierto(Nodo nodo) {
		return abierto.contiene(nodo);
	}
	
	/**
	 * Eliminar un nodo de la lista abierta
	 * 
	 * @param nodo El nodo a eliminar de la lista abierta
	 */
	protected void eliminarAbierto(Nodo nodo) {
		abierto.eliminar(nodo);
	}
	
	/**
	 * Agregar un nodo a la lista cerrada
	 * 
	 * @param nodo El nodo que se agrega a la lista cerrada
	 */
	protected void agregarCerrado(Nodo nodo) {
		cerrado.add(nodo);
	}
	
	/**
	 * Compruebe si el nodo suministrado est� en la lista cerrada
	 * 
	 * @param nodo El nodo a buscar
	 * @return True Si el nodo especificado est� en la lista cerrada
	 */
	protected boolean inListaCerrado(Nodo nodo) {
		return cerrado.contains(nodo);
	}
	
	/**
	 * Eliminar un nodo de la lista cerrada
	 * 
	 * @param nodo El nodo a eliminar de la lista cerrada
	 */
	protected void eliminarCerrado(Nodo node) {
		cerrado.remove(node);
	}
	
	/**
	 * Compruebe si un lugar determinado es v�lido para la entidad suministrado
	 * 
	 * @param entidad La entidad que se intenta mover a una ubicacion determinada 
	 * @param sx La coordenada x inicial
	 * @param sy La coordenada y inicial
	 * @param x La coordenada x de la ubicaci�n para comprobar
	 * @param y La coordenada y de la ubicaci�n para comprobar
	 * @return True Si la ubicaci�n es v�lida para la entidad dada
	 */
	protected boolean isValidLocation(Entidad entidad, int sx, int sy, int x, int y) {
		boolean invalid = (x < 0) || (y < 0) || (x >= mapa.getAnchoEnBaldosas()) || (y >= mapa.getAlturaEnBaldosas());
		
		if ((!invalid) && ((sx != x) || (sy != y))) {
			invalid = mapa.bloquea(entidad, x, y);
		}
		
		return !invalid;
	}
	
	/**
	 * Obtener el coste para desplazarse por una ubicaci�n determinada
	 * 
	 * @param entidad La entidad que se est� moviendo
	 * @param sx La coordenada x de la baldosa cuyo coste se determina
	 * @param sy La coordenada y de la baldosa cuyo coste se determina
	 * @param tx La coordenada x de la ubicaci�n de destino
	 * @param ty La coordenada y de la ubicaci�n de destino
	 * @return El coste de movimiento a trav�s de la baldosa dada
	 */
	public float getCostoMovimiento(Entidad entidad, int sx, int sy, int tx, int ty) {
		return mapa.getCosto(entidad, sx, sy, tx, ty);
	}

	/**
	 * Obtener el coste heur�stico para la ubicaci�n dada. 
	 * Esto determina en qu� orden se procesan las ubicaciones.
	 * 
	 * @param entidad La entidad que se est� moviendo
	 * @param x La coordenada x de la baldosa cuyo coste se determina
	 * @param y La coordenada y de la baldosa cuyo coste se determina
	 * @param tx La coordenada x de la ubicaci�n de destino
	 * @param ty La coordenada y de la ubicaci�n de destino
	 * @return El coste heur�stico asignado a la baldosa
	 */
	public float getCostoHeuristica(Entidad entidad, int x, int y, int tx, int ty) {
		return heuristica.getCosto(mapa, entidad, x, y, tx, ty);
	}
	
	private class SortedList {
		private ArrayList list = new ArrayList();
		
		/**
		 * Recuperar el primer elemento de la lista
		 *  
		 * @return El primer elemento de la lista
		 */
		public Object primero() {
			return list.get(0);
		}
		
		/**
		 * Vaciar la lista
		 */
		public void limpiar() {
			list.clear();
		}
		
		/**
		 * A�adir un elemento a la lista, esto hace que necesitemos hacer nuevamente el sort
		 * 
		 * @param o El elemento a a�adir
		 */
		public void agregar(Object o) {
			list.add(o);
			Collections.sort(list);
		}
		
		/**
		 * Elimina un elemento de la lista
		 * 
		 * @param o El elemento a eliminar
		 */
		public void eliminar(Object o) {
			list.remove(o);
		}
	
		/**
		 * Obtener el n�mero de elementos en la lista
		 * 
		 * @return El n�mero de elemento en la lista
 		 */
		public int size() {
			return list.size();
		}
		
		/**
		 * Comprueba si un elemento est� en la lista
		 * 
		 * @param o El elemento a buscar
		 * @return True Si el elemento est� en la lista
		 */
		public boolean contiene(Object o) {
			return list.contains(o);
		}
	}
	
	/**
	 * Un solo nodo en el gr�fico de b�squeda
	 */
	private class Nodo implements Comparable {
		
		private int x;/** La coordenada x del nodo */		
		private int y;/** y La coordenada y del nodo */
		private float costo;/** El coste de la ruta para este nodo */
		private Nodo padre;/** El padre de este nodo */
		private float heuristica;/** El coste heur�stico de este nodo */
		private int profundidad;/**La profundidad de b�squeda de este nodo */
		
		/**
		 * Crea un nuevo nodo
		 * 
		 * @param x La coordenada x del nodo
		 * @param y La coordenada y del nodo
		 */
		public Nodo(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		/**
		 * Establezca el padre de este nodo
		 * 
		 * @param padre El nodo padre que nos lleva a este nodo
		 * @return La profundidad que no hemos alcanzado en la b�squeda
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
