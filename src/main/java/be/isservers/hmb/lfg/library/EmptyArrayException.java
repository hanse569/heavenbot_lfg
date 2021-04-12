package be.isservers.hmb.lfg.library;

public class EmptyArrayException extends Exception{
    public EmptyArrayException(){}

    @Override
    public String toString() {
        return "EmptyArrayException: aucune instance ne correspond à la recherche !";
    }
}
