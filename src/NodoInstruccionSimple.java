public class NodoInstruccionSimple extends NodoTablaSimbolo {
	private String expresion;

	public NodoInstruccionSimple(String expresion) {
		this.expresion = expresion;
	}

	@Override
	public String[] generarMermaid(ContextoMermaid contexto) {
		String id = contexto.getNextID();
		contexto.codigo.append("    ").append(id).append("[\"").append(expresion.replace("\"", "#quot;")).append("\"]\n");
		return new String[]{id, id};
	}
}
