package com.company;

import java.io.*;
import java.util.*;


public class Main {

    static File archive;
    static FileReader r_archive;
    static FileWriter w_auxiliar;
    static BufferedWriter b_w_auxiliar;
    static BufferedReader b_r_archive;

    static int[] ctrls = {1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192, 16384, 32768, 65536, 131072, 262144};
    static int[] ctrolInArreglo = {0, 1, 3, 7, 15, 31, 63, 127, 255, 511, 1023, 2047, 4095, 8191, 16383, 32767, 65535, 131071, 262143};

    public static void hacerHuffman(String rutaDeArchivo, String rutaSalida, String rutaDeCodigos) throws IOException {

        HashMap <Character,Integer> apariciones = new HashMap<>();
        ArrayList<Character> listaC;
        ArrayList<Integer[]> lista_Renglon = new ArrayList<>();
        ArrayList<Nodo> data = new ArrayList<>();
        HashMap<Character, ArrayList<Integer>> estructura = new HashMap<>();
        int[] buffer_salida = new int[8];
        Integer[] caracter = new Integer[1];
        Arrays.fill(buffer_salida, -2);
        Arrays.fill(caracter, -2);

        boolean esUltimo = false;
        int indexBuffer = 0,indexCaracter = 0;
        int cont = 0;

        listaC = leerArchivo(rutaDeArchivo);
        for (Character c :listaC.subList(0,listaC.size())) {
            if (apariciones.containsKey(c)) {
                apariciones.put(c,apariciones.get(c)+1);
            } else {
                apariciones.put(c,1);
            }
        }
        for (Map.Entry<Character, Integer> entry : apariciones.entrySet()) {
            Character key = entry.getKey();
            Integer value = entry.getValue();
            data.add(new Nodo(value, key, new ArrayList<>()));
        }
        data.sort(new ComparatorNodos());
        while (data.size()>1) {
            data = unir(data);
            data.sort(new ComparatorNodos());
        }
        estructura.putAll(data.remove(data.size()-1).getEstructura());
        estructura.forEach((k,v) -> Collections.reverse(v));
        for (Character c :listaC.subList(0,listaC.size())) {
            lista_Renglon.add(estructura.get(c).toArray(new Integer[estructura.get(c).size()]));
        }
        while (distinto(buffer_salida, -2) || !lista_Renglon.isEmpty()) {
            while (contains(buffer_salida, -2) && !esUltimo) {
                if (distintoInteger(caracter, -2)) {
                    buffer_salida[indexBuffer]=caracter[indexCaracter];
                    caracter[indexCaracter]=-2;
                    indexBuffer++;
                    indexCaracter++;
                } else { //actualizar caracter
                    caracter = actualizarCaracterInteger(lista_Renglon);
                    indexCaracter=0;
                    if (lista_Renglon.isEmpty()){
                        esUltimo = true;
                    }
                }
            }
            if (distinto(buffer_salida,-2)) {
                for (int index = 0; index < buffer_salida.length; index++) {
                    if (buffer_salida[index] == -2) {
                        buffer_salida[index] = 0;
                        cont++;
                    }
                }
            }
            aAuxiliar(rutaSalida, buffer_salida);
            Arrays.fill(buffer_salida, -2);
            indexBuffer=0;
        }
        salidaDeCodigos(rutaDeCodigos,estructura,cont);
    }

    public static void deshacerHuffman(String rutaDeArchivo,String rutaDeCodigos,String rutaDescomprimido) throws IOException {
        HashMap<ArrayList<Integer>,Character> estructura;
        ArrayList<Character> listaC;
        int cont = recuperarCont(rutaDeCodigos);
        listaC = leerArchivo(rutaDeArchivo);

        estructura = llenarEstructura(rutaDeCodigos);

        ArrayList<Integer> caracter = new ArrayList<>();
        int[] arreglocaracter = new int[8];
        Arrays.fill(arreglocaracter,-2);
        int arregloIndice = 0;

        while (!listaC.isEmpty()||!caracter.isEmpty()||distinto(arreglocaracter,-2)){
            if(estructura.containsKey(caracter)){
                escrituraCaracter(rutaDescomprimido,estructura.get(caracter));
                caracter.clear();
            }else {
                if (distinto(arreglocaracter,-2)) {
                    caracter.add(arreglocaracter[arregloIndice]);
                    arreglocaracter[arregloIndice] =-2;
                    arregloIndice++;
                } else {
                    arreglocaracter = aBinarioCharacter(listaC.remove(0));
                    arregloIndice=0;
                    if (listaC.isEmpty()){//eliminar los 0 extra
                        for (int i=8-cont;i<8;i++){
                            arreglocaracter[i]=-2;
                        }
                    }
                }
            }
        }
    }

    private static int recuperarCont(String rutaDeCodigos){
        archive = new File(rutaDeCodigos);
        int cont = 0;
        try {
            b_r_archive = new BufferedReader(new InputStreamReader(new FileInputStream(rutaDeCodigos), "utf-8"));
            cont = Integer.parseInt(b_r_archive.readLine());
            b_r_archive.close();
        } catch (IOException e) {
            System.out.println("El archivo no existe\n");
        }

        return cont;
    }

