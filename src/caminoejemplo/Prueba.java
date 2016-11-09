package caminoejemplo;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import util.buscadorcamino.AEstrellaBuscadorCamino;
import util.buscadorcamino.BuscadorRuta;
import util.buscadorcamino.Camino;

public class Prueba extends JFrame {
	
	private Mapa mapa= new Mapa();
	private BuscadorRuta buscador;
	private Camino camino;/** La última ruta encontrada para ale agente actual */
	private Image[] imagenes = new Image[6];/** La lista de imágenes que se representan en el mapa */
	private Image buffer;/** El búfer fuera de pantalla usado para renderizar el Java 2D */
	private int seleccionadox = -1;/** La coordenada x de la unidad seleccionada o -1 si no se ha seleccionado ninguna */
	private int seleccionadoy = -1;/** La coordenada y de la unidad seleccionada o -1 si no se ha seleccionado ninguna */
	private int ultimoEncontradoX = -1;/** La coordenada x del objetivo de la última ruta que buscamos - Utilizado para almacenar en caché y evitar volver a buscar innecesariamente*/
	private int ultimoEncontradoY = -1;/** La coordenada y del objetivo de la última ruta que buscamos - Utilizado para almacenar en caché y evitar volver a buscar innecesariamente*/
	
	public Prueba() {
		super("Juego introduccion a la inteligencia artificial");
	
		try {
			imagenes[Mapa.ARBOLES] = ImageIO.read(getResource("res/arboles.png"));
			imagenes[Mapa.CESPED]  = ImageIO.read(getResource("res/cesped.png"));
			imagenes[Mapa.AGUA]    = ImageIO.read(getResource("res/agua.png"));
			imagenes[Mapa.TANQUE]  = ImageIO.read(getResource("res/tanque.png"));
			imagenes[Mapa.AVION]   = ImageIO.read(getResource("res/avion.png"));
			imagenes[Mapa.BARCO]   = ImageIO.read(getResource("res/barco.png"));
		} catch (IOException e) {
			System.err.println("Falla al intentar cargar una imagen: "+e.getMessage());
			System.exit(0);
		}
		
		buscador = new AEstrellaBuscadorCamino(mapa, 500, true);
		
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				hizoClick(e.getX(), e.getY());
			}
		});
		addMouseMotionListener(new MouseMotionListener() {
			public void mouseDragged(MouseEvent e) {
			}

			public void mouseMoved(MouseEvent e) {
				movioRaton(e.getX(), e.getY());
			}
		});
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
		setSize(600,600);
		setResizable(false);
		setVisible(true);
	}
	
	/**
	 * Cargar un recurso basado en una referencia a un archivo
	 * 
	 * @param ref La referencia al archivo a cargar
	 * @return La secuencia cargada desde el classpath o el sistema de archivos
	 * @throws IOException Indica que no se ha podido leer el recurso
	 */
	private InputStream getResource(String ref) throws IOException {
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(ref);
		if (in != null) {
			return in;
		}
		
		return new FileInputStream(ref);
	}

	/**
	 * Cuando se mueve el raton. En este caso queremos encontrar un camino desde 
	 * la unidad seleccionada hasta la posición en que el ratón esta.
	 * 
	 * @param x La coordenada x del cursor del ratón en la pantalla
	 * @param y La coordenada y del cursor del ratón en la pantalla
	 */
	private void movioRaton(int x, int y) {
		x -= 50;
		y -= 50;
		x /= 16;
		y /= 16;
		
		if ((x < 0) || (y < 0) || (x >= mapa.getAnchoEnBaldosas()) || (y >= mapa.getAlturaEnBaldosas())) {
			return;
		}
		
		if (seleccionadox != -1) {
			if ((ultimoEncontradoX != x) || (ultimoEncontradoY != y)) {
				ultimoEncontradoX = x;
				ultimoEncontradoY = y;
				camino = buscador.encontrarCamino(new UnidadEntidad(mapa.getAgente(seleccionadox, seleccionadoy)), 
									   seleccionadox, seleccionadoy, x, y);
				repaint(0);
			}
		}
	}
	/**
	 * Handle the mouse being pressed. If the mouse is over a unit select it. Otherwise we move
	 * the selected unit to the new target (assuming there was a path found)
	 * 
	 * Cuando se le da click. Si el ratón está sobre una unidad, selecciónelo. 
	 * De lo contrario, mover la unidad seleccionada a la nueva meta (suponiendo que había una ruta 
	 * de acceso encontrada)
	 * 
	 * @param x La coordenada x del cursor del ratón en la pantalla
	 * @param y La coordenada y del cursor del ratón en la pantalla
	 */
	private void hizoClick(int x, int y) {
		x -= 50;
		y -= 50;
		x /= 16;
		y /= 16;
		
		if ((x < 0) || (y < 0) || (x >= mapa.getAnchoEnBaldosas()) || (y >= mapa.getAlturaEnBaldosas())) {
			return;
		}
		
		if (mapa.getAgente(x, y) != 0) {
			seleccionadox = x;
			seleccionadoy = y;
			ultimoEncontradoX = - 1;
		} else {
			if (seleccionadox != -1) {
				mapa.limpiarVisitados();
				camino = buscador.encontrarCamino(new UnidadEntidad(mapa.getAgente(seleccionadox, seleccionadoy)), 
						   			   seleccionadox, seleccionadoy, x, y);
				
				if (camino != null) {
					camino = null;
					int unit = mapa.getAgente(seleccionadox, seleccionadoy);
					mapa.setAgente(seleccionadox, seleccionadoy, 0);
					mapa.setAgente(x,y,unit);
					seleccionadox = x;
					seleccionadoy = y;
					ultimoEncontradoX = - 1;
				}
			}
		}
		
		repaint(0);
	}
	
	/**
	 * @see java.awt.Container#paint(java.awt.Graphics)
	 */
	public void paint(Graphics graphics) {	
		if (buffer == null) {// Crear un búfer fuera de pantalla para representar el mapa
			buffer = new BufferedImage(600, 600, BufferedImage.TYPE_INT_ARGB);			
		}
		Graphics g = buffer.getGraphics();
		
		g.clearRect(0,0,600,600);
		g.translate(50, 50);
		
		// Ciclo a través de los agentes visuales en el mapa dibujando la imagen 
		// apropiada para el terreno y unidades donde sea apropiado

		for (int x=0;x<mapa.getAnchoEnBaldosas();x++) {
			for (int y=0;y<mapa.getAlturaEnBaldosas();y++) {
				g.drawImage(imagenes[mapa.getTerreno(x, y)],x*16,y*16,null);
				if (mapa.getAgente(x, y) != 0) {
					g.drawImage(imagenes[mapa.getAgente(x, y)],x*16,y*16,null);
				} else {
					if (camino != null) {
						if (camino.contains(x, y)) {
							g.setColor(Color.blue);
							g.fillRect((x*16)+4, (y*16)+4,7,7);
						}
					}	
				}
			}
		}

		if (seleccionadox != -1) {// Si se selecciona una unidad, dibuja una caja alrededor de ella
			g.setColor(Color.black);
			g.drawRect(seleccionadox*16, seleccionadoy*16, 15, 15);
			g.drawRect((seleccionadox*16)-2, (seleccionadoy*16)-2, 19, 19);
			g.setColor(Color.white);
			g.drawRect((seleccionadox*16)-1, (seleccionadoy*16)-1, 17, 17);
		}
		
		//Finalmente dibujar el búfer en el contexto gráfico real en una acción atómica
		graphics.drawImage(buffer, 0, 0, null);
	}
	
	public static void main(String[] argv) {
		Prueba prueba = new Prueba();
	}
}