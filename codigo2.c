#include <stdio.h>

#define NUMTABLEROS 4

// funcion para crear tableros
void crearTableros(int sudokus[NUMTABLEROS][9][9]);
// funcion para imprimir un tablero
void imprimirTablero(int tablero[9][9]);

/*
funcion principal para resolver un tablero de sudoku
retorna 1 si fue posible resolver el tablero
retorna 0 si no fue posible resolver el tablero
*/
int resolver(int tablero[9][9], int pos_fila, int pos_columna);

/*
funcion auxilar para revisar si el numero es valido en la posicion dada la fila y la columna
revisa tambien los numeros que pertenecen a su subcuadro de 3x3
retorna 0 si el numero no es valido para esa posicion
retoran 1 si el numero es valida para esa posicion
 */
int revisar(int tablero[9][9], int numero, int fila, int columna);

int main()
{
	int sudokus[NUMTABLEROS][9][9];	// INICIALIZAR TABLEROS
	crearTableros(sudokus);	// LLAMADA A FUNCION PARA CREAR TABLEROS

	// menu!!!!!
	while (1)
	{
		printf("\n\tSUDOKU!!!\n");
		printf("\n\t1. Ver tableros");
		printf("\n\t2. Resolver tableros");
		printf("\n\t3. Salir");
		printf("\n\n\tSelecciona una opcion: ");
		int opcion;
		scanf("%d", &opcion);

		switch (opcion)
		{
			case 1:
			{
				int opcionTablero = -1;
				do
				{
					printf("\nSelecciona un tablero (1-4): ");
					fflush(stdin);
					scanf("%d", &opcionTablero);
				} while (opcionTablero < 1 || opcionTablero > 4);
				imprimirTablero(sudokus[opcionTablero - 1]);
				break;
			}
			case 2:
			{
				int opcionTablero = -1;
				do
				{
					printf("\nSelecciona un tablero para resolver (1-4): ");
					fflush(stdin);
					scanf("%d", &opcionTablero);
				} while (opcionTablero < 1 || opcionTablero > 4);

				if (resolver(sudokus[opcionTablero - 1], 0, 0) == 1)
				{
					imprimirTablero(sudokus[opcionTablero - 1]);
					printf("\nSolucion encontrada para el Tablero %d!\n", opcionTablero);
				}
				else
				{
					printf("\nNo se encontro solucion para el Tablero %d\n", opcionTablero);
				}

				break;
			}
			case 3:
			{
				exit(0);	// FIN DEL PROGRAMA
			}
			default:
			{
				printf("\nOpcion no valida, intente de nuevo.\n");
			}
		}
	}
}

void crearTableros(int sudokus[NUMTABLEROS][9][9])
{
	int tableros[NUMTABLEROS][9][9] =
	{
		{	 // Tablero 1
			{0, 0, 9,	3, 4, 6,	7, 8, 5},
			{7, 5, 0,	2, 1, 0,	0, 9, 6},
			{0, 6, 8,	7, 9, 0,	4, 2, 0},

			{4, 9, 0,	0, 6, 7,	1, 5, 0},
			{6, 1, 7,	4, 5, 2,	8, 0, 0},
			{5, 8, 0,	1, 0, 9,	6, 0, 4},

			{2, 0, 0,	5, 8, 4,	9, 0, 3},
			{9, 3, 0,	6, 7, 1,	0, 4, 8},
			{8, 4, 6,	0, 0, 0,	5, 1, 7}
		},
		{	 // Tablero 2
			{0, 0, 9,	0, 4, 6,	7, 0, 5},
			{7, 0, 0,	0, 1, 0,	3, 0, 0},
			{3, 6, 8,	0, 9, 5,	0, 0, 0},

			{4, 0, 0,	8, 0, 7,	0, 0, 2},
			{0, 1, 7,	0, 0, 0,	8, 0, 9},
			{5, 0, 0,	1, 3, 0,	0, 0, 0},

			{0, 0, 1,	5, 0, 4,	9, 0, 0},
			{0, 3, 5,	0, 0, 1,	0, 4, 0},
			{8, 0, 0,	9, 2, 0,	5, 0, 7}
		},
		{	 // Tablero 3
			{0, 0, 9,	0, 4, 0,	7, 0, 5},
			{0, 5, 4,	0, 1, 0,	0, 0, 0},
			{3, 0, 0,	7, 0, 0,	0, 0, 1},

			{0, 0, 0,	0, 0, 7,	1, 0, 2},
			{6, 0, 7,	0, 5, 0,	8, 0, 0},
			{5, 8, 0,	0, 0, 0,	6, 0, 0},

			{0, 0, 0,	5, 8, 0,	0, 6, 0},
			{0, 0, 5,	6, 0, 0,	0, 4, 8},
			{8, 4, 0,	0, 0, 0,	0, 1, 0}
		},
		{	 // Tablero 4
			{0, 0, 9,	0, 4, 0,	7, 0, 0},
			{0, 5, 0,	0, 0, 0,	0, 0, 0},
			{0, 0, 0,	0, 0, 5,	0, 0, 1},

			{4, 0, 0,	8, 0, 0,	0, 0, 0},
			{6, 0, 7,	0, 0, 0,	8, 0, 0},
			{0, 0, 0,	1, 3, 0,	0, 7, 0},

			{0, 7, 0,	0, 0, 0,	0, 0, 0},
			{9, 0, 0,	0, 0, 1,	0, 4, 0},
			{0, 4, 0,	0, 0, 0,	5, 0, 7}
		}
	};
	for (int b = 0; b < 4; b++)
		for (int i = 0; i < 9; i++)
			for (int j = 0; j < 9; j++)
				sudokus[b][i][j] = tableros[b][i][j];
}