    private static HashMap<ArrayList<Integer>, Character> llenarEstructura(String rutaDeCodigos) {
        HashMap<ArrayList<Integer>,Character> estructura = new HashMap<>();

        archive = new File(rutaDeCodigos);

        try {
            b_r_archive = new BufferedReader(new InputStreamReader(new FileInputStream(rutaDeCodigos), "utf-8"));
            Character caracter;
            String[] temp;
            b_r_archive.readLine();
            String texto = b_r_archive.readLine();

            while (texto!=null){
                temp = texto.split(",");
                Integer digital[] = new Integer[temp[1].length()];
                int indice=0;
                caracter = (char) Integer.parseInt(temp[0]);
                ArrayList<Integer> list = new ArrayList<>();

                for(char c : temp[1].toCharArray()){
                    list.add(Integer.valueOf(c)-48);
                }
                estructura.put(list,caracter);
                texto = b_r_archive.readLine();
            }
            b_r_archive.close();
        } catch (IOException e) {
            System.out.println("El archivo no existe\n");
        }

        return estructura;
    }

    public static ArrayList<Nodo> unir(ArrayList<Nodo> data){
        Nodo ultimo = data.remove(data.size()-1);
        Nodo anteultimo = data.remove(data.size()-1);
        ultimo.addLastValue(ultimo,1);
        anteultimo.addLastValue(anteultimo,0);
        data.add(new Nodo (ultimo.getProbabilidad() + anteultimo.getProbabilidad(), ultimo.getEstructura(), anteultimo.getEstructura()));
        return data;
    }

    public static void hacerHamming8(String rutaDeArchivo, String rutaSalida, String rutaSalidaError) throws IOException {

        int[] hamming = new int[7];             //ultimo hamming realizado
        int[] buffer_salida = new int[8];       //informacion pendiente para salir

        int[] buffer_salida_error = new int[8];
        int[] hamming_error = new int[8];
        Random random = new Random();

        int[] info = new int[8];                //datos de caracter sin hamming pendientes

        ArrayList<int[]> lista_Renglon ;    //renglon

        ArrayList<Character> listaC;

        Arrays.fill(hamming, -2);
        Arrays.fill(buffer_salida, -2);
        Arrays.fill(info, -2);

        int indexBuffer = 0;
        int indexHamming = 0;

        listaC = leerArchivo(rutaDeArchivo);
        lista_Renglon = actualizarLista(listaC);

        while (distinto(buffer_salida, -2) || distinto(hamming, -2) || !lista_Renglon.isEmpty()) { //mientras quede info que procesar
            while (contains(buffer_salida, -2)) {//mientras el buffer no este listo para salir
                if (distinto(hamming, -2)) {      //si tengo datos para agregar

                    buffer_salida[indexBuffer] = hamming[indexHamming];
                    buffer_salida_error[indexBuffer] = hamming_error[indexHamming];
                    hamming[indexHamming] = -2;
                    hamming_error[indexHamming] = -2;

                    indexHamming++;
                    indexBuffer++;

                } else {   //si necesito hamminizar algo
                    if (distinto(info, -2)) {          //si tengo Info pendiente para hacer hamming
                        hamming = contruirHamming(recorreArreglo(info, 4), 3);
                        if (random.nextBoolean()) {
                            hamming_error = adderror(hamming, 3);
                        } else {
                            hamming_error = hamming;
                        }
                        indexHamming = 0;
                        //haming_error
                    } else {                               //si necesito info para hacer hamming
                        if (!lista_Renglon.isEmpty()) {
                            info = actualizarCaracter(lista_Renglon);
                        } else {
                            break;
                        }
                    }
                }
            }
            if (distinto(buffer_salida, -2) && (!distinto(hamming, -2) && lista_Renglon.isEmpty())) {
                //a buffer de salida lo completo con 0
                for (int index = 0; index < buffer_salida.length; index++) {
                    if (buffer_salida[index] == -2) {
                        buffer_salida[index] = 0;
                    }
                    if (buffer_salida_error[index] == -2) {
                        buffer_salida_error[index] = 0;
                    }
                }
            }
            aAuxiliar(rutaSalida, buffer_salida);
            aAuxiliar(rutaSalidaError, buffer_salida_error);
            Arrays.fill(buffer_salida, -2);
            Arrays.fill(buffer_salida_error, -2);
            indexBuffer = 0;
        }
    }

