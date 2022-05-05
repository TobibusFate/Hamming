package com.company;

import java.io.*;
import java.lang.reflect.Array;
import java.security.cert.CertPathChecker;
import java.util.*;


public class Main{

    static File archive;
    static FileReader r_archive;
    static FileWriter w_auxiliar;
    static BufferedWriter b_w_auxiliar;
    static BufferedReader b_r_archive;
    static ArrayList<String> listaS = new ArrayList<>();
    static ArrayList<Character> listaC = new ArrayList<>();


    static int[] ctrls = {1,2,4,8,16,32,64,128,256,512,1024,2048,4096,8192,16384,32768,65536,131072,262144};
    static int[] ctrolInCrols ={0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18};
    static int[] ctrolInArreglo={0,1,3,7,15,31,63,127,255,511,1023,2047,4095,8191,16383,32767,65535,131071,262143};

    public static void inicio(String rutaDeArchivo,String rutaSalida,String rutaSalidaError) throws IOException {

        int[] hamming = new int[8];             //ultimo hamming realizado
        int[] buffer_salida = new int[8];       //informacion pendiente para salir

        int[] buffer_salida_error = new int[8];
        int[] hamming_error = new int[8];
        Random random =new Random();

        int[] info = new int[8];                //datos de caracter sin hamming pendientes

        ArrayList<int[]> lista_Renglon = new ArrayList<int[]>();    //renglon

        ArrayList<String> listaStrings = new ArrayList<>();

        Arrays.fill(hamming,-2);
        Arrays.fill(buffer_salida,-2);
        Arrays.fill(info,-2);

        int indexBuffer = 0;
        int indexHamming = 0;


        listaStrings = leerArchivo(rutaDeArchivo);

        while (distinto(buffer_salida,-2)||distinto(hamming,-2)||!lista_Renglon.isEmpty()||!listaStrings.isEmpty()){ //mientras quede info que procesar
            while (contains(buffer_salida,-2)){//mientras el buffer no este listo para salir

                if(distinto(hamming,-2)) {      //si tengo datos para agregar


                    /*if(indexHamming==8||indexBuffer==8){

                    }*/

                    buffer_salida[indexBuffer] = hamming[indexHamming];
                    buffer_salida_error[indexBuffer]=hamming_error[indexHamming];
                    hamming[indexHamming]=-2;
                    hamming_error[indexHamming]=-2;

                    indexHamming++;
                    indexBuffer++;

                }
                else{   //si necesito hamminizar algo
                    if(distinto(info,-2)){          //si tengo Info pendiente para hacer hamming
                        hamming = contruirHamming(recorreArreglo(info,4),3);
                        if(random.nextBoolean()){
                            hamming_error = adderror(hamming,3);
                        }else{
                            hamming_error = hamming;
                        }
                        indexHamming=0;
                        //haming_error
                    }
                    else{                               //si necesito info para hacer hamming
                        if(lista_Renglon.isEmpty()){
                            if(!listaStrings.isEmpty()){
                                lista_Renglon =actualizarLista(listaStrings);
                            }else {
                                break;
                            }
                        }
                        else {
                            info = actualizarCaracter(lista_Renglon);
                        }
                    }
                }
            }
            if(distinto(buffer_salida,-2)&&(!distinto(hamming,-2)&&lista_Renglon.isEmpty()&&listaStrings.isEmpty())){
                //a buffer de salida lo completo con 0
                for(int index=0;index<buffer_salida.length;index++){
                    if (buffer_salida[index]==-2){
                        buffer_salida[index]=0;
                    }
                    if (buffer_salida_error[index]==-2){
                        buffer_salida_error[index]=0;
                    }
                }
            }

            aAuxiliar(rutaSalida,buffer_salida);
            aAuxiliar(rutaSalidaError,buffer_salida_error);
            Arrays.fill(buffer_salida,-2);
            Arrays.fill(buffer_salida_error,-2);
            indexBuffer=0;
        }
    }


//8 13 18
    public static void inicio256(String rutaDeArchivo,String rutaSalida,String rutaSalidaError, int tipo) throws IOException {

        int[] buffer_salida = new int[8];       //informacion pendiente para salir
        int[] info = new int[8];                //datos de caracter sin hamming pendientes

        ArrayList<int[]> lista_Renglon = new ArrayList<>();    //renglon
        Random random =new Random();
        ArrayList<String> listaStrings;

        int tamañoLocal;
        if(tipo == 8){
            //256
            tamañoLocal = ctrls[tipo]-8-1;
        }else if(tipo == 13){
            //8k
            tamañoLocal = ctrls[tipo]-13-1;
        }
        else {// Tipo == 18
            //262k
            tamañoLocal =ctrls[tipo]-18-1;
        }

        int[] hamming = new int[ctrls[tipo]];            //ultimo hamming realizado
        int[] local = new int[tamañoLocal];              //tamaño-cantControles-1

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
        Arrays.fill(hamming,-2);
        Arrays.fill(buffer_salida,-2);
        Arrays.fill(info,-2);
        Arrays.fill(local,-2);

        int indexBuffer = 0;
        int indexHamming = 0;

        boolean nomore = false;
        boolean last= false;

        listaStrings = leerArchivo(rutaDeArchivo);

        while (distinto(buffer_salida,-2)||distinto(hamming,-2)||!lista_Renglon.isEmpty()||!listaStrings.isEmpty()){ //mientras quede info que procesar

            while (contains(buffer_salida,-2)){ //mientras el buffer no este listo para salir
                if(distinto(hamming,-2)) {      //si tengo datos para agregar

                    buffer_salida[indexBuffer] = hamming[indexHamming];
                    buffer_salida_error[indexBuffer]=hamming_error[indexHamming];
                    hamming[indexHamming]=-2;
                    hamming_error[indexHamming]=-2;

                    indexHamming++;
                    indexBuffer++;

                }
                // info  = [-2,-2,-2,-2,0,0,0,0]
                // local = [1,1,1,0,0,0,0,0,0,0,0,0,0,0,-2]
                else{   //si necesito hamminizar algo
                    if(distinto(info,-2)||last==true){          //si tengo Info pendiente para hacer hamming
                        if(contains(local,-2)){
                            local = rellenaArreglo256omas(local,info,primerPosDeInfo(info));
                        }
                        else{
                            hamming = contruirHamming(local,tipo);
                            Arrays.fill(local,-2);
                            indexHamming=0;
                            last=false;
                        }

                        if(random.nextBoolean()){
                            hamming_error = adderror(hamming,tipo);
                        }else{
                            hamming_error = hamming;
                        }

                        //haming_error
                    }
                    else{                               //si necesito info para hacer hamming
                        if(lista_Renglon.isEmpty()){
                            if(!listaStrings.isEmpty()){
                                lista_Renglon = actualizarLista(listaStrings);
                            }else {
                                if(nomore==false){
                                    nomore=true;
                                    last=true;
                                    rellenarCon0(local);
                                }else{
                                    System.out.println("SE TE VACIO LA LISTA PAPA");
                                    break;
                                }
                            }
                        }
                        else {
                            info = actualizarCaracter(lista_Renglon);
                        }
                    }
                }
            }
            if(distinto(buffer_salida,-2)&&(!distinto(hamming,-2)&&lista_Renglon.isEmpty()&&listaStrings.isEmpty())){
                //a buffer de salida lo completo con 0
                for(int index=0;index<buffer_salida.length;index++){
                    if (buffer_salida[index]==-2){
                        buffer_salida[index]=0;
                    }
                    if (buffer_salida_error[index]==-2){
                        buffer_salida_error[index]=0;
                    }
                }
            }
            System.out.println(random.nextBoolean());

            aAuxiliar(rutaSalida,buffer_salida);
            aAuxiliar(rutaSalidaError,buffer_salida_error);
            Arrays.fill(buffer_salida,-2);
            Arrays.fill(buffer_salida_error,-2);
            indexBuffer=0;
        }
    }


    
    public static void fin8(String rutaDeArchivo,String rutaSalida,boolean corregir) throws IOException {
        int[] buffer_salida = new int[8];       //informacion pendiente para salir

        int[] info = new int[4];                //info sin hamming

        int[] caracter = new int[8];            // para decodificar
        int[] paraDecodificar = new int[7];            // para decodificar

        ArrayList<int[]> lista_Renglon = new ArrayList<int[]>();    //renglon

        ArrayList<String> listaStrings = new ArrayList<>();

        int indexBuffer = 0;
        int indexInfo = 0;

        Arrays.fill(caracter,-2);
        Arrays.fill(buffer_salida,-2);
        Arrays.fill(info,-2);
        Arrays.fill(paraDecodificar,-2);

        listaStrings = leerArchivo(rutaDeArchivo);

        while (distinto(buffer_salida,-2)|| distinto(info,-2)||!lista_Renglon.isEmpty()||!listaStrings.isEmpty()){

          while (contains(buffer_salida,-2)){ //pasar datos a buffer
              if (distinto(info,-2)){
                    buffer_salida[indexBuffer] = info[indexInfo];
                    info[indexInfo]= -2;

                    indexInfo++;
                    indexBuffer++;
              }else{
                  if (!contains(paraDecodificar,-2)){ //decodifico
                      info = corregirHamming(paraDecodificar, 3,corregir);
                      Arrays.fill(paraDecodificar,-2);
                      indexInfo = 0;
                  } else{
                      if(distinto(caracter,-2)){ // usarlo para llenar decodificar
                        paraDecodificar = recorreArregloAux(caracter, paraDecodificar, primerPosDeInfo(caracter));
                      }else{ //pido un nuevo caraceter
                            if(lista_Renglon.isEmpty()){
                                if(!listaStrings.isEmpty()){
                                    lista_Renglon = actualizarLista(listaStrings);
                                }else {
                                    System.out.println("SE TE VACIO LA LISTA PAPA");
                                    break;
                                }
                            }else {
                                   caracter = actualizarCaracter(lista_Renglon);
                            }
                      }
              }
            }
        }
        if(distinto(buffer_salida,-2)&&(!distinto(info,-2)&&lista_Renglon.isEmpty()&&listaStrings.isEmpty())){
            //a buffer de salida lo completo con 0
            for(int index=0;index<buffer_salida.length;index++){
                if (buffer_salida[index]==-2){
                    buffer_salida[index]=0;
                }
            }
        }
        aAuxiliar(rutaSalida,buffer_salida);
        Arrays.fill(buffer_salida,-2);
        indexBuffer=0;
     }

    }

