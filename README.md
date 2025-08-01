# Catalog Placements Assignment Solution

This project implements a solution for finding the secret value (c) from a set of polynomial roots provided in JSON format.

## Project Structure

```
catalog/
├── src/
│   ├── main/
│       ├── java/
│       │   └── com/
│       │       └── catalog/
│       │           └── SecretFinder.java
│       └── resources/
│           ├── testcase1.json
│           └── testcase2.json
└── pom.xml
```

## How It Works

The solution follows these steps:

1. **Read JSON Input**: Parse the JSON file to extract n, k, and the encoded roots.
2. **Decode Y Values**: Convert the encoded Y values from their respective bases to decimal.
3. **Find the Secret (C)**: Use polynomial interpolation to calculate the coefficients of the polynomial, where the constant term (a0) is the secret value.

## Method Used

The program uses the Lagrange interpolation method to find the polynomial coefficients. For a polynomial of degree m, we need at least m+1 points to uniquely determine the polynomial.

The program uses Apache Commons Math for matrix operations and solving the system of linear equations.

## How to Run

1. Ensure you have Java 11+ and Maven installed.
2. Build the project:
```
mvn clean package
```
3. Run the application:
```
java -jar target/catalog-placements-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## Output

The program will output:
- The number of roots and minimum roots required
- Each point with its decoded Y value
- The coefficients of the polynomial
- The secret value (c)

## Notes

- The program uses the first k points to solve for the polynomial coefficients.
- The constant term (a0) of the polynomial is the secret value we're looking for.
