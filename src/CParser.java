import java.util.List;
import java.util.ArrayList;

public class CParser {
	private List<Token> tokens;
	private int indiceToken;

	public CParser(List<Token> tokens) {
		this.tokens = tokens;
		this.indiceToken = 0;
	}

	public NodoPrograma analizar() throws Exception {
		return Programa();
	}

	private NodoPrograma Programa() throws Exception {
		while (currentToken(TipoToken.PREPROCESSOR)) {
			indiceToken++;
		}

		List<NodoPrototipo> prototipos = new ArrayList<>();
		while (!currentToken(TipoToken.INT) || !esMain(indiceToken + 1)) {
			if (currentToken(TipoToken.EOF)) break;
			prototipos.add(Prototipo());
		}

		match(TipoToken.INT);
		match(TipoToken.MAIN);
		match(TipoToken.PARENTESIS_IZQ);
		match(TipoToken.PARENTESIS_DER);
		NodoBloque bloqueMain = Bloque();

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
				params.append(" ");
			} else if (tokens.get(indiceToken).getTipo() != TipoToken.AMPERSAND) {
				params.append(" ");
			}
			indiceToken++;
		}
		return params.toString().trim();
	}

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

	private NodoBloque Bloque() throws Exception {
		NodoBloque bloque = new NodoBloque();
		match(TipoToken.LLAVE_IZQ);

		while (!currentToken(TipoToken.LLAVE_DER) && !currentToken(TipoToken.EOF)) {
			NodoTablaSimbolo instruccion = Instruccion();
			if (instruccion != null) {
				bloque.addInstruccion(instruccion);
			}
		}

		match(TipoToken.LLAVE_DER);
		return bloque;
	}

	private NodoTablaSimbolo Instruccion() throws Exception {
		if (esTipoActual()) {
			return Declaracion();
		} else if (currentToken(TipoToken.LLAMADA_FUNC)) {
			return SentenciaLlamadaFuncion(null);
		} else if (currentToken(TipoToken.IDENTIFICADOR)) {
			boolean isAsignacion = false;
			boolean isTernario = false;
			int temp = indiceToken;
			while (temp < tokens.size() && tokens.get(temp).getTipo() != TipoToken.PUNTO_Y_COMA && tokens.get(temp).getTipo() != TipoToken.EOF) {
				if (tokens.get(temp).getTipo() == TipoToken.IGUAL) isAsignacion = true;
				if (tokens.get(temp).getTipo() == TipoToken.INTERROGACION) isTernario = true;
				temp++;
			}
			
			if (isAsignacion) {
				return Asignacion();
			} else if (isTernario) {
				return Ternario();
			} else {
				// check if it's actually a function call that was tokenized as IDENTIFICADOR by mistake, or just an expression
				int next = indiceToken + 1;
				if (next < tokens.size() && tokens.get(next).getTipo() == TipoToken.PARENTESIS_IZQ) {
					return SentenciaLlamadaFuncion(null);
				}
				// Otherwise it's a simple expression statement
				return InstruccionSimple();
			}
		} else if (currentToken(TipoToken.PRINTF)) {
			return SentenciaPrintf(null);
		} else if (currentToken(TipoToken.IF)) {
			return SentenciaIf();
		} else if (currentToken(TipoToken.WHILE)) {
			return SentenciaWhile();
		} else if (currentToken(TipoToken.DO)) {
			return SentenciaDoWhile();
		} else if (currentToken(TipoToken.SWITCH)) {
			return SentenciaSwitch();
		} else if (currentToken(TipoToken.FOR)) {
			return SentenciaFor();
		} else if (currentToken(TipoToken.SCANF)) {
			return SentenciaScanf();
		} else if (currentToken(TipoToken.EXIT)) {
			return SentenciaExit();
		} else if (currentToken(TipoToken.RETURN)) {
			return SentenciaReturn();
		} else if (currentToken(TipoToken.BREAK)) {
			return SentenciaBreak();
		} else {
			throw new Exception("Error Sintáctico: Instrucción no reconocida en '" + tokens.get(indiceToken).getNombre() + "'");
		}
	}

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

	private NodoLlamadaFuncion SentenciaPrintf(String asignadoA) throws Exception {
		match(TipoToken.PRINTF);
		match(TipoToken.PARENTESIS_IZQ);
		String parametros = ExtraerParametros();
		match(TipoToken.PARENTESIS_DER);
		match(TipoToken.PUNTO_Y_COMA);
		return new NodoLlamadaFuncion("printf", asignadoA, parametros);
	}

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

	private NodoExit SentenciaExit() throws Exception {
		match(TipoToken.EXIT);
		match(TipoToken.PARENTESIS_IZQ);
		String parametros = ExtraerParametros();
		match(TipoToken.PARENTESIS_DER);
		match(TipoToken.PUNTO_Y_COMA);
		return new NodoExit(parametros);
	}

	private NodoLectura SentenciaScanf() throws Exception {
		match(TipoToken.SCANF);
		match(TipoToken.PARENTESIS_IZQ);

		match(TipoToken.CADENA);
		match(TipoToken.COMA);
		if (currentToken(TipoToken.AMPERSAND)) match(TipoToken.AMPERSAND);

		String nombreVariable = tokens.get(indiceToken).getNombre();
		match(TipoToken.IDENTIFICADOR);

		match(TipoToken.PARENTESIS_DER);
		match(TipoToken.PUNTO_Y_COMA);

		return new NodoLectura(nombreVariable);
	}

	private NodoInstruccionSimple SentenciaBreak() throws Exception {
		match(TipoToken.BREAK);
		match(TipoToken.PUNTO_Y_COMA);
		return new NodoInstruccionSimple("break");
	}

	private NodoInstruccionSimple InstruccionSimple() throws Exception {
		StringBuilder expr = new StringBuilder();
		while (!currentToken(TipoToken.PUNTO_Y_COMA) && !currentToken(TipoToken.EOF)) {
			expr.append(tokens.get(indiceToken).getNombre()).append(" ");
			indiceToken++;
		}
		match(TipoToken.PUNTO_Y_COMA);
		return new NodoInstruccionSimple(expr.toString().trim());
	}

	private NodoInstruccionSimple Ternario() throws Exception {
		return InstruccionSimple();
	}

	private NodoFor SentenciaFor() throws Exception {
		match(TipoToken.FOR);
		match(TipoToken.PARENTESIS_IZQ);

		String tipoInit = "";
		if (esTipoActual()) {
			tipoInit = tokens.get(indiceToken).getNombre() + " ";
			matchTipo();
		}
		String varInit = tokens.get(indiceToken).getNombre();
		match(TipoToken.IDENTIFICADOR);
		match(TipoToken.IGUAL);
		String valInit = tokens.get(indiceToken).getNombre();
		match(TipoToken.NUMERO);
		match(TipoToken.PUNTO_Y_COMA);
		String inicializacion = tipoInit + varInit + " = " + valInit;

		StringBuilder cond = new StringBuilder();
		while (!currentToken(TipoToken.PUNTO_Y_COMA) && !currentToken(TipoToken.EOF)) {
			cond.append(tokens.get(indiceToken).getNombre()).append(" ");
			indiceToken++;
		}
		match(TipoToken.PUNTO_Y_COMA);
		String condicion = cond.toString().trim();

		String varStep = tokens.get(indiceToken).getNombre();
		match(TipoToken.IDENTIFICADOR);
		while(!currentToken(TipoToken.PARENTESIS_DER)) {
			indiceToken++;
		}
		String actualizacion = varStep + "++";

		match(TipoToken.PARENTESIS_DER);

		NodoBloque bloque;
		if (currentToken(TipoToken.LLAVE_IZQ)) {
			bloque = Bloque();
		} else {
			bloque = new NodoBloque();
			NodoTablaSimbolo s = Instruccion();
			if (s != null) bloque.addInstruccion(s);
		}

		return new NodoFor(inicializacion, condicion, actualizacion, bloque);
	}

	private String parseCondition() throws Exception {
		match(TipoToken.PARENTESIS_IZQ);
		StringBuilder cond = new StringBuilder();
		int parens = 1;
		while (parens > 0 && !currentToken(TipoToken.EOF)) {
			if (currentToken(TipoToken.PARENTESIS_IZQ)) parens++;
			else if (currentToken(TipoToken.PARENTESIS_DER)) parens--;

			if (parens > 0) {
				cond.append(tokens.get(indiceToken).getNombre()).append(" ");
				indiceToken++;
			}
		}
		match(TipoToken.PARENTESIS_DER);
		return cond.toString().trim();
	}

	private NodoWhile SentenciaWhile() throws Exception {
		match(TipoToken.WHILE);
		String condicion = parseCondition();

		NodoBloque bloque;
		if (currentToken(TipoToken.LLAVE_IZQ)) {
			bloque = Bloque();
		} else {
			bloque = new NodoBloque();
			NodoTablaSimbolo s = Instruccion();
			if (s != null) bloque.addInstruccion(s);
		}

		return new NodoWhile(condicion, bloque);
	}

	private NodoDoWhile SentenciaDoWhile() throws Exception {
		match(TipoToken.DO);
		NodoBloque bloque;
		if (currentToken(TipoToken.LLAVE_IZQ)) {
			bloque = Bloque();
		} else {
			bloque = new NodoBloque();
			NodoTablaSimbolo s = Instruccion();
			if (s != null) bloque.addInstruccion(s);
		}
		match(TipoToken.WHILE);
		String condicion = parseCondition();
		match(TipoToken.PUNTO_Y_COMA);

		return new NodoDoWhile(condicion, bloque);
	}

	private NodoSwitch SentenciaSwitch() throws Exception {
		match(TipoToken.SWITCH);
		String condicion = parseCondition();
		match(TipoToken.LLAVE_IZQ);

		List<String> cases = new ArrayList<>();
		List<NodoBloque> bloques = new ArrayList<>();
		NodoBloque bloqueDefault = null;

		while (!currentToken(TipoToken.LLAVE_DER) && !currentToken(TipoToken.EOF)) {
			if (currentToken(TipoToken.CASE)) {
				match(TipoToken.CASE);
				StringBuilder valorCase = new StringBuilder();
				while (!currentToken(TipoToken.DOS_PUNTOS)) {
					valorCase.append(tokens.get(indiceToken).getNombre()).append(" ");
					indiceToken++;
				}
				match(TipoToken.DOS_PUNTOS);
				cases.add(valorCase.toString().trim());

				NodoBloque bloque;
				if (currentToken(TipoToken.LLAVE_IZQ)) {
					bloque = Bloque();
				} else {
					bloque = new NodoBloque();
					while (!currentToken(TipoToken.CASE) && !currentToken(TipoToken.DEFAULT) && !currentToken(TipoToken.LLAVE_DER) && !currentToken(TipoToken.EOF)) {
						NodoTablaSimbolo inst = Instruccion();
						if (inst != null) bloque.addInstruccion(inst);
					}
				}
				bloques.add(bloque);
			} else if (currentToken(TipoToken.DEFAULT)) {
				match(TipoToken.DEFAULT);
				match(TipoToken.DOS_PUNTOS);
				if (currentToken(TipoToken.LLAVE_IZQ)) {
					bloqueDefault = Bloque();
				} else {
					bloqueDefault = new NodoBloque();
					while (!currentToken(TipoToken.LLAVE_DER) && !currentToken(TipoToken.EOF)) {
						NodoTablaSimbolo inst = Instruccion();
						if (inst != null) bloqueDefault.addInstruccion(inst);
					}
				}
			}
		}
		match(TipoToken.LLAVE_DER);
		return new NodoSwitch(condicion, cases, bloques, bloqueDefault);
	}

	private NodoTablaSimbolo Declaracion() throws Exception {
		String tipo = tokens.get(indiceToken).getNombre();
		matchTipo();
		
		StringBuilder nombreVariable = new StringBuilder(tokens.get(indiceToken).getNombre());
		match(TipoToken.IDENTIFICADOR);

		while (currentToken(TipoToken.CORCHETE_IZQ)) {
			nombreVariable.append(tokens.get(indiceToken).getNombre()); match(TipoToken.CORCHETE_IZQ);
			while (!currentToken(TipoToken.CORCHETE_DER) && !currentToken(TipoToken.EOF)) {
				nombreVariable.append(tokens.get(indiceToken).getNombre());
				indiceToken++;
			}
			nombreVariable.append(tokens.get(indiceToken).getNombre()); match(TipoToken.CORCHETE_DER);
		}

		if (currentToken(TipoToken.IGUAL)) {
			match(TipoToken.IGUAL);
			boolean isLlamada = false;
			int temp = indiceToken;
			if (temp < tokens.size() && (tokens.get(temp).getTipo() == TipoToken.LLAMADA_FUNC || tokens.get(temp).getTipo() == TipoToken.IDENTIFICADOR)) {
				if (temp + 1 < tokens.size() && tokens.get(temp+1).getTipo() == TipoToken.PARENTESIS_IZQ) {
					isLlamada = true;
				}
			}

			if (isLlamada) {
				return SentenciaLlamadaFuncion(tipo + " " + nombreVariable.toString() + " = ");
			} else {
				StringBuilder expr = new StringBuilder();
				while (!currentToken(TipoToken.PUNTO_Y_COMA) && !currentToken(TipoToken.EOF)) {
					expr.append(tokens.get(indiceToken).getNombre()).append(" ");
					indiceToken++;
				}
				match(TipoToken.PUNTO_Y_COMA);
				return new NodoDeclaracion(tipo, nombreVariable.toString(), expr.toString().trim());
			}
		}

		match(TipoToken.PUNTO_Y_COMA);
		return new NodoDeclaracion(tipo, nombreVariable.toString());
	}

	private NodoAsignacion Asignacion() throws Exception {
		StringBuilder nombreVariable = new StringBuilder();
		nombreVariable.append(tokens.get(indiceToken).getNombre());
		match(TipoToken.IDENTIFICADOR);

		while (currentToken(TipoToken.CORCHETE_IZQ)) {
			nombreVariable.append(tokens.get(indiceToken).getNombre()); match(TipoToken.CORCHETE_IZQ);
			while (!currentToken(TipoToken.CORCHETE_DER) && !currentToken(TipoToken.EOF)) {
				nombreVariable.append(tokens.get(indiceToken).getNombre());
				indiceToken++;
			}
			nombreVariable.append(tokens.get(indiceToken).getNombre()); match(TipoToken.CORCHETE_DER);
		}

		match(TipoToken.IGUAL);

		StringBuilder expresion = new StringBuilder();
		while (!currentToken(TipoToken.PUNTO_Y_COMA) && !currentToken(TipoToken.EOF)) {
			expresion.append(tokens.get(indiceToken).getNombre()).append(" ");
			indiceToken++;
		}
		match(TipoToken.PUNTO_Y_COMA);

		return new NodoAsignacion(nombreVariable.toString(), expresion.toString().trim());
	}

	private NodoIfElse SentenciaIf() throws Exception {
		match(TipoToken.IF);
		String condicion = parseCondition();

		NodoBloque bloqueTrue;
		if (currentToken(TipoToken.LLAVE_IZQ)) {
			bloqueTrue = Bloque();
		} else {
			bloqueTrue = new NodoBloque();
			NodoTablaSimbolo s = Instruccion();
			if (s != null) bloqueTrue.addInstruccion(s);
		}

		NodoBloque bloqueFalse = null;
		if (currentToken(TipoToken.ELSE)) {
			match(TipoToken.ELSE);
			if (currentToken(TipoToken.LLAVE_IZQ)) {
				bloqueFalse = Bloque();
			} else {
				bloqueFalse = new NodoBloque();
				NodoTablaSimbolo s = Instruccion();
				if (s != null) bloqueFalse.addInstruccion(s);
			}
		}

		return new NodoIfElse(condicion, bloqueTrue, bloqueFalse);
	}

	private void match(TipoToken tipoEsperado) throws Exception {
		if (currentToken(tipoEsperado)) {
			indiceToken++;
		} else {
			throw new Exception("Error Sintáctico: Se esperaba " + tipoEsperado + " pero se encontró " + tokens.get(indiceToken).getTipo() + " ('" + tokens.get(indiceToken).getNombre() + "') en linea (aprox)");
		}
	}

	private boolean currentToken(TipoToken tipo) {
		if (indiceToken >= tokens.size()) return false;
		return tokens.get(indiceToken).getTipo() == tipo;
	}
}