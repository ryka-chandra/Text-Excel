import java.io.*;
import java.util.*;

/*
 * The Grid class will hold all the cells. It allows access to the cells via the
 * public methods. It will create a display String for the whole grid and process
 * many commands that update the cells. These command will include
 * sorting a range of cells and saving the grid contents to a file.
 *
 */
public class Grid extends GridBase {

  // I use these instance fields to keep track of the count of columns, rows and cell width.
  // They are scoped to the instance of this Grid object.
  // They are initialized to the prescribed default values.
  private int colCount = 7;
  private int rowCount = 10;
  private int cellWidth = 9;

  // this is an instance field that is used when
  // analyzing inputted cell locations
  private String alphabet = "ABCDEFGHIJKLMNOQRSTUVWXYZ";  

  // this instance field creates a new matrix (or grid)
  private Cell[][] matrix;

  // this is the Grid constructor that creates a new matrix
  public Grid() {
    matrix = new Cell[rowCount][colCount];
  }

  // this is a mutator that sets the cell
  public void setCell(int row, int col, Cell cell) {
    matrix[row][col] = cell;
  }

  // this is an accessor that returns a single cell
  public Cell getCell(int row, int col) {
    return matrix[row][col];
  }

   
  // this method gets the column variable from an inputted cell location
  private int getCol(String command) {
    command = command.trim();
    String colstr = command.toUpperCase().charAt(0) + "";
    int col = alphabet.indexOf(colstr);
    if (col < colCount) {
      return col;
    }
    return -1;
  }

  // this method gets the row number from an inputted cell location 
  private int getRow(String command) {
    command = command.trim();
    String rowstr = command.charAt(1) + "";
    int row = Integer.parseInt(rowstr) - 1;
    if (row < rowCount) {
      return row;
    }      
    return -1;
  }

  
  /*
    * This method processes a user command.
    * 
    * example of commands are as follows:
    *   print           : render a text based version of the matrix
    *   width = [value] : set the cell width
    *   width           : get the cell width
    *   rows = [value]  : set the row count
    *   cols = [value]  : set the column count
    *   rows            : get the row count
    *   cols            : get the column count
    *   
    *   [cell] = [expression] : set the cell's expression, for checkpoint # expressions may be...
    *          -  a value such as 5. Example:  a2 = 5
    *          -  a string such as "hello". Example: a3 = "hello"
    *                          
    *                          of the expression is a complicated formula. 
    *                          Example: a1 = ( 3.141 * b3 + b1 - c2 / 4 )
    *                           
    *                          or the expression may contain a single function, sum or avg:
    *                          Example: a1 = ( sum a1 - a3 )
    *                          Example: b1 = ( avg a1 - d1 )
    *   
    *   [cell]          : get the cell's expression, NOT the cell's value
    *   value [cell]    : get the cell value
    *   expr [cell]     : get the cell's expression, NOT the cell's value
    *   display [cell]  : get the string for how the cell wants to display itself
    *   clear           : empty out the entire matrix
    *   save [file]     : saves to a file all the commands necessary to regenerate the grid's contents
    *   clear [cell]    : empty out a single cell. Example: clear a1
    *   sorta [range]   : sort the range in ascending order. Example: sorta a1 - a5
    *   sortd [range]   : sort the range in descending order. Example: sortd b1 - e1
    *   
    *   Parameters:
    *     command : The command to be processed.
    *   Returns : The results of the command as a string to be printed by the infrastructure.
    */

