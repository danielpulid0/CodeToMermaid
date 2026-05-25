import java.util.List;

public class CParser {
    private List<Token> tokens;
    private int indiceToken;

    public CParser(List<Token> tokens) {
        this.tokens = tokens;
        this.indiceToken = 0;
    }

    // Método principal que inicia el análisis
    public NodoBloque analizar() throws Exception {
        return Programa();
    }

    // <Programa> ::= "int" "main" "(" ")" <Bloque>
    private NodoBloque Programa() throws Exception {
        match(TipoToken.INT);
        match(TipoToken.MAIN);
        match(TipoToken.PARENTESIS_IZQ);
        match(TipoToken.PARENTESIS_DER);
        return Bloque(); // Retorna el bloque principal del main
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
        if (currentToken(TipoToken.INT)) {
            return Declaracion();
        } else if (currentToken(TipoToken.IDENTIFICADOR)) {
            return Asignacion();
        } else if (currentToken(TipoToken.IF)) {
            return SentenciaIf();
        } else {
            throw new Exception("Error Sintáctico: Instrucción no reconocida en '" + tokens.get(indiceToken).getNombre() + "'");
        }
    }

    // <Declaracion> ::= "int" <Identificador> ";"
    private NodoDeclaracion Declaracion() throws Exception {
        match(TipoToken.INT);
        String nombreVariable = tokens.get(indiceToken).getNombre();
        match(TipoToken.IDENTIFICADOR);
        match(TipoToken.PUNTO_Y_COMA);
        
        return new NodoDeclaracion("int", nombreVariable);
    }

    // <Asignacion> ::= <Identificador> "=" <Valor> ";"
    private NodoAsignacion Asignacion() throws Exception {
        String nombreVariable = tokens.get(indiceToken).getNombre();
        match(TipoToken.IDENTIFICADOR);
        match(TipoToken.IGUAL);
        
        // Simplificaremos la expresión por ahora para el avance (solo toma el número o variable)
        String valor = tokens.get(indiceToken).getNombre();
        if (currentToken(TipoToken.NUMERO)) match(TipoToken.NUMERO);
        else match(TipoToken.IDENTIFICADOR);
        
        match(TipoToken.PUNTO_Y_COMA);
        
        return new NodoAsignacion(nombreVariable, valor);
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
        else match(TipoToken.IGUAL_IGUAL);
        
        String opDer = tokens.get(indiceToken).getNombre();
        match(TipoToken.NUMERO); // 10
        
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
            throw new Exception("Error Sintáctico: Se esperaba " + tipoEsperado + " pero se encontró " + tokens.get(indiceToken).getTipo());
        }
    }

    private boolean currentToken(TipoToken tipo) {
        if (indiceToken >= tokens.size()) return false;
        return tokens.get(indiceToken).getTipo() == tipo;
    }
}