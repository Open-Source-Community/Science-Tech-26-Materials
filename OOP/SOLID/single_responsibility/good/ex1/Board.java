import java.util.ArrayList;

/**
 * Created by mrk on 4/7/14.
 * Consider the Board class in the "good" example. The only thing it is responsible for
 *  is knowing the values of its spots. It is entirely unconcerned with how those spots are
 *  being manipulated per the rules of Tic Tac Toe (rows, columns, diagonals) or displayed to the user
 *  (in a console, on the web, etc.). The BoardShaper and BoardPresenter classes are similarly focused
 *  on specific tasks. They are also only passed attributes they need; for example, BoardShaper 
 * objects are initialized with only a size (they don't need the whole board).
 */
public class Board {
    int size;
    ArrayList<String> spots;

    public Board(int size) {
        this.size = size;
        this.spots = new ArrayList<String>();
        for (int i = 0; i < size; i++) {
            this.spots.add(String.valueOf(3*i));
            this.spots.add(String.valueOf(3*i + 1));
            this.spots.add(String.valueOf(3*i + 2));
        }
    }

    public ArrayList<String> valuesAt(ArrayList<Integer> indexes) {
        ArrayList<String> values = new ArrayList<String>();

        for (int index : indexes) {
            values.add(this.spots.get(index));
        }

        return values;
    }
}