    public static void fin256(String rutaDeArchivo,String rutaSalida, int tipo,boolean corregir) throws IOException {

        int tamaño;
        if(tipo == 8){
            //256
            tamaño = ctrls[tipo]-8-1;

        }else if(tipo == 13){
            //8k
            tamaño = ctrls[tipo]-13-1;
        }
        else {// Tipo == 18
            //262k
            tamaño = ctrls[tipo]-18-1;
        }

        int[] buffer_salida = new int[8];       //informacion pendiente para salir

        int[] info = new int[tamaño];

        //ctrls[tipo]
        int[] caracter = new int[8];                                // caracter tomado
        int[] paraDecodificar = new int[ctrls[tipo]-1];            // para decodificar

        ArrayList<int[]> lista_Renglon = new ArrayList<int[]>();    //renglon

        ArrayList<String> listaStrings = new ArrayList<>();

        int indexBuffer = 0;
        int indexInfo = 0;

        Arrays.fill(caracter,-2);
        Arrays.fill(buffer_salida,-2);
        Arrays.fill(info,-2);
        Arrays.fill(paraDecodificar,-2);

        listaStrings = leerArchivo(rutaDeArchivo);

        while (distinto(buffer_salida,-2)|| distinto(info,-2)||!lista_Renglon.isEmpty()||!listaStrings.isEmpty()){

          while (contains(buffer_salida,-2)){ //pasar datos a buffer
              if (distinto(info,-2)){
                    buffer_salida[indexBuffer] = info[indexInfo];
                    info[indexInfo]= -2;

                    indexInfo++;
                    indexBuffer++;
              }else{
                  if (!contains(paraDecodificar,-2)){ //decodifico
                      info = corregirHamming(paraDecodificar, tipo,corregir);
                      Arrays.fill(paraDecodificar,-2);
                      indexInfo = 0;
                  } else{
                      if(distinto(caracter,-2)){ // usarlo para llenar decodificar
                        paraDecodificar = recorreArregloAux(caracter, paraDecodificar, primerPosDeInfo(caracter));
                      }else{ //pido un nuevo caraceter
                            if(lista_Renglon.isEmpty()){
                                if(!listaStrings.isEmpty()){
                                    lista_Renglon = actualizarLista(listaStrings);
                                }else {
                                    System.out.println("SE TE VACIO LA LISTA PAPA");
                                    break;
                                }
                            }else {
                                   caracter = actualizarCaracter(lista_Renglon);
                            }
                      }
              }
            }
        }
        if(distinto(buffer_salida,-2)&&(!distinto(info,-2)&&lista_Renglon.isEmpty()&&listaStrings.isEmpty())){
            //a buffer de salida lo completo con 0
            for(int index=0;index<buffer_salida.length;index++){
                if (buffer_salida[index]==-2){
                    buffer_salida[index]=0;
                }
            }
        }
        aAuxiliar(rutaSalida,buffer_salida);
        Arrays.fill(buffer_salida,-2);
        indexBuffer=0;
     }

    }


