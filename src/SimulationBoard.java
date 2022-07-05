import java.awt.*;
import java.util.*;
import java.util.List;

public class SimulationBoard implements Iterable<SimulationBoard.Human>{

    private int columnHeight;
    private int rowLength;
    private Human topLeft;

    private final int infectiousness;
    private final int mortality;

    public SimulationBoard(int infectiousness, int mortality) {
        this.infectiousness = infectiousness;
        this.mortality = mortality;
    }

    public Boolean GenerateBoard(int columnHeight, int rowLength, Color colours[]){
        this.columnHeight = columnHeight;
        this.rowLength = rowLength;

        topLeft = new Human(null, null, null, null, HumanState.Healthy, colours);
        Human temp = topLeft;
        for(int x = 0; x < rowLength - 1; x++, temp = temp.right){
            temp.right = new Human(temp, null, null, null, HumanState.Healthy, colours);
        }
        Human upper = topLeft;
        Human inRow = topLeft;

        for(int y = 0; y < columnHeight - 1; y++, upper = upper.down){
            upper.down = new Human(null, null, upper, null, HumanState.Healthy, colours);
            Human tempUpper = upper;
            inRow = upper.down;
            for(int x = 0; x < rowLength - 1; x++, inRow = inRow.right, tempUpper = tempUpper.right){
                inRow.right = new Human(inRow, null, tempUpper.right, null, HumanState.Healthy, colours);
                tempUpper.right.down = inRow.right;
            }
        }
        return true;
    }

    public void infectStarters(TreeSet<Integer> coordsToInfect){
        Human columnHuman = topLeft;
        Human rowHuman = topLeft;
        for(int y = 0; y < columnHeight; y++, columnHuman = columnHuman.down){
            rowHuman = columnHuman;
            for(int x = 0; x < rowLength; x++, rowHuman = rowHuman.right){
                if(coordsToInfect.contains(Utils.convertXYCoords(x, y))){
                    rowHuman.changeState(HumanState.Infected, 4);
                }
            }
        }
    }

    public void setNotComplying(TreeSet<Integer> coordsForNotComplying){
        Human columnHuman = topLeft;
        Human rowHuman = topLeft;
        for(int y = 0; y < columnHeight; y++, columnHuman = columnHuman.down){
            rowHuman = columnHuman;
            for(int x = 0; x < rowLength; x++, rowHuman = rowHuman.right){
                if(coordsForNotComplying.contains(Utils.convertXYCoords(x, y))){
                    rowHuman.setIsComplying(false);
                }
                else{
                    rowHuman.setIsComplying(true);
                }
            }
        }
    }

    public void toStr(){

        StringBuilder str = new StringBuilder();

        for (Human collumn = topLeft; collumn != null; collumn = collumn.down){
            for (Human row = collumn; row != null; row = row.right){
                str.append(" " + row.toString() + " ");
            }
            str.append("\n");
        }

        System.out.println(str.toString());
    }

    @Override
    public Iterator<Human> iterator() {
        return new Iterator<>() {

            Human current = topLeft;
            Human firstInRow = topLeft;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public Human next() {
                Human tmp = current;
                if (current.right != null) {
                    current = current.right;
                    return tmp;
                }

                if(current.down != null){
                    firstInRow = firstInRow.down;
                    current = firstInRow;
                    return tmp;
                }
                current = null;
                return tmp;
            }
        };
    }

    protected class Human {

        private Human left;
        private Human right;
        private Human up;
        private Human down;

        private HumanState state;
        private boolean isComplying;
        private Color colour;
        private Color availableColours[];
        private int counter;
        Color c;

        private Random rng;

        public Human(Human left,Human right, Human up, Human down, HumanState state, Color colours[]) {
            this.left = left;
            this.right = right;
            this.up = up;
            this.down = down;
            this.state = state;
            colour = Color.green;
            counter = 0;
            rng = new Random();
            isComplying = true;
            availableColours = colours;
        }

        @Override
        public String toString(){
            List<HumanState> allStates = Arrays.stream(HumanState.values().clone()).toList();
            ArrayList<String> colors = new ArrayList<String>(
                    Arrays.asList(
                            "\u001B[32m",
                            "\u001B[31m",
                            "\u001B[34m",
                            "\u001B[30m"));
            return colors.get(allStates.indexOf(state)) + allStates.indexOf(state) + "\u001B[0m";
        }

        public void changeState(HumanState state, int counter){
            this.state = state;
            this.counter = counter;
            switch (state){
                case Healthy -> colour = availableColours[0];
                case Infected -> colour = availableColours[1];
                case Recovered -> colour = availableColours[2];
                case Deceased -> colour = availableColours[3];
            }

            if(!isComplying){
                colour = colour.darker();
            }
        }

        private ArrayList<Human> getneighbours(){
            ArrayList<Human> humans = new ArrayList<>();
            humans.add(up);
            humans.add(right);
            humans.add(left);
            humans.add(down);

            return humans;
        }

        public HumanState update(){
            if(state != HumanState.Recovered && state != HumanState.Deceased){
                switch (state) {
                    case Healthy:
                        ArrayList<Human> states = getneighbours();
                        for (Human st : states) {
                            if (st != null && st.getState() == HumanState.Infected && (!st.getIsComplying() || !isComplying)) {
                                 if(infectionRoll() == HumanState.Infected){
                                     counter = 4;
                                     return HumanState.Infected;
                                 }
                            }
                        }
                        return HumanState.Healthy;

                    case Infected:
                        if(counter > 0){
                            counter--;
                        }
                        else{
                            return recoveryRoll();
                        }
                    break;
                }
            }
            return state;
        }

        private HumanState infectionRoll(){
            int chance = rng.nextInt(0, 100);

            if(chance < infectiousness){
                return HumanState.Infected;
            }
            return HumanState.Healthy;
        }

        private HumanState recoveryRoll(){
            int chance = rng.nextInt(0, 100);

            if(chance < mortality){
                return HumanState.Deceased;
            }
            return HumanState.Recovered;
        }

        public Color getColour(){
            return colour;
        }

        public int getCounter() {
            return counter;
        }

        public HumanState getState(){
            return this.state;
        }

        public boolean getIsComplying(){
            return this.isComplying;
        }

        public void setIsComplying(boolean isComplying){
            this.isComplying = isComplying;
            changeState(state, counter);
        }
    }
}