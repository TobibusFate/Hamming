package com.company;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;


public class Main{

    static File archive;
    static FileReader r_archive;
    static FileWriter w_auxiliar;
    static BufferedWriter b_w_auxiliar;
    static BufferedReader b_r_archive;
    static ArrayList<String> listaS = new ArrayList<>();
    static ArrayList<Character> listaC = new ArrayList<>();


    static int[] ctrls = {1,2,4,8,16,32,64,128,256,512,1024,2048,4096,8192,16384,32768,65536,131072,262144};

    public static ArrayList leerArchivo(String rutaArchive) throws IOException { //se lee la info que esta en el archivo archive
        archive = new File(rutaArchive);
        //StringBuffer sb_archive = new StringBuffer();
        String texto = "";
        ArrayList<int[]> auxiliar = new ArrayList<int[]>();
        int i = 0;
        try{
            b_r_archive = new BufferedReader(new InputStreamReader(new FileInputStream(rutaArchive), "utf-8"));
            while ((texto = b_r_archive.readLine()) != null){
                //sb_archive.append(texto+"\n");
                listaS.add(texto+"\n");
                listaC = pasarCadaC(listaS); //pasamos lo que esta en listas a una listaC que tiene caracteres, no strings
                auxiliar =aBinario(listaC);

            }//new line
            b_r_archive.close();

        }catch(IOException e){
            System.out.println("El archivo no existe\n");
        }
        return auxiliar;
    }


    public void inicio() throws IOException {

        int[] hamming = new int[8];
        int[] buffer_salida = new int[8];

        //INICIALIZACION
        //TOMAR PRIMER RENGLON
        //VACIAR LOS BUFFER


        //while(mientras queden renglones, o la lista no este vacia)

            if (contains(buffer_salida,-2)){
                //completarla con lo necesesario para exportar
                if(distinto(hamming,-2)){
                    //mientras tenga datos y el buffer no este lleno, pasar a buffer
                }else{
                    //if(tengo datos)
                        //aplicar haaming a datos
                    //else
                        //actualizar DATOS para hamming
                }

            }else {
                aAuxiliar("src/com/company/hamming.txt",buffer_salida);
                Arrays.fill(buffer_salida,-2);
            }
            //lista vacia o lista con elementos
            //buffer listo para salir, o necesita datos
            //no quedan renglones para leer
    }

    public void init(){}

    public void actualizarLista(){}
    public void actualizarDatos(){}



    public static void aAuxiliar(String ruta, int[] contenido) throws IOException{
        File auxiliar = new File(ruta);
        String local="";

        if (!auxiliar.exists()) {
            auxiliar.createNewFile();
        }

        for (int c: contenido){
            local+=(char)(c +'0');
        }
        w_auxiliar = new FileWriter(auxiliar);
        b_w_auxiliar = new BufferedWriter(w_auxiliar);
        b_w_auxiliar.write(aDecimal(local));
        b_w_auxiliar.close();
    }

    public static char aDecimal(String cadena)throws IOException{
        return (char)Integer.parseInt(cadena,2); //paso a nro ascii
    }



    public static ArrayList<Character> pasarCadaC(ArrayList<String> listaS){
        int i = 0;
        ArrayList<Character> lista = new ArrayList<>();
        lista.clear();
        for (String cadena : listaS) {
            for(i = 0; i< cadena.length(); i++){
                lista.add(cadena.charAt(i));
            }
        }
        System.out.println("la lista q devuelve pasacadac es:");
        System.out.println(lista);
        return lista;
    }

    public static ArrayList<int[]> aBinario(ArrayList<Character> lista)throws IOException{
        int[] info = new int[8];
        ArrayList<int[]> aux = new ArrayList<int[]>();
        String textoBinario = "";

        //[a,s,b,a,c, ,a, , ]

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
                //ArrayList<String> listaAux= new ArrayList<>();
                String rutaAuxiliar = "src/com/company/auxiliar.txt";

                ArrayList<int[]> textoABinario;
                textoABinario = leerArchivo(rutaArchive); //se lee el archivo archive

                /*
                StringBuilder sb = new StringBuilder(); //Pasamos la lista a string
                for(String s : listaAux){
                    sb.append(s);
                    sb.append("\t");
                }
                //System.out.println(sb.toString());
                */

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
    //cant: tamaño del bloque 8 256 8192 o 262144

    public static int[] contruirHamming(int[] info, int cant){

        int [] ham = new int[cant];
        int j=0;


        //i: recorre el arreglo final
        //j: recorre el arreglo de informacion

        for (int i=0;i<cant-1;i++){
            if(inCtrls(i+1)){
                ham[i] = -1;
            }
            else{
                ham[i]=info[j];
                j++;
            }
        }
        ham[cant-1]=-2;

        ham = addcontrols(ham,cant-1);

        return ham;

        //salida hamming con controles
    }



    //recibe arreglo de 4 248 8179 o 262126 bits

    public static int[] addcontrols(int[] arr, int posMaxCtrl){ //7
        int pos;
        int cont;
        int permacont;
        int posCtrol;

        //int[] ctrls = {1,2,4,8,16,32,64,128,256,512,1024,2048,4096,8192,16384,32768,65536,131072,262144};
        //               0 1 2 3 4  5  6   7   8   9   10   11   12   13   14   15     16     17     18
        //               0 1 3 7 15 31 63 127 255 511


        for (int k = posMaxCtrl;0<=k;k--){
            int aux = 0;
            cont = ctrls[posMaxCtrl];
            permacont = ctrls[posMaxCtrl];
            posCtrol =  ctrls[posMaxCtrl]-1;

            for (pos = ((arr.length)-1); posCtrol < pos; pos--){
                aux =+ arr[pos];
                cont--;

                if(cont == 0){
                    pos= pos - permacont;
                    cont = permacont;
                }
            }

            if(aux % 2 == 0){
                arr[pos] = 0;
            }
            else{
                arr[pos] = 1;
            }
        }
        return arr;
    }


    //cant: tamaño del bloque 8 256 8192 o 262144
    public int[] adderror(int[] arr, int cant){

        Random random = new Random();
        int posErr = random.nextInt(cant-1+0)+0;

        if(arr[posErr]==0){
            arr[posErr]=1;
        }
        else{
            arr[posErr]=0;
        }
        return arr;
    }


    public static boolean inCtrls(int numero){
        return Arrays.asList(ctrls).contains(numero);
    }
    public static boolean contains(final int[] arr, final int key) {
        return Arrays.stream(arr).anyMatch(i -> i == key);
    }
    public static boolean distinto(final int[] arr, final int key){
            return Arrays.stream(arr).anyMatch(i -> i != key);
    }

}










