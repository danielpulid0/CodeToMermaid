/*
*   ToDo: PALABRAS RESERVADAS QUE SE UTILIZARAN
*/
public enum TipoToken {
    // Palabras reservadas
    INT, IF, ELSE, MAIN,
    // Símbolos y operadores
    IGUAL, MAYOR_QUE, MENOR_QUE, IGUAL_IGUAL,
    PUNTO_Y_COMA, COMA,
    PARENTESIS_IZQ, PARENTESIS_DER,
    LLAVE_IZQ, LLAVE_DER,
    // Valores y variables
    IDENTIFICADOR, // ej: x, numero
    NUMERO,        // ej: 15, 0, 1
    EOF            // Fin de archivo
}