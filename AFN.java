import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

/*
	Utilice esta clase para guardar la informacion de su
	AFN. NO DEBE CAMBIAR LOS NOMBRES DE LA CLASE NI DE LOS 
	METODOS que ya existen, sin embargo, usted es libre de 
	agregar los campos y metodos que desee.
*/
public class AFN {

    String path;
    String[] alfabeto;
    Number cantidadEstados;
    String[] estadosFinales;
    List<String[]> transiciones;

    /*
     * Implemente el constructor de la clase AFN
     * que recibe como argumento un string que
     * representa el path del archivo que contiene
     * la informacion del AFN (i.e. "Documentos/archivo.AFN").
     * Puede utilizar la estructura de datos que desee
     */
    public AFN(String path) {
        this.path = path;
        readFileAndSetValues();
    }

    /*
     * Implemente el metodo accept, que recibe como argumento
     * un String que representa la cuerda a evaluar, y devuelve
     * un boolean dependiendo de si la cuerda es aceptada o no
     * por el AFN. Recuerde lo aprendido en el proyecto 1.
     */
    public boolean accept(String string) {
        //Lista para las transicione siguientes;
        List<String> estadosActuales = new ArrayList<>();
        estadosActuales.add("1"); //En uno por que lamba es en cero
    
        List<String> cierreLambdaInicial = estadoLambda(estadosActuales);
    
        estadosActuales.addAll(cierreLambdaInicial);
    
        // Ciclo sobre cada símbolo de la cadena
        for (char simbolo : string.toCharArray()) {
            
            //LIsta para el nuevo estado
            List<String> nuevosEstados = new ArrayList<>();
    
            // Ciclo sobre cada estado actual
            for (String estado : estadosActuales) {
                int indice = searchIndex(simbolo);
                String[] transicionesEstado = transiciones.get(indice);
                String[] estadosDestino = transicionesEstado[Integer.parseInt(estado)].split(",");
    
                for (String estadoDestino : estadosDestino) {
                    //Verificar si no hay dos para el mismo
                    String[] estadosDestinoIndividuales = estadoDestino.split(";");
                    for (String destino : estadosDestinoIndividuales) {
                        nuevosEstados.add(destino);
                    }
                }
            }
    
            List<String> cierreLambdaNuevos = estadoLambda(nuevosEstados);
    
            estadosActuales.addAll(cierreLambdaNuevos);
    
            // Actualizar el conjunto de estados actuales con los nuevos
            estadosActuales = nuevosEstados;
        }
    
        //Verificacion de finales y devolver si es aceptada o no
        for (String estado : estadosActuales) {
            if (Arrays.asList(estadosFinales).contains(estado)) {
                System.out.println("La cadena '" + string + "' es aceptada por el AFN.");
                return true;
            }
        }
    
        System.out.println("La cadena '" + string + "' es rechazada por el AFN.");
        return false;
    }
    
    private List<String> estadoLambda(List<String> estados) {
        //Copiar array oroginal por cualquier cosa
        List<String> cambiosLambda = new ArrayList<>(estados);
        boolean cambios;
        do {
            cambios = false;
            List<String> nuevosEstados = new ArrayList<>();
            for (String estado : cambiosLambda) {
                int indiceEstado = Integer.parseInt(estado);
                String[] transicionesLambda = transiciones.get(0); //Lambda en posicón 0
                String[] estadosDestino = transicionesLambda[indiceEstado].split(",");
                for (String estadoDestino : estadosDestino) {
                    String[] estadosDestinoIndividuales = estadoDestino.split(";");
                    for (String destino : estadosDestinoIndividuales) {
                        if (!cambiosLambda.contains(destino) && !nuevosEstados.contains(destino)) {
                            nuevosEstados.add(destino);
                            cambios = true;
                        }
                    }
                }
            }
            cambiosLambda.addAll(nuevosEstados);
        } while (cambios);
        return cambiosLambda;
    }
    
    private int searchIndex(char simbolo) {
        //Retoran en que posición s eencuentra el simbolo dentro del alfabeto
        for (int i = 0; i < alfabeto.length; i++) {
            if (alfabeto[i].charAt(0) == simbolo) {
                return i;
            }
        }
        return -1; // No esta en el alfabeto
    }


