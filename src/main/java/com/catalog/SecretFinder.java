package com.catalog;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SecretFinder {

    public static void main(String[] args) {
        try {
            // Process testcase1
            System.out.println("Processing Test Case 1:");
            processTestCase("src/main/resources/testcase1.json");
            
            // Process testcase2
            System.out.println("\nProcessing Test Case 2:");
            processTestCase("src/main/resources/testcase2.json");
            
        } catch (IOException e) {
            System.err.println("Error processing test cases: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void processTestCase(String jsonFilePath) throws IOException {
        String jsonContent = readFileContent(jsonFilePath);
        
        // Extract n and k values using regex
        Pattern keysPattern = Pattern.compile("\"keys\"\\s*:\\s*\\{\\s*\"n\"\\s*:\\s*(\\d+)\\s*,\\s*\"k\"\\s*:\\s*(\\d+)\\s*\\}");
        Matcher keysMatcher = keysPattern.matcher(jsonContent);
        
        if (!keysMatcher.find()) {
            throw new IOException("Could not find n and k values in the JSON file");
        }
        
        int n = Integer.parseInt(keysMatcher.group(1));
        int k = Integer.parseInt(keysMatcher.group(2));
        int degree = k - 1; // polynomial degree
        
        System.out.println("Number of roots (n): " + n);
        System.out.println("Minimum roots required (k): " + k);
        System.out.println("Polynomial degree (m): " + degree);
        
        // Extract points using regex
        Pattern pointPattern = Pattern.compile("\"(\\d+)\"\\s*:\\s*\\{\\s*\"base\"\\s*:\\s*\"(\\d+)\"\\s*,\\s*\"value\"\\s*:\\s*\"([^\"]+)\"\\s*\\}");
        Matcher pointMatcher = pointPattern.matcher(jsonContent);
        
        List<Point> points = new ArrayList<>();
        
        while (pointMatcher.find()) {
            int x = Integer.parseInt(pointMatcher.group(1));
            int base = Integer.parseInt(pointMatcher.group(2));
            String encodedY = pointMatcher.group(3);
            
            // Decode y value
            BigInteger y = decodeValue(encodedY, base);
            
            points.add(new Point(x, y));
            System.out.println("Point: x=" + x + ", y=" + y + " (decoded from " + encodedY + " in base " + base + ")");
        }
        
        // Ensure we have enough points
        if (points.size() < k) {
            System.out.println("Not enough points to find the secret. Need at least " + k + " points.");
            return;
        }
        
        // Use the first k points to solve the polynomial
        List<Point> selectedPoints = points.subList(0, k);
        
        // Find the secret using Lagrange interpolation at x=0
        BigInteger secret = lagrangeInterpolationAtX0(selectedPoints);
        
        // The secret is the constant term (value at x=0)
        System.out.println("\nThe secret (c) = " + secret);
    }
    
    /**
     * Reads the entire content of a file as a string
     */
    private static String readFileContent(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }
    
    /**
     * Decodes a value from the given base to decimal
     */
    private static BigInteger decodeValue(String encodedValue, int base) {
        BigInteger result = BigInteger.ZERO;
        
        for (int i = 0; i < encodedValue.length(); i++) {
            char c = encodedValue.charAt(i);
            int digit;
            
            if (c >= '0' && c <= '9') {
                digit = c - '0';
            } else if (c >= 'a' && c <= 'z') {
                digit = c - 'a' + 10;
            } else if (c >= 'A' && c <= 'Z') {
                digit = c - 'A' + 10;
            } else {
                throw new IllegalArgumentException("Invalid character in encoded value: " + c);
            }
            
            if (digit >= base) {
                throw new IllegalArgumentException("Digit " + digit + " is not valid in base " + base);
            }
            
            result = result.multiply(BigInteger.valueOf(base)).add(BigInteger.valueOf(digit));
        }
        
        return result;
    }
    
    /**
     * Uses Lagrange interpolation to find the value at x=0 (the secret)
     * This is more efficient than finding all coefficients when we only need the constant term
     */
    private static BigInteger lagrangeInterpolationAtX0(List<Point> points) {
        BigInteger result = BigInteger.ZERO;
        int n = points.size();
        
        // For each point, calculate its contribution to the value at x=0
        for (int i = 0; i < n; i++) {
            Point currentPoint = points.get(i);
            
            // Calculate the Lagrange basis polynomial value at x=0 for this point
            BigInteger basisValue = lagrangeBasisAtX0(points, i);
            
            // Add this point's contribution: y_i * L_i(0)
            BigInteger contribution = currentPoint.y.multiply(basisValue);
            result = result.add(contribution);
        }
        
        return result;
    }
    
    private static BigInteger lagrangeBasisAtX0(List<Point> points, int i) {
        BigInteger numerator = BigInteger.ONE;
        BigInteger denominator = BigInteger.ONE;
        
        Point p_i = points.get(i);
        
        for (int j = 0; j < points.size(); j++) {
            if (j == i) continue;
            
            Point p_j = points.get(j);
            
            // Numerator: multiply by (-x_j)
            numerator = numerator.multiply(BigInteger.valueOf(-p_j.x));
            
            // Denominator: multiply by (x_i - x_j)
            denominator = denominator.multiply(BigInteger.valueOf(p_i.x - p_j.x));
        }
        
        // Return numerator / denominator
        if (numerator.remainder(denominator).equals(BigInteger.ZERO)) {
            return numerator.divide(denominator);
        } else {
            throw new ArithmeticException("Division result is not an integer: " + numerator + " / " + denominator);
        }
    }
    
    /**
     * A simple class to represent a point (x, y)
     */
    private static class Point {
        int x;
        BigInteger y;
        
        Point(int x, BigInteger y) {
            this.x = x;
            this.y = y;
        }
    }
}
