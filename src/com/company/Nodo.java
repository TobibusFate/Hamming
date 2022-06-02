package com.company;

import java.util.ArrayList;
import java.util.HashMap;

public class Nodo {
    private float probabilidad;
    private HashMap<Character, ArrayList<Integer>> estructura = new HashMap<>();


    public Nodo (float probabilidad,Character caracter,ArrayList<Integer> list){
        this.probabilidad = probabilidad;
        this.estructura.put(caracter,list);
    }

    public Nodo (float probabilidad,HashMap<Character, ArrayList<Integer>> diccionario){
        this.probabilidad = probabilidad;
        this.estructura = diccionario;
    }

    public Nodo (float probabilidad, HashMap<Character, ArrayList<Integer>> diccionario,HashMap<Character, ArrayList<Integer>> diccionario2){
        this.probabilidad = probabilidad;
        this.estructura.putAll(diccionario);
        this.estructura.putAll(diccionario2);

    }

    public Nodo addLastValue (Nodo nodo, int valor){
        Nodo local = new Nodo(nodo.getProbabilidad(),nodo.getEstructura());

        local.getEstructura().forEach((k,v)->
                v.add(valor)
        );
        return local;
    }

    public float getProbabilidad() {
        return probabilidad;
    }

    public void setProbabilidad(float probabilidad) {
        this.probabilidad = probabilidad;
    }

    public HashMap<Character, ArrayList<Integer>> getEstructura() {
        return estructura;
    }

    public void setEstructura(HashMap<Character, ArrayList<Integer>> estructura) {
        this.estructura = estructura;
    }

}