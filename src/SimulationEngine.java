import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Iterator;
import java.util.Random;
import java.util.TreeSet;

public class SimulationEngine {

    SimulationBoard currentBoard;
    SimulationBoard shadowBoard;
    int height;
    int length;
    int infectiousnessPercent;
    int infectedPercent;
    int mortalityPercent;
    int lastInfectedCount;
    int currentInfectedCount;
    int complyingPercent;
    boolean boardActive;

    ActionListener updateTask;
    GUI boardGUI;
    Timer t;

    public SimulationEngine(){
        boardGUI = new GUI(this);
    }

    public void UpdateBoard() throws IOException {
        Iterator<SimulationBoard.Human> iteratorFrom;
        Iterator<SimulationBoard.Human> iteratorTo;

        iteratorFrom = currentBoard.iterator();
        iteratorTo = shadowBoard.iterator();

        SimulationBoard temp = currentBoard;
        currentBoard = shadowBoard;
        shadowBoard = temp;

        lastInfectedCount = currentInfectedCount;
        int infectedTemp = 0;
        int recoveredTemp = 0;
        int deceasedTemp = 0;

        while (iteratorFrom.hasNext() && iteratorTo.hasNext()){
            SimulationBoard.Human humanFrom = iteratorFrom.next();
            SimulationBoard.Human humanTo = iteratorTo.next();
            HumanState stateFrom = humanFrom.update();
            humanTo.changeState(stateFrom, humanFrom.getCounter());
            humanTo.setIsComplying(humanFrom.getIsComplying());
            switch (stateFrom){
                case Infected -> infectedTemp++;
                case Recovered -> recoveredTemp++;
                case Deceased -> deceasedTemp++;
            }
        }

        currentInfectedCount = infectedTemp;
        boardGUI.updateStatsLabels(currentInfectedCount, recoveredTemp, deceasedTemp, currentInfectedCount - lastInfectedCount);
        paintBoard();
    }

    public void runButtonPressed(){
        if(t == null){
            t = new Timer(300, updateTask);
        }
        if(!t.isRunning()) {
            t.start();
        }
        else{
            t.stop();
        }
    }

    public void StartSimulation(
            int height, int length, int startingInfected, int startingComplying, int infectiousnessPercent, int mortalityPercent, Color colours[]) throws IOException {
        if(boardActive){
            return;
        }
        this.height = height;
        this.length = length;

        this.infectiousnessPercent = infectiousnessPercent;
        this.mortalityPercent = mortalityPercent;
        this.complyingPercent = startingComplying;

        currentBoard = new SimulationBoard(infectiousnessPercent, mortalityPercent);
        shadowBoard = new SimulationBoard(infectiousnessPercent, mortalityPercent);

        currentBoard.GenerateBoard(height, length, colours);
        shadowBoard.GenerateBoard(height, length, colours);

        infectedPercent = startingInfected;
        currentBoard.infectStarters(generateCoords(infectedPercent));
        currentBoard.setNotComplying(generateCoords(100 - complyingPercent));
        updateTask = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    UpdateBoard();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        };

        Dimension scaleDimensions = new Dimension();
        scaleDimensions.width = Math.round(1200 / length);
        scaleDimensions.height = Math.round(800 / height);

        boardGUI.createBoardGUI(new Dimension(length, height), scaleDimensions);
        currentInfectedCount = (int)Math.round((double)(height * length) *  ((double)infectedPercent / 100));
        boardGUI.updateStatsLabels(currentInfectedCount, 0, 0, 0);
        paintBoard();

        boardActive = true;
    }

    public void changedComplyingPercent(int complyingPercent) throws IOException {
        this.complyingPercent = complyingPercent;
        currentBoard.setNotComplying(generateCoords(100 - complyingPercent));
        paintBoard();
    }

    public void stopSimulation(){
        if(t != null && t.isRunning()){
            t.stop();
        }
        boardActive = false;
    }

    private void paintBoard() throws IOException {
        Iterator<SimulationBoard.Human> iterator = currentBoard.iterator();
        for(int x = 0; x < length; x++){
            for(int y = 0; y < height; y++){
                if(iterator.hasNext()){
                    SimulationBoard.Human human = iterator.next();
                    boardGUI.drawPoint(x,y,human.getColour());
                }
            }
        }
        boardGUI.drawImg();
    }

    public TreeSet<Integer> generateCoords(int percentOfPopulation){
        int amountToGenerate = (int)Math.round((double)(height * length) *  ((double)percentOfPopulation / 100));
        TreeSet<Integer> generatedCoords = new TreeSet<Integer>();
        Random rnd = new Random();

        int i = 0;
        while (i < amountToGenerate) {
            int x = rnd.nextInt(0, length);
            int y = rnd.nextInt(0, height);

            if(generatedCoords.add(Utils.convertXYCoords(x, y))){
                i++;
            }
        }

        return generatedCoords;
    }
}
