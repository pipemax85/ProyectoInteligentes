package util.buscadorcamino.heuristicas;

import util.buscadorcamino.AEstrellaHeuristica;
import util.buscadorcamino.Entidad;
import util.buscadorcamino.MapaConBaldosas;

/**
 * A heuristic that uses the tile that is closest to the target
 * as the next best tile.
 * 
 */
public class HeuristicaCercana implements AEstrellaHeuristica {
	/**
	 * @see AStarHeuristic#getCost(TileBasedMap, Mover, int, int, int, int)
	 */
	public float getCosto(MapaConBaldosas map, Entidad entidad, int x, int y, int tx, int ty) {		
		float dx = tx - x;
		float dy = ty - y;
		
		float result = (float) (Math.sqrt((dx*dx)+(dy*dy)));
		
		return result;
	}

}