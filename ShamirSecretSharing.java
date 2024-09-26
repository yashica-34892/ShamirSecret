import java.math.BigInteger;
import java.util.Map;
import org.json.JSONObject;

public class ShamirSecretSharing {

    public static void main(String[] args) {
        // The JSON input as a string
        String jsonString = """
            {
    "keys": {
        "n": 9,
        "k": 6
    },
    "1": {
        "base": "10",
        "value": "28735619723837"
    },
    "2": {
        "base": "16",
        "value": "1A228867F0CA"
    },
    "3": {
        "base": "12",
        "value": "32811A4AA0B7B"
    },
    "4": {
        "base": "11",
        "value": "917978721331A"
    },
    "5": {
        "base": "16",
        "value": "1A22886782E1"
    },
    "6": {
        "base": "10",
        "value": "28735619654702"
    },
    "7": {
        "base": "14",
        "value": "71AB5070CC4B"
    },
    "8": {
        "base": "9",
        "value": "122662581541670"
    },
    "9": {
        "base": "8",
        "value": "642121030037605"
    }
}
        """;

        // Parse JSON input
        JSONObject jsonObject = new JSONObject(jsonString);

        // Extract the minimum number of points required
        int k = jsonObject.getJSONObject("keys").getInt("k");

        // Store the x and y values decoded from the JSON
        double[][] points = new double[k][2];

        // Extract the first k points from the JSON
        int pointIndex = 0;
        for (String key : jsonObject.keySet()) {
            if (key.equals("keys")) continue;  // Skip the "keys" field
            if (pointIndex >= k) break;        // Only take first k points

            // Extract base and value
            int base = jsonObject.getJSONObject(key).getInt("base");
            String value = jsonObject.getJSONObject(key).getString("value");

            // Decode the y value from the specified base
            BigInteger yDecoded = new BigInteger(value, base);

            // Assign the x value and decoded y value to points array
            points[pointIndex][0] = Integer.parseInt(key);  // x is the key
            points[pointIndex][1] = yDecoded.doubleValue(); // y is the decoded value
            pointIndex++;
        }

        // Perform Lagrange interpolation to find the constant term c
        double secret = lagrangeInterpolation(points, 0);  // Evaluate at x = 0

        // Print the result (the secret c)
        System.out.println("The constant term (secret) c is: " + secret);
    }

    // Lagrange Interpolation method
    public static double lagrangeInterpolation(double[][] points, double x) {
        int k = points.length;
        double result = 0.0;

        // Loop through each point
        for (int i = 0; i < k; i++) {
            double xi = points[i][0];
            double yi = points[i][1];

            // Compute Lagrange basis polynomial Li(x)
            double Li = 1.0;
            for (int j = 0; j < k; j++) {
                if (i != j) {
                    double xj = points[j][0];
                    Li *= (x - xj) / (xi - xj);
                }
            }

            // Add the contribution of yi * Li(x) to the result
            result += yi * Li;
        }

        return result;
    }
}

// javac -cp json-20210307.jar ShamirSecretSharing.java
// java -cp .:json-20210307.jar ShamirSecretSharing