import java.io.*;
import java.util.*;

/*
 * This class, Main, is the main implementation of the TextExcel program. It
 * implements the requirements established by ExcelBase. Most methods will be
 * private as the only public methods necessary are to processCommand(). This
 * class will handle certain commands that the Grid does not: help and load
 * file.
 */
public class Main extends ExcelBase {

  // main method that creates our Grid object and assigns it to the GridBase
  public static void main(String args[]) {
        GridBase.grid = new Grid();
        Main engine = new Main();
        engine.runInputLoop();
    }

    /*
     * This method will parse a line that contains a command. It will delegate the
     * command to the Grid if the Grid should handle it. It will call the
     * appropriate method to handle the command. 
     *
     *   help  : provides help to the user on how to use Text Excel
     *   print : returns a string of the printed grid. The grid does this for itself!
     *   rows  : returns the number of rows currently in the grid. The grid knows this info.
     *   cols  : returns the number of columns currently in the grid 
     *   width : returns the width of an individual cell that is used when
     *           displaying the grid contents.
     *   rows = 5  : resizes the grid to have 5 rows. The grid contents will be cleared.
     *   cols = 3  : resizes the grid to have 3 columns. The grid contents will be cleared.
     *   width = 6 : resizes the width of a cell to be 6 characters wide
     *               when printing the grid. 
     *   load file1.txt  : opens the file specified and processes all commands in it.
     * 
     * Parameters:
     *    command : The command to be processed (described above)
     * Returns:
     *    The result of the command which will be printed by the infrastructure.
     */
    public String processCommand(String command) {
        String result = null;
      
        // if the user needs help, the help command is handled here
        if (command.equals("help")) {
          return "You have many different command options, some are as follows: rows, rows = #, cols, cols = #, width, width = #, and print. If you type in rows, it will return how many rows the Grid will manage. The rows = # sets the number of rows the Grid will manage, the count could be validated to be between 1 – 49, and it returns how many new rows are in the Grid.\nIf you type in cols, it will return how many columns the Grid will manage. If cols = # is typed, it sets the number of columns the Grid will manage, the count could be validated to be between 1 – 26 and  it returns how many new columns are in the Grid.\nIf width is typed in, it returns how wide each cell in the grid/matrix is when it is printed. If width = # is typed, it sets how wide each cell in the grid/matrix is when it is printed, the size should be validated to be between 3 – 29, and it returns the new width.\nLastly, if print is typed in, it returns a string representation of a printed grid/matrix.\nIf you would like to enter an expression, that is also possible, just assing it to a cell with parentheses before and after the expression, in the following format a1 = ( 3 * 4 ).\nYou can also input/assign a date in MM/DD/YY or MM/DD/YYYY to a cell and it will store that into whatever cell you have assigned it to.\nIn addition, you can sort in ascending order and descending order. sorta [range] will sort from smallest to alrgest and sortd [range] will sort from largest to smallest.\nIf you want, you also have the option to load and save files, just enter load \"filename\" and save \"filename\".\nFinally, you are aslo able to find the sum and average of a range of cells if you input sum [range] or avg [range]. Have fun exploring this Text Excel!";
        }
      
        // loads all file related commands here
        if (command.startsWith("load")) {
          int index = command.indexOf(" ");
          String filename = command.substring(index + 1);
          result = "File loaded successfully";
          try {
            File file = new File(filename);
            Scanner lineReader = new Scanner(file);
            while (lineReader.hasNextLine()) {
            String line = lineReader.nextLine();
            GridBase.grid.processCommand(line);
            }
          }
          catch (Exception ex) {
            result = "Could not find file";
          }
        }
                
        // Dispatches the command to the Grid object to see if it can handle it.
        if (result == null && GridBase.grid != null) {
            // Asks the grid object to process the command.
            result = GridBase.grid.processCommand(command);
        }

        // if the command is still not handled
        if (result == null)
            result = "Unhandled";

        return result;
    }

    /*
     * Method loadFromFile.
     *
     * This will process the command: load {filename}
     *
     * Call processCommand() for every line in the file. 
     * 
     * Parameter: 
     *    filename : The name/path to the file
     * Returns: 
     *    true/false: True if the file was found and the commands in the file
     *                were processed by processCommand.
     */
  
    private String loadFromFile(String filename) {
        System.out.println(filename);
        String result = "Load command not yet implemented";
        try {
            Scanner parser = new Scanner(new File("file_not_found.txt"));

            // Reads a file and processes all the commands in the file.
            File file = new File(filename);
            Scanner lineReader = new Scanner(file);
            boolean success = true;
            while (lineReader.hasNextLine()) {
              String line = processCommand(lineReader.nextLine());
              if (line.equals("Unhandled")) {
                success = false;
              }
            }
            // If successful, the result is to say that
            if (success) {
              result = "File loaded successfully";
            } else {
              result = "Error";
            } 
        } catch (FileNotFoundException e) {
            result = "Could not find file: "; 
        }

        return result;
    }

}