    public static void hacerHammingDeMasDe8(String rutaDeArchivo, String rutaSalida, String rutaSalidaError, int tipo) throws IOException {

        int[] buffer_salida = new int[8];       //informacion pendiente para salir
        int[] info = new int[8];                //datos de caracter sin hamming pendientes

        ArrayList<int[]> lista_Renglon = new ArrayList<>();    //renglon
        Random random = new Random();
        ArrayList<Character> listaC;

        int tama??oLocal;
        if (tipo == 8) {
            //256
            tama??oLocal = ctrls[tipo] - 8 - 1;
        } else if (tipo == 13) {
            //8k
            tama??oLocal = ctrls[tipo] - 13 - 1;
        } else {// Tipo == 18
            //262k
            tama??oLocal = ctrls[tipo] - 18 - 1;
        }

        int[] hamming = new int[ctrls[tipo] - 1];            //ultimo hamming realizado
        int[] local = new int[tama??oLocal];              //tama??o-cantControles-1

        /*
        static int[] ctrls =        {1,2,4,8,16,32,64,128,256,512,1024,2048,4096,8192,16384,32768,65536,131072,262144};
        static int[] ctrolInCrols = {0,1,2,3, 4, 5, 6,  7,  8,  9,  10,  11,  12,  13,   14,   15,   16,     17,   18};
        static int[] ctrolInArreglo={0,1,3,7,15,31,63,127,255,511,1023,2047,4095,8191,16383,32767,65535,131071,262143};
        */

        //[c,c,0,c,0,1,0,c,0,1, 0, 1, 0, 1, 0,-2]
        // c c 2 c 4 5 6 c 8 9 10 11 12 13 14 15

        int[] buffer_salida_error = new int[8];
        int[] hamming_error = new int[ctrls[tipo]];

        //INICIALIZACION
        Arrays.fill(hamming, -2);
        Arrays.fill(buffer_salida, -2);
        Arrays.fill(info, -2);
        Arrays.fill(local, -2);

        int indexBuffer = 0;
        int indexHamming = 0;

        boolean nomore = false;
        boolean last = false;

        listaC = leerArchivo(rutaDeArchivo);
        lista_Renglon = actualizarLista(listaC);


        while (distinto(buffer_salida, -2) || distinto(hamming, -2) || !lista_Renglon.isEmpty()) { //mientras quede info que procesar
            while (contains(buffer_salida, -2)) { //mientras el buffer no este listo para salir
                if (distinto(hamming, -2)) {      //si tengo datos para agregar
                    buffer_salida[indexBuffer] = hamming[indexHamming];
                    buffer_salida_error[indexBuffer] = hamming_error[indexHamming];
                    hamming[indexHamming] = -2;
                    hamming_error[indexHamming] = -2;

                    indexHamming++;
                    indexBuffer++;
                }
                // info  = [-2,-2,-2,-2,0,0,0,0]
                // local = [1,1,1,0,0,0,0,0,0,0,0,0,0,0,-2]
                else {   //si necesito hamminizar algo
                    if (distinto(info, -2) || last == true) {          //si tengo Info pendiente para hacer hamming
                        if (contains(local, -2)) {
                            local = rellenaArreglo256omas(local, info, primerPosDeInfo(info));
                        } else {
                            hamming = contruirHamming(local, tipo);
                            Arrays.fill(local, -2);
                            indexHamming = 0;
                            last = false;
                        }

                        if (random.nextBoolean()) {
                            hamming_error = adderror(hamming, tipo);
                        } else {
                            hamming_error = hamming;
                        }
                    } else {       //si necesito info para hacer hamming
                        if (!lista_Renglon.isEmpty()) {
                            info = actualizarCaracter(lista_Renglon);
                        } else {
                            if (nomore == false) {
                                nomore = true;
                                last = true;
                                rellenarCon0(local);
                            } else {
                                System.out.println("SE TE VACIO LA LISTA PAPA");
                                break;
                            }

                        }
                    }
                }
            }

            if (distinto(buffer_salida, -2) && (!distinto(hamming, -2) && lista_Renglon.isEmpty())) {
                //a buffer de salida lo completo con 0
                for (int index = 0; index < buffer_salida.length; index++) {
                    if (buffer_salida[index] == -2) {
                        buffer_salida[index] = 0;
                    }
                    if (buffer_salida_error[index] == -2) {
                        buffer_salida_error[index] = 0;
                    }
                }
            }

            aAuxiliar(rutaSalida, buffer_salida);
            aAuxiliar(rutaSalidaError, buffer_salida_error);
            Arrays.fill(buffer_salida, -2);
            Arrays.fill(buffer_salida_error, -2);
            indexBuffer = 0;
        }
    }

    public static void deshacerHamming8(String rutaDeArchivo, String rutaSalida, boolean corregir) throws IOException {
        int[] buffer_salida = new int[8];       //informacion pendiente para salir

        int[] info = new int[4];                //info sin hamming

        int[] caracter = new int[8];            // para decodificar
        int[] paraDecodificar = new int[7];            // para decodificar

        ArrayList<int[]> lista_Renglon = new ArrayList<int[]>();    //renglon

        ArrayList<Character> listaC = new ArrayList<>();

        int indexBuffer = 0;
        int indexInfo = 0;

        Arrays.fill(caracter, -2);
        Arrays.fill(buffer_salida, -2);
        Arrays.fill(info, -2);
        Arrays.fill(paraDecodificar, -2);

        listaC = leerArchivo(rutaDeArchivo);
        lista_Renglon = actualizarLista(listaC);

        while (distinto(buffer_salida, -2) || distinto(info, -2) || !lista_Renglon.isEmpty()) {

            while (contains(buffer_salida, -2)) { //pasar datos a buffer
                if (distinto(info, -2)) {
                    buffer_salida[indexBuffer] = info[indexInfo];
                    info[indexInfo] = -2;

                    indexInfo++;
                    indexBuffer++;
                } else {
                    if (!contains(paraDecodificar, -2)) { //decodifico
                        info = corregirHamming(paraDecodificar, 3, corregir);
                        Arrays.fill(paraDecodificar, -2);
                        indexInfo = 0;
                    } else {
                        if (distinto(caracter, -2)) { // usarlo para llenar decodificar
                            paraDecodificar = recorreArregloAux(caracter, paraDecodificar, primerPosDeInfo(caracter));
                        } else { //pido un nuevo caraceter
                            if (!lista_Renglon.isEmpty()) {
                                caracter = actualizarCaracter(lista_Renglon);
                            } else {
                                break;
                            }
                        }
                    }
                }
            }

            if (distinto(buffer_salida, -2) && (!distinto(info, -2) && lista_Renglon.isEmpty())) {
                //a buffer de salida lo completo con 0
                for (int index = 0; index < buffer_salida.length; index++) {
                    if (buffer_salida[index] == -2) {
                        buffer_salida[index] = 0;
                    }
                }
            }
            aAuxiliar(rutaSalida, buffer_salida);
            Arrays.fill(buffer_salida, -2);
            indexBuffer = 0;
        }

    }

