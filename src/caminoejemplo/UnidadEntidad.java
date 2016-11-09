package caminoejemplo;

import util.buscadorcamino.Entidad;

/**
 * Una entidad basada en ID en nuestro mapa de juegos de ejemplo
 */
public class UnidadEntidad implements Entidad {
	/** El ID de la unidad en movimiento */
	private int tipo;
	
	/**
	 * Crear una nueva entidad para ser utilizado mientras el " pathfinder"
	 * 
	 * @param tipo El ID de la unidad en movimiento
	 */
	public UnidadEntidad(int tipo) {
		this.tipo = tipo;
	}
	
	/**
	 * Obtener el ID de la unidad en movimiento
	 * 
	 * @return ID de la unidad en movimiento
	 */
	public int getTipo() {
		return tipo;
	}
}