  public String processCommand(String command) {
    String result = null;

    // prints using the print method if inputted by the user
    if (command.startsWith("print")) {
      return print();
    }

    String[] tokens = command.split(" ");
    // if the user manually changes the row, width or cols
    // the appropriate if statement amongst these three are called
    // tokens[0] = "rows" or "cols" or "width"
    // tokens[1] = "="
    // tokens[2] = ## (in a String)
    if (tokens.length >=1 && tokens[0].equalsIgnoreCase("rows")) {
      if (tokens.length == 3 && tokens[1].equals("=")) {
        int num = Integer.parseInt(tokens[2]);
        this.rowCount = num;
        return this.rowCount + "";
      } else {
        return rowCount + ""; 
      }
    } else if (tokens.length >=1 && tokens[0].equalsIgnoreCase("cols")) {
      if (tokens.length == 3 && tokens[1].equals("=")) {
        int num = Integer.parseInt(tokens[2]);
        this.colCount = num;
        return this.colCount + "";
      } else {
        return colCount + ""; 
      }
    } else if (tokens.length >=1 && tokens[0].equalsIgnoreCase("width")) {
      if (tokens.length == 3 && tokens[1].equals("=")) {
        int num = Integer.parseInt(tokens[2]);
        this.cellWidth = num;
        return this.cellWidth + "";
      } else {
        return cellWidth + ""; 
      }
    }

    // if there is a sum of a range of cells that is assigned thorugh
    // parentheses to a single cell, this else if statement is used
    // for example, if a1 = ( sum a2 - a6 ) is entered
    else if (command.contains("( sum ")) {
      int destCol = getCol(tokens[0]);
      int destRow = getRow(tokens[0]);
      double sum = getRangeSum(tokens[4], tokens[6]);
      int index = command.indexOf("=");
      String function = command.substring(index + 2);
      NumberCell cell = new NumberCell();
      cell.setFunction(function, matrix);
      matrix[destRow][destCol] = cell;
      result = "" + sum;
    }

    // if there is an average of a range of cells that is assigned thorugh
    // parentheses to a single cell, this else if statement is used
    // for example, if a1 = ( avg a2 - a6 ) is entered
    else if (command.contains("( avg ")) {
      int destCol = getCol(tokens[0]);
      int destRow = getRow(tokens[0]);
      double sum = getRangeAvg(tokens[4], tokens[6]);
      int index = command.indexOf("=");
      String function = command.substring(index + 2);
      NumberCell cell = new NumberCell();
      cell.setFunction(function, matrix);
      matrix[destRow][destCol] = cell;
      result = "" + sum;
    }

      
    // used if the user enters a string expression and assigns
    // that to a cell (like a1 = "hello")
    else if ((command.indexOf("\"") == command.indexOf("=") + 2) && command.endsWith("\"")) {
      int col = getCol(command);
      int row = getRow(command);
        
      int index = command.indexOf("=");
      command = command.substring(index + 2);
        
      TextCell cell = new TextCell();
      cell.setExpression(command);
      setCell(row, col, cell);
      return matrix[row][col].getExpression();  
    } 
    
    // if the user assigns a date or an expression to a cell 
    // (like a1 = 2/2/22 or a1 = ( 3 * 4) ) this code will be implemented
    else if (tokens.length >= 3 && isValidCellLocation(tokens[0]) && tokens[1].equals("=")) {
      int col = getCol(tokens[0]);
      int row = getRow(tokens[0]);
      
      // puts the user's inputted number into the matrix
      String value = command.substring(command.indexOf("=") + 1);
      Cell cell;
      if (!value.contains("(") && value.contains("/") ) {
        // date cell
        cell = new DateCell();
      } else {
        // number cell
        cell = new NumberCell();
      }

      // makes sure that every open parentheses has a closing one
      // to guarentee that all parentheses are in sets
      int open = 0;
      for (int index = 0; index < tokens.length; index++) {
        if (tokens[index].equals("(")) {
          open++;
        } else if (tokens[index].equals(")")) {
          open--;
        }
      }

      // if the inputted number has too many decimal points, not enough sets
      // set of parentheses, or undefined value then an error will show
      if (decimalRepeat(tokens)) {
        result = "invalid decimal";
      } else if (open != 0 ) {
        result = "invalid input";
      } else if (!cell.setExpression(value)) {
        result = tokens[0] + " is undefined";
      } else {
        matrix[row][col] = cell;
        result = matrix[row][col] + "";
      }
    } 

    // gets the sum of the range of cells that follows the sum statement
    // for example, if sum a1 - a3 is entered
    else if (command.startsWith("sum")) {
      int index = command.indexOf(" ");
      String piece = command.substring(index + 1);
      int dashIndex = piece.indexOf("-");
      String start = piece.substring(0, dashIndex);
      String end = piece.substring(dashIndex + 1);
      result = "" + getRangeSum(start, end);
    }

    // gets the average of the range of cells that follows the avg statement
    // for example, if sum a1 - a3 is entered
    else if (command.startsWith("avg")) {
      int index = command.indexOf(" ");
      String piece = command.substring(index + 1);
      int dashIndex = piece.indexOf("-");
      String start = piece.substring(0, dashIndex);
      String end = piece.substring(dashIndex + 1);
      result = "" + getRangeAvg(start, end);
    }
      
    // The value command will show the double value of a cell. 
    // NumberCells will have a value that matches their display. 
    // DateCells and TextCells will have a zero value.
    // Empty cells have a zero value.
    else if (command.startsWith("value")) {
      String position = command.substring(5).trim();
      if (!isValidCellLocation(position)) {
        return position + " is out of bounds";
      }
      int row = getRow(position);
      int col = getCol(position);
      if (matrix[row][col] == null) {
        return 0.0 + "";
      } else {
        return matrix[row][col].getValue() + "";
      }
    }

    // if the user inputs "clear" this will clear the grid
    else if (command.startsWith("clear")) {
      // used if the user wants to clear all cells in the grid
      if (command.equals("clear")) {
        for(int rows = 0; rows < this.matrix.length; rows++) {
          for(int cols = 0; cols < this.matrix[0].length; cols++) {
            this.matrix[rows][cols] = null;
          }
        }
        result = "grid cleared";
      } 
      
      // used if the user wants to clear a specific cell
      else {
        int targetRow = getRow(tokens[1]);
        int targetCol = getCol(tokens[1]);
        matrix[targetRow][targetCol] = null;
        result = "cell cleared";
      }
    }
          
    // used if the user wants to see what is in the cell that they have typed in 
    else if (command.startsWith("display")) {
      // if a two letter input that is not a cell location is inputted then it is invalid
      if (isValidCellLocation(tokens[1])) {
        int rows = getRow(tokens[1]);
        int cols = getCol(tokens[1]);
        return matrix[rows][cols].toString();
      } else {
        return "invalid cell location";
      }
    } 
          
    // if the user wants the value that is in the cell for the 
    // location they have typed in without typiong in the word "value"
    // for example if they type in a1 then they should get the value of the cell a1
    else if (!command.contains("=") && Character.isLetter(command.charAt(0)) && Character.isDigit(command.charAt(1))) {
      if (isValidCellLocation(tokens[0])) {
        int rows = getRow(command);
        int cols = getCol(command);
        
        // removes an extra space at the beginning from the value of cell
        if (!matrix[rows][cols].getExpression().contains("\"")) {
          return matrix[rows][cols].getExpression().substring(1);
        }
        return matrix[rows][cols].getExpression();
      } else {
        return "invalid command";
      }
    }
          
    // if the command starts with expr then it should display the 
    // expression (in a string format) that was used to set the cell
    else if (command.startsWith("expr")) {
      int rows = getRow(tokens[1]);
      int cols = getCol(tokens[1]);
      if (rows == -1 || cols == -1) {
        return "unknown location: " + tokens[1];
      }
      return matrix[rows][cols].getExpression();
    }

    // This will write to a file all the cells’ original expressions
    // it is in charge of making sure that a file is saved
    else if (command.startsWith("save")) {
      String filename = tokens[1];
      save(filename);
      result = "File saved successfully";
    }

    // used if the user wants to sort a series of cells in ascending 
    // order (from smallest to largest)
    else if (command.startsWith("sorta")) {
      int spaceIndex = command.indexOf(" ");
      int dashIndex = command.indexOf("-");
      String startCell = command.substring(spaceIndex + 1, dashIndex);
      String endCell = command.substring(dashIndex + 1);
      sortA(startCell, endCell);
      result = "sorted in ascending order";
    }

    // used if the user wants to sort a series of cells in descending 
    // order (from largest to smallest)
    else if (command.startsWith("sortd")) {
      int spaceIndex = command.indexOf(" ");
      int dashIndex = command.indexOf("-");
      String startCell = command.substring(spaceIndex + 1, dashIndex);
      String endCell = command.substring(dashIndex + 1);
      sortD(startCell, endCell);
      result = "sorted in descending order";
    }

    // if the user enters a malformed command that only has one
    // quotation mark or single quotation marks, then this method 
    // will recognize that and provide an error
    else if (command.contains("\"") || command.contains("'")) {
      result = "unknown or malformed command: " + command;
      return result;
    }
    
    // if the result is null an error should be returned
    if (result == null) {
      result = "unknown or malformed command: " + command;
    }
        
    return result;
  }

