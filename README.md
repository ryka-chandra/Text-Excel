# Text-Excel
A Take on Microsoft Excel

## Overview

This program manages data in a 10x7 grid layout, supporting various data types such as real numbers, strings, dates, and formulas (e.g., sums, averages). It offers streamlined printing operations with automatic truncation of excessive string lengths and advanced formula evaluation with support for order of operations and parenthesis handling.

## Features

- **Grid Layout:** Manages data in a 10x7 grid format.
- **Data Types:** Supports real numbers, strings, dates, and formulas (e.g., sum, average).
- **Printing Operations:** Automatically truncates excessive string lengths for neat output.
- **Formula Evaluation:** Implements advanced formula evaluation with order of operations and parenthesis handling.
- **Error Handling:** Robust error handling for various input anomalies to ensure system stability.
- **Help Function:** Includes a user-centric "help" function for quick guidance on program usage.

## Usage

To use the program:

1. Clone the repository to your local machine

2. Compile the program:
   ```bash
   javac MainClass.java
   ```

3. Run the compiled program:
   ```bash
   java MainClass
   ```

4. Follow the on-screen instructions for data entry and manipulation.

5. Utilize the "help" function for assistance with commands and usage.

## Example

Here is a basic example of how to use the program:

```java
// Example usage code snippet
GridManager grid = new GridManager(10, 7);
grid.setData(1, 1, 10.5); // Set a real number
grid.setData(2, 2, "Sample Data"); // Set a string
grid.setData(3, 3, "=SUM(A1:A3)"); // Set a formula
grid.printGrid(); // Display the grid
```

## Contributors

- Ryka Chandra

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
