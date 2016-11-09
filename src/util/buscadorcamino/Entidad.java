package util.buscadorcamino;

/**
 *Una interfaz para un objeto que representa a la entidad en el juego 
 *que va a moverse a lo largo de la ruta. Esto nos permite pasar información entera / estado
 * para determinar si una baldosa en particular está bloqueada, o 
 * cuánto costo tiene asociada una baldosa en particular.
 * 
 * Por ejemplo, una entidad podría representar un tanque o un avión en un mapa de juego.
 *  Pasar alrededor de esta entidad nos permite determinar si el terreno rugoso en un 
 *  mapa debe afectar el costo de la unidad para moverse a través de la baldosa.
 */
public interface Entidad {

}