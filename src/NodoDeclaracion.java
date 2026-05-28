public class NodoDeclaracion extends NodoTablaSimbolo {
	String tipo;
	String variable;
	String valor;

	public NodoDeclaracion(String tipo, String variable) {
		this.tipo = tipo;
		this.variable = variable;
		this.valor = null;
	}

	public NodoDeclaracion(String tipo, String variable, String valor) {
		this.tipo = tipo;
		this.variable = variable;
		this.valor = valor;
	}

	@Override
	public String[] generarMermaid(ContextoMermaid contexto) {
		String id = contexto.getNextID();
		if (valor != null) {
			contexto.codigo.append("    ").append(id).append("[\"").append(tipo).append(" ").append(variable).append(" = ").append(valor).append("\"]\n");
		} else {
			contexto.codigo.append("    ").append(id).append("[\"").append(tipo).append(" ").append(variable).append("\"]\n");
		}
		return new String[]{id, id};
	}
}