  // is called when the user inputs the word sum and returns the 
  // double value of the sum of the values in the range of
  // cells inputted by the user
  private double getRangeSum(String start, String end) {
    start = start.trim();
    end = end.trim();

    // gets the rows and the cols of the first and second inputted cells
    int startRow = getRow(start);
    int startCol = getCol(start);
    int endRow = getRow(end);
    int endCol = getCol(end);

    // uses a for loop to go through all the values that are in between 
    // the two cells in the grid and return the sum of them
    double sum = 0;
    for (int row = startRow; row <= endRow; row++) {
      for (int col = startCol; col <= endCol; col++) {
        if (matrix[row][col] != null) {
          sum += matrix[row][col].getValue(); 
        }
      }
    }
    return sum;
  }

  // is called when the user inputs the word avg and returns the 
  // double value of the average of the values in the range of
  // cells inputted by the user
  private double getRangeAvg(String start, String end) {
    start = start.trim();
    end = end.trim();

    // gets the rows and the cols of the first and second inputted cells
    int startRow = getRow(start);
    int startCol = getCol(start);
    int endRow = getRow(end);
    int endCol = getCol(end);

    // uses a for loop to go through all the values that are in between 
    // the two cells in the grid and return the average of them
    double sum = 0;
    int num = 0;
    for (int row = startRow; row <= endRow; row++) {
      for (int col = startCol; col <= endCol; col++) {
        if (matrix[row][col] != null) {
          sum += matrix[row][col].getValue();
        }
        num++;
      }
    }
    return sum / num;
  }