    public static ArrayList<int[]> actualizarLista(ArrayList<String> listaString) throws IOException {
        //pedir nuevo renglon
        listaC = pasarCadaC(listaString.get(0)); //pasamos lo que esta en listas a una listaC que tiene caracteres, no strings
        listaString.remove(0);
        return aBinario(listaC);
    }

    public static int[] actualizarCaracter(ArrayList<int[]> lista){
        //pedir nuevo caracter a lista renglon

        int[] caracter = new int[8];
        caracter = (lista.get(0)).clone();
        lista.remove(0);
        return caracter;
    }

    public static int[] recorreArreglo(int[] arreglo, int necesarios){
        int[] aux = new int[necesarios];
        int j=0;
        for (int i=0;i<arreglo.length;i++){
            if(arreglo[i]!=-2 && j<necesarios){
                aux[j]=arreglo[i];
                j++;
                arreglo[i]=-2;
            }
        }
        return aux;
    }

    public static int[] recorreArregloAux(int[] caracter, int[] paraDecodificar, int primero){
        int j = primero;
        for (int i=0;i<paraDecodificar.length;i++){
                if(paraDecodificar[i]==-2 && j < 8 && caracter[j]!=-2){
                    paraDecodificar[i] = caracter[j];
                    caracter[j]=-2;
                    j++;
                }
            }
        return paraDecodificar;
    }

