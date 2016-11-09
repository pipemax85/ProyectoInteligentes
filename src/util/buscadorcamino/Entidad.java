package util.buscadorcamino;

/**
 *Una interfaz para un objeto que representa a la entidad en el juego 
 *que va a moverse a lo largo de la ruta. Esto nos permite pasar informaci�n entera / estado
 * para determinar si una baldosa en particular est� bloqueada, o 
 * cu�nto costo tiene asociada una baldosa en particular.
 * 
 * Por ejemplo, una entidad podr�a representar un tanque o un avi�n en un mapa de juego.
 *  Pasar alrededor de esta entidad nos permite determinar si el terreno rugoso en un 
 *  mapa debe afectar el costo de la unidad para moverse a trav�s de la baldosa.
 */
public interface Entidad {

}