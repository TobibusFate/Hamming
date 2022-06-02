package com.company;

import java.util.Comparator;

public class ComparatorNodos implements Comparator<Nodo> {

    @Override
    public int compare(Nodo nodo1, Nodo nodo2) {
        if (nodo1.getProbabilidad()<nodo2.getProbabilidad()){
            return 1;
        }
        else {
            return -1;
        }
    }
}
