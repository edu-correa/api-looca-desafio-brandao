package org.example.Slack;
import com.google.gson.*;
import io.github.cdimascio.dotenv.Dotenv;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;

public class AbrirChamado {
    private static final String EMAIL = "suporte.banksecure@gmail.com";
    private static Dotenv dotenv = Dotenv.load();
    private static final String API_KEY = dotenv.get("API_KEY");
    private static final String AUTH = EMAIL + ":" + API_KEY;
    private static final String AUTH_HEADER = "Basic " + Base64.getEncoder().encodeToString(AUTH.getBytes(StandardCharsets.UTF_8));

    private static String nomeMaquina;
    private static String componente;
    private static Double porcentagemUso;
    private static int prioridade;
    private static String mensagem;


    public static void AbrirChamado(String nomeMaquina, String componente, Double porcentagemUso, int prioridade) {
        AbrirChamado.nomeMaquina = nomeMaquina;
        AbrirChamado.componente = componente;
        AbrirChamado.porcentagemUso = porcentagemUso;
        AbrirChamado.prioridade = prioridade;
        AbrirChamado.mensagem = String.format("Componente: %s\\nPorcentagem de uso: %.2f", AbrirChamado.componente, AbrirChamado.porcentagemUso);
        verificarChamado();
    }

    private static void postarChamado() {
        try {
            String url = "https://banksecure.atlassian.net/rest/api/3/issue";

            String payload = String.format(
                    "{\"fields\":{\"summary\":\"Alerta máquina %s\",\"project\":{\"key\":\"BSITAU\"},\"issuetype\":{\"name\":\"Task\"},\"priority\":{\"id\": \"%s\"},\"description\":{\"content\":[{\"content\":[{\"text\":\"%s\",\"type\":\"text\"}],\"type\":\"paragraph\"}],\"type\":\"doc\",\"version\":1}}}",
                    AbrirChamado.nomeMaquina,
                    AbrirChamado.prioridade,
                    AbrirChamado.mensagem
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .header("Authorization", AUTH_HEADER)
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();

            HttpClient client = HttpClient.newHttpClient();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void atualizarChamado(int chaveChamado){
        try{
            String url = String.format("https://banksecure.atlassian.net/rest/api/3/issue/%s", chaveChamado);
            String payload = String.format(
                    "{\"fields\":{\"summary\":\"Alerta máquina %s\",\"project\":{\"key\":\"BSITAU\"},\"issuetype\":{\"name\":\"Task\"},\"priority\":{\"id\": \"%s\"},\"description\":{\"content\":[{\"content\":[{\"text\":\"%s\",\"type\":\"text\"}],\"type\":\"paragraph\"}],\"type\":\"doc\",\"version\":1}}}",
                    AbrirChamado.nomeMaquina,
                    AbrirChamado.prioridade,
                    AbrirChamado.mensagem
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .header("Authorization", AUTH_HEADER)
                    .PUT(HttpRequest.BodyPublishers.ofString(payload))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private static void verificarChamado(){
        String url = "https://banksecure.atlassian.net/rest/api/3/search";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .header("Authorization", AUTH_HEADER)
                .GET()
                .build();
        HttpClient client = HttpClient.newHttpClient();

        try {
            HttpResponse<String> resposta = client.send(request, HttpResponse.BodyHandlers.ofString());
            String statusAlerta;
            String maquinaEmAlerta;
            ArrayList<JsonObject> chamados = new ArrayList<>();
            String idChamado;
            int prioridadeChamado;

            JsonArray respostaJson = JsonParser.parseString(resposta.body()).getAsJsonObject().get("issues").getAsJsonArray();
            for(JsonElement elemento: respostaJson){
                statusAlerta = String.valueOf(elemento
                        .getAsJsonObject()
                        .get("fields")
                        .getAsJsonObject()
                        .get("status")
                        .getAsJsonObject()
                        .get("name")
                );
                maquinaEmAlerta = String.valueOf(elemento
                        .getAsJsonObject()
                        .get("fields")
                        .getAsJsonObject()
                        .get("summary")
                ).replace("Alerta máquina ", "");

                if(!(statusAlerta.equals("\"Concluído\"")) && maquinaEmAlerta.equals("\"" + AbrirChamado.nomeMaquina + "\"")){
                    chamados.add(elemento.getAsJsonObject());
                }
            }
            if(chamados.isEmpty()){
                postarChamado();
            }else{
                for(JsonObject elemento: chamados){
                    idChamado = String.valueOf(elemento.get("id"));
                    prioridadeChamado = Integer.valueOf(
                            String.valueOf(
                                    elemento.get("fields")
                                            .getAsJsonObject()
                                            .get("priority")
                                            .getAsJsonObject()
                                            .get("id")
                            ).replace("\"", "")
                    );
                    if(prioridadeChamado > AbrirChamado.prioridade){
                        atualizarChamado(Integer.valueOf(idChamado.replace("\"", "")));
                        break;
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        AbrirChamado("MI-2", "CPU", 45.5, 1);
    }
}

