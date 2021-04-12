package be.isservers.hmb.lfg.library;

public class PreviousDateException extends Exception {
    public PreviousDateException(){}

    @Override
    public String toString() {
        return "NotFoundException: l instance n a pas ete trouve !";
    }
}
