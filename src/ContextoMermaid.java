/*
*   contexto auxiliar para llevar el conteo de IDs de Mermaid y
*   guardar el texto
*/
public class ContextoMermaid
{
    private int contador = 0;
    public StringBuilder codigo = new StringBuilder("graph TD\n");  // encabezado de mermaid

    public String getNextID()
    {
        return "nodo" + (contador++);
    }
}
