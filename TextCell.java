public class TextCell extends Cell {

    // method that merely gets the expression method from the Cell class
    public String getExpression() {
      return super.getExpression();
    }
  
    /*
    *  This will return the string for how a TextCell wants to display
    *  itself. All it does is remove the bounding quotes from
    *  the expression.
    */
    public String toString() {
        // gets the expression and removes the quotes
        String command = getExpression();
        command = command.substring(1, command.length() - 1);
        return command;
    }
}
