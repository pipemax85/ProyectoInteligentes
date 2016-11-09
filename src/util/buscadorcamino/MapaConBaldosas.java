package util.buscadorcamino;

/**
 * Proporciona la relacion entre los datos que se están buscando (en el mapa de juego)
 *  y las herramientas genericas de pathfinding 
 */
public interface MapaConBaldosas {

	public int getAnchoEnBaldosas();
	public int getAlturaEnBaldosas();
	
	/**
	 *La notificación de que el buscador de camino visitó una baldosa dada.
	 * Esto se utiliza para depurar nuevas heurísticas.
	 * 
	 * @param x La coordenada x de la baldosa que fue visitada
	 * @param y La coordenada y de la baldosa que fue visitada
	 */
	public void buscadorRutaVisitado(int x, int y);
	
	/**
	 * Compruebe si la ubicación dada está bloqueada, es decir, bloquea el movimiento de la entidad suministrada.
	 * 
	 * @param entidad La entidad que está potencialmente en movimiento a través de la baldosa especificado.
	 * @param x La coordenada x de la baldosa a comprobar
	 * @param y La coordenada y de la baldosa a comprobar
	 * @return True Si la ubicación está bloqueada
	 */
	public boolean bloquea(Entidad entidad, int x, int y);
	
	/**
	 * Obtener el coste de movimiento a través de la baldosa dada. Esto se puede utilizar para hacer ciertas áreas más deseables. 
	 * Una implementación sencilla y válida de este método sería devolver 1 en todos los casos.
	 * 
	 * @param entidad La entidad que está tratando de moverse a través de la baldosa
	 * @param sx La coordenada x de la baldosa que estamos moviendo, origen 
	 * @param sy La coordenada y de la baldosa que estamos moviendo, origen 
	 * @param tx La coordenada x de la baldosa a la que nos estamos moviendo
	 * @param ty La coordenada y de la baldosa a la que nos estamos moviendo
	 * @return El coste relativo de moverse a través de la baldosa dada
	 */
	public float getCosto(Entidad entidad, int sx, int sy, int tx, int ty);
}