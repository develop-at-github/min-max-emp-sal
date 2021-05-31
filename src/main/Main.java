package main;

/*
amirs@smartek21.com
Process data from the below URL. Display employee names with the highest and lowest salary.
- http://dummy.restapiexample.com/api/v1/employees
- Use any language/script for processing.
- Optimize your code to process a large number of records.
- Define validation and test boundary conditions.
- Feel free to use the internet for a solution
 */

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

public class Main {

    public static void main(String[] args) throws URISyntaxException, JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://dummy.restapiexample.com/api/v1/employees";
        MultiValueMap<String, String> headers = new HttpHeaders();

        // Adding user-agent to request header solved the issue.
        // Reason: when client and server are on different systems, server has mandated request to identify its source which is done by user-agent header
//        headers.add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36");
        // Although any random user-agent value works well with this server
        headers.add("User-Agent", "test/10");
        RequestEntity request = new RequestEntity(headers, HttpMethod.GET, new URI(url));
        ResponseEntity response = restTemplate.exchange(request, String.class);
//        System.out.println(response.getBody());
        FullResp resp = new ObjectMapper().readValue(response.getBody().toString(), FullResp.class);
        System.out.println("Stream - Employee with Min salary = " + resp.getData().parallelStream().min(Comparator.comparingInt(Employee::getEmployee_salary)).get().getEmployee_name());
        System.out.println("Stream - Employee with Max salary = " + resp.getData().parallelStream().max(Comparator.comparingInt(Employee::getEmployee_salary)).get().getEmployee_name());
        // Optimization 1 - use single loop to find both min and max at once
        Employee min = resp.getData().get(0);
        Employee max = resp.getData().get(0);
        for (Employee e : resp.getData()) {
            if (min.getEmployee_salary() > e.getEmployee_salary()) {
                min = e;
            }
            if (max.getEmployee_salary() < e.getEmployee_salary()) {
                max = e;
            }
        }
        System.out.println("Single loop - Employee with Min salary = " + min.getEmployee_name());
        System.out.println("Single loop - Employee with Max salary = " + max.getEmployee_name());
        // Optimization 3 - use TreeSet with comparator on employee salary and get first and last in the set
        Collections.sort(resp.getData(), Comparator.comparingInt(Employee::getEmployee_salary));
        System.out.println("Sort - Employee with Min salary = " + resp.getData().get(0).getEmployee_name());
        System.out.println("Sort - Employee with Max salary = " + resp.getData().get(resp.getData().size() - 1).getEmployee_name());
        // Optimization 3 - use TreeSet with comparator on employee salary and get first and last in the set
        SortedSet<Employee> set = new TreeSet<>(Comparator.comparingInt(Employee::getEmployee_salary));
        set.addAll(resp.getData());
        System.out.println("TreeSet - Employee with Min salary = " + set.first().getEmployee_name());
        System.out.println("TreeSet - Employee with Max salary = " + set.last().getEmployee_name());
        // Validations and tests
        /*
        1. Handle all client-side exceptions like incorrect request url, request method, etc.
        2. Handle all server-side exceptions like unavailable, timeout, internal-server-error etc.
        3. Handle empty, 1 and 2 employee in "data" in response
        4. Handle employee having missing "employee_salary" field
        5. Handle employee having non-numeric "employee_salary" field
        6. Handle employee having very large "employee_salary" value using BigInteger/BigDecimal
         */
    }

    public static void main1(String[] args) throws IOException {
        URL obj = new URL("http://dummy.restapiexample.com/api/v1/employees");
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        int responseCode = con.getResponseCode();
//        System.out.println("GET Response Code :: " + responseCode);
        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
//            System.out.println(response.toString());
            FullResp resp = new ObjectMapper().readValue(response.toString(), FullResp.class);

            System.out.println("Employee with Min salary = " + resp.getData().stream().min(Comparator.comparingInt(Employee::getEmployee_salary)).get().getEmployee_name());
            System.out.println("Employee with Max salary = " + resp.getData().stream().max(Comparator.comparingInt(Employee::getEmployee_salary)).get().getEmployee_name());

/*
using org.json
            JSONArray jsonArray = new JSONObject(response.toString()).getJSONArray("data");

            JSONObject min = jsonArray.getJSONObject(0);
            JSONObject max = jsonArray.getJSONObject(0);

            for (int i = 1; i < jsonArray.length(); i++) {
                JSONObject current = jsonArray.getJSONObject(i);
                if (current.getInt("employee_salary") < Integer.parseInt(min.getString("employee_salary"))) {
                    min = current;
                }
                if (Integer.parseInt(current.getString("employee_salary")) > Integer.parseInt(max.getString("employee_salary"))) {
                    max = current;
                }
            }

            System.out.println(min.getString("employee_name") + " " + min.getString("employee_salary"));
            System.out.println(max.getString("employee_name") + " " + max.getString("employee_salary"));
*/

//            System.out.println(new JSONObject(response.toString()).getJSONArray("data"));
        }
    }
}
