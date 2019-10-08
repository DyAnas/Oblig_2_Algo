package no.oslomet.cs.algdat;


////////////////// class DobbeltLenketListe //////////////////////////////


import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;
import java.util.StringJoiner;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Predicate;



public class DobbeltLenketListe<T> implements Liste<T> {

    /**
     * Node class
     * @param <T>
     */
    private static final class Node<T> {
        private T verdi;                   // nodens verdi
        private Node<T> forrige, neste;    // pekere

        private Node(T verdi, Node<T> forrige, Node<T> neste) {
            this.verdi = verdi;
            this.forrige = forrige;
            this.neste = neste;
        }

        private Node(T verdi) {
            this(verdi, null, null);
        }
    }

    // instansvariabler
    private Node<T> hode;          // peker til den første i listen
    private Node<T> hale;          // peker til den siste i listen
    private int antall;            // antall noder i listen
    private int endringer;         // antall endringer i listen

    public DobbeltLenketListe() {
        throw new NotImplementedException();
    }

    public DobbeltLenketListe(T[] a) {
        throw new NotImplementedException();
    }

    public Liste<T> subliste(int fra, int til){
        fratilKontroll (antall, fra, til);
        DobbeltLenketListe<T> liste = new DobbeltLenketListe<T> ();
        Node<T> q = hode;
        int i = fra;


        for (; i < til; i++) {
            q = finnNode (i);
            if (q != null) {
                liste.leggInn (q.verdi);
                q = q.neste;
            }
        }
        return liste;

    }

    @Override
    public int antall() {
        throw new NotImplementedException();
    }

    @Override
    public boolean tom() {
        throw new NotImplementedException();
    }

    @Override
    public boolean leggInn(T verdi) {
        throw new NotImplementedException();
    }

    private Node<T> finnNode(int indeks) {
        Node<T> p;

        if (indeks < antall / 2) {
            p = hode;
            for (int i = 0; i < indeks; i++) p = p.neste;
        } else {
            p = hale;
            for (int i = antall - 1; i > indeks; i--) p = p.forrige;
        }

        return p;
    }

    public static void fratilKontroll(int tablengde, int fra, int til) {
        if (fra < 0)                             // fra er negativ
            throw new IndexOutOfBoundsException
                    ("fra(" + fra + ") er negativ!");

        if (til > tablengde)                     // til er utenfor tabellen
            throw new IndexOutOfBoundsException
                    ("til(" + til + ") > tablengde(" + tablengde + ")");

        if (fra > til)                           // fra er større enn til
            throw new IllegalArgumentException
                    ("fra(" + fra + ") > til(" + til + ") - illegalt intervall!");
    }

    @Override
    public void leggInn(int indeks, T verdi) {
        throw new NotImplementedException();
    }

    @Override
    public boolean inneholder(T verdi) {
        return indeksTil (verdi) != -1;
    }

    @Override
    public T hent(int indeks) {
        indeksKontroll (indeks, false);
        return finnNode (indeks).verdi;
    }

    @Override
    public int indeksTil(T verdi) {
        if (verdi == null) return -1;
        Node<T> p = hode;
        for (int i = 0; i < antall; i++) {
            if (p.verdi.equals (verdi)) return i;
            p = p.neste;
        }
        return -1;
    }

    @Override
    public T oppdater(int indeks, T nyverdi) {

        Objects.requireNonNull (nyverdi, "Ikke tillat med null-verdier");
        indeksKontroll (indeks, false);
        Node<T> p = finnNode (indeks);
        T gamleverdi = p.verdi;
        p.verdi = nyverdi;
        endringer++; // oppdatere en endring
        return gamleverdi;
    }

    @Override
    public boolean fjern(T verdi) {
        if (verdi==null){
            return false;
        }
        Node<T> p= hode;
        if (p==null) return false; //  return false hvis node er lik null
        while (p !=null){
            if (p.verdi.equals(verdi)){
                break;
            }
            p=p.neste;
        }
        // fjerne node hvis den ligger i første
        if (p==hode){
            hode = hode.neste;
            if (hode != null){
                hode.forrige=null;
            }else{
                hale=null;
            }
        }
        // hvis node ligger bakerst
        else if (hode ==null){
            hale = hale.forrige;
            hale.neste=null;
        } else {
            p.forrige.neste=p.neste;
            p.neste.forrige=p.forrige;
        }
        p.verdi=null;
        p.forrige=p.neste=null;
        antall--;
        endringer++;
        return true;
    }

    @Override
    public T fjern(int indeks) {
        indeksKontroll (indeks, false);
        Node<T> p ;
        if (indeks==0){
            p = hode;
            hode= hode.neste;
            hode.forrige= null;
        }
        else if (indeks == antall-1){
            p=hale;
            hale=hale.forrige;
            hale.neste=null;
        }
        else{
            Node <T>q=finnNode (indeks-1);
            p= q.neste;
            p.neste= p.neste.neste;
            p.neste.forrige=p;
        }
        antall--;
        endringer++;
        return p.verdi;
    }

    @Override
    public void nullstill() {
        Node<T> p = hode, q;

        while (p != null)
        {
            q = p.neste;
            p.neste = null;
            p.verdi = null;
            p = q;
        }

        hode = hale = null;

        endringer++;
        antall = 0;
    }

    @Override
    public String toString() {
        throw new NotImplementedException();
    }

    public String omvendtString() {
        throw new NotImplementedException();
    }

    @Override
    public Iterator<T> iterator() {
        return  new DobbeltLenketListeIterator();
    }

    public Iterator<T> iterator(int indeks) {
        indeksKontroll(indeks, false);
        return new DobbeltLenketListeIterator(indeks);
    }

    private class DobbeltLenketListeIterator implements Iterator<T>
    {
        private Node<T> denne;
        private boolean fjernOK;
        private int iteratorendringer;

        private DobbeltLenketListeIterator(){
            denne = hode;
            fjernOK = false;                    // blir sann når next() kalles
            iteratorendringer = endringer;
        }

        private DobbeltLenketListeIterator(int indeks){
            denne = finnNode(indeks);
            fjernOK = false;
            iteratorendringer = endringer;
        }

        @Override
        public boolean hasNext(){
            return denne != null;
        }

        @Override
        public T next(){
            if(endringer != iteratorendringer) { throw new
                    ConcurrentModificationException("Listen er endret!");
            }

            if (!hasNext()) {
                throw new NoSuchElementException("Tom eller Ingen verdier!");
            }

            fjernOK = true;
            T denneVerdi = denne.verdi;
            denne = denne.neste;

            return denneVerdi;
        }

        @Override
        public void remove(){
            throw new NotImplementedException();
        }

    } // class DobbeltLenketListeIterator

    public static <T> void sorter(Liste<T> liste, Comparator<? super T> c) {
        throw new NotImplementedException();
    }

} // class DobbeltLenketListe


