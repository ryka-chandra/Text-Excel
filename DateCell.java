import java.text.DateFormat;
import java.util.*;
/*
 *   Takes a date expression in the slash format (ex: 1/3/99) and renders a 
 *   short date format such as: Jan 3, 1999.
 *   The value of this cell is zero.
 *
 */
public class DateCell extends Cell {
    // Annotate the website(s) you used to get help.
    // https://www.journaldev.com/17899/java-simpledateformat-java-date-format
    // http://web.cs.ucla.edu/classes/winter15/cs144/projects/java/simpledateformat.html

  // is a method that returns the String version of the date
    public String toString() {  
      try {
        // creates a new object date of the expression entered by the user
        // and gets the instance and formates the date correctly as a string
        Date date = new Date(getExpression());
        String dateString = DateFormat.getDateInstance().format(date);
        return dateString;
      } 
      // if there is an exception this will catch it and state that the date
      // entered is invalid
      catch (Exception e) {
        return "invalid date";
      }
    }
  
}
