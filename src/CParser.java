import java.util.List;
import java.util.ArrayList;

public class CParser {
	private List<Token> tokens;
	private int indiceToken;

	public CParser(List<Token> tokens) {
		this.tokens = tokens;
		this.indiceToken = 0;
	}

	// Método principal que inicia el análisis
	public NodoPrograma analizar() throws Exception {
		return Programa();
	}

	// <Programa> ::= { <Directiva> } { <Prototipo> } "int" "main" "(" ")" <Bloque> { <DefinicionFuncion> }
	private NodoPrograma Programa() throws Exception {
		// 1. Consumir directivas #include (ignorar o registrar)
		while (currentToken(TipoToken.PREPROCESSOR)) {
			indiceToken++;
		}

		// 2. Recopilar prototipos (void foo();) antes de main
		List<NodoPrototipo> prototipos = new ArrayList<>();
		while (!currentToken(TipoToken.INT) || !esMain(indiceToken + 1)) {
			if (currentToken(TipoToken.EOF)) break;
			prototipos.add(Prototipo()); // void IDENT ( ) ;
		}

		// 3. int main() { ... }
		match(TipoToken.INT);
		match(TipoToken.MAIN);
		match(TipoToken.PARENTESIS_IZQ);
		match(TipoToken.PARENTESIS_DER);
		NodoBloque bloqueMain = Bloque();

		// 4. Definiciones de función después de main
		List<NodoDefinicionFuncion> funciones = new ArrayList<>();
		while (!currentToken(TipoToken.EOF)) {
			funciones.add(DefinicionFuncion());
		}

		return new NodoPrograma(prototipos, bloqueMain, funciones);
	}

	private boolean esMain(int idx) {
		if (idx >= tokens.size()) return false;
		return tokens.get(idx).getTipo() == TipoToken.MAIN;
	}

	private boolean esTipoActual() {
		return currentToken(TipoToken.INT) || currentToken(TipoToken.FLOAT) || currentToken(TipoToken.DOUBLE) || currentToken(TipoToken.VOID);
	}

	private void matchTipo() throws Exception {
		if (esTipoActual()) {
			indiceToken++;
		} else {
			throw new Exception("Error Sintáctico: Se esperaba tipo (int, float, double, void) pero se encontro " + tokens.get(indiceToken).getNombre());
		}
	}

	private String ExtraerParametros() throws Exception {
		StringBuilder params = new StringBuilder();
		while (!currentToken(TipoToken.PARENTESIS_DER) && !currentToken(TipoToken.EOF)) {
			params.append(tokens.get(indiceToken).getNombre());
			if (tokens.get(indiceToken).getTipo() == TipoToken.COMA) {
				params.append(" "); // Añadir espacio después de coma
			} else if (tokens.get(indiceToken).getTipo() != TipoToken.AMPERSAND) {
				params.append(" ");
			}
			indiceToken++;
		}
		return params.toString().trim();
	}

	// <Prototipo> ::= <Tipo> IDENTIFICADOR "(" ")" ";"
	private NodoPrototipo Prototipo() throws Exception {
		String tipo = tokens.get(indiceToken).getNombre();
		matchTipo();

		String nombre = tokens.get(indiceToken).getNombre();
		if (currentToken(TipoToken.LLAMADA_FUNC)) match(TipoToken.LLAMADA_FUNC);
		else match(TipoToken.IDENTIFICADOR);

		match(TipoToken.PARENTESIS_IZQ);
		String parametros = ExtraerParametros();
		match(TipoToken.PARENTESIS_DER);
		match(TipoToken.PUNTO_Y_COMA);

		return new NodoPrototipo(tipo, nombre, parametros);
	}

	// <DefinicionFuncion> ::= <Tipo> IDENTIFICADOR "(" ")" <Bloque>
	private NodoDefinicionFuncion DefinicionFuncion() throws Exception {
		String tipo = tokens.get(indiceToken).getNombre();
		matchTipo();

		String nombre = tokens.get(indiceToken).getNombre();
		if (currentToken(TipoToken.LLAMADA_FUNC)) match(TipoToken.LLAMADA_FUNC);
		else match(TipoToken.IDENTIFICADOR);

		match(TipoToken.PARENTESIS_IZQ);
		String parametros = ExtraerParametros();
		match(TipoToken.PARENTESIS_DER);

		NodoBloque bloque = Bloque();

		return new NodoDefinicionFuncion(tipo, nombre, parametros, bloque);
	}

