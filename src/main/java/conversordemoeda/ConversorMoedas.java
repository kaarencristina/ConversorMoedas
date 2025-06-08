package conversordemoeda;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Scanner;


import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class ConversorMoedas {

    private static final String API_KEY = "c0bef6f7e564375b3c2cc6dc";  // Substitua pela sua chave da API
    private static final String API_URL = "https://v6.exchangerate-api.com/v6/";

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Digite o valor a converter: ");
        double valor = scanner.nextDouble();

        System.out.print("Digite a moeda base (ex: BRL, USD, EUR): ");
        String moedaBase = scanner.next().toUpperCase();

        System.out.print("Digite a moeda destino (ex: USD, EUR, BRL): ");
        String moedaDestino = scanner.next().toUpperCase();

        // Montar URL da API
        String url = API_URL + API_KEY + "/latest/" + moedaBase;

        // Criar client HTTP
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        System.out.println("\nConsultando taxas de câmbio...");

        // Fazer requisição e pegar resposta
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            String jsonResponse = response.body();

            // Usar Gson para interpretar JSON
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);

            // Verificar se a requisição foi bem sucedida
            String result = jsonObject.get("result").getAsString();
            if (!"success".equals(result)) {
                System.out.println("Erro ao consultar a API: " + jsonObject.get("error-type").getAsString());
                scanner.close();
                return;
            }

            // Obter o objeto com as taxas de câmbio
            JsonObject rates = jsonObject.getAsJsonObject("conversion_rates");

            // Verificar se a moeda destino existe nas taxas
            if (!rates.has(moedaDestino)) {
                System.out.println("Moeda destino inválida ou não disponível.");
                scanner.close();
                return;
            }

            // Obter a taxa de conversão
            double taxa = rates.get(moedaDestino).getAsDouble();

            // Calcular valor convertido
            double valorConvertido = valor * taxa;

            System.out.printf("%.2f %s equivalem a %.2f %s%n", valor, moedaBase, valorConvertido, moedaDestino);
        } else {
            System.out.println("Erro na requisição HTTP. Código: " + response.statusCode());
        }

        scanner.close();
    }
}
