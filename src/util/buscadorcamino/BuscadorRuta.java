package util.buscadorcamino;

/**
 * A description of an implementation that can find a path from one 
 * location on a tile map to another based on information provided
 * by that tile map.
 * 
 * @see MapaConBaldosas
 */
public interface BuscadorRuta {

	/**
	 * Find a path from the starting location provided (sx,sy) to the target
	 * location (tx,ty) avoiding blockages and attempting to honour costs 
	 * provided by the tile map.
	 * 
	 * @param mover The entity that will be moving along the path. This provides
	 * a place to pass context information about the game entity doing the moving, e.g.
	 * can it fly? can it swim etc.
	 * 
	 * @param sx La coordenada x de la ubicación de inicio
	 * @param sy La coordenada y de la ubicación de inicio
	 * @param tx La coordenada x de la ubicación de destino
	 * @param ty La coordenada y de la ubicación de destino
	 * @return La ruta encontrada de principio a fin, o null si no se puede encontrar ningún camino.
	 */
	public Camino encontrarCamino(Mover mover, int sx, int sy, int tx, int ty);
}