	// <Bloque> ::= "{" { <Instruccion> } "}"
	private NodoBloque Bloque() throws Exception {
		NodoBloque bloque = new NodoBloque();
		match(TipoToken.LLAVE_IZQ);

		// Mientras no encontremos la llave de cierre, seguimos parseando instrucciones
		while (!currentToken(TipoToken.LLAVE_DER) && !currentToken(TipoToken.EOF)) {
			NodoTablaSimbolo instruccion = Instruccion();
			if (instruccion != null) {
				bloque.addInstruccion(instruccion);
			}
		}

		match(TipoToken.LLAVE_DER);
		return bloque;
	}

	// <Instruccion> ::= <Declaracion> | <Asignacion> | <SentenciaIf>
	private NodoTablaSimbolo Instruccion() throws Exception {
		if (esTipoActual()) {
			return Declaracion();
		} else if (currentToken(TipoToken.LLAMADA_FUNC)) {
			return SentenciaLlamadaFuncion(null);
		} else if (currentToken(TipoToken.IDENTIFICADOR)) {
			// Detectar si es asignación (x = ...) o llamada (foo();)
			if (indiceToken + 1 < tokens.size() && tokens.get(indiceToken + 1).getTipo() == TipoToken.IGUAL) {
				if (indiceToken + 2 < tokens.size() && tokens.get(indiceToken + 2).getTipo() == TipoToken.LLAMADA_FUNC) {
					String nombreVariable = tokens.get(indiceToken).getNombre();
					match(TipoToken.IDENTIFICADOR);
					match(TipoToken.IGUAL);
					return SentenciaLlamadaFuncion(nombreVariable + " = ");
				} else {
					return Asignacion();
				}
			} else {
				return SentenciaLlamadaFuncion(null);
			}
		} else if (currentToken(TipoToken.PRINTF)) {
			return SentenciaPrintf(null);
		} else if (currentToken(TipoToken.IF)) {
			return SentenciaIf();
		} else if (currentToken(TipoToken.WHILE)) {
			return SentenciaWhile();
		} else if (currentToken(TipoToken.FOR)) {
			return SentenciaFor();
		}else if (currentToken(TipoToken.SCANF)) {
			return SentenciaScanf();
		}else if (currentToken(TipoToken.RETURN)) {
			return SentenciaReturn();
		}else {
			throw new Exception("Error Sintáctico: Instrucción no reconocida en '" + tokens.get(indiceToken).getNombre() + "'");
		}
	}

	// <SentenciaLlamadaFuncion> ::= IDENTIFICADOR "(" ")" ";"
	private NodoLlamadaFuncion SentenciaLlamadaFuncion(String asignadoA) throws Exception {
		String nombre = tokens.get(indiceToken).getNombre();
		if (currentToken(TipoToken.LLAMADA_FUNC)) match(TipoToken.LLAMADA_FUNC);
		else match(TipoToken.IDENTIFICADOR);
		match(TipoToken.PARENTESIS_IZQ);
		String parametros = ExtraerParametros();
		match(TipoToken.PARENTESIS_DER);
		match(TipoToken.PUNTO_Y_COMA);
		return new NodoLlamadaFuncion(nombre, asignadoA, parametros);
	}

	// <SentenciaPrintf> ::= "printf" "(" <Cadena> ")" ";"
	private NodoLlamadaFuncion SentenciaPrintf(String asignadoA) throws Exception {
		match(TipoToken.PRINTF);
		match(TipoToken.PARENTESIS_IZQ);
		String parametros = ExtraerParametros();
		match(TipoToken.PARENTESIS_DER);
		match(TipoToken.PUNTO_Y_COMA);
		return new NodoLlamadaFuncion("printf", asignadoA, parametros);
	}

	// <SentenciaReturn> ::= "return" [ <Valor> ] ";"
	private NodoReturn SentenciaReturn() throws Exception {
		match(TipoToken.RETURN);

		StringBuilder expr = new StringBuilder();
		while (!currentToken(TipoToken.PUNTO_Y_COMA) && !currentToken(TipoToken.EOF)) {
			expr.append(tokens.get(indiceToken).getNombre()).append(" ");
			indiceToken++;
		}

		match(TipoToken.PUNTO_Y_COMA);
		return new NodoReturn(expr.toString().trim());
	}

	// <SentenciaScanf> ::= "scanf" "(" <Cadena> "," "&" <Identificador> ")" ";"
	private NodoLectura SentenciaScanf() throws Exception {
		match(TipoToken.SCANF);
		match(TipoToken.PARENTESIS_IZQ);

		match(TipoToken.CADENA);
		match(TipoToken.COMA);
		match(TipoToken.AMPERSAND);

		String nombreVariable = tokens.get(indiceToken).getNombre();
		match(TipoToken.IDENTIFICADOR);

		match(TipoToken.PARENTESIS_DER);
		match(TipoToken.PUNTO_Y_COMA);

		return new NodoLectura(nombreVariable);
	}

