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
	private Image buffer;/** The offscreen buffer used for rendering in the wonder world of Java 2D */
	private int seleccionadox = -1;/** The x coordinate of selected unit or -1 if none is selected */
	private int seleccionadoy = -1;/** The y coordinate of selected unit or -1 if none is selected */
	private int ultimoEncontradoX = -1;/** The x coordinate of the target of the last path we searched for - used to cache and prevent constantly re-searching */
	private int ultimoEncontradoY = -1;/** The y coordinate of the target of the last path we searched for - used to cache and prevent constantly re-searching */
	
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
	 * Load a resource based on a file reference
	 * 
	 * @param ref The reference to the file to load
	 * @return The stream loaded from either the classpath or file system
	 * @throws IOException Indicates a failure to read the resource
	 */
	private InputStream getResource(String ref) throws IOException {
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(ref);
		if (in != null) {
			return in;
		}
		
		return new FileInputStream(ref);
	}

	/**
	 * Handle the mouse being moved. In this case we want to find a path from the
	 * selected unit to the position the mouse is at
	 * 
	 * @param x The x coordinate of the mouse cursor on the screen
	 * @param y The y coordinate of the mouse cursor on the screen
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
				camino = buscador.encontrarCamino(new UnitMover(mapa.getAgente(seleccionadox, seleccionadoy)), 
									   seleccionadox, seleccionadoy, x, y);
				repaint(0);
			}
		}
	}
	/**
	 * Handle the mouse being pressed. If the mouse is over a unit select it. Otherwise we move
	 * the selected unit to the new target (assuming there was a path found)
	 * 
	 * @param x The x coordinate of the mouse cursor on the screen
	 * @param y The y coordinate of the mouse cursor on the screen
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
				camino = buscador.encontrarCamino(new UnitMover(mapa.getAgente(seleccionadox, seleccionadoy)), 
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
		if (buffer == null) {// create an offscreen buffer to render the map
			buffer = new BufferedImage(600, 600, BufferedImage.TYPE_INT_ARGB);			
		}
		Graphics g = buffer.getGraphics();
		
		g.clearRect(0,0,600,600);
		g.translate(50, 50);
		
		// cycle through the agentesvisual in the map drawing the appropriate
		// image for the terrain and units where appropriate

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

		if (seleccionadox != -1) {// if a unit is selected then draw a box around it
			g.setColor(Color.black);
			g.drawRect(seleccionadox*16, seleccionadoy*16, 15, 15);
			g.drawRect((seleccionadox*16)-2, (seleccionadoy*16)-2, 19, 19);
			g.setColor(Color.white);
			g.drawRect((seleccionadox*16)-1, (seleccionadoy*16)-1, 17, 17);
		}
		
		// finally draw the buffer to the real graphics context in one
		// atomic action
		graphics.drawImage(buffer, 0, 0, null);
	}
	
	public static void main(String[] argv) {
		Prueba prueba = new Prueba();
	}
}