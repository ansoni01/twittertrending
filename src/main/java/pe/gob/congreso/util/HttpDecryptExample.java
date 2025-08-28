package pe.gob.congreso.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.spec.KeySpec;
import java.util.Base64;

public class HttpDecryptExample {

    public static void main(String[] args) {
        try {
            // URL del servicio
            String urlV1 = "https://archive.twitter-trending.com/tablo_request.php";

            // Datos para el cuerpo de la solicitud POST
            String postData = "param1=value1&param2=value2"; // Reemplaza con los parámetros reales

            // Realizar la solicitud POST
            String encryptedResponse = sendPostRequest(urlV1, postData);
            System.out.println("Respuesta Cifrada: " + encryptedResponse);

            // Variables proporcionadas por el servidor
            String ivHex = "3d885710afcff0397318cbaadba3d8d5";
            String saltHex = "dba6fc5d92b6af81";
            String key = "136783.5"; // Clave base (tghjy)

            // Descifrar la respuesta
            String decryptedResponse = decryptResponse(encryptedResponse, key, ivHex, saltHex);
            System.out.println("Respuesta Descifrada: " + decryptedResponse);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para realizar una solicitud POST
    public static String sendPostRequest(String urlString, String postData) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setDoOutput(true);

        // Enviar datos
        try (OutputStream os = conn.getOutputStream()) {
            os.write(postData.getBytes("UTF-8"));
            os.flush();
        }

        // Leer la respuesta
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
        }

        return response.toString(); // Respuesta cifrada en Base64
    }

    // Método para descifrar la respuesta
    public static String decryptResponse(String encryptedData, String key, String ivHex, String saltHex) throws Exception {
        // Convertir IV y Salt de Hex a bytes
        byte[] iv = hexToBytes(ivHex);
        byte[] salt = hexToBytes(saltHex);

        // Derivar clave usando PBKDF2
        SecretKeySpec secretKey = deriveKey(key, salt);

        // Configurar el cipher para descifrar
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));

        // Decodificar el texto cifrado de Base64
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);

        // Descifrar los datos
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        // Convertir los datos descifrados a texto
        return new String(decryptedBytes, "UTF-8");
    }

    // Método para convertir una cadena hex a un array de bytes
    public static byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] bytes = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            bytes[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return bytes;
    }

    // Método para derivar una clave usando PBKDF2 con la contraseña y la sal
    public static SecretKeySpec deriveKey(String password, byte[] salt) throws Exception {
        int iterationCount = 65536; // Número de iteraciones para la derivación
        int keyLength = 256; // Longitud de la clave en bits
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterationCount, keyLength);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] secretKeyBytes = factory.generateSecret(spec).getEncoded();
        return new SecretKeySpec(secretKeyBytes, "AES");
    }
}