  // this method will return true if an inputted cell location is valid
  private boolean isValidCellLocation(String token) {
    // for an input with one letter and one number (like a1)
    if (token.length() == 2 && Character.isLetter(token.charAt(0)) && 
        Character.isDigit(token.charAt(1))) {
      return true;
    } 
    
    // for an input with one letter and two numbers (like a11)
    else if (token.length() == 3 && Character.isLetter(token.charAt(0)) && 
        Character.isDigit(token.charAt(1)) &&
        Character.isDigit(token.charAt(2))) {
      return true;
    }
    return false;
  }

  // this method is called when a file needs to be saved
  // it uses PrintStream to accept a filename and accapt a file with that name
  private void save(String filename) {
    try {
      File file = new File(filename);
      PrintStream print = new PrintStream(file);
      print.println("rows = " + rowCount);
      print.println("cols = " + colCount);
      print.println("width = " + cellWidth);
      for (int rows = 0; rows < rowCount; rows++) {
        for (int cols = 0; cols < colCount; cols++) {
          if (matrix[rows][cols] != null) {
            print.println(alphabet.charAt(cols) + "" + (rows + 1) + " = " + matrix[rows][cols].getExpression());
          }
        }
      }
      print.close(); 
    } catch (Exception e) {
    }
  }

  private void sortA(String startCell, String endCell) {
    int startRow = getRow(startCell);
    int startCol = getCol(startCell);
    int endRow = getRow(endCell);
    int endCol = getCol(endCell);
    ArrayList<Cell> list = new ArrayList<Cell>();
    for (int row = startRow; row <= endRow; row++) {
      for (int col = startCol; col <= endCol; col++) {
        list.add(matrix[row][col]);
      }
    }
    list.sort(null);
    System.out.println(list);
    int index = 0;
    for (int row = startRow; row <= endRow; row++) {
      for (int col = startCol; col <= endCol; col++) {
        matrix[row][col] = list.get(index);
        index++;
      }
    } 
  }

