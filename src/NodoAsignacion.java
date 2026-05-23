public class NodoAsignacion extends NodoTablaSimbolo
{
    String variable;
    String valor;

    public NodoAsignacion(String variable, String valor)
    {
        this.variable = variable;
        this.valor = valor;
    }

    @Override
    public String[] generarMermaid(ContextoMermaid contexto)
    {
        String id = contexto.getNextID();
        // generar
        contexto.codigo.append("    ").append(id).append("[\"").append(variable).append(" = ").append(valor).append("\"]\n");
        return new String[]{id, id};
    }
}
