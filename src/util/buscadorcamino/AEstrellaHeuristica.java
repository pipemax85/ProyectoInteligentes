package util.buscadorcamino;

/**
 *Esta clase proporciona un costo para una baldosa dada en funci�n de una 
 * ubicaci�n de destino y la entidad que se est� moviendo. Esta heur�stica controla qu� prioridad 
 * se colocan en diferentes baldosas durante la b�squeda de un camino
 */
public interface AEstrellaHeuristica {

	/**
	 * Obtener el costo heur�stico adicional  de la baldosa . Esto controla el 
	 * orden en el que se realizan b�squedas de las baldosas mientras se intenta 
	 * encontrar una ruta de acceso a la ubicaci�n de destino. Cuanto menor sea el costo, 
	 * m�s probable que la baldosa se busque.
	 * 
	 * @param map   El mapa en el que se encuentra el camino
	 * @param mover La entidad que se mueve a lo largo del camino
	 * @param x     La coordenada x de la baldosa que se est� evaluando
	 * @param y		La coordenada y de la baldosa que se est� evaluando
	 * @param tx	La coordenada x de la ubicaci�n de destino
	 * @param ty	La coordenada y de la ubicaci�n de destino
	 * @return 		El coste asociado con la baldosa
	 */
	public float getCosto(MapaConBaldosas map, Entidad mover, int x, int y, int tx, int ty);
}