    public static void deshacerHammingDeMasDe8(String rutaDeArchivo, String rutaSalida, int tipo, boolean corregir) throws IOException {

        int tama??o;
        if (tipo == 8) {
            //256
            tama??o = ctrls[tipo] - 8 - 1;

        } else if (tipo == 13) {
            //8k
            tama??o = ctrls[tipo] - 13 - 1;
        } else {// Tipo == 18
            //262k
            tama??o = ctrls[tipo] - 18 - 1;
        }

        int[] buffer_salida = new int[8];       //informacion pendiente para salir

        int[] info = new int[tama??o];
        int[] caracter = new int[8];                                // caracter tomado
        int[] paraDecodificar = new int[ctrls[tipo] - 1];            // para decodificar

        ArrayList<int[]> lista_Renglon = new ArrayList<int[]>();    //renglon

        ArrayList<Character> listaC;

        int indexBuffer = 0;
        int indexInfo = 0;

        Arrays.fill(caracter, -2);
        Arrays.fill(buffer_salida, -2);
        Arrays.fill(info, -2);
        Arrays.fill(paraDecodificar, -2);

        listaC = leerArchivo(rutaDeArchivo);
        lista_Renglon = actualizarLista(listaC);

        while (distinto(buffer_salida, -2) || distinto(info, -2) || !lista_Renglon.isEmpty()) {

            while (contains(buffer_salida, -2)) { //pasar datos a buffer
                if (distinto(info, -2)) {
                    buffer_salida[indexBuffer] = info[indexInfo];
                    info[indexInfo] = -2;

                    indexInfo++;
                    indexBuffer++;
                } else {
                    if (!contains(paraDecodificar, -2)) { //decodifico
                        info = corregirHamming(paraDecodificar, tipo, corregir);
                        Arrays.fill(paraDecodificar, -2);
                        indexInfo = 0;
                    } else {
                        if (distinto(caracter, -2)) { // usarlo para llenar decodificar
                            paraDecodificar = recorreArregloAux(caracter, paraDecodificar, primerPosDeInfo(caracter));
                        } else { //pido un nuevo caraceter
                            if (!lista_Renglon.isEmpty()) {
                                caracter = actualizarCaracter(lista_Renglon);
                            } else {
                                break;
                            }
                        }
                    }
                }
            }
            if (distinto(buffer_salida, -2) && (!distinto(info, -2) && lista_Renglon.isEmpty())) {
                //a buffer de salida lo completo con 0
                for (int index = 0; index < buffer_salida.length; index++) {
                    if (buffer_salida[index] == -2) {
                        buffer_salida[index] = 0;
                    }
                }
            }
            if (distinto(buffer_salida, 0)) {
                aAuxiliar(rutaSalida, buffer_salida);
            }

            Arrays.fill(buffer_salida, -2);
            indexBuffer = 0;
        }

    }

    public static ArrayList<int[]> actualizarLista(ArrayList<Character> listaC) throws IOException {
        return aBinario(listaC);
    }

    public static int[] actualizarCaracter(ArrayList<int[]> lista) {
        //pedir nuevo caracter a lista renglon

        int[] caracter;
        caracter = (lista.get(0)).clone();
        lista.remove(0);
        return caracter;
    }

    public static Integer[] actualizarCaracterInteger(ArrayList<Integer[]> lista) {
        //pedir nuevo caracter a lista renglon

        Integer[] caracter;
        caracter = (lista.get(0)).clone();
        lista.remove(0);
        return caracter;
    }

    public static int[] recorreArreglo(int[] arreglo, int necesarios) {
        int[] aux = new int[necesarios];
        int j = 0;
        for (int i = 0; i < arreglo.length; i++) {
            if (arreglo[i] != -2 && j < necesarios) {
                aux[j] = arreglo[i];
                j++;
                arreglo[i] = -2;
            }
        }
        return aux;
    }

    public static int[] recorreArregloAux(int[] caracter, int[] paraDecodificar, int primero) {
        int j = primero;
        for (int i = 0; i < paraDecodificar.length; i++) {
            if (paraDecodificar[i] == -2 && j < 8 && caracter[j] != -2) {
                paraDecodificar[i] = caracter[j];
                caracter[j] = -2;
                j++;
            }
        }
        return paraDecodificar;
    }

    public static int[] rellenaArreglo256omas(int[] arreglo, int[] caracter, int primero) {
        int j = primero;//recorre caracter

        for (int i = 0; i < arreglo.length; i++) {
            if (arreglo[i] == -2 && j < 8 && caracter[j] != -2) {
                arreglo[i] = caracter[j];
                caracter[j] = -2;
                j++;
            }
        }
        return arreglo;
    }

    public static int[] rellenarCon0(int[] arreglo) {

        for (int i = 0; i < arreglo.length; i++) {
            if (arreglo[i] == -2) {
                arreglo[i] = 0;
            }
        }
        return arreglo;
    }