void imprimirTablero(int tablero[9][9])
{
	printf("\n=====================================\n\n");

	for (int k = 0; k < 16; k++) printf("- ");
	printf("\n");
	for (int i = 0; i < 9; i++)
	{
		printf("|");
		for (int j = 0; j < 9; j++)
		{
			//printf(" %d ", tablero[i][j]);
			tablero[i][j] == 0 ? printf(" _ ") : printf(" %d ", tablero[i][j]);
			if (j == 2 || j == 5)
				printf("|");
		}
		printf("|");
		printf("\n");
		if (i == 2 || i == 5)
		{
			for (int k = 0; k < 16; k++) printf("- ");
			printf("\n");
		}
	}
	for (int k = 0; k < 16; k++) printf("- ");
	printf("\n");
}

/*
 * Esta funcion se encargará de resolver el sudoku
 */
int resolver(int tablero[9][9], int pos_fila, int pos_columna)
{
	// si llegamos a la fila 9 (no existe) entonces terminamos
	if (pos_fila == 9)
		return 1;	// tablero completado con exito

	// si ya terminamos las columnas entonces cambiamos
	// de fila y reiniciamos el contador de columnas
	if (pos_columna == 9)
		return resolver(tablero, pos_fila + 1, 0);

	// si la casilla actual tiene un numero avanzamos a la siguiente
	if (tablero[pos_fila][pos_columna] != 0)
		return resolver(tablero, pos_fila, pos_columna + 1);

	for (int num = 1; num <= 9; num++)
	{
		if (revisar(tablero, num, pos_fila, pos_columna))
		{
			tablero[pos_fila][pos_columna] = num;

			if (resolver(tablero, pos_fila, pos_columna + 1) == 1)
				return 1;

			// si la llamada anterior retorna 0
			// entonces fue que no pudo encontrar un valor valido
			// se vacía la casilla y seguimos probando números
			tablero[pos_fila][pos_columna] = 0;
		}
	}
	// caso: ningun numero se pudo poner
	return 0;
}

/*
 * Esta funcion se encargará de verificar si el número se
 * puede colocar
 */
int revisar(int tablero[9][9], int numero, int fila, int columna)
{
	// Revisar fila
	for (int i = 0; i < 9; i++)
	{
		if (tablero[fila][i] == numero)
			return 0;	// sale a la primer coincidencia
	}

	// Revisar columna
	for (int i = 0; i < 9; i++)
	{
		if (tablero[i][columna] == numero)
			return 0;	// sale a la primer coincidencia
	}

	// Revisar la caja
	int inicioFila = (fila / 3) * 3;
	int inicioCol = (columna / 3) * 3;

	for (int i = 0; i < 3; i++)
	{
		for (int j = 0; j < 3; j++)
		{
			if (tablero[inicioFila + i][inicioCol + j] == numero)
				return 0;
		}
	}

	// si en ninguna momento detuvo, el numero es valido!
	return 1;
}
