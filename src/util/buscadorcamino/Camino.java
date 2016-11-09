package util.buscadorcamino;

import java.util.ArrayList;

/**
 * Un camino determinado por alg�n algoritmo de b�squeda de camino. 
 * Una serie de pasos desde la ubicaci�n de inicio hasta la ubicaci�n de destino. 
 * Esto incluye un paso para la ubicaci�n inicial.
 */
public class Camino {
	
	private ArrayList<Paso> pasos = new ArrayList<>();/** La lista de pasos que construyen este camino */
	
	public Camino() {
		
	}

	/**
	 * Obtener la longitud de la ruta, i.s. El n�mero de pasos
	 * 
	 * @return El n�mero de pasos en esta ruta
	 */
	public int getLength() {
		return pasos.size();
	}
	
	/**
	 * Obtener el paso en un �ndice dado de la ruta
	 * 
	 * @param indice El �ndice del paso a recuperar. >= 0 and < getLength();
	 * @return La informaci�n del paso, la posici�n en el mapa.
	 */
	public Paso getPaso(int indice) {
		return (Paso) pasos.get(indice);
	}
	
	/**
	 * Obtener la coordenada x para el paso en el �ndice dado
	 * 
	 * @param indice El �ndice del paso cuya coordenada x debe ser recuperada
	 * @return La coordenada x en el paso
	 */
	public int getX(int indice) {
		return getPaso(indice).x;
	}

	/**
	 * Obtener la coordenada y para el paso en el �ndice dado
	 * 
	 * @param indice El �ndice del paso cuya coordenada y debe ser recuperada
	 * @return La coordenada y en el paso
	 */
	public int getY(int indice) {
		return getPaso(indice).y;
	}
	
	/**
	 * A�ade un paso a la ruta.
	 * 
	 * @param x La coordenada x del nuevo paso
	 * @param y La coordenada y del nuevo paso
	 */
	public void agregarPaso(int x, int y) {
		pasos.add(new Paso(x,y));
	}

	/**
	 * El paso anterior en el camino
	 * 
	 * @param x La coordenada x del nuevo paso
	 * @param y La coordenada y del nuevo paso
	 */
	public void pasoPrevio(int x, int y) {
		pasos.add(0, new Paso(x, y));
	}
	
	/**
	 * Compruebe si esta ruta contiene el paso dado
	 * 
	 * @param x La coordenada x del paso para comprobar
	 * @param y La coordenada y del paso para comprobar
	 * @return True Si la ruta contiene el paso dado
	 */
	public boolean contains(int x, int y) {
		return pasos.contains(new Paso(x,y));
	}
	
	/**
	 * Un solo paso dentro del camino
	 */
	public class Paso {
		/** La coordenada x en el paso dado*/
		private int x;
		/** La coordenada y en el paso dado */
		private int y;
		
		/**
		 * Crear un nuevo paso
		 * 
		 * @param x La coordenada x del nuevo paso
		 * @param y La coordenada y del nuevo paso
		 */
		public Paso(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		/**
		 * Obtener la coordenada x del nuevo paso
		 * 
		 * @return La coordenada x del nuevo paso
		 */
		public int getX() {
			return x;
		}

		/**
		 * Obtener la coordenada y del nuevo paso
		 * 
		 * @return La coordenada y del nuevo paso
		 */
		public int getY() {
			return y;
		}
		
		/**
		 * @see Object#hashCode()
		 */
		public int hashCode() {
			return x*y;
		}

		/**
		 * @see Object#equals(Object)
		 */
		public boolean equals(Object other) {
			if (other instanceof Paso) {
				Paso o = (Paso) other;
				
				return (o.x == x) && (o.y == y);
			}
			
			return false;
		}
	}
}