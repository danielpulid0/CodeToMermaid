public enum TipoToken {
	// Palabras reservadas
	INT("int"),
	FLOAT("float"),
	DOUBLE("double"),
	IF("if"),
	ELSE("else"),
	MAIN("main"),
	WHILE("while"),
	DO("do"),
	FOR("for"),
	SCANF("scanf"),
	RETURN("return"),
	SWITCH("switch"),
	CASE("case"),
	DEFAULT("default"),
	BREAK("break"),
	EXIT("exit"),

	// Símbolos y operadores
	IGUAL_IGUAL("=="),
	DIFERENTE("!="),
	IGUAL("="),
	MAYOR_QUE(">"),
	MENOR_QUE("<"),
	AMPERSAND("&"),
	OR("\\|\\|"),

	// Operadores aritméticos
	SUMA("\\+"),             // Escapado para Regex
	RESTA("-"),
	MULTIPLICACION("\\*"),   // Escapado para Regex
	DIVISION("/"),

	// Delimitadores
	PUNTO_Y_COMA(";"),
	DOS_PUNTOS(":"),
	COMA(","),
	PARENTESIS_IZQ("\\("),
	PARENTESIS_DER("\\)"),
	LLAVE_IZQ("\\{"),
	LLAVE_DER("\\}"),
	CORCHETE_IZQ("\\["),
	CORCHETE_DER("\\]"),
	INTERROGACION("\\?"),

	PREPROCESSOR("#[^\n]*"),	// captura toda la linea #include <...>
	VOID("void"),
	PRINTF("printf"),
	LLAMADA_FUNC("[a-zA-Z_][a-zA-Z0-9_]*(?=\\s*\\()"), // lookahead al paréntesis

	// Valores y variables
	NUMERO("\\d+(\\.\\d+)?"),
	IDENTIFICADOR("[a-zA-Z_][a-zA-Z0-9_]*"),
	CADENA("\"[^\"]*\""),

	EOF(""); // Fin de archivo

	public final String patron;

	TipoToken(String patron) {
		this.patron = patron;
	}
}