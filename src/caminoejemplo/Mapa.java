package caminoejemplo;

import util.buscadorcamino.MapaConBaldosas;
import util.buscadorcamino.Mover;;

/**
 * El mapa de nuestro juego. Contiene la configuracion de cada baldosa en . 
 * Implementa la interfaz del buscador de rutas.  Es la implementación de 
 * los métodos relacionados con el buscador de rutas, agregando manejo específico 
 * para los tipos de unidades y el terreno en el juego de ejemplo.
 */
public class Mapa implements MapaConBaldosas {
	
	public static final int ANCHO = 30;/** Ancho del mapa en baldosas */
	public static final int ALTURA = 30;/** Altura del mapa en baldosas */
	public static final int CESPED = 0;
	public static final int AGUA = 1;
	public static final int ARBOLES = 2;
	public static final int AVION = 3;
	public static final int BARCO = 4;
	public static final int TANQUE = 5;
	private int[][] terreno = new int[ANCHO][ALTURA];/** Configuracion del terreno para cada una de sus baldosas */
	private int[][] agentes = new int[ANCHO][ALTURA];/** Los lugares donde estan los agentes */
	private boolean[][] visitado = new boolean[ANCHO][ALTURA];/** Indica las baldosas que han sido visitadas en la busqueda de la ruta */
	
	
	public Mapa() {/**Crea un mapa con algunas configuraciones por defecto*/
		llenarTerreno(0,0,5,5,AGUA);
		llenarTerreno(0,5,3,10,AGUA);
		llenarTerreno(0,5,3,10,AGUA);
		llenarTerreno(0,15,7,15,AGUA);
		llenarTerreno(7,26,22,4,AGUA);
		
		llenarTerreno(17,5,10,3,ARBOLES);
		llenarTerreno(20,8,5,3,ARBOLES);
		
		llenarTerreno(8,2,7,3,ARBOLES);
		llenarTerreno(10,5,3,3,ARBOLES);
		
		agentes[15][15] = TANQUE;
		agentes[2][7] = BARCO;
		agentes[20][25] = AVION;
	}

	/**
	 * Llena un area con cierto tipo de terreno
	 * 
	 * @param x La coordenada en x donde empieza a llenar
	 * @param y La coordenada en y donde empieza a llenar
	 * @param ancho Ancho de area a llenar
	 * @param altura Altura de area a llenar
	 * @param tipo Tipo de terreno con el que se va a llenar
	 */
	private void llenarTerreno(int x, int y, int ancho, int altura, int tipo) {
		for (int xp=x;xp<x+ancho;xp++) {
			for (int yp=y;yp<y+altura;yp++) {
				terreno[xp][yp] = tipo;
			}
		}
	}
	
	public void limpiarVisitados() {/**Limpia la matriz en la que se llenan los espacios que han sido visitados en la busca de una ruta*/
		for (int x=0;x<getAnchoEnBaldosas();x++) {
			for (int y=0;y<getAlturaEnBaldosas();y++) {
				visitado[x][y] = false;
			}
		}
	}
	
	public boolean visitado(int x, int y) {
		return visitado[x][y];
	}

	public int getTerreno(int x, int y) {
		return terreno[x][y];
	}
	
	public int getAgente(int x, int y) {
		return agentes[x][y];
	}
	
	public void setAgente(int x, int y, int unit) {
		agentes[x][y] = unit;
	}
	
	public boolean bloquea(Mover mover, int x, int y) {// Bloquea los movimientos dependiendo de que sea
		if (getAgente(x,y) != 0) {
			return true;
		}
		int unit = ((UnitMover) mover).getType();
		if (unit == AVION) {// Los aviones pueden moverse a donde sea
			return false;
		}
		if (unit == TANQUE) {// Los tanques solo pueden moverse en el pasto
			return terreno[x][y] != CESPED;
		}	
		if (unit == BARCO) {// Los barcos solo pueden moverse en el agua
			return terreno[x][y] != AGUA;
		}
		return true;
	}
	
	public float getCosto(Mover mover, int sx, int sy, int tx, int ty) {
		return 1;
	}

	public int getAlturaEnBaldosas() {
		return ALTURA;
	}

	public int getAnchoEnBaldosas() {
		return ANCHO;
	}

	public void buscadorRutaVisitado(int x, int y) {
		visitado[x][y] = true;
	}
	
	
}