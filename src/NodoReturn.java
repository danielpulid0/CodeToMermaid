public class NodoReturn extends NodoTablaSimbolo {
	private String valor;

	public NodoReturn(String valor) {
		this.valor = valor;
	}

	@Override
	public String[] generarMermaid(ContextoMermaid contexto) {
		String id = "nodo" + contexto.incContador();

		contexto.codigo.append(id).append("[\"return ").append(valor).append("\"]\n");

		return new String[]{id, id};
	}
}