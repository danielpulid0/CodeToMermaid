public class NodoExit extends NodoTablaSimbolo {
	private String valor;

	public NodoExit(String valor) {
		this.valor = valor;
	}

	@Override
	public String[] generarMermaid(ContextoMermaid contexto) {
		String id = contexto.getNextID();
		contexto.codigo.append("    ").append(id).append("[[\"exit(").append(valor.replace("\"", "#quot;")).append(")\"]]\n");
		contexto.codigo.append("    ").append(id).append(" --> nodoFinGlobal\n");
		return new String[]{id, null};
	}
}
