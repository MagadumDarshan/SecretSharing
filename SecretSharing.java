package jason;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class SecretSharing {
    public static void main(String[] args) {
        try {
            // Update this path to match the location of your input.json file
            String filePath = "C:\\Users\\DARSHAN\\IdeaProjects\\Catalog\\src\\jason\\input.json";
            String content = new Scanner(new File(filePath)).useDelimiter("\\Z").next();
            JSONObject json = new JSONObject(content);

            // Access the test cases
            JSONArray testCases = json.getJSONArray("testCases");

            // Process each test case
            for (int i = 0; i < testCases.length(); i++) {
                JSONObject testCase = testCases.getJSONObject(i);
                int k = testCase.getJSONObject("keys").getInt("k");

                // Store x and decoded y values
                Map<Integer, BigInteger> points = new HashMap<>();

                // Extract all the points
                for (String key : testCase.keySet()) {
                    if (!key.equals("keys")) {
                        JSONObject point = testCase.getJSONObject(key);
                        int x = Integer.parseInt(key);
                        int base = Integer.parseInt(point.getString("base"));
                        String value = point.getString("value");
                        BigInteger y = new BigInteger(value, base); // Decode y using the provided base

                        points.put(x, y);
                    }
                }

                // Calculate the constant term (c) using Lagrange interpolation
                BigInteger constantTerm = lagrangeInterpolation(points, k);
                System.out.printf("Test case %d: The constant term (c) is: %s (rounded: %d)%n",
                        i + 1, constantTerm.toString(), constantTerm.longValue());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (org.json.JSONException e) {
            System.out.println("Error parsing JSON: " + e.getMessage());
        }
    }

    public static BigInteger lagrangeInterpolation(Map<Integer, BigInteger> points, int k) {
        BigInteger result = BigInteger.ZERO;
        for (Map.Entry<Integer, BigInteger> p1 : points.entrySet()) {
            int x1 = p1.getKey();
            BigInteger y1 = p1.getValue();

            // Calculate the Lagrange basis polynomial
            BigInteger term = y1;
            for (Map.Entry<Integer, BigInteger> p2 : points.entrySet()) {
                int x2 = p2.getKey();
                if (x1 != x2) {
                    term = term.multiply(BigInteger.valueOf(-x2)).divide(BigInteger.valueOf(x1 - x2));
                }
            }

            result = result.add(term);
        }
        return result;
    }
}