    //[-2,-2,-2,-2,-2,-2,-2,-2,-2,-2,-2,-2,-2,-2....,-2]

    //[1,1,1,1,1,0,0,1]

    public static int[] rellenaArreglo256omas(int[] arreglo, int[] caracter,int primero){
        int j=primero;//recorre caracter

        for (int i=0;i<arreglo.length;i++){
            if(arreglo[i]==-2 && j < 8 && caracter[j]!=-2){
                arreglo[i]=caracter[j];
                caracter[j]=-2;
                j++;
            }
        }
        return arreglo;
    }

    public static int[] rellenarCon0(int[] arreglo){

        for (int i=0;i<arreglo.length;i++){
            if(arreglo[i]==-2){
                arreglo[i]=0;
            }
        }
        return arreglo;
    }

    public static int primerPosDeInfo(int[] info){
        int i;
        for (i=0;i<info.length;i++){
            if(info[i]!=-2){
                break;
            }
        }
        return i;
    }

    public static ArrayList leerArchivo(String rutaArchive) throws IOException { //se lee la info que esta en el archivo archive
        archive = new File(rutaArchive);
        String texto = "";

        ArrayList<String> lis = new ArrayList<>();
        try{
            b_r_archive = new BufferedReader(new InputStreamReader(new FileInputStream(rutaArchive), "utf-8"));
            while ((texto = b_r_archive.readLine()) != null){
                //sb_archive.append(texto+"\n");
                lis.add(texto+"\n");
            }//new line

            b_r_archive.close();
        }catch(IOException e){
            System.out.println("El archivo no existe\n");
        }
        return lis;
    }


    public static void aAuxiliar(String ruta, int[] contenido) throws IOException{
        File auxiliar = new File(ruta);
        String local="";
        char finString[] = new char[local.length()];
        char aux[] = new char[8];

        if (!auxiliar.exists()) {
            auxiliar.createNewFile();
        }

        for (int c: contenido){
            local+=(char)(c +'0');
        }
        w_auxiliar = new FileWriter(auxiliar.getAbsoluteFile(),true);
        b_w_auxiliar = new BufferedWriter(w_auxiliar);
        b_w_auxiliar.write(aDecimal(local));
        b_w_auxiliar.close();
    }

    public static char aDecimal(String cadena) throws NumberFormatException{
        if(cadena.equals("........")){
            char c = (char) 32;
            return c;
        }else{
            return (char) Integer.parseInt(cadena,2); //paso a nro ascii
        }
    }


    public static ArrayList<Character> pasarCadaC(String cadena){
        int i;
        ArrayList<Character> lista = new ArrayList<>();
        /*for (String cadena : listaS) {
            for(i = 0; i< cadena.length(); i++){
                lista.add(cadena.charAt(i));
            }
        }*/
        for(i = 0; i< cadena.length(); i++){
            lista.add(cadena.charAt(i));
        }
        return lista;
    }

    public static ArrayList<int[]> aBinario(ArrayList<Character> lista)throws IOException{
        int[] info = new int[8];
        ArrayList<int[]> aux = new ArrayList<int[]>();
        String textoBinario = "";

        for (Character character : lista) {
            int n = character.charValue(); //obtengo el valor en ascii del caracter
            String letra = Integer.toBinaryString(n); //pasa a binario el nro anterior
            int nCeros = Integer.parseInt(letra); //pasa a entero el string letra para luego poder agregarle los ceros necesarios
            textoBinario += (String.format("%08d",nCeros));

        }
        // Separa los bits para formar bytes

        int cadaN = 8;
        String separarCon = " ";
        textoBinario = textoBinario.replaceAll("(?s).{" + cadaN + "}(?!$)", "$0" + separarCon);

        String[] lenguajesComoArreglo = textoBinario.split(" ");

        int r;
        for(String a: lenguajesComoArreglo){
            for (r=0;r<8;r++){
                info[r]=Character.getNumericValue(a.charAt(r));
            }
            aux.add(info.clone());
        }
        return aux;
    }

    //Funciones que nos serviran más adelante

