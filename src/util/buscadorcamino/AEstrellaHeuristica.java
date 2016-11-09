package util.buscadorcamino;

/**
 *Esta clase proporciona un costo para una baldosa dada en función de una 
 * ubicación de destino y la entidad que se está moviendo. Esta heurística controla qué prioridad 
 * se colocan en diferentes baldosas durante la búsqueda de un camino
 */
public interface AEstrellaHeuristica {

	/**
	 * Obtener el costo heurístico adicional  de la baldosa . Esto controla el 
	 * orden en el que se realizan búsquedas de las baldosas mientras se intenta 
	 * encontrar una ruta de acceso a la ubicación de destino. Cuanto menor sea el costo, 
	 * más probable que la baldosa se busque.
	 * 
	 * @param map   El mapa en el que se encuentra el camino
	 * @param mover La entidad que se mueve a lo largo del camino
	 * @param x     La coordenada x de la baldosa que se está evaluando
	 * @param y		La coordenada y de la baldosa que se está evaluando
	 * @param tx	La coordenada x de la ubicación de destino
	 * @param ty	La coordenada y de la ubicación de destino
	 * @return 		El coste asociado con la baldosa
	 */
	public float getCosto(MapaConBaldosas map, Entidad mover, int x, int y, int tx, int ty);
}
