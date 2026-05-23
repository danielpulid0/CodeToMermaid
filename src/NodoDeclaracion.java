public class NodoDeclaracion extends NodoTablaSimbolo {
    String tipo;
    String variable;

    public NodoDeclaracion(String tipo, String variable) {
        this.tipo = tipo;
        this.variable = variable;
    }

    @Override
    public String[] generarMermaid(ContextoMermaid contexto) {
        String id = contexto.getNextID();
        // Genera: nodo2["int x"]
        contexto.codigo.append("    ").append(id).append("[\"").append(tipo).append(" ").append(variable).append("\"]\n");
        return new String[]{id, id};
    }
}