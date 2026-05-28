@Deprecated
public class PruebaTablaSimbolos {
	public static void main(String[] args) {
		// 1. Armamos el AST "a mano" simulando lo que haría el Parser
		NodoBloque funcionMain = new NodoBloque();

		funcionMain.addInstruccion(new NodoDeclaracion("int", "x"));
		funcionMain.addInstruccion(new NodoAsignacion("x", "15"));

		NodoBloque bloqueTrue = new NodoBloque();
		bloqueTrue.addInstruccion(new NodoAsignacion("x", "1"));

		NodoBloque bloqueFalse = new NodoBloque();
		bloqueFalse.addInstruccion(new NodoAsignacion("x", "0"));

		funcionMain.addInstruccion(new NodoIfElse("x > 10", bloqueTrue, bloqueFalse));

		// Agregamos una instrucción extra al final para ver cómo se conectan las ramas del IF
		funcionMain.addInstruccion(new NodoAsignacion("return", "0"));

		// 2. Ejecutamos la Traducción a Mermaid
		ContextoMermaid contexto = new ContextoMermaid();
		funcionMain.generarMermaid(contexto);

		// 3. Imprimimos el código resultante
		System.out.println(contexto.codigo.toString());
	}
}