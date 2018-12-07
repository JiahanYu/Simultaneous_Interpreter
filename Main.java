import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class Main {
    public static void main(String[] args) {
        try {
            HttpResponse<String> response = Unirest.post("http://10.20.253.1:5000/translate/")
                    .header("Content-Type", "application/json")
                    .body("{\n\t\"data\": \"I am from China, too.\"\n}")
                    .asString();
            System.out.println(response.getBody());
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }
}
