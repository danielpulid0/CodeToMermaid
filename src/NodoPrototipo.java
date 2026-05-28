public class NodoPrototipo extends NodoTablaSimbolo {
    private String tipo;
    private String nombre;
    private String parametros;

    public NodoPrototipo(String tipo, String nombre, String parametros) {
        this.tipo = tipo;
        this.nombre = nombre;
        this.parametros = parametros;
    }

    @Override
    public String[] generarMermaid(ContextoMermaid contexto) {
        String id = "nodo" + contexto.incContador();
        contexto.codigo.append(id).append("[\"").append(tipo).append(" ").append(nombre).append("(").append(parametros).append(");\"]\n");
        return new String[]{id, id};
    }
}
