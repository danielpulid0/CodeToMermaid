public enum TipoToken {
	// Palabras reservadas
	INT("int"),
	IF("if"),
	ELSE("else"),
	MAIN("main"),
	WHILE("while"),
	FOR("for"),
	SCANF("scanf"),
	RETURN("return"),

	// Símbolos y operadores
	IGUAL_IGUAL("=="),
	IGUAL("="),
	MAYOR_QUE(">"),
	MENOR_QUE("<"),
	AMPERSAND("&"),

	// Operadores aritméticos
	SUMA("\\+"),             // Escapado para Regex
	RESTA("-"),
	MULTIPLICACION("\\*"),   // Escapado para Regex
	DIVISION("/"),

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
	CADENA("\"[^\"]*\""),

	EOF(""); // Fin de archivo

	public final String patron;

	TipoToken(String patron) {
		this.patron = patron;
	}
}