    /*
     * Implemente el metodo toAFD. Este metodo debe generar un archivo
     * de texto que contenga los datos de un AFD segun las especificaciones
     * del proyecto.
     */
    public void toAFD(String afdPath) {
        System.out.println("Alfabeto: " + Arrays.toString(this.alfabeto));
        System.out.println("Cantidad de estados de transicion: " + this.cantidadEstados);
        System.out.println("Estados finales: " + Arrays.toString(this.estadosFinales));
        System.out.println("Transisciones");
        for (int i = 0; i < this.transiciones.size(); i++) {
            System.out.println(
                    "Transiciones para: " + this.alfabeto[i] + ": " + Arrays.toString(this.transiciones.get(i)));
        }
    }

    /* Leer archivo y set valores */
    public void readFileAndSetValues() {
        System.out.println("Analizando el archivo...");
        List<String[]> lineas = new ArrayList<>();
        List<String[]> matriz = new ArrayList<>();

        // Try por si falla en elgún momento
        try (BufferedReader leerArchivo = new BufferedReader(new FileReader(this.path))) {
            // Aqui vamos poniendo cada linea del archivo leido en un array por separado
            String linea;
            while ((linea = leerArchivo.readLine()) != null) {
                String[] partes = linea.split(",");
                lineas.add(partes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<String> alfabetoList = new ArrayList<>(Arrays.asList(lineas.get(0)));
        alfabetoList.add(0, "lambda");
        this.alfabeto = alfabetoList.toArray(new String[0]);

        this.cantidadEstados = Integer.parseInt(lineas.get(1)[0]);
        this.estadosFinales = lineas.get(2);

        for (int i = 3; i < lineas.size(); i++) {
            String[] partes = lineas.get(i);
            matriz.add(partes);
        }

        this.transiciones = matriz;

        System.out.println("Se termino de analizar el archivo...");
    }

    /* Validaciones propias */
    public static boolean validarExtencion(String cuerda) {
        return cuerda.matches(".*\\.afn$");
    }

    /*
     * El metodo main debe recibir como primer argumento el path
     * donde se encuentra el archivo ".afd", como segundo argumento
     * una bandera ("-f" o "-i"). Si la bandera es "-f", debe recibir
     * como tercer argumento el path del archivo con las cuerdas a
     * evaluar, y si es "-i", debe empezar a evaluar cuerdas ingresadas
     * por el usuario una a una hasta leer una cuerda vacia (""), en cuyo
     * caso debe terminar. Tiene la libertad de implementar este metodo
     * de la forma que desee.
     */
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        int longituDeArgs = args.length;

        switch (longituDeArgs) {
            case 0:
                System.out.println("Se necesita por lo menos el nombre del archivo");
                break;
            case 1:
                // Esta es la primera parte del proyecto
                System.out.println("******** Validador de cuerdas ********");
                String nameFile = args[0];

                // Valida si la cuerda termina en .afn
                if (!validarExtencion(nameFile)) {
                    System.out.println("La extencion del archivo no es valido");
                    return;
                }

                // Crear la instancia de la clase;
                AFN afn = new AFN(nameFile);

                // Leemos archivo y seteamos valores
                afn.readFileAndSetValues();

                System.out.println("Ingrese una cuerda (o una cuerda de longitud 0 para salir):");

                while (true) {
                    String cuerda = scanner.nextLine();

                    if (cuerda.length() == 0) {
                        break;
                    }

                    afn.accept(cuerda);

                    System.out.println("Ingrese otra cuerda (o una cuerda de longitud 0 para salir):");
                }
                break;
            case 3:
                String nameaAFN = args[0];
                String command = args[1];
                String nameAFD = args[2];

                // Valida si la cuerda termina en .afn
                if (!validarExtencion(nameaAFN)) {
                    System.out.println("La extencion del archivo de ingreso no es valido");
                    return;
                }

                // Valida si lleva el comando indicado
                if (!Objects.equals(command, "-to-afd")) {
                    System.out.println("El comando ingresado no es reconocido, se distingen mayusculas de minusculas");
                    return;
                }

                System.out.println("*********** Convertir AFN a AFD ***********");

                // Crear la instancia de la clase;
                AFN afn_afd = new AFN(nameaAFN);

                // Leemos archivo y seteamos valores
                afn_afd.readFileAndSetValues();

                // Se llama el metodo y en el metodo debe de pasar la magia 😄
                afn_afd.toAFD(nameAFD);
                break;
            default:
                System.out.println("Ninguna valida");
        }
    }

}