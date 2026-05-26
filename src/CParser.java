import java.util.List;

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

    // <Programa> ::= "int" "main" "(" ")" <Bloque>
    private NodoPrograma Programa() throws Exception {
        match(TipoToken.INT);
        match(TipoToken.MAIN);
        match(TipoToken.PARENTESIS_IZQ);
        match(TipoToken.PARENTESIS_DER);
        NodoBloque bloqueMain = Bloque();
        return new NodoPrograma(bloqueMain); // Envolvemos el main
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
        } else if (currentToken(TipoToken.WHILE)) { 
            return SentenciaWhile();
        } else if (currentToken(TipoToken.FOR)) { 
            return SentenciaFor();
        }else if (currentToken(TipoToken.SCANF)) {
            return SentenciaScanf();
        }else {
            throw new Exception("Error Sintáctico: Instrucción no reconocida en '" + tokens.get(indiceToken).getNombre() + "'");
        }
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

    // <Declaracion> ::= "int" <Identificador> ";"
    private NodoDeclaracion Declaracion() throws Exception {
        match(TipoToken.INT);
        String nombreVariable = tokens.get(indiceToken).getNombre();
        match(TipoToken.IDENTIFICADOR);
        match(TipoToken.PUNTO_Y_COMA);
        
        return new NodoDeclaracion("int", nombreVariable);
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