    /*public static File crearArchivos(File entrada, int tipo, int bloque){
        //TIPO 0 -> HE ->Hamming con Error en Archivo
        //TIPO 1 -> DE ->Archivo Decodificado con Error sobre Archivo
        //TIPO 2 -> DC ->Archivo Decodificado Corregido sobre Archivo

        String url = generarNombres(entrada, tipo, bloque); //cada archivo tiene un nombre diferente segun el tipo y la cant de bits que se toman por bloque
        String ruta = entrada.getParent()+"\\"+url;
        File fileOutput = new File(ruta);
        try {
            if (fileOutput.createNewFile()) { //se crea el archivo con su debido nombre
                System.out.println("Archivo creado: " + fileOutput.getName());
            }
            else {
                fileOutput.delete();
                fileOutput = new File(ruta);
                fileOutput.createNewFile();
            }
        } catch (IOException ex) {
            System.out.println("No se pudo crear el archivo\n");
        }
        return fileOutput;
    }

    public static String generarNombres(File entrada, int tipo, int bloque){
        //TIPO 0 -> HE ->Hamming con Error en Archivo
        //TIPO 1 -> DE ->Archivo Decodificado con Error sobre Archivo
        //TIPO 2 -> DC ->Archivo Decodificado Corregido sobre Archivo
        String nombre = entrada.getName().split("\\.")[0];
        switch(tipo){
            case 0 ->{return nombre.concat(".HE"+String.valueOf(bloque));}
            case 1 ->{return nombre.concat(".DE"+String.valueOf(bloque));}
            case 2 ->{return nombre.concat(".DC"+String.valueOf(bloque));}
        }
        return "";
    }*/

