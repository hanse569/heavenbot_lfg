package be.isservers.hmb.lfg.library;

public class NotFoundException extends Exception{
    public NotFoundException(){}

    @Override
    public String toString() {
        return "NotFoundException: l instance n a pas ete trouve !";
    }
}
