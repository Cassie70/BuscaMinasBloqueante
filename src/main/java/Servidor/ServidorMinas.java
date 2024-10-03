package Servidor;

import LogicaBuscaminas.Dificultad;
import LogicaBuscaminas.Tablero;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServidorMinas {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String puertoS;
        int puerto=8000;
        boolean esValido = false;

        String regex = "^([0-9]{1,5})$";
        while (!esValido) {
            System.out.print("Ingrese un número de puerto válido (1024-65535): ");
            puertoS = scanner.nextLine();

            if (puertoS.matches(regex)) {
                int puertoInt = Integer.parseInt(puertoS);

                if (puertoInt >= 1024 && puertoInt <= 65535) {
                    esValido = true;
                    System.out.println("Puerto válido: " + puertoInt);
                } else {
                    System.out.println("Error: El puerto debe estar entre 0 y 65535.");
                }
            } else {
                System.out.println("Error: Ingrese un número válido.");
            }
        }

        scanner.close();
        try {
            ServerSocket serverSocket = new ServerSocket(puerto);
            serverSocket.setReuseAddress(true);
            System.out.println("Servicio iniciado... esperando cliente...");

            for (; ; ) {
                Socket clientSocket = null;
                try {
                    // Aceptar cliente
                    clientSocket = serverSocket.accept();
                    System.out.println("Cliente conectado desde -> " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());

                    // Iniciar el contador de tiempo
                    ContadorTiempo contador = new ContadorTiempo();
                    contador.iniciar();

                    // Leer la dificultad
                    DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
                    int dificultad = dis.readInt();
                    System.out.println("Dificultad recibida desde cliente: " + dificultad);

                    // Crear el tablero en función de la dificultad
                    Tablero tablero = switch (dificultad) {
                        case 0 -> new Tablero(Dificultad.PRINCIPIANTE);
                        case 1 -> new Tablero(Dificultad.INTERMEDIO);
                        case 2 -> new Tablero(Dificultad.EXPERTO);
                        default -> throw new RuntimeException();
                    };
                    System.out.println("Tablero enviado: ");
                    tablero.imprimirTablero();

                    // Enviar el tablero al cliente
                    ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
                    oos.writeObject(tablero);
                    oos.flush();

                    // Leer los comandos del cliente
                    BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    Pattern revelar = Pattern.compile("revelar (\\d+) (\\d+)");
                    Pattern bandera = Pattern.compile("bandera (\\d+) (\\d+)");

                    boolean gameOver = false;
                    while (!gameOver) {
                        String comando = br.readLine();
                        if (comando != null) {
                            Matcher matcher = revelar.matcher(comando);
                            System.out.println("Comando recibido: " + comando);
                            if (matcher.find()) {
                                System.out.println("Revelando: " + matcher.group(1) + " " + matcher.group(2));
                                tablero.revelarCasilla(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
                                tablero.imprimirJuego();
                                oos.writeObject(tablero.clone());
                                oos.flush();
                            }

                            matcher = bandera.matcher(comando);
                            if (matcher.find()) {
                                System.out.println("Colocando bandera en: " + matcher.group(1) + " " + matcher.group(2));
                                tablero.alternarBandera(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
                                tablero.imprimirJuego();
                                oos.writeObject(tablero.clone());
                                oos.flush();
                            }

                            if (tablero.isVictoria()) {
                                System.out.println("¡GANASTE!");
                                gameOver = true;
                            }
                            if (tablero.isDerrota()) {
                                System.out.println("¡PERDISTE!");
                                gameOver = true;
                            }
                        }
                    }

                    contador.detener();
                    System.out.println("Tiempo total a enviar: " + contador.getTiempoTotal());
                    DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
                    dos.writeLong(contador.getTiempoTotal());

                    if(tablero.isVictoria()){
                        String name = br.readLine();
                        System.out.println("nombre ganador:"+name);
                        guardarRecord(name,contador.getTiempoTotal());
                    }
                    dis.close();
                    oos.close();
                    br.close();
                    dos.close();
                    clientSocket.close();
                    System.out.println("Cliente desconectado, esperando un nuevo cliente...");

                } catch (Exception e) {
                    e.printStackTrace();
                    if (clientSocket != null) {
                        try {
                            clientSocket.close();
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void guardarRecord(String nombre, Long tiempo) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("records.txt", true))) {

            Duration duration = Duration.ofMillis(tiempo);
            long horas = duration.toHours();
            long minutos = duration.toMinutes() % 60;
            long segundos = duration.getSeconds() % 60;

            writer.write("Nombre: " + nombre + ", Tiempo: " + String.format("%02d:%02d:%02d", horas, minutos, segundos));
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al guardar el récord.");
        }
    }
}