	// <SentenciaFor> ::= "for" "(" <AsignacionSencilla> ";" <Condicion> ";" <ActualizacionSencilla> ")" <Bloque>
	private NodoFor SentenciaFor() throws Exception {
		match(TipoToken.FOR);
		match(TipoToken.PARENTESIS_IZQ);

		// 1. Parsear la inicialización (ej: i = 0)
		String varInit = tokens.get(indiceToken).getNombre();
		match(TipoToken.IDENTIFICADOR);
		match(TipoToken.IGUAL);
		String valInit = tokens.get(indiceToken).getNombre();
		match(TipoToken.NUMERO);
		match(TipoToken.PUNTO_Y_COMA);
		String inicializacion = varInit + " = " + valInit;

		// 2. Parsear la condición (ej: i < 10)
		String opIzq = tokens.get(indiceToken).getNombre();
		match(TipoToken.IDENTIFICADOR);
		String operador = tokens.get(indiceToken).getNombre();
		if (currentToken(TipoToken.MAYOR_QUE)) match(TipoToken.MAYOR_QUE);
		else match(TipoToken.MENOR_QUE);
		String opDer = tokens.get(indiceToken).getNombre();
		match(TipoToken.NUMERO);
		match(TipoToken.PUNTO_Y_COMA);
		String condicion = opIzq + " " + operador + " " + opDer;

		// 3. Parsear la actualización (ej: i = i + 1 o simplificado)
		// Nota: para mantener el avance simple, asumiremos un formato de asignación corta o id
		String varStep = tokens.get(indiceToken).getNombre();
		match(TipoToken.IDENTIFICADOR);
		// Consumimos lo que quede de la expresión de incremento hasta el paréntesis de cierre
		while(!currentToken(TipoToken.PARENTESIS_DER)) {
			indiceToken++;
		}
		String actualizacion = varStep + "++";

		match(TipoToken.PARENTESIS_DER);

		// 4. Parsear el cuerpo interno
		NodoBloque bloque = Bloque();

		return new NodoFor(inicializacion, condicion, actualizacion, bloque);
	}

	// <SentenciaWhile> ::= "while" "(" <Condicion> ")" <Bloque>
	private NodoWhile SentenciaWhile() throws Exception {
		match(TipoToken.WHILE);
		match(TipoToken.PARENTESIS_IZQ);

		// Usamos el mismo parseo simplificado de condición que usaste en el IF
		String opIzq = tokens.get(indiceToken).getNombre();
		match(TipoToken.IDENTIFICADOR);

		String operador = tokens.get(indiceToken).getNombre();
		if (currentToken(TipoToken.MAYOR_QUE)) match(TipoToken.MAYOR_QUE);
		else if (currentToken(TipoToken.MENOR_QUE)) match(TipoToken.MENOR_QUE);
		else if (currentToken(TipoToken.DIFERENTE)) match(TipoToken.DIFERENTE);
		else match(TipoToken.IGUAL_IGUAL);

		String opDer = tokens.get(indiceToken).getNombre();
		if (currentToken(TipoToken.NUMERO)) match(TipoToken.NUMERO);
		else match(TipoToken.IDENTIFICADOR);

		match(TipoToken.PARENTESIS_DER);

		String condicion = opIzq + " " + operador + " " + opDer;

		// Extraemos todo el contenido del bucle
		NodoBloque bloque = Bloque();

		return new NodoWhile(condicion, bloque);
	}

	// <Declaracion> ::= <Tipo> <Identificador> [ "=" <Valor> ] ";"
	private NodoTablaSimbolo Declaracion() throws Exception {
		String tipo = tokens.get(indiceToken).getNombre();
		matchTipo();
		
		String nombreVariable = tokens.get(indiceToken).getNombre();
		match(TipoToken.IDENTIFICADOR);

		if (currentToken(TipoToken.IGUAL)) {
			match(TipoToken.IGUAL);
			// Ver qué hay a la derecha
			if (currentToken(TipoToken.LLAMADA_FUNC)) {
				NodoLlamadaFuncion llamada = SentenciaLlamadaFuncion(tipo + " " + nombreVariable + " = ");
				return llamada; // esto ya consumio el ;
			} else {
				// es valor o expresion. Lo mas facil es leer hasta el ;
				StringBuilder expr = new StringBuilder();
				while (!currentToken(TipoToken.PUNTO_Y_COMA) && !currentToken(TipoToken.EOF)) {
					expr.append(tokens.get(indiceToken).getNombre()).append(" ");
					indiceToken++;
				}
				match(TipoToken.PUNTO_Y_COMA);
				return new NodoDeclaracion(tipo, nombreVariable, expr.toString().trim());
			}
		}

		match(TipoToken.PUNTO_Y_COMA);
		return new NodoDeclaracion(tipo, nombreVariable);
	}

