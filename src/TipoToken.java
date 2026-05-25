public enum TipoToken {
    // Palabras reservadas
    INT("int"), 
    IF("if"), 
    ELSE("else"), 
    MAIN("main"),
    
    // Símbolos y operadores (Ojo: IGUAL_IGUAL debe ir antes que IGUAL)
    IGUAL_IGUAL("=="),
    IGUAL("="), 
    MAYOR_QUE(">"), 
    MENOR_QUE("<"), 
    
    // Delimitadores
    PUNTO_Y_COMA(";"), 
    COMA(","),
    PARENTESIS_IZQ("\\("), 
    PARENTESIS_DER("\\)"),
    LLAVE_IZQ("\\{"), 
    LLAVE_DER("\\}"),
    
    // Valores y variables
    NUMERO("\\d+"),
    IDENTIFICADOR("[a-zA-Z_][a-zA-Z0-9_]*"), 
    
    EOF(""); // Fin de archivo

    public final String patron;
    
    TipoToken(String patron) {
        this.patron = patron;
    }
}