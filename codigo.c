#include <stdio.h>
#include <stdlib.h>

void saludo();
int suma(int a, int b);
float division(float a, float b);

int main()
{
    int x;
    scanf("%d", &x);
    x = x + 1;
    while (x < 5)
    {
         x = x + 1;
    }
    int i;
    for (i = 0 ; i < 10; i = i + 1)
    {
        x = i;
    }
    saludo();

    int a = 5;
    int b = 10;
    int resultadoSuma = suma(a, b);
    printf("\n La suma de %d y %d es: %d", a, b, resultadoSuma);
    float resultadoDivision = division(a, b);
    printf("\n La division de %f y %f es: %f", a, b, resultadoDivision);
    return 0;
}

void saludo()
{
    printf("\n SALUDO!");
}

int suma(int a, int b)
{
    return a + b;
}

float division(float a, float b)
{
    if (b != 0)
    {
        return a / b;
    }
    else
    {
        printf("\n Error: Division por cero no permitida.");
        return 0; // Retorna 0 o algun valor que indique error
    }
}