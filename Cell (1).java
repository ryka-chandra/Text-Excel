public class Cell implements Comparable<Cell> {

    /*
     * Every cell class as a expression.
     */
    private String expression;

    /*
     * Constructor which sets expression to empty.
     */
    public Cell() {
        expression = "";
    }

    /*
     * Accessor to the expression for this cell.
     */
    public String getExpression() {
        return expression;
    }

    /*
     * Sets the expression of the cell.
     * 
     * Parameters:
     *    expression : A string that tells the cell how to calculate its value.
     *                 A value can be either a number, date, or text value (label).
     * Returns:
     *    True if we are successful at setting the cell's expression
     */
    public boolean setExpression(String expression) {
        this.expression = expression;
        return true;
    }

    /*
     * An empty cell has a zero value. Subclasses will override.
     * 
     * Returns:
     *    0.0 because we are an empty cell.
     */
    public double getValue() {

        return 0.0;
    }

    /*
     * Returns the presentation of the cell's value.
     * Subclasses will override.
     * 
     * Returns:
     *    "", an empty string.
     */
    public String toString() {

        return "";
    }

    /*
     *
     * This method is the implementation of the interface Comparable<T> which allows
     * the cells to be compared to one another even if they are different types. As
     * implemented, empty cells are "less than" TextCell, DateCell and NumberCell.
     * 
     * One should override this implementation in the derived classes to get proper
     * sorting. Use "public int compareTo(Cell other)" when overriding.
     * 
     * Collections.sort(arr) will sort arr of type ArrayList<Cell>.
     * 
     * Collections.sort(arr, Collections.reverseOrder()) will sort it in reverse.
     * 
     * The sort order should be:
     *     empty
     *     numbers
     *     dates
     *     text
     * 
     * Parameters:
     *     other : This is the cell to which 'thi's' cell is being compared to.
     * Returns:
     *    -1 if this cell is less than the other cell. 
     *     0 if this cell is equal to other cell.
     *     1 if this cell is greater than the other cell.
     */
    public int compareTo(Cell other) {
        // if the other cell has an expression, then it is NOT an empty cell
        // If it is NOT an empty cell, this empty cell is "less than" other.
        if (other.getExpression().length() > 0) {
            return -1;
        }

        // empty cell == empty cell
        return 0;
    }
}