	// <Asignacion> ::= <Identificador> "=" <Valor> [ <OperadorAritmetico> <Valor> ] ";"
	private NodoAsignacion Asignacion() throws Exception {
		String nombreVariable = tokens.get(indiceToken).getNombre();
		match(TipoToken.IDENTIFICADOR);
		match(TipoToken.IGUAL);

		StringBuilder expresion = new StringBuilder();

		// 1. Leemos el primer valor (ej. la 'x' en x = x * 2)
		expresion.append(tokens.get(indiceToken).getNombre());
		if (currentToken(TipoToken.NUMERO)) match(TipoToken.NUMERO);
		else match(TipoToken.IDENTIFICADOR);

		// 2. ¿Hay una operación aritmética después? (+, -, *, /)
		if (currentToken(TipoToken.SUMA) || currentToken(TipoToken.RESTA) ||
				currentToken(TipoToken.MULTIPLICACION) || currentToken(TipoToken.DIVISION)) {

			// Extraemos y guardamos el signo
			expresion.append(" ").append(tokens.get(indiceToken).getNombre()).append(" ");

			// Consumimos el token correcto
			if (currentToken(TipoToken.SUMA)) match(TipoToken.SUMA);
			else if (currentToken(TipoToken.RESTA)) match(TipoToken.RESTA);
			else if (currentToken(TipoToken.MULTIPLICACION)) match(TipoToken.MULTIPLICACION);
			else match(TipoToken.DIVISION);

			// Leemos el segundo valor (ej. el '2' en x = x * 2)
			expresion.append(tokens.get(indiceToken).getNombre());
			if (currentToken(TipoToken.NUMERO)) match(TipoToken.NUMERO);
			else match(TipoToken.IDENTIFICADOR);
		}

		match(TipoToken.PUNTO_Y_COMA);

		return new NodoAsignacion(nombreVariable, expresion.toString());
	}

	// <SentenciaIf> ::= "if" "(" <Condicion> ")" <Bloque> [ "else" <Bloque> ]
	private NodoIfElse SentenciaIf() throws Exception {
		match(TipoToken.IF);
		match(TipoToken.PARENTESIS_IZQ);

		// Condición simple: ej. "x > 10"
		String opIzq = tokens.get(indiceToken).getNombre();
		match(TipoToken.IDENTIFICADOR); // x

		String operador = tokens.get(indiceToken).getNombre();
		if (currentToken(TipoToken.MAYOR_QUE)) match(TipoToken.MAYOR_QUE);
		else if (currentToken(TipoToken.MENOR_QUE)) match(TipoToken.MENOR_QUE);
		else if (currentToken(TipoToken.DIFERENTE)) match(TipoToken.DIFERENTE);
		else match(TipoToken.IGUAL_IGUAL);

		String opDer = tokens.get(indiceToken).getNombre();
		if (currentToken(TipoToken.NUMERO)) match(TipoToken.NUMERO);
		else match(TipoToken.IDENTIFICADOR); // 10

		match(TipoToken.PARENTESIS_DER);

		String condicion = opIzq + " " + operador + " " + opDer;

		// Parseamos el bloque True
		NodoBloque bloqueTrue = Bloque();
		NodoBloque bloqueFalse = null;

		// ¿Hay un ELSE opcional?
		if (currentToken(TipoToken.ELSE)) {
			match(TipoToken.ELSE);
			bloqueFalse = Bloque();
		}

		return new NodoIfElse(condicion, bloqueTrue, bloqueFalse);
	}

	// --- Métodos Auxiliares del Parser ---

	private void match(TipoToken tipoEsperado) throws Exception {
		if (currentToken(tipoEsperado)) {
			indiceToken++;
		} else {
			throw new Exception("Error Sintáctico: Se esperaba " + tipoEsperado + " pero se encontró " + tokens.get(indiceToken).getTipo() + " ('" + tokens.get(indiceToken).getNombre() + "')");
		}
	}

	private boolean currentToken(TipoToken tipo) {
		if (indiceToken >= tokens.size()) return false;
		return tokens.get(indiceToken).getTipo() == tipo;
	}
}