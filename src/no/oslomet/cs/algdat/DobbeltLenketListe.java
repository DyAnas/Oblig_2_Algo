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
     *
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

    }

    public DobbeltLenketListe(T[] a) {


        if (a == null) {
            throw new NullPointerException("Tablellen er er null");
        }

        int i = 0;

        for (; i < a.length; i++) {
            if (a[i] != null) {

                break;

            }
        }
        if (i < a.length) {
            Node<T> p = new Node<>(a[i]);
            hode = hale = p;

            antall++;
            endringer++;

            for (i++; i < a.length; i++) {
                if (a.length == 1) {

                    break;
                }
                if (a[i] != null) {

                    Node<T> q = new Node<>(a[i]);
                    q.forrige = p;
                    p.neste = q;
                    hale = q;
                    antall++;
                    endringer++;
                    p = q;

                }
            }
        }
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
        return antall;
    }

    @Override
    public boolean tom() {
        if (this.antall==0){
            return true;
        }
        return false;
    }

    @Override
    public boolean leggInn(T verdi) {
        Objects.requireNonNull(verdi, "Ikke tillatt med null-verdier!");

        if (antall == 0)  hode = hale = new Node<>(verdi);

        else hale = hale.neste = new Node<>(verdi,hale,null);

        antall++;
        return true;
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
        if (indeks < 0 || indeks > antall)
            throw new IndexOutOfBoundsException ();
        if (verdi == null) throw new NullPointerException ();
        if (indeks == 0) {
            Node<T> p = new Node<> (verdi);
            p.neste = hode;
            hode.forrige = p;
            hode = p;
        } else if (indeks == antall ()) {
            Node<T> q = new Node<T> (verdi);
            hale.neste = q;
            q.forrige = hale;
            hale = q;
        } else {
            Node<T> p = hode;
            for (int i = 0; i < indeks; i++) p = p.neste;
            Node<T> q = new Node<T> (verdi, p.forrige, p);
            p = q;
            q.forrige.neste = p.neste.forrige = p;
        }
        antall++;
        endringer++;
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
        (verdi == null) return false;
        Node<T> p = hode;
        while (p != null)
        {
            if (p.verdi.equals(verdi)) break;
            p = p.neste;
        }
        if (p == null)
        {
            return false;
        }
        else if (antall == 1)
        {
            hode = hale = null;
        }
        else if (p == hode)
        {
            hode = hode.neste;
            hode.forrige = null;
        }
        else if (p == hale)
        {
            hale = hale.forrige;
            hale.neste = null;
        }
        else
        {
            p.forrige.neste = p.neste;
            p.neste.forrige = p.forrige;
        }
        p.verdi = null;
        p.forrige = p.neste = null;
        antall--;
        endringer++;
        return true;
    }

    @Override
    public T fjern(int indeks) {
        indeksKontroll(indeks,false);
        if(tom()) return null;
        Node<T> node;
        T verdi;
        if (antall == 1){
            verdi = hode.verdi;
            hode = hale = null;
        }else if(indeks == 0){
            if(antall == 2){
                hode = hale;
                hale.neste = null;
                hode.forrige = null;
            }else{
                node = hode.neste;
                node.forrige = null;
                hode = node;
            }
            verdi = hode.verdi;
        } else if (indeks == antall-1){
            if (antall == 2){
                hale = hode;
                hale.neste = null;
                hode.forrige = null;
            }else{
                node = hale.forrige;
                node.neste = null;
                hale = node;
            }
            verdi = hale.verdi;
        } else{
            node = finnNode(indeks);
            verdi = node.verdi;
            node.neste.forrige = node.forrige;
            node.forrige.neste = node.neste;
        }
        antall--;
        endringer++;
        return verdi;
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
        StringBuilder s = new StringBuilder();
        s.append('[');
        if (antall!=0) {
            Node<T> noden = hode;
            if (noden.verdi != null)
                s.append(noden.verdi);
            noden = noden.neste;
            while (noden != null) {
                if (noden.verdi != null){
                    s.append(',');
                    s.append(noden.verdi);
                }
                noden = noden.neste;
            }

        }
        s.append(']');
        return s.toString();
    }

    public String omvendtString() {
        StringBuilder s = new StringBuilder();
        s.append('[');
        if (antall!=0) {
            Node<T> noden = hale;
            if (noden.verdi != null)
                s.append(noden.verdi);
            noden = noden.forrige;
            while (noden != null) {
                if (noden.verdi != null){
                    s.append(',');
                    s.append(noden.verdi);
                }
                noden = noden.forrige;
            }

        }
        s.append(']');
        return s.toString();
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

            if(iteratorendringer != endringer){
                throw new ConcurrentModificationException();}
            if (fjernOK == false){
                throw new IllegalStateException();}

            if (antall == 1){
                //hvis lengden bare er en node
                hode = hale = null;
            } else if (denne == null){
                //nullstiller fra høyre
                hale = hale.forrige;
                hale.neste = null;
            } else if (denne.forrige == hode){
                //nullstiller fra venstre
                hode = hode.neste;
                hode.forrige = null;
            } else{
                //flytter alt mot venstre
                denne.forrige.forrige.neste = denne;
                denne.forrige = denne.forrige.forrige;
            }
            fjernOK = false;
            antall--;
            endringer++;
            iteratorendringer++;
        }

    } // class DobbeltLenketListeIterator

    public static <T> void sorter(Liste<T> liste, Comparator<? super T> c) {

        T temp;
        boolean sluttPaaListe = false;
        for (int i = 0; i < liste.antall() -1; i++){
            if (c.compare(liste.hent(i), liste.hent(i+1)) > 0){
                sluttPaaListe = true;
                temp = liste.hent(i);
                liste.oppdater(i, liste.hent(i+1));
                liste.oppdater(i+1, temp);
            }
        }
        if (sluttPaaListe){sorter(liste, c);}

    }

} // class DobbeltLenketListe


