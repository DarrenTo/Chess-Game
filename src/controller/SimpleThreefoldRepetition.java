package controller;

public class SimpleThreefoldRepetition implements IThreefoldRepetition{

    // only checks for a consecutive threefold repetition
    public SimpleThreefoldRepetition() {

    }

    @Override
    public boolean isThreefoldRepetition() {
        return false;
    }

    @Override
    public void LogMove(Piece piece, int x, int y) {

    }
}