    public static int primerPosDeInfo(int[] info) {
        int i;
        for (i = 0; i < info.length; i++) {
            if (info[i] != -2) {
                break;
            }
        }
        return i;
    }

    public static ArrayList leerArchivo(String rutaArchive) throws IOException { //se lee la info que esta en el archivo archive
        archive = new File(rutaArchive);
        ArrayList<Character> lis = new ArrayList<>();

        try {
            b_r_archive = new BufferedReader(new InputStreamReader(new FileInputStream(rutaArchive), "utf-8"));
            int caracter = b_r_archive.read();
            while ((caracter != -1) /*&& size != tama??o*/) {
                lis.add((char) caracter);
                //size++;
                caracter = b_r_archive.read();
            }//new line

            b_r_archive.close();
        } catch (IOException e) {
            System.out.println("El archivo no existe\n");
        }
        return lis;
    }

    public static void aAuxiliar(String ruta, int[] contenido) throws IOException {
        File auxiliar = new File(ruta);
        String local = "";

        if (!auxiliar.exists()) {
            auxiliar.createNewFile();
        }

        for (int c : contenido) {
            local += (char) (c + '0');
        }
        w_auxiliar = new FileWriter(auxiliar.getAbsoluteFile(), true);
        b_w_auxiliar = new BufferedWriter(w_auxiliar);

        b_w_auxiliar.write(aDecimal(local));
        b_w_auxiliar.close();
    }

    public static void escrituraCaracter(String rutaArchivo,Character caracter) throws IOException {
        File archivo = new File(rutaArchivo);

        if (!archivo.exists()){
            archivo.createNewFile();
        }

        w_auxiliar = new FileWriter(archivo.getAbsoluteFile(), true);
        b_w_auxiliar = new BufferedWriter(w_auxiliar);
        b_w_auxiliar.write(caracter.charValue());
        b_w_auxiliar.close();
    }