    public static void main(String[] args) throws IOException {
        int select = 0;
        Scanner scan = new Scanner(System.in);
        while (select!=5){
            System.out.println("MENU\n\n");
            System.out.println("1- CARGAR UN ARCHIVO PARA PROTEGER\n");
            System.out.println("2- CARGAR UN ARCHIVO PARA DECODIFICAR UN ARCHIVO CORRECTO\n");
            System.out.println("3- CARGAR UN ARCHIVO PARA DECODIFICAR CON CORRECCION DE ERROR\n");
            System.out.println("4- CARGAR UN ARCHIVO PARA DECODIFICAR SIN CORRECCION DE ERROR\n");
            System.out.println("5- SALIR\n");

            select = scan.nextInt();

            System.out.println("\n -----------------");

            switch (select){

                case 1:{
                    int select1 = 0;
                    System.out.println("MENU SELECCIONAR PROTECCION\n");
                    System.out.println("1- BLOQUE DE 8 bits\n");
                    System.out.println("2- BLOQUE DE 256 bits\n");
                    System.out.println("3- BLOQUE DE 8192 bits\n");
                    System.out.println("4- BLOQUE DE 262144 bits\n");
                    System.out.println("5- SALIR\n");
                    select1 = scan.nextInt();

                    switch (select1){
                        case 1:{
                            //AQUI HAGO EL 8
                            String rutaArchive = "src/com/company/archivo.txt";
                            String rutaSalida = "src/com/company/hamming8.txt";
                            String rutaSalidaError = "src/com/company/hammingError8.txt";
                            inicio(rutaArchive,rutaSalida,rutaSalidaError);
                            break;
                        }
                        case 2:{
                            //AQUI HAGO EL 256
                            String rutaArchive = "src/com/company/archivo.txt";
                            String rutaSalida = "src/com/company/hamming256.txt";
                            String rutaSalidaError = "src/com/company/hammingError256.txt";
                            inicio256(rutaArchive,rutaSalida,rutaSalidaError,8);
                            break;
                        }
                        case 3:{
                            //AQUI HAGO EL 8192
                            String rutaArchive = "src/com/company/archivo.txt";
                            String rutaSalida = "src/com/company/hamming8192.txt";
                            String rutaSalidaError = "src/com/company/hammingError8192.txt";
                            inicio256(rutaArchive,rutaSalida,rutaSalidaError,13);
                            break;
                        }
                        case 4:{
                            //AQUI HAGO EL 262144
                            String rutaArchive = "src/com/company/archivo.txt";
                            String rutaSalida = "src/com/company/hamming262144.txt";
                            String rutaSalidaError = "src/com/company/hammingError262144.txt";
                            inicio256(rutaArchive,rutaSalida,rutaSalidaError,18);
                            break;
                        }
                        case 5:{
                            System.out.println("salir");
                            break;
                        }
                    }
                    break;
                }

                case 2:{
                    int select1 = 0;
                    System.out.println("MENU SELECCIONAR PROTECCION A DESHACER SIN ERROR\n");
                    System.out.println("1- BLOQUE DE 8 bits\n");
                    System.out.println("2- BLOQUE DE 256 bits\n");
                    System.out.println("3- BLOQUE DE 8192 bits\n");
                    System.out.println("4- BLOQUE DE 262144 bits\n");
                    System.out.println("5- SALIR\n");
                    select1 = scan.nextInt();

                    switch (select1){
                        case 1:{
                            System.out.println("8");
                            String rutaArchive = "src/com/company/hamming.txt";
                            String rutaSalida = "src/com/company/decodificacion_de_correcto8.txt";
                            fin8(rutaArchive, rutaSalida,true);
                            break;
                        }
                        case 2:{
                            System.out.println("256");
                            String rutaArchive = "src/com/company/hamming256.txt";
                            String rutaSalida = "src/com/company/decodificacion_de_correcto256.txt";
                            fin256(rutaArchive, rutaSalida, 8,true);
                            break;
                        }
                        case 3:{
                            System.out.println("8192");
                            String rutaArchive = "src/com/company/hamming8192.txt";
                            String rutaSalida = "src/com/company/decodificacion_de_correcto8192.txt";
                            fin256(rutaArchive, rutaSalida, 13,true);
                            break;
                        }
                        case 4:{
                            System.out.println("262144");
                            String rutaArchive = "src/com/company/hamming262144.txt";
                            String rutaSalida = "src/com/company/decodificacion_de_correcto262144.txt";
                            fin256(rutaArchive, rutaSalida, 18,true);
                            break;
                        }
                        case 5:{
                            break;
                        }
                    }
                    break;
                }
                case 3:{
                    int select1 = 0;
                    System.out.println("MENU SELECCIONAR PROTECCION A DESHACER CON CORRECCION\n");
                    System.out.println("1- BLOQUE DE 8 bits\n");
                    System.out.println("2- BLOQUE DE 256 bits\n");
                    System.out.println("3- BLOQUE DE 8192 bits\n");
                    System.out.println("4- BLOQUE DE 262144 bits\n");
                    System.out.println("5- SALIR\n");
                    select1 = scan.nextInt();

                    switch (select1){
                        case 1:{
                            System.out.println("8");
                            String rutaArchive = "src/com/company/hammingError8.txt";
                            String rutaSalida = "src/com/company/decodificacion_con_correccion8.txt";
                            fin8(rutaArchive, rutaSalida,true);
                            break;
                        }
                        case 2:{
                            System.out.println("256");
                            String rutaArchive = "src/com/company/hammingError256.txt";
                            String rutaSalida = "src/com/company/decodificacion_con_correccion256.txt";
                            fin256(rutaArchive, rutaSalida, 8,true);
                            break;
                        }
                        case 3:{
                            System.out.println("8192");
                            String rutaArchive = "src/com/company/hammingError8192.txt";
                            String rutaSalida = "src/com/company/decodificacion_con_correccion8192.txt";
                            fin256(rutaArchive, rutaSalida, 13,true);
                            break;
                        }
                        case 4:{
                            System.out.println("262144");
                            String rutaArchive = "src/com/company/hammingError262144.txt";
                            String rutaSalida = "src/com/company/decodificacion_con_correccion262144.txt";
                            fin256(rutaArchive, rutaSalida, 18,true);
                            break;
                        }
                        case 5:{
                            break;
                        }
                    }
                    break;
                }
                case 4:{
                    int select1 = 0;
                    System.out.println("MENU SELECCIONAR PROTECCION A DESHACER SIN CORRECCION\n");
                    System.out.println("1- BLOQUE DE 8 bits\n");
                    System.out.println("2- BLOQUE DE 256 bits\n");
                    System.out.println("3- BLOQUE DE 8192 bits\n");
                    System.out.println("4- BLOQUE DE 262144 bits\n");
                    System.out.println("5- SALIR\n");
                    select1 = scan.nextInt();

                    switch (select1){
                        case 1:{
                            System.out.println("8");
                            String rutaArchive = "src/com/company/hammingError8.txt";
                            String rutaSalida = "src/com/company/decodificacion_sin_correccion8.txt";
                            fin8(rutaArchive, rutaSalida,false);
                            break;
                        }
                        case 2:{
                            System.out.println("256");
                            String rutaArchive = "src/com/company/hammingError256.txt";
                            String rutaSalida = "src/com/company/decodificacion_sin_correccion256.txt";
                            fin256(rutaArchive, rutaSalida, 8,false);
                            break;
                        }
                        case 3:{
                            System.out.println("8192");
                            String rutaArchive = "src/com/company/hammingError8192.txt";
                            String rutaSalida = "src/com/company/decodificacion_sin_correccion8192.txt";
                            fin256(rutaArchive, rutaSalida, 13,false);
                            break;
                        }
                        case 4:{
                            System.out.println("262144");
                            String rutaArchive = "src/com/company/hammingError262144.txt";
                            String rutaSalida = "src/com/company/decodificacion_sin_correccion262144.txt";
                            fin256(rutaArchive, rutaSalida, 18,false);
                            break;
                        }
                        case 5:{
                            break;
                        }
                    }
                    break;
                }
                case 5:{
                    System.out.println("SALIENDO DEL PROGRAMA");
                    break;
                }
            }

        }


    }
/*
    public void addcontrol3(int[] arr){
        int cont = 4;
        int ctrol = 3;
        int pos; //15 = tope-1
        int aux = 0;
        for(pos = 14;ctrol < pos;pos--) {
            aux =+ arr[pos];
            cont--;
            if (cont == 0){
                pos = pos-4;
                cont=4;
            }
        }
        if (aux % 2 == 0){
            arr[pos] = 0;
        }
        else {
            arr[pos]=1;
        }
    }
    public void addcontrol2(int[] arr){
        int cont = 2;
        int ctrol = 1;
        int pos; //15 = tope-1
        int aux = 0;
        for(pos = 14;ctrol < pos;pos--) {
            aux =+ arr[pos];
            cont--;
            if (cont == 0){
                pos = pos-2;
                cont=2;
            }
        }
        if (aux % 2 == 0){
            arr[pos]=0;
        }
        else {
            arr[pos]=1;
        }
    }
    public void addcontrol1(int[] arr){
        int cont = 1;
        int ctrol = 0;
        int aux = 0;
        int pos; //15 = tope-1
        for(pos = 14;ctrol < pos;pos--) {
            aux =+ arr[pos];
            cont--;
            if (cont == 0){
                pos = pos-1;
                cont=1;
            }
        }
        if (aux % 2 == 0){
            arr[pos] = 0;
        }
        else {
            arr[pos] = 1;
        }
    }*/




