import java.io.*;
import java.net.*;
import java.util.*;

public class JogoDistribuido {
    private static final int PORTA = 8081;

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORTA);
            System.out.println("Servidor iniciado...");

            while (true) {
                Socket player1Socket = serverSocket.accept();
                System.out.println("Jogador 1 conectado...");

                Socket player2Socket = serverSocket.accept();
                System.out.println("Jogador 2 conectado...");

                Jogo jogo = new Jogo(player1Socket, player2Socket);
                new Thread(jogo).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class Jogo implements Runnable {
        private static final int PEDRA = 0;
        private static final int PAPEL = 1;
        private static final int TESOURA = 2;

        private Socket player1Socket;
        private Socket player2Socket;
        private int vitoriasJogador1;
        private int vitoriasJogador2;
        private int empates;

        public Jogo(Socket player1Socket, Socket player2Socket) {
            this.player1Socket = player1Socket;
            this.player2Socket = player2Socket;
        }

        @Override
        public void run() {
            try {
                Scanner player1Input = new Scanner(player1Socket.getInputStream());
                PrintWriter player1Output = new PrintWriter(player1Socket.getOutputStream(), true);
                Scanner player2Input = new Scanner(player2Socket.getInputStream());
                PrintWriter player2Output = new PrintWriter(player2Socket.getOutputStream(), true);

                while (true) {
                    player1Output.println("Bem-vindo! Você é o Jogador 1. Escolha sua jogada (0 para Pedra, 1 para Papel, 2 para Tesoura):");
                    player2Output.println("Bem-vindo! Você é o Jogador 2. Escolha sua jogada (0 para Pedra, 1 para Papel, 2 para Tesoura):");

                    int escolhaJogador1 = player1Input.nextInt();
                    int escolhaJogador2 = player2Input.nextInt();

                    player1Output.println("Você escolheu " + jogadaToString(escolhaJogador1));
                    player2Output.println("Você escolheu " + jogadaToString(escolhaJogador2));

                    String resultado = determinarVencedor(escolhaJogador1, escolhaJogador2);
                    player1Output.println(resultado);
                    player2Output.println(resultado);

                    if (resultado.contains("Jogador 1 vence!")) {
                        vitoriasJogador1++;
                    } else if (resultado.contains("Jogador 2 vence!")) {
                        vitoriasJogador2++;
                    } else {
                        empates++;
                    }

                    player1Output.println("Placar: Vitórias-" + vitoriasJogador1 + ", Derrotas-" + vitoriasJogador2 + ", Empates-" + empates);
                    player2Output.println("Placar: Vitórias-" + vitoriasJogador2 + ", Derrotas-" + vitoriasJogador1 + ", Empates-" + empates);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    player1Socket.close();
                    player2Socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private String jogadaToString(int jogada) {
            switch (jogada) {
                case PEDRA:
                    return "Pedra";
                case PAPEL:
                    return "Papel";
                case TESOURA:
                    return "Tesoura";
                default:
                    return "";
            }
        }

        private String determinarVencedor(int escolhaJogador1, int escolhaJogador2) {
            if (escolhaJogador1 == escolhaJogador2) {
                return "Empate!";
            } else if ((escolhaJogador1 == PEDRA && escolhaJogador2 == TESOURA) ||
                    (escolhaJogador1 == PAPEL && escolhaJogador2 == PEDRA) ||
                    (escolhaJogador1 == TESOURA && escolhaJogador2 == PAPEL)) {
                return "Jogador 1 vence!";
            } else {
                return "Jogador 2 vence!";
            }
        }
    }

}