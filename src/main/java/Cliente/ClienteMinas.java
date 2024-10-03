package Cliente;

import LogicaBuscaminas.Dificultad;
import LogicaBuscaminas.Tablero;
import java.io.*;
import java.net.Socket;
import java.time.Duration;

public class ClienteMinas {

    Socket cl;
    ObjectInputStream ois;
    public Tablero conectar(String host, int port, Dificultad dificultad) {
        try {
             cl = new Socket(host, port);
            System.out.println("Conexion establecida con el servidor... ");
            DataOutputStream dos = new DataOutputStream(cl.getOutputStream());

            int dificultadInt = 0;

            switch (dificultad) {
                case PRINCIPIANTE -> dificultadInt = 0;
                case INTERMEDIO -> dificultadInt = 1;
                case EXPERTO -> dificultadInt = 2;
            }
            System.out.println("Dificultad enviada al servidor: " + dificultadInt);
            dos.writeInt(dificultadInt);

            ois = new ObjectInputStream(cl.getInputStream());
            Tablero tablero = (Tablero) ois.readObject();
            tablero.imprimirTablero();

            return tablero;

        } catch (IOException e) {
            System.err.println("Error al conectar con el servidor:"+e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public Tablero revelarCasilla(int fila, int columna){
        String comando = "revelar " + fila + " " + columna;
        try {
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(cl.getOutputStream()), true);
            System.out.println("Comando enviado: " + comando);
            pw.println(comando);
            return (Tablero) ois.readObject();

        }catch (IOException | ClassNotFoundException e){
            System.err.println("Se perdio la conexión con el servidor (enviar casilla): "+e);
        }
        return null;
    }

    public void enviarNombre(String name){
        try {
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(cl.getOutputStream()), true);
            System.out.println("Nombre enviado: " + name);
            pw.println(name);

        }catch (IOException e){
            System.err.println("Se perdio la conexión con el servidor (enviar nombre): "+e);
        }
    }

    public void cerrarConexion(){
        try {
            cl.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Tablero alternarBandera(int fila, int columna) {
        String comando = "bandera " + fila + " " + columna;
        try {
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(cl.getOutputStream()), true);
            System.out.println("Comando enviado: " + comando);
            pw.println(comando);
            return (Tablero) ois.readObject();
        }catch (IOException | ClassNotFoundException e){
            System.err.println("Se perdio la conexión con el servidor (enviar alternar bandera): "+e);
        }
        return null;
    }

    public String recibirTiempo() {
        try {
            DataInputStream dis = new DataInputStream(cl.getInputStream());
            long tiempo = dis.readLong();
            Duration duration = Duration.ofMillis(tiempo);
            long horas = duration.toHours();
            long minutos = duration.toMinutes() % 60;
            long segundos = duration.getSeconds() % 60;

            return String.format("%02d:%02d:%02d", horas, minutos, segundos);
        } catch (IOException e) {
            System.err.println("Se perdio la conexión con el servidor (recibir tiempo): "+e);
            return null;
        }
    }
}