    public static void salidaDeCodigos(String ruta,HashMap<Character, ArrayList<Integer>> estructura,int cont) throws IOException {
        File auxiliar = new File(ruta);
        if (!auxiliar.exists()) {
            auxiliar.createNewFile();
        }
        w_auxiliar = new FileWriter(auxiliar.getAbsoluteFile(), true);
        b_w_auxiliar = new BufferedWriter(w_auxiliar);
        String a = cont+"\n";
        b_w_auxiliar.write(a);

        estructura.forEach((key,value) -> {
            String loc = "";
            for (Integer c : value) {
                loc += (char) (c + '0');
            }
            String local = key.hashCode()+","+loc+"\n";
            try {
                b_w_auxiliar.write(local);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        b_w_auxiliar.close();
    }

    public static char aDecimal(String cadena) throws NumberFormatException {
        if (cadena.equals("........")) {
            char c = (char) 0;
            return c;
        } else {
            return (char) Integer.parseInt(cadena, 2); //paso a nro ascii
        }
    }

    public static int[] aBinarioCharacter (Character caracter){
        int[] info = new int[8];
        String textoBinario = "";

        int n = caracter.charValue(); //obtengo el valor en ascii del caracter
        String letra = Integer.toBinaryString(n); //pasa a binario el nro anterior
        int nCeros = Integer.parseInt(letra); //pasa a entero el string letra para luego poder agregarle los ceros necesarios
        textoBinario += (String.format("%08d", nCeros));

        int cadaN = 8;
        String separarCon = " ";
        textoBinario = textoBinario.replaceAll("(?s).{" + cadaN + "}(?!$)", "$0" + separarCon);

        String[] lenguajesComoArreglo = textoBinario.split(" ");

        int r;
        for (String a : lenguajesComoArreglo) {
            for (r = 0; r < 8; r++) {
                info[r] = Character.getNumericValue(a.charAt(r));
            }
        }
        return info;
    }

    public static ArrayList<int[]> aBinario(ArrayList<Character> lista) throws IOException {
        int[] info = new int[8];
        ArrayList<int[]> aux = new ArrayList<>();
        String textoBinario = "";

        for (Character character : lista) {
            int n = character.charValue(); //obtengo el valor en ascii del caracter
            String letra = Integer.toBinaryString(n); //pasa a binario el nro anterior
            int nCeros = Integer.parseInt(letra); //pasa a entero el string letra para luego poder agregarle los ceros necesarios
            textoBinario += (String.format("%08d", nCeros));

        }
        // Separa los bits para formar bytes

        int cadaN = 8;
        String separarCon = " ";
        textoBinario = textoBinario.replaceAll("(?s).{" + cadaN + "}(?!$)", "$0" + separarCon);

        String[] lenguajesComoArreglo = textoBinario.split(" ");

        int r;
        for (String a : lenguajesComoArreglo) {
            for (r = 0; r < 8; r++) {
                info[r] = Character.getNumericValue(a.charAt(r));
            }
            aux.add(info.clone());
        }
        return aux;
    }

    public static void main(String[] args) throws IOException {
        int select = 0;
        Scanner scan = new Scanner(System.in);
        while (select != 5) {
            System.out.println("MENU\n\n");
            System.out.println("1- CARGAR UN ARCHIVO PARA PROTEGER\n");
            System.out.println("2- CARGAR UN ARCHIVO PARA DECODIFICAR UN ARCHIVO CORRECTO\n");
            System.out.println("3- CARGAR UN ARCHIVO PARA DECODIFICAR CON CORRECCION DE ERROR\n");
            System.out.println("4- CARGAR UN ARCHIVO PARA DECODIFICAR SIN CORRECCION DE ERROR\n");
            System.out.println("5- SALIR\n");

            select = scan.nextInt();

            System.out.println("\n -----------------");

            switch (select) {

                case 1: {
                    int select1 = 0;
                    System.out.println("MENU SELECCIONAR PROTECCION\n");
                    System.out.println("1- BLOQUE DE 8 bits\n");
                    System.out.println("2- BLOQUE DE 256 bits\n");
                    System.out.println("3- BLOQUE DE 8192 bits\n");
                    System.out.println("4- BLOQUE DE 262144 bits\n");
                    System.out.println("5- SALIR\n");
                    select1 = scan.nextInt();

                    switch (select1) {

                        //codifica
                        case 1: {
                            //AQUI HAGO EL 8
                            String rutaArchive = "src/com/company/archivo.txt";
                            String rutaSalida = "src/com/company/hamming8.txt";
                            String rutaSalidaError = "src/com/company/hammingError8.txt";
                            hacerHamming8(rutaArchive, rutaSalida, rutaSalidaError);
                            break;
                        }
                        case 2: {
                            //AQUI HAGO EL 256
                            String rutaArchive = "src/com/company/archivo.txt";
                            String rutaSalida = "src/com/company/hamming256.txt";
                            String rutaSalidaError = "src/com/company/hammingError256.txt";
                            hacerHammingDeMasDe8(rutaArchive, rutaSalida, rutaSalidaError, 8);
                            break;
                        }
                        case 3: {
                            //AQUI HAGO EL 8192
                            String rutaArchive = "src/com/company/archivo.txt";
                            String rutaSalida = "src/com/company/hamming8192.txt";
                            String rutaSalidaError = "src/com/company/hammingError8192.txt";
                            hacerHammingDeMasDe8(rutaArchive, rutaSalida, rutaSalidaError, 13);
                            break;
                        }
                        case 4: {
                            //AQUI HAGO EL 262144
                            String rutaArchive = "src/com/company/archivo.txt";
                            String rutaSalida = "src/com/company/hamming262144.txt";
                            String rutaSalidaError = "src/com/company/hammingError262144.txt";
                            hacerHammingDeMasDe8(rutaArchive, rutaSalida, rutaSalidaError, 18);
                            break;
                        }
                        case 5: {
                            System.out.println("salir");
                            break;
                        }
                    }

                    System.out.println("Codificaccion Exitosa");
                    break;
                }

                case 2: {

                    //decodifica el correcto
                    int select1 = 0;
                    System.out.println("MENU SELECCIONAR PROTECCION A DESHACER\n");
                    System.out.println("1- BLOQUE DE 8 bits\n");
                    System.out.println("2- BLOQUE DE 256 bits\n");
                    System.out.println("3- BLOQUE DE 8192 bits\n");
                    System.out.println("4- BLOQUE DE 262144 bits\n");
                    System.out.println("5- SALIR\n");
                    select1 = scan.nextInt();

                    switch (select1) {
                        case 1: {
                            System.out.println("8");
                            String rutaArchive = "src/com/company/hamming8.txt";
                            String rutaSalida = "src/com/company/decodificacion_de_correcto8.txt";
                            deshacerHamming8(rutaArchive, rutaSalida, false);
                            break;
                        }
                        case 2: {
                            System.out.println("256");
                            String rutaArchive = "src/com/company/hamming256.txt";
                            String rutaSalida = "src/com/company/decodificacion_de_correcto256.txt";
                            deshacerHammingDeMasDe8(rutaArchive, rutaSalida, 8, false);
                            break;
                        }
                        case 3: {
                            System.out.println("8192");
                            String rutaArchive = "src/com/company/hamming8192.txt";
                            String rutaSalida = "src/com/company/decodificacion_de_correcto8192.txt";
                            deshacerHammingDeMasDe8(rutaArchive, rutaSalida, 13, false);
                            break;
                        }
                        case 4: {
                            System.out.println("262144");
                            String rutaArchive = "src/com/company/hamming262144.txt";
                            String rutaSalida = "src/com/company/decodificacion_de_correcto262144.txt";
                            deshacerHammingDeMasDe8(rutaArchive, rutaSalida, 18, false);
                            break;
                        }
                        case 5: {
                            break;
                        }
                    }
                    break;
                }
                case 3: {
                    int select1 = 0;
                    System.out.println("MENU SELECCIONAR PROTECCION A DESHACER CON CORRECCION\n");
                    System.out.println("1- BLOQUE DE 8 bits\n");
                    System.out.println("2- BLOQUE DE 256 bits\n");
                    System.out.println("3- BLOQUE DE 8192 bits\n");
                    System.out.println("4- BLOQUE DE 262144 bits\n");
                    System.out.println("5- SALIR\n");
                    select1 = scan.nextInt();

                    switch (select1) {
                        case 1: {
                            System.out.println("8");
                            String rutaArchive = "src/com/company/hammingError8.txt";
                            String rutaSalida = "src/com/company/decodificacion_con_correccion8.txt";
                            deshacerHamming8(rutaArchive, rutaSalida, true);
                            break;
                        }
                        case 2: {
                            System.out.println("256");
                            String rutaArchive = "src/com/company/hammingError256.txt";
                            String rutaSalida = "src/com/company/decodificacion_con_correccion256.txt";
                            deshacerHammingDeMasDe8(rutaArchive, rutaSalida, 8, true);
                            break;
                        }
                        case 3: {
                            System.out.println("8192");
                            String rutaArchive = "src/com/company/hammingError8192.txt";
                            String rutaSalida = "src/com/company/decodificacion_con_correccion8192.txt";
                            deshacerHammingDeMasDe8(rutaArchive, rutaSalida, 13, true);
                            break;
                        }
                        case 4: {
                            System.out.println("262144");
                            String rutaArchive = "src/com/company/hammingError262144.txt";
                            String rutaSalida = "src/com/company/decodificacion_con_correccion262144.txt";
                            deshacerHammingDeMasDe8(rutaArchive, rutaSalida, 18, true);
                            break;
                        }
                        case 5: {
                            break;
                        }
                    }
                    break;
                }
                case 4: {
                    int select1 = 0;
                    System.out.println("MENU SELECCIONAR PROTECCION A DESHACER SIN CORRECCION\n");
                    System.out.println("1- BLOQUE DE 8 bits\n");
                    System.out.println("2- BLOQUE DE 256 bits\n");
                    System.out.println("3- BLOQUE DE 8192 bits\n");
                    System.out.println("4- BLOQUE DE 262144 bits\n");
                    System.out.println("5- SALIR\n");
                    select1 = scan.nextInt();

                    switch (select1) {
                        case 1: {
                            System.out.println("8");
                            String rutaArchive = "src/com/company/hammingError8.txt";
                            String rutaSalida = "src/com/company/decodificacion_sin_correccion8.txt";
                            deshacerHamming8(rutaArchive, rutaSalida, false);
                            break;
                        }
                        case 2: {
                            System.out.println("256");
                            String rutaArchive = "src/com/company/hammingError256.txt";
                            String rutaSalida = "src/com/company/decodificacion_sin_correccion256.txt";
                            deshacerHammingDeMasDe8(rutaArchive, rutaSalida, 8, false);
                            break;
                        }
                        case 3: {
                            System.out.println("8192");
                            String rutaArchive = "src/com/company/hammingError8192.txt";
                            String rutaSalida = "src/com/company/decodificacion_sin_correccion8192.txt";
                            deshacerHammingDeMasDe8(rutaArchive, rutaSalida, 13, false);
                            break;
                        }
                        case 4: {
                            System.out.println("262144");
                            String rutaArchive = "src/com/company/hammingError262144.txt";
                            String rutaSalida = "src/com/company/decodificacion_sin_correccion262144.txt";
                            deshacerHammingDeMasDe8(rutaArchive, rutaSalida, 18, false);
                            break;
                        }
                        case 5: {
                            break;
                        }
                    }
                    break;
                }
                case 5: {
                    hacerHuffman("src/com/company/huffman.txt","src/com/company/huffmanSalida.txt","src/com/company/codigosHuffman.txt");
                    //System.out.println("SALIENDO DEL PROGRAMA");
                    break;
                }
                case 6:
                    deshacerHuffman("src/com/company/huffmanSalida.txt","src/com/company/codigosHuffman.txt","src/com/company/huffmanDescomprimido.txt");
                    break;
            }

        }


    }

    public static int[] contruirHamming(int[] info, int cant) {

        int[] ham = new int[ctrls[cant] - 1];
        int j = 0;

        for (int i = 0; i < ctrls[cant] - 1; i++) {
            if (inCtrls(i + 1)) {
                ham[i] = -1;
            } else {
                if (j < info.length) {
                    ham[i] = info[j];
                    j++;
                }
            }
        }
        ham = addcontrols(ham, cant);
        return ham;
        //salida hamming con controles
    }

    public static int[] addcontrols(int[] arr, int posInCtrol) {

        int posicionArreglo;
        int contadorDeRecoleccion;
        int contFijo;                                   //valor que reinicia contadorDeRecoleccion

        int posCtrol;                                   //posicion donde colocar el control
        int tope = ((ctrls[posInCtrol]) - 2);           //ultima posicion de informacion


        //int[] ctrls               {1,2,4,8,16,32,64,128,256,512,1024,2048,4096,8192,16384,32768,65536,131072,262144};
//cant  //int[] ctrolInCrols         0 1 2 3 4  5  6   7   8   9   10   11   12   13   14   15     16     17     18
        //int[] ctrolInArreglo       0 1 3 7 15 31 63 127 255 511,1023,2047,4095,8191,16383,32767,65535,131071,262143


        //[0...255] // 8controles
        //   [-1,-1,0,-1,1,1,0,-2]
        //    0  1  2  3 4 5 6  7

        //3 //8  //13  //18
        //3//127//4095//131071
        //7


        int acumulador;
        for (int p = posInCtrol - 1; 0 <= p; p--) {
            posCtrol = ctrolInArreglo[p]; //128
            acumulador = 0;
            contFijo = ctrls[p]; //128
            contadorDeRecoleccion = contFijo;

            for (posicionArreglo = tope; posCtrol < posicionArreglo; posicionArreglo--) {
                acumulador = acumulador + arr[posicionArreglo];
                contadorDeRecoleccion--;

                if (contadorDeRecoleccion == 0) {
                    posicionArreglo = posicionArreglo - contFijo;
                    contadorDeRecoleccion = contFijo;
                }
            }
            if (acumulador % 2 == 0) {
                arr[posicionArreglo] = 0;
            } else {
                arr[posicionArreglo] = 1;
            }
        }
        return arr;
    }

    public static int[] adderror(int[] arr, int cant) {
        int tope = (ctrls[cant] - 2);
        Random random = new Random();
        int[] local;
        local = arr.clone();
        int posErr = random.nextInt(tope + 1);

        if (local[posErr] == 0) {
            local[posErr] = 1;
        } else {
            local[posErr] = 0;
        }
        return local;
    }

    public static int[] corregirHamming(int a[], int parity_count, boolean corregir) {

        //esta funcion recibe un codigo hamming en un array a -> [1,0,1,1,0,0,0,0](por ej, ni idea si esta bien)
        //tambien pasamos por parametro el numero de bits de paridad o de control q se a??adio a la informacion original.
        // es decir, si la info era: [1,0,0,0] y los bits de control son: c1 = 1, c2 = 0 y c3 = 1, tenemos 3 bits de control.
        // Ahora tendremos que detectar el error y corregirlo, si es que hay uno.


        int posicionArreglo;
        int contadorDeRecoleccion;
        int contFijo;                                   //valor que reinicia contadorDeRecoleccion

        int posCtrol;   //posicion donde colocar el control
        int tope = ((ctrls[parity_count]) - 2);           //ultima posicion de informacion


        int acumulador;
        int[] syn = new int[parity_count];
        int indiceSyn = parity_count - 1;

        for (int p = parity_count - 1; 0 <= p; p--) {
            posCtrol = ctrolInArreglo[p];
            acumulador = 0;
            contFijo = ctrls[p];
            contadorDeRecoleccion = contFijo;

            for (posicionArreglo = tope; posCtrol <= posicionArreglo; posicionArreglo--) {
                acumulador = acumulador + a[posicionArreglo];
                contadorDeRecoleccion--;
                if (contadorDeRecoleccion == 0) {
                    posicionArreglo = posicionArreglo - contFijo;
                    contadorDeRecoleccion = contFijo;
                }
            }
            if (acumulador % 2 == 0) {
                syn[indiceSyn] = 0;
                indiceSyn--;
            } else {
                syn[indiceSyn] = 1;
                indiceSyn--;
            }
        }


        //[0,1,1,1]

        String syndrome = "";

        for (int j = 0; j < syn.length; j++) {
            syndrome += syn[j];
        }
        StringBuilder s = new StringBuilder(syndrome);
        syndrome = s.reverse().toString();


        int tama??oLocal;
        if (parity_count == 3) {
            tama??oLocal = ctrls[parity_count] - 3 - 1;
        } else {
            if (parity_count == 8) {
                //256
                tama??oLocal = ctrls[parity_count] - 8 - 1;
            } else if (parity_count == 13) {
                //8k
                tama??oLocal = ctrls[parity_count] - 13 - 1;
            } else {// Tipo == 18
                //262k
                tama??oLocal = ctrls[parity_count] - 18 - 1;
            }
        }

        if (distinto(syn, 0)) {
            System.out.println("alto ahi pirata");
        }

        int infoEntero[] = new int[tama??oLocal];

        if (corregir == false) {
            syndrome = "0";
        }

        // usando estos valores, ahora verificaremos si hay un error de un solo bit y luego lo corregiremos

        int error_location = Integer.parseInt(syndrome, 2);
        System.out.println("error en:" + error_location);
        String codigoCorregido = "";
        String infoFinal = "";

        if (error_location != 0) {
            a[error_location - 1] = (a[error_location - 1] + 1) % 2;

            //1  0  0  1  1  1  1
            //error_location = 1, pero como en el arreglo los elementos se guardan desde la posicion 0, le restamos 1.
            //entonces a[error_location -1] -> en este caso a[0] = 1, haremos:
            //a[error_location-1]+1 = 2 % 2 = 0 -> es decir que colocamos un cero.
            //si tuvieramos un 0 en lugar de un 1 en a[0], entonces tendriamos: 1 % 2 = 1 -> es decir que colocamos un uno.

            for (int i = 0; i < a.length; i++) {
                codigoCorregido += (a[a.length - i - 1]);
            }
            StringBuilder sb = new StringBuilder(codigoCorregido);
            codigoCorregido = sb.reverse().toString();
        } else {
        }

        //extraemos los datos originales del c??digo recibido (y corregido)

        int power = parity_count - 1;
        int r = 0;
        for (int i = a.length; i > 0; i--) {
            if (Math.pow(2, power) != i) {
                infoFinal += (a[i - 1]);
            } else {
                power--;
            }
        }
        StringBuilder sbFinal = new StringBuilder(infoFinal);
        infoFinal = sbFinal.reverse().toString();

        for (int i = 0; i < infoFinal.length(); i++) {
            infoEntero[i] = Character.getNumericValue(infoFinal.charAt(i));
        }
        return infoEntero; 

    }

    public static boolean inCtrls(int numero) {
        return Arrays.stream(ctrls).anyMatch(i -> i == numero);
    }

    public static boolean contains(final int[] arr, final int key) {
        return Arrays.stream(arr).anyMatch(i -> i == key);
    }

    public static boolean distinto(final int[] arr, final int key) {
        return Arrays.stream(arr).anyMatch(i -> i != key);
    }

    public static boolean distintoInteger(final Integer[] arr, final int key) {
        return Arrays.stream(arr).anyMatch(i -> i != key);
    }
}