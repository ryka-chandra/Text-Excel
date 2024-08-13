/*
 * The NumberCell may hold numbers, an expression, or functions and it 
 * calculates the double value of a cell from the expression and it 
 * evaluates an expression on demand (LazyEvaluation) to avoid problems 
 * when caching values. It uses the GridBase.smartSplit() to 
 * tokenize an expression.
 *
 */
public class NumberCell extends Cell {
    // these are private instance fields that ar e refernced throughout the
    // class 
    private String input;
    private boolean equation = false;
    private String function = "";
    private Cell[][] matrix;

    // this is a mutator that accepts a String function and a matrix
    // and sets the value of the parameters and equation inside of the method
    public void setFunction(String function, Cell[][] matrix) {
      this.function = function;
      setExpression(function);
      this.matrix = matrix;
      equation = true;
    }

    // this acceptes a Cell that is used when sorting over a region and
    // sorting integers in a row as well as decimals in a column
    // it returns an int based on the results of the comparison between other 
    // the result of the getValue()
    public int compareTo(Cell other) {
      if (other == null) {
        return 1;
      }
      if ((getValue() - other.getValue()) < 0 ) {      
        return -1;
      } else if ((getValue() - other.getValue()) > 0 ) {
        return 1;
      } else {
        return 0;
      }
    }

    // this is a mutator that accepts a String input and returns a boolean
    // if the tokens are null or have a value that is invalid, it will 
    // return false, otherwise it will return true
    public boolean setExpression(String input) {
      String[] tokens = GridBase.smartSplit(input);
      if (tokens == null) {
        return false;
      }
      for(int index = 0; index < tokens.length - 1; index++) {
        if(tokens[index].equals("/") && tokens[index+1].equals("0") ) {
          return false;
        }
      }
      super.setExpression(input);
      return true;
    }
  
    /*
     * This returns the string to be presented in the grid.
     */  
    public String toString() {
        return getValue() + "";
    }
  
    /*
     * This will return the number for this cell.
     */
    public double getValue() {
      // gets tokens from the expression using a robust parser
      String[] tokens = GridBase.smartSplit(getExpression());

      // if the input is an equation then it will get the average
      // or sum depending on the input
      if (equation) {
        String start = tokens[2];
        String end = tokens[4];
        String f = tokens[1];
        if (f.equals("sum")) {
          return getRangeSum(start, end);
        }
        if (f.equals("avg")) {
          return getRangeAvg(start, end);
        }
        System.out.println("ERROR");
        return -500;
      }
      
      // deals with any starting parenthesis in the string value 
      // (which is everything after the equal sign, minus the space 
      // and parentheses at the beginning and end)
      if(tokens[0].equals("(")) {
        String[] copy = new String[tokens.length - 2];
        for(int index = 1; index < tokens.length - 1; index++) {
          copy[index-1] = tokens[index];
        }
        tokens = copy;
      }

      // if the user inputs a log or sqrt into the expression, those
      // operators take highest precedence and as such they should
      // be done first
      for (int index = 0; index < tokens.length; index++) {
        double num = Double.MIN_VALUE;
        if(tokens[index].equals("log")) {
        num = getTokenValue(tokens[index+1]);
        num = Math.log(num);
      }
      if(tokens[index].equals("sqrt")) {
        num = getTokenValue(tokens[index+1]);
        num = Math.sqrt(num);
      }
      if(num != Double.MIN_VALUE) {
        tokens = removeIndex(tokens, index);
        tokens[index] = "" + num;
        index--;
      }
    } 

    // while there are more tokens/values left in the expression, this while
    // loop will continue and complete the operations in the expression
    while(tokens.length > 1) {
      int index = highestPriority(tokens);
      String answer = "ERROR";
      double one = getTokenValue(tokens[index]);
      String operator = tokens[index + 1];
      double two = getTokenValue(tokens[index + 2]);

      // conducts the operations based on order of priority (highest
      // priority on top) and does the appropriate operation
      if(operator.equals("^")) {
        answer = "" + Math.pow(one, two);
      } else if(operator.equals("*")) {
        answer = "" + (one * two);
      } else if(operator.equals("/")) {
        answer = "" + (one / two);
      } else if(operator.equals("+")) {  
        answer = "" + (one + two);
      } else if(operator.equals("-")) {
        answer = "" + (one - two);
      }
      String[] replacement = new String[tokens.length - 2];
      for(int num = 0; num < index; num++) {
        replacement[num] = tokens[num];
      }
      replacement[index] = answer;
      for (int num = index + 3; num < tokens.length; num++) {
        replacement[num-2] = tokens[num];
      }
      tokens = replacement;
    }
    return getTokenValue(tokens[0]);
  }

  
  // attempts to parse through the token
  // (if the token is not an actual number, it must
  // be a cell location)
  private double parseTokenValue(String token) {
    try {
      // returns the value as a double
      return Double.parseDouble(token);
    } catch (Exception e) {
      try {
        // Asks the Grid to get the value.
        String value = GridBase.grid.processCommand("value " + token);
        return Double.parseDouble(value);
      } catch (Exception x) {
        return 0.0;
      }
    }

  }

  // attempts to get a double value from the token
  // (if the token is not an actual number, it must
  // be a cell location -- and that is represented by the 
  // first if statement in the method)
  private double getTokenValue(String token) {
    if( Character.isLetter(token.charAt(0))) {
      return parseTokenValue(token);
    } else {
      return Double.parseDouble(token);
    }
  }

  // this method will accept an array of tokens as well 
  // as an index and it will return a String array that 
  // removes the token at index
  private String[] removeIndex(String[] tokens, int index) {
    String[] replacement = new String[tokens.length - 1];
    for(int num = 0; num < index; num++) {
      replacement[num] = tokens[num];
    }
    for(int num = index + 1; num < tokens.length; num++) {
      replacement[num-1] = tokens[num];
    }
    return replacement;
  }

  // returns an int number that represents the priority that should
  // should be given based on the operator 
  private int highestPriority(String[] tokens) {
    int highestIndex = 0;
    int maxValue = 0;
    for (int index = 0; index < tokens.length; index++) {
      // the highger the piority of the operator, the higher the number of value
      int value = 0;
      if (tokens[index].equals("^")) {
        value = 3;
      }
      if (tokens[index].equals("*") || tokens[index].equals("/")) {
        value = 2;
      }
      if (tokens[index].equals("+") || tokens[index].equals("-")) {
        value = 1;
      }

      // gets the index with the greatest value (highest priority)
      if (value > maxValue) {
        maxValue = value;
        highestIndex = index;
      }
    }
    return highestIndex - 1;
  }

  // this method gets the column variable from an inputted cell location
  private int getCol(String command) {
    String alphabet = "ABCDEFGHIJKLMNOQRSTUVWXYZ";  
    command = command.trim();
    String colstr = command.toUpperCase().charAt(0) + "";
    int col = alphabet.indexOf(colstr);
    return col;
  }

  // this method gets the row number from an inputted cell location 
  private int getRow(String command) {
    command = command.trim();
    String rowstr = command.charAt(1) + "";
    int row = Integer.parseInt(rowstr) - 1;
    return row;
  }

  // is called when the user inputs the word sum and returns the 
  // double value of the sum of the values in the range of
  // cells inputted by the user
  private double getRangeSum(String start, String end) {
      start = start.trim();
      end = end.trim();
      int startRow = getRow(start);
      int startCol = getCol(start);
      int endRow = getRow(end);
      int endCol = getCol(end);
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
    int startRow = getRow(start);
    int startCol = getCol(start);
    int endRow = getRow(end);
    int endCol = getCol(end);
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


}
