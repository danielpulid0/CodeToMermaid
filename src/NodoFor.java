public class NodoFor extends NodoTablaSimbolo {
    private String inicializacion;
    private String condicion;
    private String actualizacion;
    private NodoBloque bloqueTrue;

    public NodoFor(String inicializacion, String condicion, String actualizacion, NodoBloque bloqueTrue) {
        this.inicializacion = inicializacion;
        this.condicion = condicion;
        this.actualizacion = actualizacion;
        this.bloqueTrue = bloqueTrue;
    }

    @Override
    public String[] generarMermaid(ContextoMermaid contexto) {
        // 1. Nodo de Inicialización (ej: i = 0)
        String idInit = "nodo" + contexto.incContador();
        contexto.codigo.append(idInit).append("[\"").append(inicializacion).append("\"]\n");

        // 2. Nodo de Condición (ej: i < 10)
        String idCondicion = "nodo" + contexto.incContador();
        contexto.codigo.append(idCondicion).append("{\"").append(condicion).append("\"}\n");

        // 3. Nodo de Actualización (ej: i = i + 1)
        String idStep = "nodo" + contexto.incContador();
        contexto.codigo.append(idStep).append("[\"").append(actualizacion).append("\"]\n");

        // 4. Nodo Dummy de Fin de ciclo
        String idFin = "nodo" + contexto.incContador();
        contexto.codigo.append(idFin).append("(( ))\n");

        // Conexión inicial: Entrada de la estructura va directo a la Inicialización
        contexto.codigo.append(idInit).append(" --> ").append(idCondicion).append("\n");

        // 5. Generar el bloque interno (cuerpo del for)
        String[] idsBloque = bloqueTrue.generarMermaid(contexto);

        // 6. Conexión condicional: Condición -- Sí --> Cuerpo
        contexto.codigo.append(idCondicion).append(" -- Sí --> ").append(idsBloque[0]).append("\n");

        // 7. El Bucle: Final del cuerpo --> Va al paso de Actualización
        contexto.codigo.append(idsBloque[1]).append(" --> ").append(idStep).append("\n");

        // 8. Retorno del Bucle: Actualización --> Regresa a evaluar la Condición
        contexto.codigo.append(idStep).append(" --> ").append(idCondicion).append("\n");

        // 9. Salida del ciclo: Condición -- No --> Fin del for
        contexto.codigo.append(idCondicion).append(" -- No --> ").append(idFin).append("\n");

        // Hacia el exterior: El flujo entra por la inicialización y sale por el nodo dummy fin
        return new String[]{idInit, idFin};
    }
}