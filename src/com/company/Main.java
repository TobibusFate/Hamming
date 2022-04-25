package com.company;

import java.io.*;
import java.lang.reflect.Array;
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

    public static void inicio(String rutaDeArchivo,String rutaSalida) throws IOException {

        int[] hamming = new int[8];             //ultimo hamming realizado
        int[] buffer_salida = new int[8];       //informacion pendiente para salir

        //int[] buffer_salida_error = new int[8];
        //int[] hamming_error = new int[8];

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
            while (contains(buffer_salida,-2)){ //mientras el buffer no este listo para salir
                if(distinto(hamming,-2)) {      //si tengo datos para agregar


                    /*if(indexHamming==8||indexBuffer==8){
                        System.out.println("hamming:"+indexHamming+"\nbuffer:"+indexBuffer );
                    }*/

                    buffer_salida[indexBuffer] = hamming[indexHamming];
                    //buffer_salida_error[indexBuffer]=hamming_error[indexHamming];
                    hamming[indexHamming]=-2;
                    //hamming_error[indexHamming]=-2;

                    indexHamming++;
                    indexBuffer++;

                }
                else{   //si necesito hamminizar algo
                    if(distinto(info,-2)){          //si tengo Info pendiente para hacer hamming
                        hamming = contruirHamming(recorreArreglo(info,4),3);
                        //hamming_error = adderror(hamming,3);

                        indexHamming=0;
                        //haming_error
                    }
                    else{                               //si necesito info para hacer hamming
                        if(lista_Renglon.isEmpty()){
                            if(!listaStrings.isEmpty()){
                                lista_Renglon =actualizarLista(listaStrings);
                            }else {
                                System.out.println("SE TE VACIO LA LISTA PAPA");
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
                }
            }

            aAuxiliar("src/com/company/hamming8.txt",buffer_salida);
            //aAuxiliar("src/com/company/hamming8_error.txt",buffer_salida_error);
            Arrays.fill(buffer_salida,-2);
            //Arrays.fill(buffer_salida_error,-2);
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

    public static char aDecimal(String cadena)throws IOException{
        return (char)Integer.parseInt(cadena,2); //paso a nro ascii
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

    public static File crearArchivos(File entrada, int tipo, int bloque){
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
    }

    public static void main(String[] args) throws IOException {
        int select = 0;
        Scanner scan = new Scanner(System.in);

        System.out.println("MENU\n\n");
        System.out.println("1- CARGAR UN ARCHIVO\n");
        System.out.println("2- PROTEGER ARCHIVO\n");
        System.out.println("3- INTRODUCIR ERRORES\n");
        System.out.println("4- DESPROTEGER ARCHIVO SIN CORREGIR\n");
        System.out.println("5- DESPROTEGER ARCHIVO CORRIGIENDO\n");
        System.out.println("6- SALIR\n");

        select = scan.nextInt();

        System.out.println("\n -----------------");

        switch (select){

            case 1:{
                select = 0;
                System.out.println("CARGADO");
                String rutaArchive = "src/com/company/archivo.txt";
                String rutaSalida = "src/com/company/hamming.txt";

                inicio(rutaArchive,rutaSalida);



                //aAuxiliar(rutaAuxiliar, textoABinario); //escribimos en el archivo auxiliar el string sb(que es el arraylist lista)

                break;
            }
            case 2:{
                int select1 = 0;
                System.out.println("MENU SELECCIONAR PROTECCION\n");
                System.out.println("1- BLOQUE DE 8 bits\n");
                System.out.println("2- BLOQUE DE 256 bits\n");
                System.out.println("3- BLOQUE DE 8192 bits\n");
                System.out.println("4- BLOQUE DE 262144 bits\n");
                System.out.println("5- SALIR\n");
                select = scan.nextInt();

                switch (select1){
                    case 1:{
                        System.out.println("8");
                        break;
                    }
                    case 2:{
                        System.out.println("256");
                        break;
                    }
                    case 3:{
                        System.out.println("8192");
                        break;
                    }
                    case 4:{
                        System.out.println("262144");
                        break;
                    }
                    case 5:{
                        break;
                    }
                }
                break;
            }
            case 3:{
                select = 0;
                System.out.println("COLOCAR ERROR");
                break;
            }
            case 4:{
                select = 0;
                System.out.println("DESPROTEGIDO SIN CORREGIR");
                break;
            }
            case 5:{
                select = 0;
                System.out.println("DESPROTEGIDO CORREGIDO");
                break;
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
                acumulador = +arr[posicionArreglo];
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

        int posErr = random.nextInt(tope+1);

        if(arr[posErr]==0){
            arr[posErr]=1;
        }
        else{
            arr[posErr]=0;
        }
        return arr;
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










