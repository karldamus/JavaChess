package testing;

import stockfish.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static java.util.function.UnaryOperator.identity;

public class Stockfish {
    static String fen = "8/8/4Rp2/5P2/1PP1pkP1/7P/1P1r4/7K b - - 0 40";

    public String bestMove(String fen) throws IOException, ExecutionException, InterruptedException, TimeoutException {
        var client = new Client();
        client.start("stockfish");

        // We initialise the engine to use the UCI interface
        client.command("uci", identity(), (s) -> s.startsWith("uciok"), 2000l);

        // We set the give position
        client.command("position fen " + fen, identity(), s -> s.startsWith("readyok"), 2000l);

        String bestMove = client.command(
                "go movetime 3000",
                lines -> lines.stream().filter(s->s.startsWith("bestmove")).findFirst().get(),
                line -> line.startsWith("bestmove"),
                5000l)
                .split(" ")[1];

        client.close();

        return bestMove;
    }
}

// rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR KQkq w - 0 1