  private void sortD(String startCell, String endCell) {
    int startRow = getRow(startCell);
    int startCol = getCol(startCell);
    int endRow = getRow(endCell);
    int endCol = getCol(endCell);
    ArrayList<Cell> list = new ArrayList<Cell>();
    for (int row = startRow; row <= endRow; row++) {
      for (int col = startCol; col <= endCol; col++) {
        list.add(matrix[row][col]);
      }
    }
    list.sort(null);
    int index = list.size() - 1;
    for (int row = startRow; row <= endRow; row++) {
      for (int col = startCol; col <= endCol; col++) {
        matrix[row][col] = list.get(index);
        index--;
      }
    } 
  }

  // if the user inputs an invalid number with too many decimal
  // points, then this will return false
  private boolean decimalRepeat(String[] tokens) {
    for(int i = 0; i < tokens.length; i++) {
      int periods = 0;
      for (int c = 0; c < tokens[i].length(); c++) {
        if (tokens[i].charAt(c) == '.') {
          periods++;
        }
      }
      if (periods > 1) {
        return true;
      }
    }
    return false;
  }
  
  // method that prints the grid in correct format
  private String print() {
    String result = "";

    int gap1;
    int gap2;

    gap2 = cellWidth / 2;
    if (cellWidth % 2 == 0) {
      gap1 = (cellWidth - 1) / 2;
    } else {
      gap1 = (cellWidth - 1) / 2;
    }

    String gap1str = "";
    String gap2str = "";

    for (int index = 0; index < gap1; index++) {
      gap2str += " ";
    }
    for (int index = 0; index < gap2; index++) {
      gap1str += " ";
    }
    
    String gap = "";
    String dashGap = "";
    for (int index = 0; index < cellWidth; index++) {
      gap += " ";
      dashGap += "-";
    }

    String topRow = "    |";
    
    for (int index = 0; index < colCount; index++) {
      topRow += gap1str + alphabet.charAt(index) + gap2str + "|";
    }
    result += topRow + "\n";
    result += "----+";
    for (int cols = 0; cols < colCount; cols++) {
      result += dashGap + "+";
    }
    result += "\n";

    // all the code in this method above this comment is for the standardized printing of the
    // top row and the left-most column and the rest of the method (below) is 
    // printing the body of the matrix
    int gapWithVar = 0;
    
    for (int rows = 0; rows < rowCount; rows++) {
      result += String.format("%3d |", (rows + 1));
      for (int cols = 0; cols < colCount; cols++) {
        if (matrix[rows][cols] == null) {
          result += gap + "|";
        } else {
          int length = matrix[rows][cols].toString().length();
          if (length < cellWidth) {
            gapWithVar = cellWidth - length;
            for (int index = 0; index < gapWithVar; index++) {
              result += " ";
            }
            result += matrix[rows][cols].toString();
            result += "|";
          } else {
            result += matrix[rows][cols].toString().substring(0, cellWidth) + "|";
          }
        }
      }
      result += "\n";
      result += "----+";
      for (int cols = 0; cols < colCount; cols++) {
        result += dashGap + "+";
      }
      result += "\n";
    }

    return result;
  }

}
