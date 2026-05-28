public class NodoLectura extends NodoTablaSimbolo {
	private String variable;

	public NodoLectura(String variable) {
		this.variable = variable;
	}

	@Override
	public String[] generarMermaid(ContextoMermaid contexto) {
		String id = "nodo" + contexto.incContador();
		contexto.codigo.append(id).append("[/\"Leer ").append(variable).append("\"/]\n");

		return new String[]{id, id};
	}
}