package util.buscadorcamino;

/**
 * Proporciona la relacion entre los datos que se est�n buscando (en el mapa de juego)
 *  y las herramientas genericas de pathfinding 
 */
public interface MapaConBaldosas {

	public int getAnchoEnBaldosas();
	public int getAlturaEnBaldosas();
	
	/**
	 *La notificaci�n de que el buscador de camino visit� una baldosa dada.
	 * Esto se utiliza para depurar nuevas heur�sticas.
	 * 
	 * @param x La coordenada x de la baldosa que fue visitada
	 * @param y La coordenada y de la baldosa que fue visitada
	 */
	public void buscadorRutaVisitado(int x, int y);
	
	/**
	 * Compruebe si la ubicaci�n dada est� bloqueada, es decir, bloquea el movimiento de la entidad suministrada.
	 * 
	 * @param entidad La entidad que est� potencialmente en movimiento a trav�s de la baldosa especificado.
	 * @param x La coordenada x de la baldosa a comprobar
	 * @param y La coordenada y de la baldosa a comprobar
	 * @return True Si la ubicaci�n est� bloqueada
	 */
	public boolean bloquea(Entidad entidad, int x, int y);
	
	/**
	 * Obtener el coste de movimiento a trav�s de la baldosa dada. Esto se puede utilizar para hacer ciertas �reas m�s deseables. 
	 * Una implementaci�n sencilla y v�lida de este m�todo ser�a devolver 1 en todos los casos.
	 * 
	 * @param entidad La entidad que est� tratando de moverse a trav�s de la baldosa
	 * @param sx La coordenada x de la baldosa que estamos moviendo, origen 
	 * @param sy La coordenada y de la baldosa que estamos moviendo, origen 
	 * @param tx La coordenada x de la baldosa a la que nos estamos moviendo
	 * @param ty La coordenada y de la baldosa a la que nos estamos moviendo
	 * @return El coste relativo de moverse a trav�s de la baldosa dada
	 */
	public float getCosto(Entidad entidad, int sx, int sy, int tx, int ty);
}