    //info: arreglo de 4 248 8179 o 262126 bits de informacion
    //cant: 3 8 13 18

    //int[] ctrls               {1,2,4,8,16,32,64,128,256,512,1024,2048,4096,8192,16384,32768,65536,131072,262144};
//cant  //int[] ctrolInCrols     0 1 2 3 4  5  6   7   8   9   10   11   12   13   14   15     16     17     18
    //int[] ctrolInArreglo       0 1 3 7 15 31 63 127 255 511,1023,2047,4095,8191,16383,32767,65535,131071,262143

    public static int[] contruirHamming(int[] info, int cant){

        int [] ham = new int[ctrls[cant]];
        int j=0;

        //[0,0,0,0]

        //i: recorre el arreglo final
        //j: recorre el arreglo de informacion

        for (int i=0;i<ctrls[cant]-1;i++){
            if(inCtrls(i+1)){
                ham[i] = -1;
            }
            else{
                if(j<info.length){
                    ham[i]=info[j];
                    j++;
                }

            }
        }
        ham[ctrls[cant]-1]=-2;

        ham = addcontrols(ham,cant);

        return ham;

        //salida hamming con controles
    }



    //recibe arreglo de 4 248 8179 o 262126 bits
   //posInCtrol //3//8//13//18

    public static int[] addcontrols(int[] arr, int posInCtrol) {

        int posicionArreglo;
        int contadorDeRecoleccion;
        int contFijo;                                   //valor que reinicia contadorDeRecoleccion

        int posCtrol;   //posicion donde colocar el control
        int tope = ((ctrls[posInCtrol]) - 2);           //ultima posicion de informacion


        //int[] ctrls               {1,2,4,8,16,32,64,128,256,512,1024,2048,4096,8192,16384,32768,65536,131072,262144};
//cant  //int[] ctrolInCrols         0 1 2 3 4  5  6   7   8   9   10   11   12   13   14   15     16     17     18
        //int[] ctrolInArreglo       0 1 3 7 15 31 63 127 255 511,1023,2047,4095,8191,16383,32767,65535,131071,262143


        //[0...255] //8controles

        //   [-1,-1,0,-1,1,1,0,-2]
        //    0  1  2  3 4 5 6  7


        //3 //8  //13  //18
        //3//127//4095//131071

        //7

        int acumulador;
        for (int p = posInCtrol - 1; 0 <= p; p--) {
            posCtrol = ctrolInArreglo[p];
            acumulador = 0;

            contFijo = ctrls[p];
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


          //int[] ctrls               {1,2,4,8,16,32,64,128,256,512,1024,2048,4096,8192,16384,32768,65536,131072,262144};
    //cant//int[] ctrolInCrols         0 1 2 3 4  5  6   7   8   9   10   11   12   13   14   15     16     17     18
          //int[] ctrolInArreglo       0 1 3 7 15 31 63 127 255 511,1023,2047,4095,8191,16383,32767,65535,131071,262143

    //cant: 3 8 13 18
    public static int[] adderror(int[] arr,int cant){
        int tope =(ctrls[cant]-2);
        Random random = new Random();
        int[]local;
        local=arr.clone();
        int posErr = random.nextInt(tope+1);

        if(local[posErr]==0){
            local[posErr]=1;
        }
        else{
            local[posErr]=0;
        }
        return local;
    }

    
    public static int[] corregirHamming(int a[], int parity_count,boolean corregir) {

        //esta funcion recibe un codigo hamming en un array a -> [1,0,1,1,0,0,0,0](por ej, ni idea si esta bien)
        //tambien pasamos por parametro el numero de bits de paridad o de control q se añadio a la informacion original.
        // es decir, si la info era: [1,0,0,0] y los bits de control son: c1 = 1, c2 = 0 y c3 = 1, tenemos 3 bits de control.
		// Ahora tendremos que detectar el error y corregirlo, si es que hay uno.


        int posicionArreglo;
        int contadorDeRecoleccion;
        int contFijo;                                   //valor que reinicia contadorDeRecoleccion

        int posCtrol;   //posicion donde colocar el control
        int tope = ((ctrls[parity_count]) - 2);           //ultima posicion de informacion


        int acumulador;
        int[] syn = new int[parity_count];
        int indiceSyn = parity_count-1;

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

        String syndrome="";

        for (int j=0;j<syn.length;j++){
            syndrome+=syn[j];
        }


        int tamañoLocal;
        if(parity_count == 3){
            tamañoLocal = ctrls[parity_count]-3-1;
        }else{
            if(parity_count == 8){
                //256
                tamañoLocal = ctrls[parity_count]-8-1;
            }else if(parity_count == 13){
                //8k
                tamañoLocal = ctrls[parity_count]-13-1;
            }
            else {// Tipo == 18
                //262k
                tamañoLocal = ctrls[parity_count]-18-1;
            }
        }

        int infoEntero[] = new int[tamañoLocal];
  /*
		int power;

		int parity[] = new int[parity_count];           //almacenara los valores de las comprobaciones de paridad

		String syndrome = new String();                 //almacenara el valor entero de la ubicacion del error



		for(power=0 ; power < parity_count ; power++) { //necesitamos verificar las paridades, la misma cantidad de veces que la cantidad de bits de paridad agregados

            for(int i=0 ; i < a.length ; i++) {         //extrayendo el bit de 2^(power)
				
				int k = i+1;
				String s = Integer.toBinaryString(k);
				int bit = ((Integer.parseInt(s))/((int) Math.pow(10, power)))%10;

                if(bit == 1) {
					if(a[i] == 1) {
						parity[power] = (parity[power]+1)%2;
					}
				}
			}
			syndrome = parity[power] + syndrome;
		}*/


        if(corregir==false){
            syndrome="0";
        }

		// usando estos valores, ahora verificaremos si hay un error de un solo bit y luego lo corregiremos
		
		int error_location = Integer.parseInt(syndrome, 2);
        String codigoCorregido = "";
        String infoFinal = "";

		if(error_location != 0) {
			a[error_location-1] = (a[error_location-1]+1)%2;

            //1  0  0  1  1  1  1
            //error_location = 1, pero como en el arreglo los elementos se guardan desde la posicion 0, le restamos 1.
            //entonces a[error_location -1] -> en este caso a[0] = 1, haremos:
            //a[error_location-1]+1 = 2 % 2 = 0 -> es decir que colocamos un cero.
            //si tuvieramos un 0 en lugar de un 1 en a[0], entonces tendriamos: 1 % 2 = 1 -> es decir que colocamos un uno.

			for(int i=0; i<a.length ; i++) {
				codigoCorregido += (a[a.length-i-1]);
			}
            StringBuilder sb = new StringBuilder(codigoCorregido);
            codigoCorregido = sb.reverse().toString();
		}
		else {
		}
		
		//extraemos los datos originales del código recibido (y corregido)

		int power = parity_count-1;
        int r = 0;
		for(int i=a.length ; i > 0 ; i--) {
			if(Math.pow(2, power) != i) {
                infoFinal += (a[i-1]);
			}
			else {
				power--;
			}
		}
        StringBuilder sbFinal = new StringBuilder(infoFinal);
        infoFinal = sbFinal.reverse().toString();

        for(int i=0; i< infoFinal.length(); i++){
            infoEntero[i] = Character.getNumericValue(infoFinal.charAt(i));
        }

        return infoEntero; 
	}


    //static int[] ctrls = {1,2,4,8,16,32,64,128,256,512,1024,2048,4096,8192,16384,32768,65536,131072,262144};

    public static boolean inCtrls(int numero){
        return Arrays.stream(ctrls).anyMatch(i -> i == numero);
    }
    public static boolean contains(final int[] arr, final int key) {
        return Arrays.stream(arr).anyMatch(i -> i == key);
    }
    public static boolean distinto(final int[] arr, final int key){
            return Arrays.stream(arr).anyMatch(i -> i != key);
    }

}










